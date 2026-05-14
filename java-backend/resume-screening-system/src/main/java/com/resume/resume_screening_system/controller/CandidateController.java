package com.resume.resume_screening_system.controller;

import com.resume.resume_screening_system.entity.Candidate;
import com.resume.resume_screening_system.repository.CandidateRepository;
import com.resume.resume_screening_system.service.EmailService;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/candidates")
@CrossOrigin("*")
public class CandidateController {

    @Autowired
    private CandidateRepository candidateRepository;

    @Autowired
    private EmailService emailService;

    // GET ALL CANDIDATES
    @GetMapping
    public List<Candidate> getAllCandidates() {

        return candidateRepository.findAll();
    }

    // GET CANDIDATE BY ID
    @GetMapping("/{id}")
    public Candidate getCandidateById(@PathVariable Long id) {

        return candidateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Candidate not found"));
    }

    // UPLOAD CANDIDATE WITH RESUME
    @PostMapping("/upload")
    public Candidate uploadCandidate(
            @RequestParam("name") String name,
            @RequestParam("email") String email,
            @RequestParam("phoneNumber") String phoneNumber,
            @RequestParam("experience") Double experience,
            @RequestParam("education") String education,
            @RequestParam("resume") MultipartFile file
    ) throws IOException {

        // CREATE UPLOAD DIRECTORY
        String uploadDir = System.getProperty("user.dir")
                + File.separator
                + "uploads";

        File directory = new File(uploadDir);

        if (!directory.exists()) {
            directory.mkdirs();
        }

        // FILE NAME
        String fileName = file.getOriginalFilename();

        // FULL FILE PATH
        String filePath = uploadDir
                + File.separator
                + fileName;

        // SAVE FILE
        File destinationFile = new File(filePath);

        file.transferTo(destinationFile);

        // READ PDF TEXT
        PDDocument document = PDDocument.load(destinationFile);

        PDFTextStripper pdfStripper = new PDFTextStripper();

        String resumeText = pdfStripper.getText(document);

        document.close();

        // EXTRACT SKILLS
        String extractedSkills = "";

        if (resumeText.toLowerCase().contains("java")) {
            extractedSkills += "Java, ";
        }

        if (resumeText.toLowerCase().contains("springboot")
                || resumeText.toLowerCase().contains("spring boot")) {
            extractedSkills += "SpringBoot, ";
        }

        if (resumeText.toLowerCase().contains("mysql")) {
            extractedSkills += "MySQL, ";
        }

        if (resumeText.toLowerCase().contains("python")) {
            extractedSkills += "Python, ";
        }

        if (resumeText.toLowerCase().contains("html")) {
            extractedSkills += "HTML, ";
        }

        // CALCULATE SCORE
        double calculatedScore = 0;

        // SKILL SCORE

        if (resumeText.toLowerCase().contains("java")) {
            calculatedScore += 20;
        }

        if (resumeText.toLowerCase().contains("springboot")
                || resumeText.toLowerCase().contains("spring boot")) {
            calculatedScore += 20;
        }

        if (resumeText.toLowerCase().contains("mysql")) {
            calculatedScore += 20;
        }

        if (resumeText.toLowerCase().contains("python")) {
            calculatedScore += 20;
        }

        if (resumeText.toLowerCase().contains("html")) {
            calculatedScore += 20;
        }

        // EXPERIENCE SCORE

        if (experience >= 5) {
            calculatedScore += 30;
        }
        else if (experience >= 3) {
            calculatedScore += 20;
        }
        else if (experience >= 1) {
            calculatedScore += 10;
        }

        // EDUCATION SCORE

        if (education.toLowerCase().contains("mca")
                || education.toLowerCase().contains("msc")) {

            calculatedScore += 15;
        }
        else if (education.toLowerCase().contains("be")
                || education.toLowerCase().contains("btech")) {

            calculatedScore += 10;
        }

        // AI RANKING

        String rank;

        if (calculatedScore >= 90) {
            rank = "Excellent";
        }
        else if (calculatedScore >= 70) {
            rank = "Good";
        }
        else if (calculatedScore >= 50) {
            rank = "Average";
        }
        else {
            rank = "Low";
        }

        // SAVE DATA TO DATABASE

        Candidate candidate = new Candidate();

        candidate.setName(name);
        candidate.setEmail(email);
        candidate.setPhoneNumber(phoneNumber);
        candidate.setExperience(experience);
        candidate.setEducation(education);

        // SET SCORE
        candidate.setScore(calculatedScore);

        // SET SKILLS
        candidate.setSkills(extractedSkills);

        // SET RANK
        candidate.setRanking(rank);

        // SAVE FILE PATH
        candidate.setResumeFilePath(filePath);

        // SAVE CANDIDATE
        Candidate savedCandidate =
                candidateRepository.save(candidate);

        // SEND EMAIL
        emailService.sendEmail(
                email,
                name,
                calculatedScore,
                rank
        );

        return savedCandidate;
    }

    // UPDATE CANDIDATE
    @PutMapping("/{id}")
    public Candidate updateCandidate(
            @PathVariable Long id,
            @RequestBody Candidate updatedCandidate
    ) {

        Candidate candidate = candidateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Candidate not found"));

        candidate.setName(updatedCandidate.getName());
        candidate.setEmail(updatedCandidate.getEmail());
        candidate.setPhoneNumber(updatedCandidate.getPhoneNumber());
        candidate.setExperience(updatedCandidate.getExperience());
        candidate.setEducation(updatedCandidate.getEducation());
        candidate.setScore(updatedCandidate.getScore());
        candidate.setSkills(updatedCandidate.getSkills());
        candidate.setRanking(updatedCandidate.getRanking());

        return candidateRepository.save(candidate);
    }

    // DELETE CANDIDATE
    @DeleteMapping("/{id}")
    public String deleteCandidate(@PathVariable Long id) {

        candidateRepository.deleteById(id);

        return "Candidate deleted successfully";
    }

    // DOWNLOAD RESUME
    @GetMapping("/download/{id}")
    public ResponseEntity<Resource> downloadResume(
            @PathVariable Long id
    ) throws IOException {

        Candidate candidate = candidateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Candidate not found"));

        File file = new File(candidate.getResumeFilePath());

        Resource resource = new UrlResource(file.toURI());

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + file.getName() + "\""
                )
                .contentType(MediaType.APPLICATION_PDF)
                .body(resource);
    }
}