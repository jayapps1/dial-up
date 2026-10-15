package com.gec.backend.admin.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "admin_access_request")
public class AdminAccessRequestEntity {

    @Id
    private UUID id;

    @Column(name = "request_type", nullable = false)
    private String requestType = "ADDITIONAL_AGENCY_ADMIN";

    @Column(name = "agency_id", nullable = false)
    private UUID agencyId;

    @Column(name = "requested_role", nullable = false)
    private String requestedRole = "AGENCY_ADMIN";

    @Column(name = "candidate_first_name", nullable = false)
    private String candidateFirstName;

    @Column(name = "candidate_last_name", nullable = false)
    private String candidateLastName;

    @Column(name = "candidate_email", nullable = false)
    private String candidateEmail;

    @Column(name = "candidate_phone")
    private String candidatePhone;

    @Column(nullable = false)
    private String reason;

    @Column(nullable = false)
    private String status = "PENDING";

    @Column(name = "requested_by", nullable = false)
    private UUID requestedBy;

    @Column(name = "reviewed_by")
    private UUID reviewedBy;

    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;

    @Column(name = "rejection_reason")
    private String rejectionReason;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    public AdminAccessRequestEntity() {}

    public AdminAccessRequestEntity(UUID id, UUID agencyId, String candidateFirstName, String candidateLastName, String candidateEmail, String candidatePhone, String reason, UUID requestedBy) {
        this.id = id;
        this.agencyId = agencyId;
        this.candidateFirstName = candidateFirstName;
        this.candidateLastName = candidateLastName;
        this.candidateEmail = candidateEmail;
        this.candidatePhone = candidatePhone;
        this.reason = reason;
        this.requestedBy = requestedBy;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getRequestType() { return requestType; }
    public void setRequestType(String requestType) { this.requestType = requestType; }
    public UUID getAgencyId() { return agencyId; }
    public void setAgencyId(UUID agencyId) { this.agencyId = agencyId; }
    public String getRequestedRole() { return requestedRole; }
    public void setRequestedRole(String requestedRole) { this.requestedRole = requestedRole; }
    public String getCandidateFirstName() { return candidateFirstName; }
    public void setCandidateFirstName(String candidateFirstName) { this.candidateFirstName = candidateFirstName; }
    public String getCandidateLastName() { return candidateLastName; }
    public void setCandidateLastName(String candidateLastName) { this.candidateLastName = candidateLastName; }
    public String getCandidateEmail() { return candidateEmail; }
    public void setCandidateEmail(String candidateEmail) { this.candidateEmail = candidateEmail; }
    public String getCandidatePhone() { return candidatePhone; }
    public void setCandidatePhone(String candidatePhone) { this.candidatePhone = candidatePhone; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public UUID getRequestedBy() { return requestedBy; }
    public void setRequestedBy(UUID requestedBy) { this.requestedBy = requestedBy; }
    public UUID getReviewedBy() { return reviewedBy; }
    public void setReviewedBy(UUID reviewedBy) { this.reviewedBy = reviewedBy; }
    public LocalDateTime getReviewedAt() { return reviewedAt; }
    public void setReviewedAt(LocalDateTime reviewedAt) { this.reviewedAt = reviewedAt; }
    public String getRejectionReason() { return rejectionReason; }
    public void setRejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
