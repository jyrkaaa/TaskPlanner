package com.jurgen.task_planner.services.auth;


import org.springframework.security.core.userdetails.UserDetails;
import com.jurgen.task_planner.models.entities.auth.UserPrincipal;

import io.jsonwebtoken.Claims;

public interface IJwtService {
    public String generateToken(UserPrincipal principal);
    public boolean isTokenValid(String token, UserDetails details);
    public Claims extractAllClaims(String token);
}
