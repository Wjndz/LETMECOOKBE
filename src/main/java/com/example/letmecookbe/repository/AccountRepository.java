package com.example.letmecookbe.repository;

import com.example.letmecookbe.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, String> {
    boolean existsAccountByUsername(String username);
    boolean existsAccountByEmail(String email);
    Optional<Account> findAccountByEmail(String email);
    Optional<Account> findByUsername(String username);
}
