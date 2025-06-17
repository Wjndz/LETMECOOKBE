package com.example.letmecookbe.entity;
import com.example.letmecookbe.enums.CommentStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
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
    String id;
    String commentText;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", referencedColumnName = "id")
    Account account;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_id", referencedColumnName = "id") // RecipeID là khóa ngoại tới Recipe
    Recipe recipe;
    LocalDateTime createdAt;
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false) // Đảm bảo cột status không null
    CommentStatus status;
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

}