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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;
    String commentText;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", referencedColumnName = "id")
    Account account;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_id", referencedColumnName = "id") // RecipeID là khóa ngoại tới Recipe
    Recipe recipe;
    LocalDateTime createdAt;
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}