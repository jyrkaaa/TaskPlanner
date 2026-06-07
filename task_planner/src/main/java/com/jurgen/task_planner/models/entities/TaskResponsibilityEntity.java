package com.jurgen.task_planner.models.entities;

import com.jurgen.task_planner.models.entities.auth.TenantEntity;

import jakarta.persistence.*;

@Entity
@Table(name = "task_responsibilities")
public class TaskResponsibilityEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private TenantEntity tenant;

    @Column(nullable = false)
    private String name;

    public static TaskResponsibilityEntity create(TenantEntity tenant, String name) {
        TaskResponsibilityEntity e = new TaskResponsibilityEntity();
        e.tenant = tenant;
        e.name = name;
        return e;
    }

    public int getId()             { return id; }
    public TenantEntity getTenant() { return tenant; }
    public String getName()         { return name; }
}
