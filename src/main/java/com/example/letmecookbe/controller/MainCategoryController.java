package com.example.letmecookbe.controller;

import com.example.letmecookbe.dto.request.MainCategoryCreationRequest;
import com.example.letmecookbe.dto.response.ApiResponse;
import com.example.letmecookbe.dto.response.MainCategoryResponse;
import com.example.letmecookbe.entity.MainCategory;
import com.example.letmecookbe.service.MainCategoryService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/mainCategory")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MainCategoryController {
    MainCategoryService service;
    @PostMapping("/create")
    public ApiResponse<MainCategoryResponse> createMainCategory(@RequestBody @Valid MainCategoryCreationRequest request){
        ApiResponse<MainCategoryResponse> response = new ApiResponse<>();
        response.setMessage("Create Main Category: "+ request.getCategoryName());
        response.setResult(service.createMainCategory(request));
        return response;
    }

    @PutMapping("/update/{id}")
    public ApiResponse<MainCategoryResponse> updateCategoryName(@PathVariable String id, @RequestBody @Valid MainCategoryCreationRequest request){
        ApiResponse<MainCategoryResponse> response = new ApiResponse<>();
        response.setMessage("Update Main Category: "+ request.getCategoryName());
        response.setResult(service.updateCategoryName(id, request));
        return response;
    }

    @GetMapping("/getAll")
    public ApiResponse<List<MainCategory>> getAll(){
        ApiResponse<List<MainCategory>> response = new ApiResponse<>();
        response.setMessage("Get all Main Categories: ");
        response.setResult(service.getAllMainCategory());
        return response;
    }

    @DeleteMapping("/delete/{id}")
    public ApiResponse<String> deleteMainCategory(@PathVariable String id){
        ApiResponse<String> response = new ApiResponse<>();
        response.setResult(service.deleteMainCategory(id));
        return response;
    }
}
