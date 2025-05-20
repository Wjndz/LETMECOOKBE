package com.example.letmecookbe.controller;

import com.example.letmecookbe.dto.request.SubCategoryRequest;
import com.example.letmecookbe.dto.request.SubCategoryUpdateRequest;
import com.example.letmecookbe.dto.response.ApiResponse;
import com.example.letmecookbe.dto.response.SubCategoryResponse;
import com.example.letmecookbe.entity.MainCategory;
import com.example.letmecookbe.service.SubCategoryService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/sub-category")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SubCategoryController {
    SubCategoryService service;
    @PostMapping("/create")
    ApiResponse<SubCategoryResponse> createSubCategory(@RequestBody @Valid SubCategoryRequest request){
        ApiResponse<SubCategoryResponse> response = new ApiResponse<>();
        response.setMessage("create sub category: "+ request.getSubCategoryName());
        response.setResult(service.createSubCategory(request));
        return response;
    }

    @PutMapping("/update/{id}")
    ApiResponse<SubCategoryResponse> updateSubCategoryName(@PathVariable String id, @RequestBody @Valid SubCategoryUpdateRequest request){
        ApiResponse<SubCategoryResponse> response = new ApiResponse<>();
        response.setMessage("update sub category: "+ request.getSubCategoryName());
        response.setResult(service.updateSubCategoryName(id, request));
        return response;
    }
}
