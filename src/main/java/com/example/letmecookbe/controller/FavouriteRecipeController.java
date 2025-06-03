package com.example.letmecookbe.controller;


import com.example.letmecookbe.dto.request.FavouriteRecipeRequest;
import com.example.letmecookbe.dto.response.ApiResponse;
import com.example.letmecookbe.dto.response.FavouriteRecipeResponse;
import com.example.letmecookbe.entity.FavouriteRecipe;
import com.example.letmecookbe.service.FavouriteRecipeService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/favourite")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FavouriteRecipeController {
    FavouriteRecipeService favouriteRecipeService;

    @PostMapping("/create/{recipeId}")
    public ApiResponse<FavouriteRecipeResponse> createFavouriteRecipe(@PathVariable String recipeId, @RequestBody @Valid FavouriteRecipeRequest request) {
        ApiResponse<FavouriteRecipeResponse> response = new ApiResponse<>();
        response.setMessage("Create favourite recipe");
        response.setResult(favouriteRecipeService.createFavouriteRecipe(recipeId,request));
        return response;
    }

    @GetMapping("/getAllFavourite")
    public ApiResponse<List<FavouriteRecipeResponse>> getAllFavourite() {
        ApiResponse<List<FavouriteRecipeResponse>> response = new ApiResponse<>();
        response.setMessage("Get all favourite recipes");
        response.setResult(favouriteRecipeService.getFavouriteRecipeByAccountId());
        return response;
    }

    @DeleteMapping("/deleteFavourite/{recipeId}")
    public ApiResponse<String> deleteFavouriteRecipe(@PathVariable String recipeId) {
        ApiResponse<String> response = new ApiResponse<>();
        response.setMessage("Delete favourite recipe");
        response.setResult(favouriteRecipeService.deleteFavouriteRecipe(recipeId));
        return response;
    }
}
