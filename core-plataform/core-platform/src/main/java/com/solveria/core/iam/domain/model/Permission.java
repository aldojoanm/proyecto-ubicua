package com.solveria.core.iam.domain.model;

import com.solveria.core.shared.base.BaseEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "iam_permission")
public class Permission extends BaseEntity {

    @ManyToOne(optional = false)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @ManyToOne(optional = false)
    @JoinColumn(name = "module_id", nullable = false)
    private Module module;

    @ManyToOne(optional = false)
    @JoinColumn(name = "resource_id", nullable = false)
    private Resource resource;

    @ManyToOne(optional = false)
    @JoinColumn(name = "action_id", nullable = false)
    private Action action;

    @ManyToOne
    @JoinColumn(name = "field_id")
    private Field field;

    protected Permission() {
        // requerido por JPA
    }

    public Permission(
            Role role,
            Module module,
            Resource resource,
            Action action,
            Field field
    ) {
        this.role = role;
        this.module = module;
        this.resource = resource;
        this.action = action;
        this.field = field;
    }

    public Role getRole() {
        return role;
    }

    void changeRole(Role newRole) {
        this.role = newRole;
    }
}
