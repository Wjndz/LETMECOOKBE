package com.example.letmecookbe.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults (level = lombok.AccessLevel.PRIVATE)
public class AccountCreationRequest {
    @NotBlank(message = "NOT_BLANK")
    @Size(min = 5, message ="USERNAME_INVALID")
    String username;
    @NotBlank(message = "NOT_BLANK")
    @Size(min = 7, message = "PASSWORD_INVALID")
    String password;
    @NotBlank(message = "NOT_BLANK")
    @Email(message = "EMAIL_INVALID")
    String email;
    @NotBlank(message = "NOT_BLANK")
    String code;
}
