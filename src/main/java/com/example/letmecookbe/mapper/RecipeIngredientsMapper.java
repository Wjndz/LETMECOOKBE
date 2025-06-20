package com.example.letmecookbe.mapper;

import com.example.letmecookbe.dto.request.RecipeIngredientsCreationRequest;
import com.example.letmecookbe.dto.response.RecipeIngredientsResponse;
import com.example.letmecookbe.entity.RecipeIngredients;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RecipeIngredientsMapper {

    RecipeIngredients toRecipeIngredients(RecipeIngredientsCreationRequest ingredients);

    @Mapping(source = "ingredient.measurementUnit",target = "unit")
    @Mapping(source = "ingredient.id",target = "ingredientId")
    @Mapping(source = "ingredient.ingredientName", target = "ingredientName")
//    @Mapping(source = "RecipeIngredient.id", target = "id")
    RecipeIngredientsResponse toRecipeIngredientsResponse(RecipeIngredients recipeIngredients);
}
