package com.resume.resume_screening_system.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "candidates")
public class Candidate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long candidateId;

    private String name;

    private String email;

    private String phoneNumber;

    private Double experience;

    private String education;

    private String resumeFilePath;

    private Double score;

    // SKILLS
    private String skills;

    // AI RANKING
    private String ranking;

    // GETTERS AND SETTERS

    public Long getCandidateId() {
        return candidateId;
    }

    public void setCandidateId(Long candidateId) {
        this.candidateId = candidateId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Double getExperience() {
        return experience;
    }

    public void setExperience(Double experience) {
        this.experience = experience;
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public String getResumeFilePath() {
        return resumeFilePath;
    }

    public void setResumeFilePath(String resumeFilePath) {
        this.resumeFilePath = resumeFilePath;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    // SKILLS GETTER
    public String getSkills() {
        return skills;
    }

    // SKILLS SETTER
    public void setSkills(String skills) {
        this.skills = skills;
    }

    // RANKING GETTER
    public String getRanking() {
        return ranking;
    }

    // RANKING SETTER
    public void setRanking(String ranking) {
        this.ranking = ranking;
    }
}