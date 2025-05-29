package com.example.letmecookbe.controller;

import com.example.letmecookbe.dto.request.RecipeStepsCreationRequest;
import com.example.letmecookbe.dto.request.RecipeStepsUpdateRequest;
import com.example.letmecookbe.dto.response.ApiResponse;
import com.example.letmecookbe.dto.response.RecipeStepsResponse;
import com.example.letmecookbe.entity.RecipeSteps;
import com.example.letmecookbe.service.RecipeStepsService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/recipeSteps")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RecipeStepsController {
    RecipeStepsService recipeStepsService;

    @PostMapping("/create")
    ApiResponse<RecipeStepsResponse> createRecipeSteps(@RequestBody @Valid RecipeStepsCreationRequest request){
        ApiResponse<RecipeStepsResponse> response = new ApiResponse<>();
        response.setMessage("Create Recipe Steps");
        response.setResult(recipeStepsService.createRecipeSteps(request));
        return response;
    }

    @PutMapping("/update/{id}")
    ApiResponse<RecipeStepsResponse> updateRecipeSteps(@PathVariable String id,@RequestBody @Valid RecipeStepsUpdateRequest request){
        ApiResponse<RecipeStepsResponse> response = new ApiResponse<>();
        response.setMessage("Update Recipe Steps");
        response.setResult(recipeStepsService.updateRecipeSteps(id,request));
        return response;
    }

    @GetMapping("/getAllRecipeSteps/{id}")
    ApiResponse<List<RecipeSteps>> getRecipeStepsByRecipeId(@PathVariable String id){
        ApiResponse<List<RecipeSteps>> response = new ApiResponse<>();
        response.setMessage("Get all recipe steps by recipe id: "+ id);
        response.setResult(recipeStepsService.getRecipeStepsByRecipeId(id));
        return response;
    }

    @DeleteMapping("/delete/{id}")
    ApiResponse<String> deleteRecipeSteps(@PathVariable String id){
        ApiResponse<String> response = new ApiResponse<>();
        response.setResult(recipeStepsService.deleteRecipeSteps(id));
        return response;
    }

}