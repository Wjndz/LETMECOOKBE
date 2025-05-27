package com.example.letmecookbe.mapper;

import com.example.letmecookbe.dto.request.RecipeCreationRequest;
import com.example.letmecookbe.dto.response.RecipeResponse;
import com.example.letmecookbe.entity.Recipe;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RecipeMapper {
    Recipe toRecipe(RecipeCreationRequest recipeCreationRequest);
    RecipeResponse toRecipeResponse(Recipe recipe);
}
