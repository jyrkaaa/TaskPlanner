package com.jurgen.task_planner.repositories;

import com.jurgen.task_planner.models.entities.auth.RefreshTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, Integer> {

    @Query("SELECT rt FROM RefreshTokenEntity rt JOIN FETCH rt.user WHERE rt.token = :token")
    Optional<RefreshTokenEntity> findByToken(@Param("token") String token);
}
