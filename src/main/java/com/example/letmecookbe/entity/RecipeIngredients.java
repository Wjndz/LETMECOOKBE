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
public class RecipeIngredients {

    @EmbeddedId
    RecipeIngredientsId id;

    @ManyToOne
    @MapsId("recipeId") // ánh xạ đến id.recipeId
    @JoinColumn(name = "recipe_id", referencedColumnName = "id")
    Recipe recipe;

    @ManyToOne
    @MapsId("ingredientId") // ánh xạ đến id.ingredientId
    @JoinColumn(name = "ingredient_id", referencedColumnName = "id")
    Ingredients ingredient;

    String quantity;
}

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
class RecipeIngredientsId implements java.io.Serializable {
    String recipeId;
    String ingredientId;
}