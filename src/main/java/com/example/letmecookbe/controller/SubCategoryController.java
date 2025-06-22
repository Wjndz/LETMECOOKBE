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
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.awt.*;
import java.util.List;

@RestController
@RequestMapping("/subCategory")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SubCategoryController {
    SubCategoryService service;

    @PostMapping(value = "/create/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<SubCategoryResponse> createSubCategory(
            @PathVariable String id,
            @RequestPart("subCategoryName") String subCategoryName,
            @RequestPart("file") MultipartFile file) {

        SubCategoryCreationRequest request = SubCategoryCreationRequest.builder()
                .subCategoryName(subCategoryName)
                .build();

        ApiResponse<SubCategoryResponse> response = new ApiResponse<>();
        response.setMessage("create sub category: " + subCategoryName);
        response.setResult(service.createSubCategory(id, request, file));
        return response;
    }


    @PutMapping(value = "/update/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<SubCategoryResponse> updateSubCategoryName(
            @PathVariable String id,
            @RequestParam("subCategoryName") String subCategoryName,
            @RequestParam("categoryId") String categoryId,
            @RequestPart(value = "file", required = false) MultipartFile file) {

        SubCategoryUpdateRequest request = SubCategoryUpdateRequest.builder()
                .subCategoryName(subCategoryName)
                .categoryId(categoryId)
                .build();

        ApiResponse<SubCategoryResponse> response = new ApiResponse<>();
        response.setMessage("update sub category: " + request.getSubCategoryName());
        response.setResult(service.updateSubCategoryName(id, request, file));
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

    @GetMapping("/countAllSubCategory")
    public ApiResponse<Integer> countAllSubCategory(){
        ApiResponse<Integer> response = new ApiResponse<>();
        response.setMessage("get all sub category");
        response.setResult(service.countAllSubCategories());
        return response;
    }

    @GetMapping("/getAllSubCategory")
    public ApiResponse<List<SubCategoryResponse>> getAllSubCategory(){
        ApiResponse<List<SubCategoryResponse>> response = new ApiResponse<>();
        response.setMessage("Get all Sub Categories: ");
        response.setResult(service.getAllSubCategory());
        return response;
    }
}
