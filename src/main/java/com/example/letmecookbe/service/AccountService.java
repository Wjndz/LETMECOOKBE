package com.example.letmecookbe.service;

import com.example.letmecookbe.constant.PreDefinedRole;
import com.example.letmecookbe.dto.request.AccountCreationRequest;
import com.example.letmecookbe.dto.response.AccountResponse;
import com.example.letmecookbe.entity.Account;
import com.example.letmecookbe.entity.Role;
import com.example.letmecookbe.enums.AccountStatus;
import com.example.letmecookbe.exception.AppException;
import com.example.letmecookbe.exception.ErrorCode;
import com.example.letmecookbe.mapper.AccountMapper;
import com.example.letmecookbe.repository.AccountRepository;
import com.example.letmecookbe.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.AccessLevel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class AccountService {
    AccountMapper accountMapper;
    AccountRepository accountRepository;
    EmailVerification emailVerification;
    PasswordEncoder passwordEncoder;
    RoleRepository roleRepository;

    public void sendVerificationCode(String email) {
        String trimmedEmail = email.trim();
        log.debug("Checking if email [{}] exists", trimmedEmail);
        if (accountRepository.existsAccountByEmail(trimmedEmail)) {
            log.debug("Email [{}] already exists, throwing exception", trimmedEmail);
            throw new AppException(ErrorCode.EMAIL_ALREADY_EXISTED);
        }
        emailVerification.sendCode(trimmedEmail);
    }

    public AccountResponse createAccount(AccountCreationRequest req) {
        String code = req.getCode() == null ? "" : req.getCode().trim();
        if (!emailVerification.verifyCode(req.getEmail(), code)) {
            log.debug("Verification failed for email [{}], code [{}]", req.getEmail(), code);
            throw new AppException(ErrorCode.VERIFICATION_CODE_INVALID);
        }

        if (accountRepository.existsAccountByUsername(req.getUsername())) {
            throw new AppException(ErrorCode.USERNAME_EXISTED);
        }

        Account account = accountMapper.toAccount(req);
        account.setPassword(passwordEncoder.encode(req.getPassword()));
        account.setCreatedAt(LocalDateTime.now());

        if (account.getStatus() == null) {
            account.setStatus(AccountStatus.ACTIVE);
        }
        HashSet<Role> roles = new HashSet<>();
        roleRepository.findById(PreDefinedRole.USER_ROLE).ifPresent(roles::add);;

        account.setRoles(roles);

        Account saved = accountRepository.save(account);
        log.debug("Created account [{}] with id [{}]", saved.getUsername(), saved.getId());
        return accountMapper.toAccountResponse(saved);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void banAccount(String accountId, int days) {
        if (days <= 0 && days != -1) {
            throw new AppException(ErrorCode.INVALID_BAN_DURATION);
        }

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_FOUND));
        account.setStatus(days == -1 ? AccountStatus.BANNED_PERMANENT : AccountStatus.BANNED);

        if (days > 0) {
            account.setBanEndDate(LocalDateTime.now().plusDays(days));
        } else {
            account.setBanEndDate(null);
        }
        accountRepository.save(account);
        log.debug("Banned account [{}] with id [{}] for [{}] days", account.getUsername(), account.getId(), days);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void banAccountPermanently(String accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_FOUND));
        account.setStatus(AccountStatus.BANNED_PERMANENT);
        account.setBanEndDate(null); // Không cần ban_end_date khi ban vĩnh viễn
        accountRepository.save(account);
        log.debug("Permanently banned account [{}] with id [{}]", account.getUsername(), account.getId());
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void activateAccount(String accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_FOUND));
        account.setStatus(AccountStatus.ACTIVE);
        account.setBanEndDate(null); // Xóa thời gian ban
        accountRepository.save(account);
        log.debug("Activated account [{}] with id [{}]", account.getUsername(), account.getId());
    }

    public boolean isAccountActive(String accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_FOUND));
        if (account.getStatus() == AccountStatus.ACTIVE) {
            return true;
        }
        if (account.getStatus() != AccountStatus.BANNED_PERMANENT &&
                account.getBanEndDate() != null &&
                account.getBanEndDate().isBefore(LocalDateTime.now())) {
            account.setStatus(AccountStatus.ACTIVE);
            account.setBanEndDate(null);
            accountRepository.save(account);
            return true;
        }
        return false;
    }

    public void sendPasswordResetCode(String email) {
        String trimmedEmail = email.trim();
        log.debug("Checking if email [{}] exists for password reset", trimmedEmail);

        // Kiểm tra xem email có tồn tại không
        if (!accountRepository.existsAccountByEmail(trimmedEmail)) {
            log.debug("Email [{}] does not exist", trimmedEmail);
            throw new AppException(ErrorCode.ACCOUNT_NOT_EXISTED);
        }

        // Gửi mã xác nhận qua email
        emailVerification.sendCode(trimmedEmail);
    }

    public void resetPassword(String email, String code, String newPassword) {
        String trimmedEmail = email.trim();
        String trimmedCode = code == null ? "" : code.trim();

        // Xác minh mã
        if (!emailVerification.verifyCode(trimmedEmail, trimmedCode)) {
            log.debug("Password reset failed: Invalid code for email [{}]", trimmedEmail);
            throw new AppException(ErrorCode.VERIFICATION_CODE_INVALID);
        }

        // Tìm tài khoản
        Account account = accountRepository.findAccountByEmail(trimmedEmail)
                .orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_EXISTED));

        // Mã hóa mật khẩu mới
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        account.setPassword(passwordEncoder.encode(newPassword));

        // Lưu tài khoản với mật khẩu mới
        accountRepository.save(account);
        log.debug("Password reset successful for email [{}]", trimmedEmail);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<AccountResponse> getAllAcounts() {
        return accountRepository.findAll()
                .stream()
                .map(accountMapper::toAccountResponse)
                .toList();
    }

    @PreAuthorize("hasRole('ADMIN')")
    public AccountResponse getAccountById(String id) {
        return accountRepository.findById(id)
                .map(accountMapper::toAccountResponse)
                .orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_FOUND));
    }

    public void deleteAccount(String id) {
        accountRepository.deleteById(id);
    }
}
