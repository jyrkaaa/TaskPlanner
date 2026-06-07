package com.jurgen.task_planner.models.entities.auth;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "tenants")
@Getter
@Setter
public class TenantEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(name = "confirmed", nullable = false)
    private boolean isConfirmed = false;

    protected TenantEntity() {}

    public TenantEntity(String name, String code) {
        this.name = name;
        this.code = code;
    }

    public boolean isConfirmed() {
        return isConfirmed;
    }

    @OneToMany(mappedBy = "tenant", fetch = FetchType.EAGER)
    private List<TenantUsersEntity> tenantUsers = new ArrayList<>();
}
