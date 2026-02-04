package com.solveria.core.iam.domain.model;

import com.solveria.core.shared.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "iam_field")
public class Field extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @ManyToOne(optional = false)
    private Resource resource;
}
