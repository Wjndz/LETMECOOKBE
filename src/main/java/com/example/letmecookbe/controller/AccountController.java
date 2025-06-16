package com.example.letmecookbe.controller;

import com.example.letmecookbe.dto.request.AccountCreationRequest;
import com.example.letmecookbe.dto.request.ResetPasswordRequest;
import com.example.letmecookbe.dto.response.AccountResponse;
import com.example.letmecookbe.dto.response.ApiResponse;
import com.example.letmecookbe.dto.response.EmailResponse;
import com.example.letmecookbe.service.AccountService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Data
@RequestMapping("/accounts")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AccountController {
    AccountService accountService;

    @PostMapping("/send-code")
    public ApiResponse<String> sendCode(@RequestParam("email") String email) {
        accountService.sendVerificationCode(email);
        return ApiResponse.<String>builder()
                .message("Mã xác thực đã được gửi về " + email)
                .build();
    }

    @PostMapping
    public ApiResponse<AccountResponse> createAccount(
            @RequestBody @Valid AccountCreationRequest request) {
        AccountResponse result = accountService.createAccount(request);
        return ApiResponse.<AccountResponse>builder()
                .result(result)
                .build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{accountId}/manage")
    public ApiResponse<String> manageAccount(
            @PathVariable String accountId,
            @RequestParam String action,
            @RequestParam(required = false) Integer days) {
        boolean isActive = accountService.isAccountActive(accountId);

        switch (action.toLowerCase()) {
            case "ban":
                if (days == null || days <= 0) {
                    throw new IllegalArgumentException("Days must be a positive number for ban action");
                }
                accountService.banAccount(accountId, days);
                return ApiResponse.<String>builder()
                        .message("Tài khoản với id [" + accountId + "] đã bị ban " + days + " ngày")
                        .build();
            case "activate":
                accountService.activateAccount(accountId);
                return ApiResponse.<String>builder()
                        .message("Tài khoản với id [" + accountId + "] đã được kích hoạt lại")
                        .build();
            default:
                throw new IllegalArgumentException("Invalid action. Supported actions: ban, activate");
        }
    }

    @PostMapping("/request-password-reset")
    public ApiResponse<String> requestPasswordReset(@RequestParam("email") String email) {
        accountService.sendPasswordResetCode(email);
        return ApiResponse.<String>builder()
                .message("Mã xác nhận đổi mật khẩu đã được gửi về " + email)
                .build();
    }

    @PostMapping("/reset-password")
    public ApiResponse<Void> resetPassword(@RequestBody @Valid ResetPasswordRequest request) {
        accountService.resetPassword(request.getEmail(), request.getCode(), request.getNewPassword());
        return ApiResponse.<Void>builder()
                .message("Password reset successful")
                .build();
    }

    @GetMapping
    public ApiResponse<Page<AccountResponse>> getAllAccounts(@PageableDefault(size = 3 , page = 0) Pageable pageable) {
        Page<AccountResponse> result = accountService.getAllAccounts(pageable);
        return ApiResponse.<Page<AccountResponse>>builder()
                .result(result)
                .build();
    }

    @GetMapping("/{accountId}")
    public ApiResponse<AccountResponse> getAccountById(@PathVariable String accountId){
        AccountResponse result = accountService.getAccountById(accountId);
        return ApiResponse.<AccountResponse>builder()
                .result(result)
                .build();
    }

    @DeleteMapping("/{accountId}")
    public ApiResponse<String> deleteAccount(@PathVariable String accountId){
        accountService.deleteAccount(accountId);
        return ApiResponse.<String>builder()
                .result("Tài khoản với id [" + accountId + "] đã bị xóa")
                .build();
    }

    @GetMapping("/search")
    public ApiResponse<List<EmailResponse>> searchByEmail(@RequestParam("keyword") String keyword) {
        List<EmailResponse> result = accountService.searchByEmail(keyword);
        return ApiResponse.<List<EmailResponse>>builder()
                .result(result)
                .build();
    }
}