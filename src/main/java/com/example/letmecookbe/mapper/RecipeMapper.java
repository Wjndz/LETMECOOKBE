package com.example.letmecookbe.mapper;

import com.example.letmecookbe.dto.request.RecipeCreationRequest;
import com.example.letmecookbe.dto.response.RecipeResponse;
import com.example.letmecookbe.entity.Account;
import com.example.letmecookbe.entity.Recipe;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RecipeMapper {
    Recipe toRecipe(RecipeCreationRequest recipeCreationRequest);

    @Mapping(source = "account.id", target = "accountId")
    @Mapping(source = "subCategory.id", target = "subCategoryId")
    @Mapping(source = "recipe.id",target = "id")
    @Mapping(source = "status", target = "status")
    RecipeResponse toRecipeResponse(Recipe recipe);

}
