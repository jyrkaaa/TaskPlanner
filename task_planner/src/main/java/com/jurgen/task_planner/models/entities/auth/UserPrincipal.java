package com.jurgen.task_planner.models.entities.auth;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class UserPrincipal implements UserDetails {

    private final UserEntity user;
    private final List<GrantedAuthority> authorities;

    public UserPrincipal(UserEntity user) {
        this.user = user;
        this.authorities = user.getTenants().stream()
            .filter(TenantUsersEntity::isAccepted)
            .map(tu -> (GrantedAuthority) new SimpleGrantedAuthority(tu.getRole().getName()))
            .distinct()
            .collect(Collectors.toList());
    }

    public int getUserId()             { return user.getId(); }
    public UserEntity getUserEntity()  { return user; }

    public List<TenantUsersEntity> getAcceptedTenants() {
        List<TenantUsersEntity> tenants = user.getTenants();
        return tenants != null
            ? tenants.stream().filter(TenantUsersEntity::isAccepted).toList()
            : List.of();
    }

    @Override public String getUsername()               { return user.getEmail(); }
    @Override public String getPassword()               { return user.getHashPassword(); }
    @Override public Collection<? extends GrantedAuthority> getAuthorities() { return authorities; }
    @Override public boolean isEnabled()                { return true; }
    @Override public boolean isAccountNonExpired()      { return true; }
    @Override public boolean isAccountNonLocked()       { return true; }
    @Override public boolean isCredentialsNonExpired()  { return true; }
}
