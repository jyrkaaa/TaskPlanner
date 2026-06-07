package com.jurgen.task_planner.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.jurgen.task_planner.models.entities.IssueEntity;

public interface IssueRepository extends JpaRepository<IssueEntity, Integer> {

    @Query("SELECT i FROM IssueEntity i WHERE i.tenant.id = :tenantId")
    List<IssueEntity> findAllByTenantId(@Param("tenantId") int tenantId);

    @Query("SELECT i FROM IssueEntity i WHERE i.id = :id AND i.tenant.id = :tenantId")
    Optional<IssueEntity> findByIdAndTenantId(@Param("id") int id, @Param("tenantId") int tenantId);

    boolean existsByTenantIdAndCode(int tenantId, String code);
}
