package com.example.letmecookbe.mapper;

import com.example.letmecookbe.dto.request.RecipeStepsCreationRequest;
import com.example.letmecookbe.dto.response.RecipeStepsResponse;
import com.example.letmecookbe.entity.RecipeSteps;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RecipeStepsMapper {
    RecipeSteps toRecipeSteps(RecipeStepsCreationRequest recipeSteps);
    RecipeStepsResponse toRecipeStepsResponse(RecipeSteps recipeSteps);
}
