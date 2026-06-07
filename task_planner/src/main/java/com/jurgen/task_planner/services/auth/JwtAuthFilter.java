package com.jurgen.task_planner.services.auth;

import java.io.IOException;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.jurgen.task_planner.models.auth.CurrentUserContext;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }

        try {
            Claims claims = jwtService.extractAllClaims(authHeader.substring(7));

            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                String email  = claims.getSubject();
                int    userId = claims.get("userId", Integer.class);

                @SuppressWarnings("unchecked")
                Map<String, String> raw = (Map<String, String>) claims.get("tenantRoles");
                Map<Integer, String> tenantRoles = raw == null ? Map.of() :
                    raw.entrySet().stream()
                        .collect(Collectors.toMap(
                            e -> Integer.parseInt(e.getKey()),
                            Map.Entry::getValue
                        ));

                var authorities = tenantRoles.values().stream()
                    .distinct()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());

                var ctx = new CurrentUserContext(userId, email, tenantRoles);
                var auth = new UsernamePasswordAuthenticationToken(ctx, null, authorities);
                auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(auth);
            }

            chain.doFilter(request, response);

        } catch (JwtException e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }
}
