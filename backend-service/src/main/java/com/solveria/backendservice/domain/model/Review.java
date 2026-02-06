package com.solveria.backendservice.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

@Entity
@Table(name = "reviews")
public class Review extends TenantAwareEntity {

    @Column(name = "subject", nullable = false, length = 160)
    private String subject;

    @Column(name = "rating", nullable = false)
    private int rating;

    @Column(name = "comment", length = 500)
    private String comment;

    @Enumerated(EnumType.STRING)
    @Column(name = "target_type", nullable = false, length = 40)
    private ReviewTargetType targetType;

    @Column(name = "target_id", nullable = false, length = 120)
    private String targetId;

    protected Review() {
    }

    public Review(String subject, int rating, String comment, ReviewTargetType targetType, String targetId) {
        this.subject = subject;
        this.rating = rating;
        this.comment = comment;
        this.targetType = targetType;
        this.targetId = targetId;
    }

    public String getSubject() {
        return subject;
    }

    public int getRating() {
        return rating;
    }

    public String getComment() {
        return comment;
    }

    public ReviewTargetType getTargetType() {
        return targetType;
    }

    public String getTargetId() {
        return targetId;
    }
}
