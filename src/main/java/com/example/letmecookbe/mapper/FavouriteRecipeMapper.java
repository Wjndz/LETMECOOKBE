package com.example.letmecookbe.mapper;

import com.example.letmecookbe.dto.request.FavouriteRecipeRequest;
import com.example.letmecookbe.dto.response.FavouriteRecipeResponse;
import com.example.letmecookbe.entity.FavouriteRecipe;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FavouriteRecipeMapper {
    FavouriteRecipe toFavouriteRecipe(FavouriteRecipeRequest favouriteRecipeRequest);

    @Mapping(source = "recipe.title",target = "recipeName")
    @Mapping(source = "account.username",target ="accountName")
    FavouriteRecipeResponse toFavouriteRecipeResponse(FavouriteRecipe favouriteRecipe);
}
