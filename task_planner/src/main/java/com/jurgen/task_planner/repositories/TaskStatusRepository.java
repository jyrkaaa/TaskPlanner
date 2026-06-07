package com.jurgen.task_planner.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.jurgen.task_planner.models.entities.TaskStatusEntity;

public interface TaskStatusRepository extends JpaRepository<TaskStatusEntity, Integer> {

    @Query("SELECT s FROM TaskStatusEntity s WHERE s.tenant.id = :tenantId ORDER BY s.orderNr")
    List<TaskStatusEntity> findAllByTenantIdOrdered(@Param("tenantId") int tenantId);
}
