package com.example.letmecookbe.mapper;

import com.example.letmecookbe.dto.request.PermissionRequest;
import com.example.letmecookbe.dto.response.PermissionResponse;
import org.mapstruct.Mapper;
import com.example.letmecookbe.entity.Permission;

@Mapper(componentModel = "spring")
public interface PermissionMapper {
    Permission toPermission(PermissionRequest request);
    PermissionResponse toPermissionResponse(Permission permission);
}
