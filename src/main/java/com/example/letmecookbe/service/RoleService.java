package com.example.letmecookbe.service;

import com.example.letmecookbe.dto.request.RoleRequest;
import com.example.letmecookbe.dto.response.RoleResponse;
import com.example.letmecookbe.entity.Role;
import com.example.letmecookbe.mapper.RoleMapper;
import com.example.letmecookbe.repository.PermissionRepository;
import com.example.letmecookbe.repository.RoleRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoleService {
    RoleRepository roleRepository;
    PermissionRepository permissionRepository;
    RoleMapper roleMapper;

    @PreAuthorize("hasRole('ADMIN')")
    public RoleResponse create(RoleRequest request) {
        String roleName = request.getName();
        Role role = roleRepository.findById(roleName).orElse(null);

        if (role == null) {
            role = roleMapper.toRole(request);
        } else {
            role.setDescription(request.getDescription());
        }

        var permissions = permissionRepository.findAllById(request.getPermissions());

        if (role.getPermissions() == null) {
            role.setPermissions(new HashSet<>(permissions));
        } else {
            role.getPermissions().addAll(permissions);
        }

        role = roleRepository.save(role);
        return roleMapper.toRoleResponse(role);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<RoleResponse> getAll() {
        return roleRepository.findAll()
                .stream()
                .map(roleMapper::toRoleResponse)
                .toList();
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void delete(String role) {
        roleRepository.deleteById(role);
    }
}
