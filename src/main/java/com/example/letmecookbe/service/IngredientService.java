package com.example.letmecookbe.service;

import com.example.letmecookbe.dto.request.IngredientsCreationRequest;
import com.example.letmecookbe.dto.request.IngredientsUpdateRequest;
import com.example.letmecookbe.dto.response.ApiResponse;
import com.example.letmecookbe.dto.response.IngredientsResponse;
import com.example.letmecookbe.entity.Ingredients;
import com.example.letmecookbe.exception.AppException;
import com.example.letmecookbe.exception.ErrorCode;
import com.example.letmecookbe.mapper.IngredientsMapper;
import com.example.letmecookbe.repository.IngredientsRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class IngredientService {

    IngredientsRepository ingredientsRepository;
    IngredientsMapper ingredientsMapper;

    @PreAuthorize("hasRole('ADMIN')")
    public IngredientsResponse createIngredients(IngredientsCreationRequest request){
        if(ingredientsRepository.existsByIngredientName(request.getIngredientName())){
            throw new AppException(ErrorCode.INGREDIENT_EXISTED);
        }
        Ingredients ingredients = ingredientsMapper.toIngredients(request);
        Ingredients savedIngredients = ingredientsRepository.save(ingredients);
        return ingredientsMapper.toIngredientsResponse(savedIngredients);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public IngredientsResponse UpdateIngredients(String id,IngredientsUpdateRequest request){
        Ingredients ingredients = ingredientsRepository.findById(id).orElseThrow(
                ()-> new AppException(ErrorCode.INGREDIENT_NOT_EXISTED));

        if(!request.getIngredientName().isBlank()){
            if(ingredientsRepository.existsByIngredientName(request.getIngredientName())){
                throw new AppException(ErrorCode.INGREDIENT_EXISTED);
            }
            ingredients.setIngredientName(request.getIngredientName());
        }
        if(!request.getMeasurementUnit().isBlank()){
            ingredients.setMeasurementUnit(request.getMeasurementUnit());
        }

        if(!request.getCaloriesPerUnit().isBlank())
            ingredients.setCaloriesPerUnit(request.getCaloriesPerUnit());

        Ingredients savedIngredients = ingredientsRepository.save(ingredients);
        return ingredientsMapper.toIngredientsResponse(savedIngredients);
    }

    @PreAuthorize("hasAuthority('GET_INGREDIENTS')")
    public List<Ingredients> getAllIngredients(){
        if(ingredientsRepository.findAll().isEmpty())
            throw new AppException(ErrorCode.LIST_EMPTY);
        return ingredientsRepository.findAll();
    }

    @PreAuthorize("hasRole('ADMIN')")
    public String deleteIngredients(String id){
        Ingredients ingredients = ingredientsRepository.findById(id).orElseThrow(
                ()-> new AppException(ErrorCode.INGREDIENT_NOT_EXISTED));
        ingredientsRepository.delete(ingredients);
        if(ingredientsRepository.existsByIngredientName(ingredients.getIngredientName())){
            return "delete ingredients failed: "+ id;
        }
        return "delete ingredients success: "+ id;
    }


}
