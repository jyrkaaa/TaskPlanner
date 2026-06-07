package com.jurgen.task_planner.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.jurgen.task_planner.models.entities.auth.TenantUsersEntity;
import com.jurgen.task_planner.models.entities.auth.TenantUsersEntity.TenantUsersId;

public interface TenantUsersRepository extends JpaRepository<TenantUsersEntity, TenantUsersId> {

    @Query("SELECT tu FROM TenantUsersEntity tu WHERE tu.tenant.id = :tenantId AND tu.user.id = :userId")
    Optional<TenantUsersEntity> findByTenantIdAndUserId(@Param("tenantId") int tenantId, @Param("userId") int userId);

    @Query("SELECT tu FROM TenantUsersEntity tu WHERE tu.tenant.id = :tenantId")
    List<TenantUsersEntity> findAllByTenantId(@Param("tenantId") int tenantId);
}
