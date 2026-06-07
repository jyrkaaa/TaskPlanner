package com.jurgen.task_planner.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.jurgen.task_planner.models.entities.TaskEntity;

public interface TaskRepository extends JpaRepository<TaskEntity, Integer> {

    @Query("SELECT t FROM TaskEntity t WHERE t.issue.id = :issueId")
    List<TaskEntity> findAllByIssueId(@Param("issueId") int issueId);

    @Query("SELECT t FROM TaskEntity t WHERE t.id = :id AND t.issue.id = :issueId")
    Optional<TaskEntity> findByIdAndIssueId(@Param("id") int id, @Param("issueId") int issueId);

    @Query("SELECT COUNT(t) FROM TaskEntity t WHERE t.taskStatus.id = :statusId")
    long countByTaskStatusId(@Param("statusId") int statusId);
}
