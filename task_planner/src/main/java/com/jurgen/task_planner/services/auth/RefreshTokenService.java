package com.jurgen.task_planner.services.auth;

import com.jurgen.task_planner.models.entities.auth.RefreshTokenEntity;
import com.jurgen.task_planner.repositories.RefreshTokenRepository;
import com.jurgen.task_planner.repositories.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    @Value("${app.jwt.refresh-expiration-ms:604800000}")
    private long refreshExpirationMs;

    private final RefreshTokenRepository refreshTokenRepo;
    private final UserJpaRepository userRepo;

    public record RotateResult(String newRefreshToken, String userEmail) {}

    @Transactional
    public RefreshTokenEntity createForUser(int userId) {
        var user = userRepo.findById(userId).orElseThrow();
        var token = new RefreshTokenEntity(
            UUID.randomUUID().toString(),
            user,
            Instant.now().plusMillis(refreshExpirationMs)
        );
        return refreshTokenRepo.save(token);
    }

    // Validates, revokes old token, and issues a new one (rotation).
    // Returns a RotateResult so the caller gets the user email without triggering lazy load outside a transaction.
    @Transactional
    public RotateResult rotate(String rawToken) {
        RefreshTokenEntity old = refreshTokenRepo.findByToken(rawToken)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token not found"));

        if (old.isRevoked() || old.getExpiresAt().isBefore(Instant.now())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token expired or revoked");
        }

        String userEmail = old.getUser().getEmail(); // safe: JOIN FETCH loaded user eagerly
        int userId = old.getUser().getId();

        old.revoke();
        refreshTokenRepo.save(old);

        RefreshTokenEntity newToken = createForUser(userId);
        return new RotateResult(newToken.getToken(), userEmail);
    }
}
