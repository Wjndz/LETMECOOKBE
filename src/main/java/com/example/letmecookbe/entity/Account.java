package com.example.letmecookbe.entity;

import com.example.letmecookbe.enums.AccountStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "accounts")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;
    String username;
    String password;
    @Column(unique = true)
    String email;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    AccountStatus status = AccountStatus.ACTIVE;

//    Set<String> roles;
    LocalDateTime createdAt;
    //    String permissions;
    String code;

    @Column(name = "ban_end_date")
    LocalDateTime banEndDate;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}