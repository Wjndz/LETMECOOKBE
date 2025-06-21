package com.example.letmecookbe.util;

import com.example.letmecookbe.dto.request.AccountCreationRequest;
import com.example.letmecookbe.entity.Account;
import com.example.letmecookbe.enums.AccountStatus;
import com.example.letmecookbe.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
@Slf4j
public class TempAccountStorage {
    private final Map<String, AccountCreationRequest> tempAccounts = new ConcurrentHashMap<>();
    private final Map<String, LocalDateTime> creationTimes = new ConcurrentHashMap<>();

    private final AccountRepository accountRepository;

    // ✅ Store account temporarily
    public void store(String email, AccountCreationRequest request) {
        tempAccounts.put(email, request);
        creationTimes.put(email, LocalDateTime.now());
        log.info("📦 Stored temp account: {}", email);
    }

    // ✅ Check if temp account exists
    public boolean exists(String email) {
        return tempAccounts.containsKey(email);
    }

    // ✅ Get temp account data
    public AccountCreationRequest get(String email) {
        return tempAccounts.get(email);
    }

    // ✅ Remove temp account
    public void remove(String email) {
        tempAccounts.remove(email);
        creationTimes.remove(email);
        log.info("🗑️ Removed temp account: {}", email);
    }

    // ✅ Get creation time
    public LocalDateTime getCreationTime(String email) {
        return creationTimes.get(email);
    }

    // ✅ Production: Check if account is expired (24 hours)
    public boolean isExpired(String email) {
        LocalDateTime creationTime = creationTimes.get(email);
        if (creationTime == null) {
            return true;
        }
        return creationTime.isBefore(LocalDateTime.now().minusHours(24)); // ✅ 24 hours for production
    }

    // ✅ Production: Auto cleanup after 24 hours (runs every 1 hour)
    @Scheduled(fixedRate = 3600000) // Every 1 hour (3600000 ms)
    public void cleanupExpiredAccounts() {
        LocalDateTime cutoff = LocalDateTime.now().minusHours(24); // ✅ 24 hours cutoff

        log.info("🧹 CLEANUP START - Cutoff: {}", cutoff);
        log.info("📊 Current temp accounts: {}", tempAccounts.size());

        creationTimes.entrySet().removeIf(entry -> {
            String email = entry.getKey();
            LocalDateTime created = entry.getValue();

            log.info("🔍 Checking: {} - Created: {} - Expired: {}",
                    email, created, created.isBefore(cutoff));

            if (created.isBefore(cutoff)) {
                log.info("⏰ Account {} is expired, removing...", email);

                // Check database status before deleting
                accountRepository.findAccountByEmail(email)
                        .ifPresent(account -> {
                            log.info("📋 Found account in DB: {} - Status: {}",
                                    email, account.getStatus());

                            if (account.getStatus() == AccountStatus.INACTIVE) {
                                log.info("🗑️ Deleting INACTIVE account: {}", email);
                                accountRepository.delete(account);
                            } else {
                                log.warn("⚠️ Account {} is NOT INACTIVE: {}",
                                        email, account.getStatus());
                            }
                        });

                tempAccounts.remove(email);
                return true;
            }
            return false;
        });

        log.info("🧹 CLEANUP END - Remaining temp accounts: {}", tempAccounts.size());
    }

    // ✅ Get all temp accounts (for debugging)
    public Map<String, AccountCreationRequest> getAllTempAccounts() {
        return new ConcurrentHashMap<>(tempAccounts);
    }

    // ✅ Clear all temp accounts
    public void clear() {
        tempAccounts.clear();
        creationTimes.clear();
        log.info("🗑️ Cleared all temp accounts");
    }

    // ✅ Get count of temp accounts
    public int size() {
        return tempAccounts.size();
    }
}