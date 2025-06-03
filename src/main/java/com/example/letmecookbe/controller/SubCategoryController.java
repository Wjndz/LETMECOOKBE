package com.example.letmecookbe.controller;

import com.example.letmecookbe.dto.request.SubCategoryCreationRequest;
import com.example.letmecookbe.dto.request.SubCategoryUpdateRequest;
import com.example.letmecookbe.dto.response.ApiResponse;
import com.example.letmecookbe.dto.response.SubCategoryResponse;
import com.example.letmecookbe.entity.SubCategory;
import com.example.letmecookbe.service.SubCategoryService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/subCategory")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SubCategoryController {
    SubCategoryService service;
    @PostMapping("/create/{id}")
    public ApiResponse<SubCategoryResponse> createSubCategory(@PathVariable String id, @RequestBody @Valid SubCategoryCreationRequest request){
        ApiResponse<SubCategoryResponse> response = new ApiResponse<>();
        response.setMessage("create sub category: "+ request.getSubCategoryName());
        response.setResult(service.createSubCategory(id, request));
        return response;
    }

    @PutMapping("/update/{id}")
    public ApiResponse<SubCategoryResponse> updateSubCategoryName(@PathVariable String id, @RequestBody @Valid SubCategoryUpdateRequest request){
        ApiResponse<SubCategoryResponse> response = new ApiResponse<>();
        response.setMessage("update sub category: "+ request.getSubCategoryName());
        response.setResult(service.updateSubCategoryName(id, request));
        return response;
    }

    @GetMapping("/getAllSubByMain/{id}")
    public ApiResponse<List<SubCategory>> getAllSubByMain(@PathVariable String id){
        ApiResponse<List<SubCategory>> response = new ApiResponse<>();
        response.setMessage("get all sub category by main category id: "+ id);
        response.setResult(service.getSubCategoryByManCategoryId(id));
        return response;
    }

    @DeleteMapping("/deleteSubCategory/{id}")
    public ApiResponse<String> deleteSubCategory(@PathVariable String id){
        ApiResponse<String> response = new ApiResponse<>();
        response.setResult(service.deleteSubCategory(id));
        return response;
    }
}
