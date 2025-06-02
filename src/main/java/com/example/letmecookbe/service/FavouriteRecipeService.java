package com.example.letmecookbe.service;

import com.example.letmecookbe.dto.request.FavouriteRecipeRequest;
import com.example.letmecookbe.dto.response.FavouriteRecipeResponse;
import com.example.letmecookbe.entity.Account;
import com.example.letmecookbe.entity.FavouriteRecipe;
import com.example.letmecookbe.entity.Recipe;
import com.example.letmecookbe.exception.AppException;
import com.example.letmecookbe.exception.ErrorCode;
import com.example.letmecookbe.mapper.FavouriteRecipeMapper;
import com.example.letmecookbe.repository.AccountRepository;
import com.example.letmecookbe.repository.FavouriteRecipeRepository;
import com.example.letmecookbe.repository.RecipeRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FavouriteRecipeService {
    FavouriteRecipeRepository favouriteRecipeRepository;
    FavouriteRecipeMapper favouriteRecipeMapper;
    AccountRepository accountRepository;
    private final RecipeRepository recipeRepository;

    private String getAccountIdFromContext() {
        var context = SecurityContextHolder.getContext();
        String email = context.getAuthentication().getName();
        Account account = accountRepository.findAccountByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_FOUND));
        return account.getId();
    }

    public FavouriteRecipeResponse createFavouriteRecipe (String recipeId, FavouriteRecipeRequest request){
        Recipe recipe = recipeRepository.findById(recipeId).orElseThrow(
                () -> new AppException(ErrorCode.RECIPE_NOT_FOUND)
        );
        Account account = accountRepository.findById(getAccountIdFromContext()).orElseThrow(
                ()->new RuntimeException("Account not found")
        );
        FavouriteRecipe favouriteRecipe = favouriteRecipeMapper.toFavouriteRecipe(request);
        favouriteRecipe.setAccount(account);
        favouriteRecipe.setRecipe(recipe);
        favouriteRecipeRepository.save(favouriteRecipe);
        return favouriteRecipeMapper.toFavouriteRecipeResponse(favouriteRecipe);
    }
}

