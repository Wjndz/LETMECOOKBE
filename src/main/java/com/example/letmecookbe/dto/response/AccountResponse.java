package com.example.letmecookbe.dto.response;

import com.example.letmecookbe.entity.Role;
import com.example.letmecookbe.enums.AccountStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class AccountResponse {
    String id;
    String username;
    String email;
    AccountStatus status;
    LocalDateTime createdAt;
    LocalDateTime banEndDate;
    Set<RoleResponse> roles;
}


