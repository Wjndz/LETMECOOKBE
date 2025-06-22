package com.example.letmecookbe.service;

import com.example.letmecookbe.dto.request.LikeRecipeRequest;
import com.example.letmecookbe.dto.response.LikeRecipeResponse;
import com.example.letmecookbe.dto.response.RecipeResponse;
import com.example.letmecookbe.entity.Account;
import com.example.letmecookbe.entity.LikeRecipe;
import com.example.letmecookbe.entity.Recipe;
import com.example.letmecookbe.exception.AppException;
import com.example.letmecookbe.exception.ErrorCode;
import com.example.letmecookbe.mapper.LikeRecipeMapper;
import com.example.letmecookbe.mapper.RecipeMapper;
import com.example.letmecookbe.repository.AccountRepository;
import com.example.letmecookbe.repository.LikeRecipeRepository;
import com.example.letmecookbe.repository.RecipeRepository;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LikeRecipeService {
    LikeRecipeRepository likeRecipeRepository;
    LikeRecipeMapper likeRecipeMapper;
    RecipeRepository RecipeRepository;
    AccountRepository accountRepository;

    private String getAccountIdFromContext() {
        var context = SecurityContextHolder.getContext();
        String email = context.getAuthentication().getName();
        Account account = accountRepository.findAccountByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_FOUND));
        return account.getId();
    }

    @PreAuthorize("hasAuthority('LIKE')")
    public LikeRecipeResponse createLike(String id, LikeRecipeRequest request) {
        Recipe recipe = RecipeRepository.findById(id).orElseThrow(
                () -> new AppException(ErrorCode.RECIPE_NOT_FOUND)
        );
        Account account = accountRepository.findById(getAccountIdFromContext()).orElseThrow(
                ()->new AppException(ErrorCode.ACCOUNT_NOT_FOUND)
        );
        if(likeRecipeRepository.existsByRecipeIdAndAccountId(getAccountIdFromContext(), id)){
            throw new AppException(ErrorCode.LIKE_RECIPE_EXISTED);
        }
        recipe.setTotalLikes(recipe.getTotalLikes() + 1);
        RecipeRepository.save(recipe);
        LikeRecipe likeRecipe = likeRecipeMapper.toLikeRecipe(request);
        likeRecipe.setAccount(account);
        likeRecipe.setRecipe(recipe);
        likeRecipeRepository.save(likeRecipe);
        return likeRecipeMapper.toLikeRecipeResponse(likeRecipe);
    }

    @PreAuthorize("hasAuthority('GET_ALL_ACCOUNT_LIKE')")
    public List<LikeRecipeResponse> getAllLikeRecipeByAccountId(String recipeId){
        List<LikeRecipe> likeRecipes = likeRecipeRepository.getAllLikeRecipeByAccountIdAndRecipeId(getAccountIdFromContext(),recipeId);
        return likeRecipes.stream()
                .map(likeRecipeMapper::toLikeRecipeResponse)
                .toList();
    }

    @Transactional
    @PreAuthorize("hasAuthority('DISLIKE')")
    public String disLike(String id) {
        Recipe recipe = RecipeRepository.findById(id).orElseThrow(
                () -> new AppException(ErrorCode.RECIPE_NOT_FOUND)
        );
        Account account = accountRepository.findById(getAccountIdFromContext()).orElseThrow(
                ()->new AppException(ErrorCode.ACCOUNT_NOT_FOUND)
        );
        recipe.setTotalLikes(recipe.getTotalLikes() - 1);
        RecipeRepository.save(recipe);
        likeRecipeRepository.deleteByRecipeIdAndAccountId(id, getAccountIdFromContext());
        if(likeRecipeRepository.existsByRecipeIdAndAccountId(id, getAccountIdFromContext())){
            return "delete dislike recipe failed";
        }
        return "delete dislike recipe successfully";
    }
}
