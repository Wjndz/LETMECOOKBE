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
    @EmbeddedId
    RecipeStepsId id;

//    int step;

    @ManyToOne
    @MapsId("recipeId")
    @JoinColumn(name = "recipe_id", referencedColumnName = "id")
    Recipe recipe;

    String description;
    String waitingTime;
    String recipeStepsImg;
}

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
class RecipeStepsId implements java.io.Serializable {
    String recipeId;
    int step;
}