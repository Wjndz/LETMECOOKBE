package com.example.letmecookbe.service;

import com.example.letmecookbe.dto.request.PermissionRequest;
import com.example.letmecookbe.dto.response.PermissionResponse;
import com.example.letmecookbe.entity.Permission;
import com.example.letmecookbe.entity.Role;
import com.example.letmecookbe.exception.AppException;
import com.example.letmecookbe.exception.ErrorCode;
import com.example.letmecookbe.mapper.PermissionMapper;
import com.example.letmecookbe.repository.PermissionRepository;
import com.example.letmecookbe.repository.RoleRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PermissionService {
    PermissionRepository permissionRepository;
    PermissionMapper permissionMapper;
    RoleRepository roleRepository;

    @PreAuthorize("hasRole('ADMIN')" )
    public PermissionResponse create(PermissionRequest request) {
        Permission permission = permissionMapper.toPermission(request);
        permission = permissionRepository.save(permission);
        return permissionMapper.toPermissionResponse(permission);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<PermissionResponse> getAll() {
        var permissions = permissionRepository.findAll();
        return permissions.stream().map(permissionMapper::toPermissionResponse).toList();
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void delete(String permissionName) {
        // Tìm tất cả các Role có Permission này
        List<Role> roles = roleRepository.findAll();
        for (Role role : roles) {
            if (role.getPermissions() != null && role.getPermissions().removeIf(p -> p.getName().equals(permissionName))) {
                roleRepository.save(role); // Cập nhật Role sau khi xóa Permission khỏi tập hợp
            }
        }

        // Sau khi xóa tham chiếu, xóa Permission
        permissionRepository.deleteById(permissionName);
    }
}
