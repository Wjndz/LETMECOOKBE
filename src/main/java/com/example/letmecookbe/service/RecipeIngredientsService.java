package com.example.letmecookbe.service;

import com.example.letmecookbe.dto.request.RecipeIngredientsCreationRequest;
import com.example.letmecookbe.dto.request.RecipeIngredientsUpdateRequest;
import com.example.letmecookbe.dto.response.RecipeIngredientsResponse;
import com.example.letmecookbe.entity.Ingredients;
import com.example.letmecookbe.entity.Recipe;
import com.example.letmecookbe.entity.RecipeIngredients;
import com.example.letmecookbe.exception.AppException;
import com.example.letmecookbe.exception.ErrorCode;
import com.example.letmecookbe.mapper.RecipeIngredientsMapper;
import com.example.letmecookbe.repository.IngredientsRepository;
import com.example.letmecookbe.repository.RecipeIngredientsRepository;
import com.example.letmecookbe.repository.RecipeRepository;
import jakarta.annotation.PreDestroy;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RecipeIngredientsService {
    RecipeIngredientsRepository RecipeIngredientsRepository;
    RecipeRepository recipeRepository;
    IngredientsRepository ingredientsRepository;
    RecipeIngredientsMapper recipeIngredientsMapper;
    private final RecipeIngredientsRepository recipeIngredientsRepository;

    @PreAuthorize("hasAuthority('CREATE_RECIPE_INGREDIENTS')")
    public RecipeIngredientsResponse createRecipeIngredients (RecipeIngredientsCreationRequest request){
        Ingredients ingredient = ingredientsRepository.findById(request.getIngredientId()).orElseThrow(
                ()-> new AppException(ErrorCode.INGREDIENT_NOT_FOUND)
        );

        Recipe recipe = recipeRepository.findById(request.getRecipeId()).orElseThrow(
                ()-> new AppException(ErrorCode.RECIPE_NOT_FOUND)
        );

        RecipeIngredients ingredients = recipeIngredientsMapper.toRecipeIngredients(request);
        ingredients.setRecipe(recipe);
        ingredients.setIngredient(ingredient);
        RecipeIngredients savedIngredients = RecipeIngredientsRepository.save(ingredients);
        return recipeIngredientsMapper.toRecipeIngredientsResponse(savedIngredients);
    }

@PreAuthorize("hasAuthority('UPDATE_RECIPE_INGREDIENTS')")
    public RecipeIngredientsResponse updateRecipeIngredients(String RecipeId,String ingredientId,RecipeIngredientsUpdateRequest request){
        RecipeIngredients recipeIngredients = recipeIngredientsRepository.findRecipeIngredientsByRecipeIdAndIngredientId(RecipeId,ingredientId);
        if(recipeIngredients == null){
            throw new AppException(ErrorCode.RECIPE_INGREDIENTS_NOT_EXISTED);
        }
        if(!ingredientsRepository.existsById(request.getIngredientId())){
            throw new AppException(ErrorCode.INGREDIENT_NOT_FOUND);
        }
        if(!request.getIngredientId().isBlank()){
            Ingredients ingredient = ingredientsRepository.findById(request.getIngredientId()).orElseThrow(
                    ()-> new AppException(ErrorCode.INGREDIENT_NOT_EXISTED)
            );
            recipeIngredients.setIngredient(ingredient);
        }
        if(request.getQuantity() != 0){
            recipeIngredients.setQuantity(request.getQuantity());
        }
        RecipeIngredients savedRecipeIngredients = recipeIngredientsRepository.save(recipeIngredients);
        return recipeIngredientsMapper.toRecipeIngredientsResponse(savedRecipeIngredients);
    }

    @PreAuthorize("hasAuthority('GET_RECIPE_INGREDIENTS_BY_RECIPE_ID')")
    public List<RecipeIngredientsResponse> getRecipeIngredientsByRecipeId(String RecipeId){
        if(!recipeRepository.existsById(RecipeId)){
            throw new AppException(ErrorCode.RECIPE_NOT_FOUND);
        }
        List<RecipeIngredients> ingredients =recipeIngredientsRepository.findAllByRecipeId(RecipeId);
        return ingredients.stream()
                .map(recipeIngredientsMapper::toRecipeIngredientsResponse)
                .collect(Collectors.toList());
    }

    @PreAuthorize("hasAuthority('DELETE_RECIPE_INGREDIENTS')")
    public String deleteRecipeIngredients(String IngredientId){
        RecipeIngredients recipeIngredients = recipeIngredientsRepository.findById(IngredientId).orElseThrow(
                ()-> new AppException(ErrorCode.RECIPE_INGREDIENTS_NOT_EXISTED)
        );
        recipeIngredientsRepository.deleteById(IngredientId);
        if(recipeIngredientsRepository.findById(IngredientId).isPresent()){
            return "delete recipe ingredients failed";
        }
        return "delete recipe ingredients successfully";
    }

}
