package com.example.letmecookbe.controller;

import com.example.letmecookbe.dto.request.RecipeCreationRequest;
import com.example.letmecookbe.dto.request.RecipeIngredientsCreationRequest;
import com.example.letmecookbe.dto.request.RecipeIngredientsUpdateRequest;
import com.example.letmecookbe.dto.request.RecipeStepsUpdateRequest;
import com.example.letmecookbe.dto.response.ApiResponse;
import com.example.letmecookbe.dto.response.RecipeIngredientsResponse;
import com.example.letmecookbe.entity.RecipeIngredients;
import com.example.letmecookbe.service.RecipeIngredientsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/recipeIngredients")
@RequiredArgsConstructor
public class RecipeIngredientsController {
    RecipeIngredientsService recipeIngredientsService;

    @PostMapping("/create")
    ApiResponse<RecipeIngredientsResponse> createRecipeIngredients(@RequestBody @Valid RecipeIngredientsCreationRequest request){
        ApiResponse<RecipeIngredientsResponse> response = new ApiResponse<>();
        response.setMessage("Create Recipe Ingredients");
        response.setResult(recipeIngredientsService.createRecipeIngredients(request));
        return response;
    }

    @PutMapping("/update/{id}")
    ApiResponse<RecipeIngredientsResponse> updateRecipeIngredients(@PathVariable String id, @RequestBody @Valid RecipeIngredientsUpdateRequest request){
        ApiResponse<RecipeIngredientsResponse> response = new ApiResponse<>();
        response.setMessage("Update Recipe Ingredients");
        response.setResult(recipeIngredientsService.updateRecipeIngredients(id, request));
        return response;
    }

    @GetMapping("/getAllRecipeIngredients/{id}")
    ApiResponse<List<RecipeIngredients>> getRecipeIngredientsByRecipeId(@PathVariable String id){
        ApiResponse<List<RecipeIngredients>> response = new ApiResponse<>();
        response.setMessage("Get all recipe ingredients by recipe id: "+ id);
        response.setResult(recipeIngredientsService.getRecipeIngredientsByRecipeId(id));
        return response;
    }

    @DeleteMapping("/deleteRecipeIngredients/{recipeId}/{ingredientId}")
    ApiResponse<String> deleteRecipeIngredients(@PathVariable String recipeId, @PathVariable String ingredientId){
        ApiResponse<String> response = new ApiResponse<>();
        response.setResult(recipeIngredientsService.deleteRecipeIngredients(recipeId, ingredientId));
        return response;
    }
}
