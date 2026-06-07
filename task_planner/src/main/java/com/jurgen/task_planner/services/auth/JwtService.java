package com.jurgen.task_planner.services.auth;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.jurgen.task_planner.models.entities.auth.UserPrincipal;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService implements IJwtService {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration-ms:3600000}")
    private long expirationMs;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
    }

    public String generateToken(UserPrincipal principal) {
        LinkedHashMap<Integer, String> tenantRoles = principal.getAcceptedTenants().stream()
            .collect(Collectors.toMap(
                tu -> tu.getTenant().getId(),
                tu -> tu.getRole().getName(),
                (existing, replacement) -> existing,
                LinkedHashMap::new
            ));

        JwtBuilder jwt = Jwts.builder()
            .subject(principal.getUsername())
            .claim("userId", principal.getUserId())
            .claim("tenantRoles", tenantRoles)
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + expirationMs))
            .signWith(getSigningKey());
        return jwt.compact();
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parser()
            .verifyWith(getSigningKey())
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }

    public boolean isTokenValid(String token, UserDetails details) {
        Claims claims = extractAllClaims(token);
        return claims.getSubject().equals(details.getUsername())
            && claims.getExpiration().after(new Date());
    }
}
