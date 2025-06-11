package com.example.letmecookbe.service;

import com.example.letmecookbe.dto.request.RecipeCreationRequest;
import com.example.letmecookbe.dto.request.RecipeUpdateRequest;
import com.example.letmecookbe.dto.response.RecipeResponse;
import com.example.letmecookbe.entity.Account;
import com.example.letmecookbe.entity.Recipe;
import com.example.letmecookbe.entity.SubCategory;
import com.example.letmecookbe.enums.RecipeStatus;
import com.example.letmecookbe.exception.AppException;
import com.example.letmecookbe.exception.ErrorCode;
import com.example.letmecookbe.mapper.RecipeMapper;
import com.example.letmecookbe.repository.AccountRepository;
import com.example.letmecookbe.repository.RecipeRepository;
import com.example.letmecookbe.repository.SubCategoryRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RecipeService {
    RecipeRepository RecipeRepository;
    RecipeMapper recipeMapper;
    SubCategoryRepository subCategoryRepository;
    AccountRepository accountRepository;
    private final FileStorageService fileStorageService;

    private String getAccountIdFromContext() {
        var context = SecurityContextHolder.getContext();
        String email = context.getAuthentication().getName();
        Account account = accountRepository.findAccountByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_FOUND));
        return account.getId();
    }

    public RecipeResponse createRecipe(String subCategoryId, RecipeCreationRequest request, MultipartFile file){
        SubCategory subCategory = subCategoryRepository.findById(subCategoryId).orElseThrow(
                () -> new AppException(ErrorCode.SUB_CATEGORY_NOT_EXIST)
        );
        Account account = accountRepository.findById(getAccountIdFromContext()).orElseThrow(
                ()->new RuntimeException("Account not found")
        );
        Recipe recipe = recipeMapper.toRecipe(request);
        String recipeImg = fileStorageService.uploadFile(file);
        recipe.setImg(recipeImg);
        recipe.setSubCategory(subCategory);
        recipe.setAccount(account);
        recipe.setStatus(String.valueOf(RecipeStatus.NOT_APPROVED));
        Recipe savedRecipe = RecipeRepository.save(recipe);
        return recipeMapper.toRecipeResponse(savedRecipe);
    }

    public RecipeResponse updateRecipe(String id, RecipeUpdateRequest updateRequest, MultipartFile file){
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

        if( file!= null && !file.isEmpty() ){
            String recipeImg = fileStorageService.uploadFile(file);
            recipe.setImg(recipeImg);
        }

        if(!updateRequest.getSubCategoryId().isBlank()){
            SubCategory sub = subCategoryRepository.findById(updateRequest.getSubCategoryId()).orElseThrow(
                    ()-> new AppException(ErrorCode.SUB_CATEGORY_NOT_EXIST)
            );
            recipe.setSubCategory(sub);
        }

        Recipe savedRecipe = RecipeRepository.save(recipe);
        return recipeMapper.toRecipeResponse(savedRecipe);
    }



    public List<RecipeResponse> getAllRecipe() {
        List<Recipe> recipes = RecipeRepository.findAll();
        if (recipes.isEmpty()) {
            throw new AppException(ErrorCode.LIST_EMPTY);
        }
        return recipes.stream()
                .map(recipeMapper::toRecipeResponse)
                .collect(Collectors.toList());
    }

    public List<RecipeResponse> getRecipeByAccountId(){
        List<Recipe> accountRecipes = RecipeRepository.findRecipeByAccountId(getAccountIdFromContext());
        if (accountRecipes.isEmpty()) {
            throw new AppException(ErrorCode.LIST_EMPTY);
        }
        return accountRecipes.stream()
                .map(recipeMapper::toRecipeResponse)
                .collect(Collectors.toList());
    }

    public List<RecipeResponse> getRecipeBySubCategoryId(String id){
        if(!subCategoryRepository.existsById(id)){
            throw new AppException(ErrorCode.SUB_CATEGORY_NOT_EXIST);
        }
        List<Recipe> recipes = RecipeRepository.findRecipeBySubCategoryId(id);

        return  recipes.stream()
                .filter(recipe -> "APPROVED".equalsIgnoreCase(recipe.getStatus()))
                .map(recipeMapper::toRecipeResponse)
                .collect(Collectors.toList());
    }

    public List<RecipeResponse> findRecipeByKeyword(String keyword){
        List<Recipe> recipes = RecipeRepository.findRecipeByKeyword(keyword);
        return  recipes.stream()
                .map(recipeMapper::toRecipeResponse)
                .collect(Collectors.toList());
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

    public RecipeResponse Like(String id) {
        Recipe recipe = RecipeRepository.findById(id).orElseThrow(
                () -> new AppException(ErrorCode.RECIPE_NOT_FOUND)
        );
        recipe.setTotalLikes(recipe.getTotalLikes() + 1);
        Recipe updatedRecipe = RecipeRepository.save(recipe);
        return recipeMapper.toRecipeResponse(updatedRecipe);
    }

//    public RecipeResponse disLike(String id) {
//        Recipe recipe = RecipeRepository.findById(id).orElseThrow(
//                () -> new AppException(ErrorCode.RECIPE_NOT_FOUND)
//        );
//        recipe.setTotalLikes(recipe.getTotalLikes() - 1);
//        Recipe updatedRecipe = RecipeRepository.save(recipe);
//        return recipeMapper.toRecipeResponse(updatedRecipe);
//    }

    public RecipeResponse changeStatusToApprove(String id){
        Recipe recipe = RecipeRepository.findById(id).orElseThrow(
                () -> new AppException(ErrorCode.RECIPE_NOT_FOUND)
        );
        recipe.setStatus(String.valueOf(RecipeStatus.APPROVED));
        Recipe updatedRecipe = RecipeRepository.save(recipe);
        return recipeMapper.toRecipeResponse(updatedRecipe);
    }


}
