package com.jurgen.task_planner.services;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jurgen.task_planner.models.Constants;
import com.jurgen.task_planner.models.dtos.HttpErrorException;
import com.jurgen.task_planner.models.dtos.TenantDto;
import com.jurgen.task_planner.models.dtos.TenantMemberDto;
import com.jurgen.task_planner.models.entities.auth.RoleEntity;
import com.jurgen.task_planner.models.entities.auth.TenantEntity;
import com.jurgen.task_planner.models.entities.auth.TenantUsersEntity;
import com.jurgen.task_planner.models.entities.auth.UserEntity;
import com.jurgen.task_planner.models.mappers.TenantMapper;
import com.jurgen.task_planner.models.requests.CreateTenantRequest;
import com.jurgen.task_planner.repositories.RoleRepository;
import com.jurgen.task_planner.repositories.TenantRepository;
import com.jurgen.task_planner.repositories.TenantUsersRepository;
import com.jurgen.task_planner.repositories.UserJpaRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TenantService implements ITenantService {

    private final TenantRepository      _tenantRepository;
    private final TenantUsersRepository _tenantUsersRepository;
    private final RoleRepository        _roleRepository;
    private final UserJpaRepository     _userRepository;

    @Override
    public TenantDto getTenantById(int id) throws HttpErrorException {
        return TenantMapper.toDto(
            _tenantRepository.findById(id)
                .orElseThrow(() -> new HttpErrorException("Tenant not found", HttpStatus.NOT_FOUND))
        );
    }

    @Override
    public List<TenantDto> getAll() {
        return _tenantRepository.findAll().stream().map(TenantMapper::toDto).toList();
    }

    @Override
    @Transactional
    public void createTenant(CreateTenantRequest request) {
        TenantEntity tenant = _tenantRepository.save(new TenantEntity(request.name(), request.code()));
        _tenantRepository.save(tenant);
    }

    @Override
    @Transactional
    public boolean updateTenant(TenantDto tenant, int userId) throws HttpErrorException {
        TenantEntity entity = _tenantRepository.findById(tenant.id())
            .orElseThrow(() -> new HttpErrorException("Tenant not found", HttpStatus.NOT_FOUND));

        if (entity.getName().equals(tenant.name()) && entity.isConfirmed() == tenant.isConfirmed()) {
            return false;
        }

        entity.setName(tenant.name());
        entity.setConfirmed(tenant.isConfirmed());
        _tenantRepository.save(entity);
        return true;
    }

    @Override
    @Transactional
    public void joinTenant(int tenantId, int userId) throws HttpErrorException {
        TenantEntity tenant = _tenantRepository.findById(tenantId)
            .orElseThrow(() -> new HttpErrorException("Tenant not found", HttpStatus.NOT_FOUND));

        if (_tenantUsersRepository.findByTenantIdAndUserId(tenantId, userId).isPresent()) {
            throw new HttpErrorException("Already a member of this tenant", HttpStatus.CONFLICT);
        }

        _tenantUsersRepository.save(
            TenantUsersEntity.create(loadUser(userId), tenant, loadRole(Constants.Roles.UNACCEPTED))
        );
    }

    @Override
    @Transactional
    public void acceptUser(int tenantId, int targetUserId) throws HttpErrorException {
        TenantUsersEntity membership = _tenantUsersRepository
            .findByTenantIdAndUserId(tenantId, targetUserId)
            .orElseThrow(() -> new HttpErrorException("User not found in this tenant", HttpStatus.NOT_FOUND));

        if (membership.isAccepted()) {
            throw new HttpErrorException("User is not pending acceptance", HttpStatus.BAD_REQUEST);
        }

        membership.setRole(loadRole(Constants.Roles.USER));
        _tenantUsersRepository.save(membership);
    }

    @Override
    @Transactional
    public void rejectUser(int tenantId, int targetUserId) throws HttpErrorException {

        TenantUsersEntity membership = _tenantUsersRepository
            .findByTenantIdAndUserId(tenantId, targetUserId)
            .orElseThrow(() -> new HttpErrorException("User not found in this tenant", HttpStatus.NOT_FOUND));

        _tenantUsersRepository.delete(membership);
    }

    @Override
    public List<TenantMemberDto> getTenantMembers(int tenantId) {

        return _tenantUsersRepository.findAllByTenantId(tenantId).stream()
            .map(tu -> new TenantMemberDto(
                tu.getUser().getId(),
                tu.getUser().getUsername(),
                tu.getUser().getEmail(),
                tu.getRole().getName()
            ))
            .toList();
    }

    private UserEntity loadUser(int userId) {
        return _userRepository.findById(userId)
            .orElseThrow(() -> new IllegalStateException("User not found: " + userId));
    }

    private RoleEntity loadRole(String name) {
        return _roleRepository.findByName(name)
            .orElseThrow(() -> new IllegalStateException(name + " not seeded"));
    }
}
