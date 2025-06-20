package com.example.letmecookbe.service;

import com.example.letmecookbe.constant.PreDefinedRole;
import com.example.letmecookbe.dto.request.AccountCreationRequest;
import com.example.letmecookbe.dto.response.AccountResponse;
import com.example.letmecookbe.dto.response.AccountStatusResponse;
import com.example.letmecookbe.dto.response.EmailResponse;
import com.example.letmecookbe.entity.Account;
import com.example.letmecookbe.entity.Role;
import com.example.letmecookbe.enums.AccountStatus;
import com.example.letmecookbe.exception.AppException;
import com.example.letmecookbe.exception.ErrorCode;
import com.example.letmecookbe.mapper.AccountMapper;
import com.example.letmecookbe.repository.AccountRepository;
import com.example.letmecookbe.repository.RoleRepository;
import com.example.letmecookbe.repository.UserInfoRepository;
import com.example.letmecookbe.util.TempAccountStorage;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.AccessLevel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

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
    TempAccountStorage tempAccountStorage;
    UserInfoRepository userInfoRepository;

    public void sendVerificationCode(String email) {
        String trimmedEmail = email.trim();
        log.debug("Checking email [{}] status", trimmedEmail);

        // ✅ Kiểm tra account thật trong DB (đã hoàn tất)
        boolean realAccountExists = accountRepository.existsAccountByEmail(trimmedEmail);

        // ✅ Kiểm tra temp account
        boolean tempAccountExists = tempAccountStorage.exists(trimmedEmail);

        if (realAccountExists) {
            // Account đã hoàn tất → không thể register lại
            log.debug("Real account exists for email [{}]", trimmedEmail);
            throw new AppException(ErrorCode.EMAIL_ALREADY_EXISTED);
        }

        if (tempAccountExists) {
            // Có temp account → cho phép gửi lại code (có thể user muốn thử lại)
            log.debug("Temp account exists for email [{}], allowing resend", trimmedEmail);
            tempAccountStorage.remove(trimmedEmail); // Clear old temp data
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

        // Tạo account thật ngay lập tức với ID thật thay vì "pending"
        Account account = accountMapper.toAccount(req);
        account.setPassword(passwordEncoder.encode(req.getPassword()));
        account.setCreatedAt(LocalDateTime.now());
        account.setStatus(AccountStatus.INACTIVE); // Đánh dấu là chưa hoàn tất

        // Chưa gán role, chỉ gán khi hoàn tất đăng ký userInfo

        Account savedAccount = accountRepository.save(account);

        // Lưu thông tin trong TempAccountStorage để biết đây là account mới
        tempAccountStorage.store(req.getEmail(), req);

        return accountMapper.toAccountResponse(savedAccount);
    }


    public AccountStatusResponse checkAccountStatus(String email) {
        String trimmedEmail = email.trim();

        boolean realAccountExists = accountRepository.existsAccountByEmail(trimmedEmail);
        boolean tempAccountExists = tempAccountStorage.exists(trimmedEmail);

        if (realAccountExists) {
            return AccountStatusResponse.builder()
                    .status("COMPLETED") // Account đã hoàn tất
                    .canLogin(true)
                    .canRegister(false)
                    .message("Account exists, please login")
                    .build();
        }

        if (tempAccountExists) {
            return AccountStatusResponse.builder()
                    .status("PENDING") // Account chưa hoàn tất
                    .canLogin(false)
                    .canRegister(true) // Cho phép register lại hoặc continue
                    .message("Account setup not completed")
                    .build();
        }

        return AccountStatusResponse.builder()
                .status("NOT_EXISTS")
                .canLogin(false)
                .canRegister(true)
                .message("Email not registered")
                .build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void banAccount(String accountId, int days) {
        if (days <= 0 && days != -1) {
            throw new AppException(ErrorCode.INVALID_BAN_DURATION);
        }

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_FOUND));
        account.setStatus(AccountStatus.BANNED);

        if (days > 0) {
            account.setBanEndDate(LocalDateTime.now().plusDays(days));
        } else {
            account.setBanEndDate(null);
        }
        accountRepository.save(account);
        log.debug("Banned account [{}] with id [{}] for [{}] days", account.getUsername(), account.getId(), days);
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
        if (account.getStatus() == AccountStatus.BANNED &&
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
    public Page<AccountResponse> getAllAccounts(Pageable pageable) {
        Page<Account> accounts = accountRepository.findAll(pageable);
        return accounts.map(accountMapper::toAccountResponse);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public AccountResponse getAccountById(String id) {
        return accountRepository.findById(id)
                .map(accountMapper::toAccountResponse)
                .orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_FOUND));
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void deleteAccount(String id) {
        userInfoRepository.findByAccountId(id)
                .ifPresent(userInfo -> userInfoRepository.delete(userInfo));
        accountRepository.deleteById(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<EmailResponse> searchByEmail(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            throw new AppException(ErrorCode.INVALID_INPUT);
        }
        return accountRepository.searchByEmail(keyword.trim())
                .stream()
                .map(account -> EmailResponse.builder().email(account.getEmail()).build())
                .collect(Collectors.toList());
    }
}
