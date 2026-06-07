package com.jurgen.task_planner.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.jurgen.task_planner.models.entities.TaskResponsibilityEntity;

public interface TaskResponsibilityRepository extends JpaRepository<TaskResponsibilityEntity, Integer> {

    @Query("SELECT r FROM TaskResponsibilityEntity r WHERE r.tenant.id = :tenantId")
    List<TaskResponsibilityEntity> findAllByTenantId(@Param("tenantId") int tenantId);
}
