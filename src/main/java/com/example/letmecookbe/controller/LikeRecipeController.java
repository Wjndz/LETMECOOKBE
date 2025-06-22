package com.example.letmecookbe.controller;

import com.example.letmecookbe.dto.request.LikeRecipeRequest;
import com.example.letmecookbe.dto.response.ApiResponse;
import com.example.letmecookbe.dto.response.LikeRecipeResponse;
import com.example.letmecookbe.service.LikeRecipeService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/like")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LikeRecipeController {
    LikeRecipeService likeRecipeService;

    @PostMapping("/createLike/{id}")
    public ApiResponse<LikeRecipeResponse> likeRecipe(@PathVariable String id, LikeRecipeRequest request){
        ApiResponse<LikeRecipeResponse> response = new ApiResponse<>();
        response.setMessage("Like Recipe: "+ id);
        response.setResult(likeRecipeService.createLike(id,request));
        return response;
    }

    @DeleteMapping("/dislike/{id}")
    public ApiResponse<String> dislikeRecipe(@PathVariable String id){
        ApiResponse<String> response = new ApiResponse<>();
        response.setMessage("Dislike Recipe: "+ id);
        response.setResult(likeRecipeService.disLike(id));
        return response;
    }

    @GetMapping("/getAllAccountLikes/{id}")
    public ApiResponse<List<LikeRecipeResponse>> getAllAccountLikes(@PathVariable String id){
        ApiResponse<List<LikeRecipeResponse>> response = new ApiResponse<>();
        response.setMessage("Get all account likes by recipe id: "+ id);
        response.setResult(likeRecipeService.getAllLikeRecipeByAccountId(id));
        return response;
    }
}
