package com.example.letmecookbe.mapper;

import com.example.letmecookbe.dto.request.RecipeStepsCreationRequest;
import com.example.letmecookbe.dto.response.RecipeStepsResponse;
import com.example.letmecookbe.entity.RecipeSteps;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RecipeStepsMapper {
    RecipeSteps toRecipeSteps(RecipeStepsCreationRequest recipeSteps);

    @Mapping(source = "recipe.title", target = "recipeName")
    @Mapping(source = "recipeSteps.recipeStepsImg",target = "recipeStepImage")
    @Mapping(source = "recipeSteps.id", target = "id")
    RecipeStepsResponse toRecipeStepsResponse(RecipeSteps recipeSteps);
}
