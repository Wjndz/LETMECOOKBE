package com.example.letmecookbe.service;

import com.example.letmecookbe.dto.request.RecipeCreationRequest;
import com.example.letmecookbe.dto.request.RecipeUpdateRequest;
import com.example.letmecookbe.dto.response.RecipeResponse;
import com.example.letmecookbe.entity.Account;
import com.example.letmecookbe.entity.Recipe;
import com.example.letmecookbe.entity.SubCategory;
import com.example.letmecookbe.exception.AppException;
import com.example.letmecookbe.exception.ErrorCode;
import com.example.letmecookbe.mapper.RecipeMapper;
import com.example.letmecookbe.repository.AccountRepository;
import com.example.letmecookbe.repository.RecipeRepository;
import com.example.letmecookbe.repository.SubCategoryRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RecipeService {
    RecipeRepository RecipeRepository;
    RecipeMapper recipeMapper;
    SubCategoryRepository subCategoryRepository;
    AccountRepository accountRepository;

    public RecipeResponse createRecipe(RecipeCreationRequest request){
        SubCategory subCategory = subCategoryRepository.findById(request.getSubCategoryId()).orElseThrow(
                () -> new AppException(ErrorCode.SUB_CATEGORY_NOT_EXIST)
        );
        Account account = accountRepository.findById(request.getAccountId()).orElseThrow(
                ()->new RuntimeException("Account not found")
        );
        Recipe recipe = recipeMapper.toRecipe(request);
        recipe.setSubCategory(subCategory);
        recipe.setAccount(account);
        Recipe savedRecipe = RecipeRepository.save(recipe);
        return recipeMapper.toRecipeResponse(savedRecipe);
    }

    public RecipeResponse updateRecipe(String id, RecipeUpdateRequest updateRequest){
        Recipe recipe = RecipeRepository.findById(id).orElseThrow(
                () -> new AppException(ErrorCode.RECIPE_NOT_FOUND)
        );
        if(!updateRequest.getTitle().isBlank())
            recipe.setTitle(updateRequest.getTitle());
        if(!updateRequest.getDescription().isBlank())
            recipe.setDescription(updateRequest.getDescription());

        if(!updateRequest.getDifficulty().isBlank()) // Sửa lại theo enum
            recipe.setDifficulty(updateRequest.getDifficulty());

        if(!updateRequest.getCookingTime().isBlank())
            recipe.setCookingTime(updateRequest.getCookingTime());
        if(!updateRequest.getStatus().isBlank())
            recipe.setStatus(updateRequest.getStatus());
        Recipe savedRecipe = RecipeRepository.save(recipe);
        return recipeMapper.toRecipeResponse(savedRecipe);
    }



    public List<Recipe> getAllRecipe(){
        if(RecipeRepository.findAll().isEmpty())
            throw new AppException(ErrorCode.LIST_EMPTY);
        return RecipeRepository.findAll();
    }

//    public List<Recipe> getRecipeByAccountId(String id){
//
//    }

    public List<Recipe> getRecipeBySubCategoryId(String id){
        if(!subCategoryRepository.existsById(id)){
            throw new AppException(ErrorCode.SUB_CATEGORY_NOT_EXIST);
        }
        return RecipeRepository.findRecipeBySubCategoryId(id);
    }

    public List<Recipe> findRecipeByKeyword(String keyword){
        return RecipeRepository.findRecipeByKeyword(keyword);
    }

    public String deleteRecipe(String id){
        Recipe recipe = RecipeRepository.findById(id).orElseThrow(
                () -> new AppException(ErrorCode.RECIPE_NOT_FOUND)
        );
        RecipeRepository.delete(recipe);
        if(RecipeRepository.existsById(id)){
            return "delete recipe failed: "+ id;
        }
        return "delete recipe success: "+ id;
    }

    public RecipeResponse incrementLike(String id) {
        Recipe recipe = RecipeRepository.findById(id).orElseThrow(
                () -> new AppException(ErrorCode.RECIPE_NOT_FOUND)
        );
        recipe.setTotalLikes(recipe.getTotalLikes() + 1);
        Recipe updatedRecipe = RecipeRepository.save(recipe);
        return recipeMapper.toRecipeResponse(updatedRecipe);
    }
}