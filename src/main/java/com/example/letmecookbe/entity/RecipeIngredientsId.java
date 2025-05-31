package com.example.letmecookbe.entity;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecipeIngredientsId implements java.io.Serializable {
    String recipeId;
    String ingredientId;
}