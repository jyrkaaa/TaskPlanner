package com.jurgen.task_planner.models.entities;

import java.time.OffsetDateTime;

import com.jurgen.task_planner.models.entities.auth.TenantEntity;

import jakarta.persistence.*;

@Entity
@Table(name = "task_statuses")
public class TaskStatusEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private TenantEntity tenant;

    @Column(nullable = false)
    private String name;

    @Column(name = "order_nr", nullable = false)
    private int orderNr;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = OffsetDateTime.now();
    }

    public static TaskStatusEntity create(TenantEntity tenant, String name, int orderNr) {
        TaskStatusEntity e = new TaskStatusEntity();
        e.tenant = tenant;
        e.name = name;
        e.orderNr = orderNr;
        return e;
    }

    public int getId()               { return id; }
    public TenantEntity getTenant()  { return tenant; }
    public String getName()          { return name; }
    public int getOrderNr()          { return orderNr; }
    public OffsetDateTime getCreatedAt() { return createdAt; }

    public void setName(String name)    { this.name = name; }
    public void setOrderNr(int orderNr) { this.orderNr = orderNr; }
}
