package com.example.letmecookbe.repository;

import com.example.letmecookbe.entity.RecipeSteps;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecipeStepsRepository extends JpaRepository<RecipeSteps, String> {
    boolean  existsByRecipeIdAndStep(String recipeId,int step);
    List<RecipeSteps> findRecipeStepsByRecipeId(String recipeId);

    RecipeSteps findRecipeStepsByRecipeIdAndStep(String recipeId, int step);

    @Modifying
    @Query("DELETE FROM RecipeSteps rs WHERE rs.recipe.subCategory.mainCategory.id = :mainCategoryId")
    void deleteByMainCategoryId(String mainCategoryId);

}
