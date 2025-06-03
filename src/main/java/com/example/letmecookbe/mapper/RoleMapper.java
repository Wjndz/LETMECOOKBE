package com.example.letmecookbe.mapper;

import com.example.letmecookbe.dto.request.RoleRequest;
import com.example.letmecookbe.dto.response.RoleResponse;
import com.example.letmecookbe.entity.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    @Mapping(target = "permissions", ignore = true)
    Role toRole(RoleRequest request);

    RoleResponse toRoleResponse(Role role);
}
