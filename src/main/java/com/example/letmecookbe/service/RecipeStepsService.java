package com.example.letmecookbe.service;

import com.example.letmecookbe.dto.request.RecipeStepsCreationRequest;
import com.example.letmecookbe.dto.request.RecipeStepsUpdateRequest;
import com.example.letmecookbe.dto.response.RecipeStepsResponse;
import com.example.letmecookbe.entity.RecipeSteps;
import com.example.letmecookbe.exception.AppException;
import com.example.letmecookbe.exception.ErrorCode;
import com.example.letmecookbe.mapper.RecipeStepsMapper;
import com.example.letmecookbe.repository.RecipeRepository;
import com.example.letmecookbe.repository.RecipeStepsRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RecipeStepsService {
    RecipeStepsRepository recipeStepsRepository;
    RecipeStepsMapper recipeStepsMapper;
    RecipeRepository recipeRepository;

    public RecipeStepsResponse createRecipeSteps(RecipeStepsCreationRequest request){
        if(!recipeRepository.existsById(request.getRecipeId())){
            throw new AppException(ErrorCode.RECIPE_NOT_FOUND);
        }
        RecipeSteps recipeSteps = recipeStepsMapper.toRecipeSteps(request);
        RecipeSteps savedRecipeSteps = recipeStepsRepository.save(recipeSteps);
        return recipeStepsMapper.toRecipeStepsResponse(savedRecipeSteps);
    }

    public RecipeStepsResponse updateRecipeSteps(String RecipeId, RecipeStepsUpdateRequest request){
        RecipeSteps recipeSteps = recipeStepsRepository.findByRecipeIdAndSteps(RecipeId,request.getStep());
        if(recipeSteps == null){
            throw new AppException(ErrorCode.RECIPE_STEPS_NOT_EXISTED);
        }
        if(!recipeRepository.existsById(RecipeId)){
            throw new AppException(ErrorCode.RECIPE_NOT_FOUND);
        }
        if(request.getStep() !=0){
            recipeSteps.setStep(recipeSteps.getStep());
        }
        if(!request.getDescription().isBlank()){
            recipeSteps.setDescription(request.getDescription());
        }
        if(!request.getWaitingTime().isBlank()){
            recipeSteps.setWaitingTime(request.getWaitingTime());
        }
        if(!request.getImage().isBlank()){
            recipeSteps.setRecipeStepsImg(request.getImage());
        }
        RecipeSteps savedRecipeSteps = recipeStepsRepository.save(recipeSteps);
        return recipeStepsMapper.toRecipeStepsResponse(savedRecipeSteps);
    }

    public List<RecipeSteps> getRecipeStepsByRecipeId(String RecipeId){
        if(!recipeRepository.existsById(RecipeId)){
            throw new AppException(ErrorCode.RECIPE_NOT_FOUND);
        }
        return recipeStepsRepository.findRecipeStepsByRecipeId(RecipeId);
    }

    public String deleteRecipeSteps(String RecipeStepsId){
        if(!recipeStepsRepository.existsById(RecipeStepsId)){
            throw new AppException(ErrorCode.RECIPE_STEPS_NOT_EXISTED);
        }
        recipeStepsRepository.deleteById(RecipeStepsId);
        if(recipeStepsRepository.existsById(RecipeStepsId)){
            return "delete recipe steps failed: "+ RecipeStepsId;
        }
        return "delete recipe steps success: "+ RecipeStepsId;
    }

}
