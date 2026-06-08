package com.jurgen.task_planner.controllers;

import com.jurgen.task_planner.models.auth.SecurityUtils;
import com.jurgen.task_planner.models.dtos.HttpErrorException;
import com.jurgen.task_planner.models.dtos.PermissionsDto;
import com.jurgen.task_planner.models.entities.auth.UserPrincipal;
import com.jurgen.task_planner.models.mappers.PermissionsMapper;
import com.jurgen.task_planner.services.auth.IJwtService;
import com.jurgen.task_planner.services.auth.RefreshTokenService;
import com.jurgen.task_planner.services.auth.RegistrationService;
import com.jurgen.task_planner.services.auth.RefreshTokenService.RotateResult;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(produces = "application/json", path = "api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authManager;
    private final UserDetailsService userDetailsService;
    private final IJwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final RegistrationService registrationService;

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody LoginRequest req) {
        authManager.authenticate(
            new UsernamePasswordAuthenticationToken(req.email(), req.password())
        );
        UserPrincipal principal = (UserPrincipal) userDetailsService.loadUserByUsername(req.email());
        String accessToken = jwtService.generateToken(principal);
        String refreshToken = refreshTokenService.createForUser(principal.getUserId()).getToken();
        return ResponseEntity.ok(new TokenResponse(accessToken, refreshToken));
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refresh(@RequestBody RefreshRequest req) {
        RotateResult result = refreshTokenService.rotate(req.refreshToken());
        UserPrincipal principal = (UserPrincipal) userDetailsService.loadUserByUsername(result.userEmail());
        String accessToken = jwtService.generateToken(principal);
        return ResponseEntity.ok(new TokenResponse(accessToken, result.newRefreshToken()));
    }

    @PostMapping("/register")
    public ResponseEntity<TokenResponse> register(@RequestBody RegisterRequest req) throws HttpErrorException {
        var user = registrationService.register(req.email(), req.password(), req.username());
        UserPrincipal principal = (UserPrincipal) userDetailsService.loadUserByUsername(user.getEmail());
        String accessToken = jwtService.generateToken(principal);
        String refreshToken = refreshTokenService.createForUser(principal.getUserId()).getToken();
        return ResponseEntity.status(HttpStatus.CREATED).body(new TokenResponse(accessToken, refreshToken));
    }

    @GetMapping("/permissions")
    public ResponseEntity<List<PermissionsDto>> permissions() {
        if (!SecurityUtils.isAuthenticated()) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        UserPrincipal principal = (UserPrincipal) userDetailsService.loadUserByUsername(SecurityUtils.getCurrentUserEmail());
        List<PermissionsDto> dto = PermissionsMapper.toDto(principal);
        return ResponseEntity.ok(dto);
    }
}

record LoginRequest(String email, String password) {}
record RefreshRequest(String refreshToken) {}
record RegisterRequest(String email, String password, String username) {}
record TokenResponse(String token, String refreshToken) {}
