package com.example.letmecookbe.mapper;

import com.example.letmecookbe.dto.request.LikeRecipeRequest;
import com.example.letmecookbe.dto.response.LikeRecipeResponse;
import com.example.letmecookbe.entity.LikeRecipe;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LikeRecipeMapper {
    LikeRecipe toLikeRecipe(LikeRecipeRequest likeRecipeRequest);

    @Mapping(source = "recipe.title",target = "recipeName")
    @Mapping(source = "account.username",target ="accountName")
    @Mapping(source = "recipe.id",target = "recipeId")
    LikeRecipeResponse toLikeRecipeResponse(LikeRecipe likeRecipe);
}
