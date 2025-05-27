package com.example.letmecookbe.repository;

import com.example.letmecookbe.entity.RecipeIngredients;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecipeIngredientsRepository extends JpaRepository<RecipeIngredients, String> {
    boolean existsByRecipeIdAndIngredientId(String recipeId, String ingredientId);
    void deleteByRecipeIdAndIngredientId(String recipeId, String ingredientId);
    void deleteByRecipeId(String recipeId);
    void deleteByIngredientId(String ingredientId);
    RecipeIngredients findRecipeIngredientsByRecipeIdAndIngredientId(String recipeId, String ingredientId);
    List<RecipeIngredients> findAllByRecipeId(String recipeId);
}
