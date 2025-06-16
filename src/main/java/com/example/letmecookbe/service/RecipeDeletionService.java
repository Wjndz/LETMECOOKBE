package com.example.letmecookbe.service;

import com.example.letmecookbe.entity.Recipe;
import com.example.letmecookbe.entity.RecipeIngredients;
import com.example.letmecookbe.entity.RecipeSteps;
import com.example.letmecookbe.repository.CommentRepository;
import com.example.letmecookbe.repository.RecipeIngredientsRepository;
import com.example.letmecookbe.repository.RecipeRepository;
import com.example.letmecookbe.repository.RecipeStepsRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RecipeDeletionService {
    private final CommentRepository commentRepository;
    private final RecipeIngredientsRepository recipeIngredientsRepository;
    private final RecipeStepsRepository recipeStepsRepository;
    private final RecipeRepository recipeRepository;

    @Transactional
    public void deleteRecipesAndRelatedData(List<Recipe> recipes) {
        for (Recipe recipe : recipes) {
            commentRepository.deleteByRecipeId(recipe.getId());

            List<RecipeIngredients> recipeIngredients =
                    recipeIngredientsRepository.findAllByRecipeId(recipe.getId());
            if (!recipeIngredients.isEmpty()) {
                recipeIngredientsRepository.deleteAll(recipeIngredients);
            }

            List<RecipeSteps> recipeSteps =
                    recipeStepsRepository.findRecipeStepsByRecipeId(recipe.getId());
            if (!recipeSteps.isEmpty()) {
                recipeStepsRepository.deleteAll(recipeSteps);
            }
        }

        if (!recipes.isEmpty()) {
            recipeRepository.deleteAll(recipes);
        }
    }
}

