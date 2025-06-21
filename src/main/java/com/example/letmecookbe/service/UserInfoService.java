package com.example.letmecookbe.service;

import com.example.letmecookbe.constant.PreDefinedRole;
import com.example.letmecookbe.dto.request.UserInfoCreationRequest;
import com.example.letmecookbe.dto.request.UserInfoUpdateRequest;
import com.example.letmecookbe.dto.response.UserInfoResponse;
import com.example.letmecookbe.dto.response.UsernameResponse;
import com.example.letmecookbe.entity.Account;
import com.example.letmecookbe.entity.Role;
import com.example.letmecookbe.entity.UserInfo;
import com.example.letmecookbe.enums.AccountStatus;
import com.example.letmecookbe.exception.AppException;
import com.example.letmecookbe.exception.ErrorCode;
import com.example.letmecookbe.mapper.UserInfoMapper;
import com.example.letmecookbe.repository.AccountRepository;
import com.example.letmecookbe.repository.RoleRepository;
import com.example.letmecookbe.repository.UserInfoRepository;
import com.example.letmecookbe.util.TempAccountStorage;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import org.springframework.web.multipart.MultipartFile;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UserInfoService {
    UserInfoRepository userInfoRepository;
    AccountRepository accountRepository;
    UserInfoMapper userInfoMapper;
    RoleRepository roleRepository;
    TempAccountStorage tempAccountStorage;
    FileStorageService fileStorageService;

    public UserInfoResponse createUserInfo(String accountId, UserInfoCreationRequest request) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_FOUND));

        log.info("🔍 Creating UserInfo for account: {}", account.getEmail());
        log.info("📊 Current account status: {}", account.getStatus());
        log.info("🔍 Account in temp storage: {}", tempAccountStorage.exists(account.getEmail()));

        // ✅ Kiểm tra xem account này có phải từ temp storage không
        if (tempAccountStorage.exists(account.getEmail())) {
            log.info("🔄 Finalizing account creation for [{}]", account.getEmail());

            // Hoàn tất việc tạo account
            HashSet<Role> roles = new HashSet<>();
            roleRepository.findById(PreDefinedRole.USER_ROLE).ifPresent(roles::add);
            account.setRoles(roles);
            account.setStatus(AccountStatus.ACTIVE); // ✅ Từ INACTIVE → ACTIVE

            Account savedAccount = accountRepository.save(account);
            log.info("✅ Account status updated to: {}", savedAccount.getStatus());
            log.info("✅ Account roles: {}", savedAccount.getRoles().size());

            // ✅ Clear temp storage
            tempAccountStorage.remove(account.getEmail());
            log.info("🗑️ Account [{}] removed from temp storage", account.getEmail());
        } else {
            log.warn("⚠️ Account [{}] NOT found in temp storage, status will remain: {}",
                    account.getEmail(), account.getStatus());
        }

        UserInfo userInfo = userInfoMapper.toUserInfo(request);
        userInfo.setAccount(account);
        UserInfo savedUserInfo = userInfoRepository.save(userInfo);

        log.info("✅ UserInfo created successfully for account: {}", account.getEmail());
        return userInfoMapper.toUserInfoResponse(savedUserInfo);
    }

    @PreAuthorize("hasRole('USER')")
    public UserInfoResponse updateUserInfo(String id, UserInfoUpdateRequest request) {
        UserInfo userInfo = userInfoRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_INFO_NOT_FOUND));

        String accountId = getAccountIdFromContext();

        if (!userInfo.getAccount().getId().equals(accountId)) {
            throw new AppException(ErrorCode.USER_INFO_NOT_FOUND);
        }

        userInfoMapper.updateUserInfo(request, userInfo);
        UserInfo updatedUserInfo = userInfoRepository.save(userInfo);
        return userInfoMapper.toUserInfoResponse(updatedUserInfo);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public Page<UserInfoResponse> getAllUserInfo(Pageable pageable) {
        Page<UserInfo> userInfos = userInfoRepository.findAll(pageable);
        return userInfos.map(userInfoMapper::toUserInfoResponse);
    }

    @PreAuthorize("hasRole('USER')")
    public UserInfoResponse getUserInfo() {
        String accountId = getAccountIdFromContext();
        UserInfo userInfo = userInfoRepository.findByAccountId(accountId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_INFO_NOT_EXISTED));
        return userInfoMapper.toUserInfoResponse(userInfo);
    }

    @PreAuthorize("hasRole('USER')")
    public UserInfoResponse uploadAvatar(MultipartFile avatar) {
        String accountId = getAccountIdFromContext();
        UserInfo userInfo = userInfoRepository.findByAccountId(accountId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_INFO_NOT_FOUND));

        // Sử dụng FileStorageService để upload file
        String avatarUrl = fileStorageService.uploadFile(avatar);
        userInfo.setAvatar(avatarUrl);
        UserInfo updatedUserInfo = userInfoRepository.save(userInfo);
        return userInfoMapper.toUserInfoResponse(updatedUserInfo);
    }

    @PreAuthorize("hasRole('USER')")
    public UserInfoResponse deleteAvatar() {
        String accountId = getAccountIdFromContext();
        UserInfo userInfo = userInfoRepository.findByAccountId(accountId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_INFO_NOT_FOUND));

        // Xóa file avatar nếu có
        if (userInfo.getAvatar() != null) {
            fileStorageService.deleteFile(userInfo.getAvatar());
            userInfo.setAvatar(null); // Đặt lại avatar thành null
            UserInfo updatedUserInfo = userInfoRepository.save(userInfo);
            return userInfoMapper.toUserInfoResponse(updatedUserInfo);
        }

        return userInfoMapper.toUserInfoResponse(userInfo); // Nếu không có avatar, trả về thông tin hiện tại
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<UsernameResponse> searchByUsername(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            throw new AppException(ErrorCode.INVALID_INPUT);
        }
        return userInfoRepository.searchByUsername(keyword.trim())
                .stream()
                .map(userInfo -> UsernameResponse.builder()
                        .username(userInfo.getAccount().getUsername())
                        .build())
                .collect(Collectors.toList());
    }

    private String getAccountIdFromContext() {
        var context = SecurityContextHolder.getContext();
        String email = context.getAuthentication().getName();
        Account account = accountRepository.findAccountByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_FOUND));
        return account.getId();
    }
}