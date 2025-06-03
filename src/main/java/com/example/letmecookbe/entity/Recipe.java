package com.example.letmecookbe.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter // <-- Rất quan trọng!
@Setter // <-- Rất quan trọng!
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Recipe {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;
    String title;
    String description;
    String img;
    String cookingTime;
    String difficulty;
    int totalLikes;

    @ManyToOne
    @JoinColumn(name = "sub_Category_id", referencedColumnName = "id")
    SubCategory subCategory;

    @ManyToOne
    @JoinColumn(name = "account_id", referencedColumnName = "id")
    Account account;

    String status;
}
