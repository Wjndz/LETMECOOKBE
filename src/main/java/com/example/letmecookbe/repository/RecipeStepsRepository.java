package com.example.letmecookbe.repository;

import com.example.letmecookbe.entity.RecipeSteps;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecipeStepsRepository extends JpaRepository<RecipeSteps, String> {
    List<RecipeSteps> findRecipeStepsByRecipeId(String recipeId);
    RecipeSteps findRecipeStepsByRecipeIdAndStep(String recipeId, String step);

}
