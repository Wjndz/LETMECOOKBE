package com.example.letmecookbe.service;

import com.example.letmecookbe.dto.request.RecipeStepsCreationRequest;
import com.example.letmecookbe.dto.request.RecipeStepsUpdateRequest;
import com.example.letmecookbe.dto.response.RecipeStepsResponse;
import com.example.letmecookbe.entity.Recipe;
import com.example.letmecookbe.entity.RecipeSteps;
import com.example.letmecookbe.exception.AppException;
import com.example.letmecookbe.exception.ErrorCode;
import com.example.letmecookbe.mapper.RecipeStepsMapper;
import com.example.letmecookbe.repository.RecipeRepository;
import com.example.letmecookbe.repository.RecipeStepsRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RecipeStepsService {
    RecipeStepsRepository recipeStepsRepository;
    RecipeStepsMapper recipeStepsMapper;
    RecipeRepository recipeRepository;

    @PreAuthorize("hasAuthority('CREATE_RECIPE_STEPS')")
    public RecipeStepsResponse createRecipeSteps(String id,RecipeStepsCreationRequest request){
        Recipe recipe = recipeRepository.findById(id).orElseThrow(
                () -> new AppException(ErrorCode.RECIPE_NOT_FOUND)
        );
        if(recipeStepsRepository.existsByRecipeIdAndStep(id,request.getStep())){
            throw new AppException(ErrorCode.RECIPE_STEPS_EXISTED);
        }
        RecipeSteps recipeSteps = recipeStepsMapper.toRecipeSteps(request);
        recipeSteps.setRecipe(recipe);
        RecipeSteps savedRecipeSteps = recipeStepsRepository.save(recipeSteps);
        return recipeStepsMapper.toRecipeStepsResponse(savedRecipeSteps);
    }

    @PreAuthorize("hasAuthority('UPDATE_RECIPE_STEPS')")
    public RecipeStepsResponse updateRecipeSteps(String RecipeId,int stepNum, RecipeStepsUpdateRequest request){
        if(!recipeRepository.existsById(RecipeId)){
            throw new AppException(ErrorCode.RECIPE_NOT_FOUND);
        }

        RecipeSteps recipeSteps = recipeStepsRepository.findRecipeStepsByRecipeIdAndStep(RecipeId,stepNum);
        if(recipeSteps == null){
            throw new AppException(ErrorCode.RECIPE_STEPS_NOT_EXISTED);
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
        if(!request.getRecipeStepsImg().isBlank()){
            recipeSteps.setRecipeStepsImg(request.getRecipeStepsImg());
        }
        RecipeSteps savedRecipeSteps = recipeStepsRepository.save(recipeSteps);
        return recipeStepsMapper.toRecipeStepsResponse(savedRecipeSteps);
    }

    @PreAuthorize("hasAuthority('GET_RECIPE_STEPS_BY_RECIPE_ID')")
    public List<RecipeStepsResponse> getRecipeStepsByRecipeId(String RecipeId){
        if(!recipeRepository.existsById(RecipeId)){
            throw new AppException(ErrorCode.RECIPE_NOT_FOUND);
        }
        List<RecipeSteps> recipeSteps = recipeStepsRepository.findRecipeStepsByRecipeId(RecipeId);
        return  recipeSteps.stream()
                .map(recipeStepsMapper::toRecipeStepsResponse)
                .collect(Collectors.toList());
    }


    @PreAuthorize("hasAuthority('DELETE_RECIPE_STEPS_BY_RECIPE_ID')")
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
