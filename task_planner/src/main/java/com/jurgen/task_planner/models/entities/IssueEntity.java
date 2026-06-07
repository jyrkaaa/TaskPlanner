package com.jurgen.task_planner.models.entities;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import com.jurgen.task_planner.models.entities.auth.TenantEntity;

import jakarta.persistence.*;

@Entity
@Table(name = "issues")
public class IssueEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private TenantEntity tenant;

    @Column(nullable = false)
    private String code;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @OneToMany(mappedBy = "issue", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TaskEntity> tasks = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        createdAt = updatedAt = OffsetDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = OffsetDateTime.now();
    }

    public static IssueEntity create(TenantEntity tenant, String code) {
        IssueEntity e = new IssueEntity();
        e.tenant = tenant;
        e.code = code;
        return e;
    }

    public int getId()             { return id; }
    public TenantEntity getTenant() { return tenant; }
    public String getCode()         { return code; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public OffsetDateTime getUpdatedAt() { return updatedAt; }
    public List<TaskEntity> getTasks()   { return tasks; }

    public void setCode(String code) { this.code = code; }
}
