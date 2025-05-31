package com.example.letmecookbe.service;

import com.example.letmecookbe.dto.request.RecipeIngredientsCreationRequest;
import com.example.letmecookbe.dto.request.RecipeIngredientsUpdateRequest;
import com.example.letmecookbe.dto.response.RecipeIngredientsResponse;
import com.example.letmecookbe.dto.response.RecipeResponse;
import com.example.letmecookbe.entity.Ingredients;
import com.example.letmecookbe.entity.Recipe;
import com.example.letmecookbe.entity.RecipeIngredients;
import com.example.letmecookbe.entity.RecipeIngredientsId;
import com.example.letmecookbe.exception.AppException;
import com.example.letmecookbe.exception.ErrorCode;
import com.example.letmecookbe.mapper.RecipeIngredientsMapper;
import com.example.letmecookbe.repository.IngredientsRepository;
import com.example.letmecookbe.repository.RecipeIngredientsRepository;
import com.example.letmecookbe.repository.RecipeRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RecipeIngredientsService {
    RecipeIngredientsRepository RecipeIngredientsRepository;
    RecipeRepository recipeRepository;
    IngredientsRepository ingredientsRepository;
    RecipeIngredientsMapper recipeIngredientsMapper;
    private final RecipeIngredientsRepository recipeIngredientsRepository;

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
        ingredients.setId(new RecipeIngredientsId(recipe.getId(), ingredient.getId()));
        RecipeIngredients savedIngredients = RecipeIngredientsRepository.save(ingredients);
        return recipeIngredientsMapper.toRecipeIngredientsResponse(savedIngredients);
    }

    public RecipeIngredientsResponse updateRecipeIngredients(String RecipeId,RecipeIngredientsUpdateRequest request){
        RecipeIngredients recipeIngredients = recipeIngredientsRepository.findRecipeIngredientsByRecipeIdAndIngredientId(RecipeId,request.getIngredientId());
        if(recipeIngredients == null){
            throw new AppException(ErrorCode.RECIPE_INGREDIENTS_NOT_EXISTED);
        }
        if(!ingredientsRepository.existsById(request.getIngredientId())){
            throw new AppException(ErrorCode.INGREDIENT_NOT_FOUND);
        }
        if(!request.getIngredientId().isBlank()){
            recipeIngredients.setIngredient(ingredientsRepository.findById(request.getIngredientId()).orElseThrow(
                    ()-> new AppException(ErrorCode.INGREDIENT_NOT_EXISTED)
            ));
        }
        if(request.getQuantity() != 0){
            recipeIngredients.setQuantity(request.getQuantity());
        }
        RecipeIngredients savedRecipeIngredients = recipeIngredientsRepository.save(recipeIngredients);
        return recipeIngredientsMapper.toRecipeIngredientsResponse(savedRecipeIngredients);
    }

    public List<RecipeIngredients> getRecipeIngredientsByRecipeId(String RecipeId){
        if(!recipeRepository.existsById(RecipeId)){
            throw new AppException(ErrorCode.RECIPE_NOT_FOUND);
        }
        return recipeIngredientsRepository.findAllByRecipeId(RecipeId);
    }

    public String deleteRecipeIngredients(String RecipeId,String IngredientId){
        RecipeIngredients recipeIngredients = recipeIngredientsRepository.findRecipeIngredientsByRecipeIdAndIngredientId(RecipeId,IngredientId);
        if(recipeIngredients == null){
            throw new AppException(ErrorCode.RECIPE_INGREDIENTS_NOT_EXISTED);
        }
        recipeIngredientsRepository.delete(recipeIngredients);
        if(recipeIngredientsRepository.findRecipeIngredientsByRecipeIdAndIngredientId(RecipeId,IngredientId) != null){
            return "delete recipe ingredients failed: "+ RecipeId + " " + IngredientId;
        }
        return "delete recipe ingredients success: "+ RecipeId + " " + IngredientId;
    }

}
