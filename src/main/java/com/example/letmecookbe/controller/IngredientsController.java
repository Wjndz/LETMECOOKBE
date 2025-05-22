package com.example.letmecookbe.controller;

import com.example.letmecookbe.dto.request.IngredientsCreationRequest;
import com.example.letmecookbe.dto.request.IngredientsUpdateRequest;
import com.example.letmecookbe.dto.response.ApiResponse;
import com.example.letmecookbe.dto.response.IngredientsResponse;
import com.example.letmecookbe.entity.Ingredients;
import com.example.letmecookbe.service.IngredientService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ingredients")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class IngredientsController {
    IngredientService ingredientService;

    @PostMapping("/create")
    ApiResponse<IngredientsResponse> createIngredients(@RequestBody @Valid IngredientsCreationRequest request){
        ApiResponse<IngredientsResponse> response = new ApiResponse<>();
        response.setMessage("Create Ingredients: "+ request.getIngredientName());
        response.setResult(ingredientService.createIngredients(request));
        return response;
    }

    @PutMapping("/update/{id}")
    ApiResponse<IngredientsResponse> updateIngredients(@PathVariable String id, @RequestBody @Valid IngredientsUpdateRequest request){
        ApiResponse<IngredientsResponse> response = new ApiResponse<>();
        response.setMessage("Update Ingredients: "+ request.getIngredientName());
        response.setResult(ingredientService.UpdateIngredients(id,request));
        return response;
    }

    @GetMapping("/getAll")
    ApiResponse<List<Ingredients>> getAllIngredients(){
        ApiResponse<List<Ingredients>> response = new ApiResponse<>();
        response.setMessage("Get all Ingredients: ");
        response.setResult(ingredientService.getAllIngredients());
        return response;
    }

    @DeleteMapping("/delete/{id}")
    ApiResponse<String> deleteIngredients(@PathVariable String id){
        ApiResponse<String> response = new ApiResponse<>();
        response.setResult(ingredientService.deleteIngredients(id));
        return response;
    }
}
