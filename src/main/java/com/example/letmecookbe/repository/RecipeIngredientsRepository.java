package com.example.letmecookbe.repository;

import com.example.letmecookbe.entity.RecipeIngredients;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecipeIngredientsRepository extends JpaRepository<RecipeIngredients, String> {

    RecipeIngredients findRecipeIngredientsByRecipeIdAndIngredientId(String recipeId, String ingredientId);
    List<RecipeIngredients> findAllByRecipeId(String recipeId);

    @Modifying
    @Query("DELETE FROM RecipeIngredients ri WHERE ri.recipe.subCategory.mainCategory.id = :mainCategoryId")
    void deleteByMainCategoryId(String mainCategoryId);

}
