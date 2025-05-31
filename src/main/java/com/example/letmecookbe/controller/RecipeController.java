package com.example.letmecookbe.controller;

import com.example.letmecookbe.dto.request.RecipeCreationRequest;
import com.example.letmecookbe.dto.request.RecipeUpdateRequest;
import com.example.letmecookbe.dto.response.ApiResponse;
import com.example.letmecookbe.dto.response.RecipeResponse;
import com.example.letmecookbe.entity.Recipe;
import com.example.letmecookbe.service.RecipeService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/recipe")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RecipeController {
    RecipeService recipeService;

    @PostMapping("/create/{subCategoryId}")
    public ApiResponse<RecipeResponse> createRecipe(@PathVariable String subCategoryId,@RequestBody @Valid RecipeCreationRequest request){
        ApiResponse<RecipeResponse> response = new ApiResponse<>();
        response.setMessage("Create Recipe: "+ request.getTitle());
        response.setResult(recipeService.createRecipe(subCategoryId,request));
        return response;
    }

    @PutMapping("/update/{id}")
    public ApiResponse<RecipeResponse> updateRecipe(@PathVariable String id, @RequestBody @Valid RecipeUpdateRequest request){
        ApiResponse<RecipeResponse> response = new ApiResponse<>();
        response.setMessage("Update Recipe: "+ request.getTitle());
        response.setResult(recipeService.updateRecipe(id, request));
        return response;
    }

    @GetMapping("/getAll")
    public ApiResponse<List<RecipeResponse>> getAllRecipe(){
        ApiResponse<List<RecipeResponse>> response = new ApiResponse<>();
        response.setMessage("Get all Recipe: ");
        response.setResult(recipeService.getAllRecipe());
        return response;
    }

    @GetMapping("/getBySubCategory/{id}")
    public ApiResponse<List<RecipeResponse>> getRecipeBySubCategory(@PathVariable String id){
        ApiResponse<List<RecipeResponse>> response = new ApiResponse<>();
        response.setMessage("Get all Recipe by Sub Category: "+ id);
        response.setResult(recipeService.getRecipeBySubCategoryId(id));
        return response;
    }

    @GetMapping("/findByKeyWord/{keyword}")
    public ApiResponse<List<RecipeResponse>> findRecipeByKeyWord(@PathVariable String keyword){
        ApiResponse<List<RecipeResponse>> response = new ApiResponse<>();
        response.setMessage("find all Recipe by keyword: "+ keyword);
        response.setResult(recipeService.findRecipeByKeyword(keyword));
        return response;
    }

    @GetMapping("/getRecipeByAccount")
    public ApiResponse<List<RecipeResponse>> getRecipeByAccount(){
        ApiResponse<List<RecipeResponse>> response = new ApiResponse<>();
        response.setMessage("Get all Recipe by Account: ");
        response.setResult(recipeService.getRecipeByAccountId());
        return response;
    }

    @PostMapping("/changeStatus/{id}")
    public ApiResponse<RecipeResponse> changeStatus(@PathVariable String id){
        ApiResponse<RecipeResponse> response = new ApiResponse<>();
        response.setMessage("Change Status: "+ id);
        response.setResult(recipeService.changeStatusToApprove(id));
        return response;
    }

    @DeleteMapping("/deleteRecipe/{id}")
    public ApiResponse<String> deleteRecipe(@PathVariable String id){
        ApiResponse<String> response = new ApiResponse<>();
        response.setResult(recipeService.deleteRecipe(id));
        return response;
    }
}
