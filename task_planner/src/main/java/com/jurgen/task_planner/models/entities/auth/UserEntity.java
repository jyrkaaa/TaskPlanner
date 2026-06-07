package com.jurgen.task_planner.models.entities.auth;

import jakarta.persistence.*;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "hash_password", nullable = false)
    private String hashPassword;

    @Column(nullable = false)
    private String username;

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    private List<TenantUsersEntity> tenantUsers = new ArrayList<>();

    public static UserEntity create(String email, String hashPassword, String username) {
        UserEntity u = new UserEntity();
        u.email = email;
        u.hashPassword = hashPassword;
        u.username = username;
        return u;
    }

    public List<TenantUsersEntity> getTenants() {
        return tenantUsers;
    }
}
