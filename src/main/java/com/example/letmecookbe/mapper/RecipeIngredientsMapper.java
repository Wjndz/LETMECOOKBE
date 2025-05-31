package com.example.letmecookbe.mapper;

import com.example.letmecookbe.dto.request.RecipeIngredientsCreationRequest;
import com.example.letmecookbe.dto.response.RecipeIngredientsResponse;
import com.example.letmecookbe.entity.RecipeIngredients;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RecipeIngredientsMapper {

    RecipeIngredients toRecipeIngredients(RecipeIngredientsCreationRequest ingredients);
    RecipeIngredientsResponse toRecipeIngredientsResponse(RecipeIngredients recipeIngredients);
}
