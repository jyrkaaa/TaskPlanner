package com.jurgen.task_planner.models.entities.auth;

import java.io.Serializable;
import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
@Table(name = "tenant_users")
public class TenantUsersEntity {

    @EmbeddedId
    private TenantUsersId id;

    @ManyToOne
    @MapsId("tenant_id")
    @JoinColumn(name = "tenant_id")
    private TenantEntity tenant;

    @ManyToOne
    @MapsId("user_id")
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id", nullable = false)
    private RoleEntity role;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public TenantEntity getTenant() { return tenant; }
    public UserEntity getUser()     { return user; }
    public RoleEntity getRole()     { return role; }

    public void setRole(RoleEntity role) { this.role = role; }

    public boolean isAccepted() {
        return role != null && !role.getName().equals("ROLE_UNACCEPTED");
    }

    public static TenantUsersEntity create(UserEntity user, TenantEntity tenant, RoleEntity role) {
        TenantUsersEntity e = new TenantUsersEntity();
        e.user   = user;
        e.tenant = tenant;
        e.role   = role;
        e.id     = new TenantUsersId();
        return e;
    }

    @Embeddable
    public static class TenantUsersId implements Serializable {
        private int tenant_id;
        private int user_id;

        @Override public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            TenantUsersId that = (TenantUsersId) o;
            return tenant_id == that.tenant_id && user_id == that.user_id;
        }

        @Override public int hashCode() {
            return 31 * tenant_id + user_id;
        }
    }
}
