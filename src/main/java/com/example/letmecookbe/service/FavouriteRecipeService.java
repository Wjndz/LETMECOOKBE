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
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

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

    @PreAuthorize("hasAuthority('CREATE_FAVOURITE_RECIPE')")
    public FavouriteRecipeResponse createFavouriteRecipe (String recipeId, FavouriteRecipeRequest request){
        Recipe recipe = recipeRepository.findById(recipeId).orElseThrow(
                () -> new AppException(ErrorCode.RECIPE_NOT_FOUND)
        );
        Account account = accountRepository.findById(getAccountIdFromContext()).orElseThrow(
                ()->new AppException(ErrorCode.ACCOUNT_NOT_FOUND)
        );
        if(favouriteRecipeRepository.existsByAccountIdAndRecipeId(getAccountIdFromContext(), recipeId)){
                throw new AppException(ErrorCode.FAVOURITE_RECIPE_EXISTED);
        }
        FavouriteRecipe favouriteRecipe = favouriteRecipeMapper.toFavouriteRecipe(request);
        favouriteRecipe.setAccount(account);
        favouriteRecipe.setRecipe(recipe);
        favouriteRecipeRepository.save(favouriteRecipe);
        return favouriteRecipeMapper.toFavouriteRecipeResponse(favouriteRecipe);
    }

    @PreAuthorize("hasAuthority('GET_FAVOURITE_RECIPE')")
    public List<FavouriteRecipeResponse> getFavouriteRecipeByAccountId (){
        if(!accountRepository.existsById(getAccountIdFromContext())){
            throw new AppException(ErrorCode.ACCOUNT_NOT_FOUND);
        }
        List<FavouriteRecipe> favouriteRecipes = favouriteRecipeRepository.findFavouriteRecipeByAccountId(getAccountIdFromContext());
        return favouriteRecipes.stream()
                .map(favouriteRecipeMapper::toFavouriteRecipeResponse)
                .collect(Collectors.toList());
    }


    @Transactional
    @PreAuthorize("hasAuthority('DELETE_FAVOURITE_RECIPE')")
    public String deleteFavouriteRecipe(String recipeId) {
        if (!recipeRepository.existsById(recipeId)) {
            throw new AppException(ErrorCode.RECIPE_NOT_FOUND);
        }
        if (!accountRepository.existsById(getAccountIdFromContext())) {
            throw new AppException(ErrorCode.ACCOUNT_NOT_FOUND);
        }
        favouriteRecipeRepository.deleteByRecipeIdAndAccountId(recipeId, getAccountIdFromContext());
        if (favouriteRecipeRepository.existsByAccountIdAndRecipeId(getAccountIdFromContext(), recipeId)) {
            return "delete favourite recipe failed";
        }
        return "delete favourite recipe successfully";
    }
}

