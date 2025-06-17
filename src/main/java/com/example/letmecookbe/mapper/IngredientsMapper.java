package com.example.letmecookbe.mapper;

import com.example.letmecookbe.dto.request.IngredientsCreationRequest;
import com.example.letmecookbe.dto.response.IngredientsResponse;
import com.example.letmecookbe.entity.Ingredients;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface IngredientsMapper {
    @Mapping(source = "measurementUnit", target = "measurementUnit")
    Ingredients toIngredients(IngredientsCreationRequest ingredients);

    @Mapping(source = "measurementUnit", target = "measurementUnit")
    IngredientsResponse toIngredientsResponse(Ingredients ingredients);
}
