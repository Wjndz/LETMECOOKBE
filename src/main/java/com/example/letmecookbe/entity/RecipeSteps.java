package com.example.letmecookbe.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RecipeSteps {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    int step;

    @ManyToOne
    @JoinColumn(name = "recipe_id", referencedColumnName = "id")
    Recipe recipe;

    String description;
    String waitingTime;
    String recipeStepsImg;

}
