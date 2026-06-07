package com.jurgen.task_planner.services.auth;

import com.jurgen.task_planner.models.dtos.HttpErrorException;
import com.jurgen.task_planner.models.entities.auth.UserEntity;
import com.jurgen.task_planner.repositories.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RegistrationService {

    private final UserJpaRepository userRepo;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserEntity register(String email, String rawPassword, String username) throws HttpErrorException {
        if (userRepo.existsByEmail(email)) {
            throw new HttpErrorException("Email already in use", HttpStatus.CONFLICT);
        }
        UserEntity user = UserEntity.create(email, passwordEncoder.encode(rawPassword), username);
        return userRepo.save(user);
    }
}
