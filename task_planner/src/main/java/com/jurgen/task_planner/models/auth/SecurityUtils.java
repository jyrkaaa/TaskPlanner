package com.jurgen.task_planner.models.auth;

import java.util.Map;
import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.jurgen.task_planner.models.Constants;

public final class SecurityUtils {

    private SecurityUtils() {}

    private static CurrentUserContext context() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof CurrentUserContext ctx) {
            return ctx;
        }
        throw new IllegalStateException("No JWT authentication in security context");
    }

    public static int getCurrentUserId() {
        return context().userId();
    }

    public static boolean isAuthenticated() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.isAuthenticated() && auth.getPrincipal() instanceof CurrentUserContext;
    }

    public static String getCurrentUserEmail() {
        return context().email();
    }

    /** All tenant memberships the user was accepted into, with their role per tenant. */
    public static Map<Integer, String> getTenantRoles() {
        return context().tenantRoles();
    }

    /** Role of the current user in a specific tenant, empty if not a member. */
    public static Optional<String> getRoleInTenant(int tenantId) {
        return Optional.ofNullable(context().tenantRoles().get(tenantId));
    }

    public static boolean isMemberOfTenant(int tenantId) {
        Optional<String> role = getRoleInTenant(tenantId);
        return role.isPresent() && !role.get().equals(Constants.Roles.UNACCEPTED);
    }

    public static boolean isAdminInTenant(int tenantId) {
        return Constants.Roles.ADMIN.equals(context().tenantRoles().get(tenantId));
    }
}
