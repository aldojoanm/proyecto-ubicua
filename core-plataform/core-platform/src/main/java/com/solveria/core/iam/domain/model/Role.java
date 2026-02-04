package com.solveria.core.iam.domain.model;

import com.solveria.core.shared.base.BaseEntity;
import jakarta.persistence.*;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "iam_role")
public class Role extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column
    private String description;

    @ManyToMany(mappedBy = "roles")
    private Set<User> users = new HashSet<>();

    @OneToMany(mappedBy = "role")
    private Set<Permission> permissions = new HashSet<>();

    protected Role() {
        // Constructor requerido por JPA
    }

    public Role(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Set<Permission> getPermissions() {
        return permissions;
    }

    public void assignPermissions(Collection<Permission> newPermissions) {
        if (newPermissions == null || newPermissions.isEmpty()) {
            return;
        }

        for (Permission permission : newPermissions) {
            if (permission != null && !this.permissions.contains(permission)) {
                permission.changeRole(this);
                this.permissions.add(permission);
            }
        }
    }
}
