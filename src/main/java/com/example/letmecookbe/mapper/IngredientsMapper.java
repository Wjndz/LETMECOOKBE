package com.example.letmecookbe.mapper;

import com.example.letmecookbe.dto.request.IngredientsCreationRequest;
import com.example.letmecookbe.dto.response.IngredientsResponse;
import com.example.letmecookbe.entity.Ingredients;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface IngredientsMapper {
    Ingredients toIngredients(IngredientsCreationRequest ingredients);
    IngredientsResponse toIngredientsResponse(Ingredients ingredients);
}
