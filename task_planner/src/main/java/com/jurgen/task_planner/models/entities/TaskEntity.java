package com.jurgen.task_planner.models.entities;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;

@Entity
@Table(name = "tasks")
public class TaskEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "issue_id", nullable = false)
    private IssueEntity issue;

    @Column
    private String code;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TaskUsersEntity> taskUsers = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        createdAt = updatedAt = OffsetDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = OffsetDateTime.now();
    }

    public static TaskEntity create(IssueEntity issue, String code, String description) {
        TaskEntity e = new TaskEntity();
        e.issue = issue;
        e.code = code;
        e.description = description;
        return e;
    }

    public int getId()              { return id; }
    public IssueEntity getIssue()    { return issue; }
    public String getCode()          { return code; }
    public String getDescription()   { return description; }
    public OffsetDateTime getCreatedAt()       { return createdAt; }
    public OffsetDateTime getUpdatedAt()       { return updatedAt; }
    public List<TaskUsersEntity> getTaskUsers(){ return taskUsers; }

    public void setCode(String code)               { this.code = code; }
    public void setDescription(String description) { this.description = description; }
}
