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
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/recipeSteps")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RecipeStepsController {
    RecipeStepsService recipeStepsService;

    @PostMapping(value = "/create/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<RecipeStepsResponse> createRecipeSteps(
            @PathVariable String id,
            @RequestPart("step") String step,
            @RequestParam("description") String description,
            @RequestParam("waitingTime") String waitingTime,
            @RequestPart(value = "file", required = false) MultipartFile file) {
        int step2 = Integer.parseInt(step);
        RecipeStepsCreationRequest request = RecipeStepsCreationRequest.builder()
                .step(step2)
                .description(description)
                .waitingTime(waitingTime)
                .build();
        ApiResponse<RecipeStepsResponse> response = new ApiResponse<>();
        response.setMessage("Create Recipe Steps");
        response.setResult(recipeStepsService.createRecipeSteps(id, request,file));
        return response;
    }

    @PutMapping(value = "/update/{id}/{step}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<RecipeStepsResponse> updateRecipeSteps(
            @PathVariable String id,
            @PathVariable int step,
            @RequestParam("step") String stepNum,
            @RequestParam("description") String description,
            @RequestParam("waitingTime") String waitingTime,
            @RequestPart(value = "file",required = false) MultipartFile file) {

        Integer newStep = null;
        if (stepNum != null && !stepNum.trim().isEmpty()) {
            try {
                newStep = Integer.parseInt(stepNum.trim());
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid step number: " + stepNum);
            }
        }
        RecipeStepsUpdateRequest request = RecipeStepsUpdateRequest.builder()
                .step(newStep != null ? newStep : 0)
                .description(description)
                .waitingTime(waitingTime)
                .build();
        ApiResponse<RecipeStepsResponse> response = new ApiResponse<>();
        response.setMessage("Update Recipe Steps");
        response.setResult(recipeStepsService.updateRecipeSteps(id,step,request,file));
        return response;
    }

    @GetMapping("/getAllRecipeSteps/{id}")
    public ApiResponse<List<RecipeStepsResponse>> getRecipeStepsByRecipeId(@PathVariable String id){
        ApiResponse<List<RecipeStepsResponse>> response = new ApiResponse<>();
        response.setMessage("Get all recipe steps by recipe id: "+ id);
        response.setResult(recipeStepsService.getRecipeStepsByRecipeId(id));
        return response;
    }

    @DeleteMapping("/delete/{id}")
    public ApiResponse<String> deleteRecipeSteps(@PathVariable String id){
        ApiResponse<String> response = new ApiResponse<>();
        response.setResult(recipeStepsService.deleteRecipeSteps(id));
        return response;
    }

}
