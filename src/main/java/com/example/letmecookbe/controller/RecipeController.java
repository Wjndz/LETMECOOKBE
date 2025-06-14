package com.example.letmecookbe.controller;

import com.example.letmecookbe.dto.request.RecipeCreationRequest;
import com.example.letmecookbe.dto.request.RecipeUpdateRequest;
import com.example.letmecookbe.dto.response.ApiResponse;
import com.example.letmecookbe.dto.response.RecipeResponse;
import com.example.letmecookbe.service.RecipeService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/recipe")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RecipeController {
    RecipeService recipeService;

    @PostMapping(value="/create/{subCategoryId}",consumes =  MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<RecipeResponse> createRecipe(
            @PathVariable String subCategoryId,
            @RequestPart("title") String title,
            @RequestPart("description") String description,
            @RequestPart("difficulty") String difficulty,
            @RequestPart("cookingTime") String cookingTime,
            @RequestPart("file") MultipartFile file){
        RecipeCreationRequest request = RecipeCreationRequest.builder()
                .title(title)
                .description(description)
                .difficulty(difficulty)
                .cookingTime(cookingTime)
                .build();
        ApiResponse<RecipeResponse> response = new ApiResponse<>();
        response.setMessage("Create Recipe: "+ request.getTitle());
        response.setResult(recipeService.createRecipe(subCategoryId,request,file));
        return response;
    }

    @PutMapping(value = "/update/{id}",consumes =  MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<RecipeResponse> updateRecipe(
            @PathVariable String id,
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("difficulty") String difficulty,
            @RequestParam("cookingTime") String cookingTime,
            @RequestParam("subCategoryId") String subCategoryId,
            @RequestPart(value = "file",required = false) MultipartFile file){
        RecipeUpdateRequest request = RecipeUpdateRequest.builder()
                .title(title)
                .description(description)
                .difficulty(difficulty)
                .cookingTime(cookingTime)
                .subCategoryId(subCategoryId)
                .build();
        ApiResponse<RecipeResponse> response = new ApiResponse<>();
        response.setMessage("Update Recipe: "+ request.getTitle());
        response.setResult(recipeService.updateRecipe(id, request, file));
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

    @PostMapping("/like/{id}")
    public ApiResponse<RecipeResponse> likeRecipe(@PathVariable String id){
        ApiResponse<RecipeResponse> response = new ApiResponse<>();
        response.setMessage("Like Recipe: "+ id);
        response.setResult(recipeService.Like(id));
        return response;
    }

    @DeleteMapping("/deleteRecipe/{id}")
    public ApiResponse<String> deleteRecipe(@PathVariable String id){
        ApiResponse<String> response = new ApiResponse<>();
        response.setResult(recipeService.deleteRecipe(id));
        return response;
    }

    @GetMapping("/getTop5Recipe")
    public ApiResponse<List<RecipeResponse>> getTop5Recipe(){
        ApiResponse<List<RecipeResponse>> response= new ApiResponse<>();
        response.setMessage("Top 5 recipe: ");
        response.setResult(recipeService.getTop5RecipesByTotalLikes());
        return response;
    }

    @GetMapping("/getRecipeBySubAndLike/{subCategoryId}")
    public ApiResponse<List<RecipeResponse>> getRecipeBySubAndLike(@PathVariable String subCategoryId){
         ApiResponse<List<RecipeResponse>> response = new ApiResponse<>();
         response.setMessage("Recipe By SubCategory AND TotalLike");
         response.setResult(recipeService.GetRecipesBySubCategoryIdCreatedTodayOrderByLikes(subCategoryId));
         return response;
    }

    @GetMapping("/getTotalRecipe")
    public ApiResponse<Integer> getTotalRecipe(){
        ApiResponse<Integer> response = new ApiResponse<>();
        response.setMessage("Get Total Recipe");
        response.setResult(recipeService.countRecipeBySubCategoryId());
        return response;
    }

    @GetMapping("/count/{accountId}")
    public ApiResponse<Integer> getRecipeCountByUser(@PathVariable String accountId) {
        ApiResponse<Integer> response = new ApiResponse<>();
        response.setMessage("Get Recipe Count for User: " + accountId);
        response.setResult(recipeService.countRecipeByUserId(accountId));
        return response;
    }
}
