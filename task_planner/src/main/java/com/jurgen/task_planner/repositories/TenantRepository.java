package com.jurgen.task_planner.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jurgen.task_planner.models.entities.auth.TenantEntity;

public interface TenantRepository extends JpaRepository<TenantEntity, Integer> {
    
}
