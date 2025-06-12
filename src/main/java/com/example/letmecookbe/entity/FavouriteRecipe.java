package com.example.letmecookbe.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FavouriteRecipe {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @ManyToOne
    @JoinColumn(name = "recipe_id", referencedColumnName = "id")
    Recipe recipe;

    @ManyToOne
    @JoinColumn(name = "account_id", referencedColumnName = "id")
    Account account;
}
