package com.example.letmecookbe.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id; // Sẽ tự động ánh xạ thành cột 'id'
    String commentText; // Sẽ tự động ánh xạ thành cột 'commentText' (kiểu VARCHAR(255) mặc định)
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    Account account; // Sẽ tự động tạo khóa ngoại 'user_id'
    @ManyToOne
    @JoinColumn(name = "recipe_id", referencedColumnName = "id")
    Recipe recipe; // Sẽ tự động tạo khóa ngoại 'recipe_id'
    LocalDateTime createdAt; // Sẽ tự động ánh xạ thành cột 'createdAt' (kiểu DATETIME/TIMESTAMP)
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}