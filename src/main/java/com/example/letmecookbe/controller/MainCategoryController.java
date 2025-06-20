package com.example.letmecookbe.controller;

import com.example.letmecookbe.dto.request.MainCategoryCreationRequest;
import com.example.letmecookbe.dto.response.ApiResponse;
import com.example.letmecookbe.dto.response.MainCategoryResponse;
import com.example.letmecookbe.entity.MainCategory;
import com.example.letmecookbe.service.MainCategoryService;
import jakarta.validation.Valid;
import jakarta.websocket.server.PathParam;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/mainCategory")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MainCategoryController {
    MainCategoryService service;
    @PostMapping(value="/create",consumes =  MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<MainCategoryResponse> createMainCategory(
            @PathParam("categoryName") String categoryName,
            @RequestPart(value = "file") MultipartFile file){
        MainCategoryCreationRequest request = MainCategoryCreationRequest.builder()
                .categoryName(categoryName)
                .build();
        ApiResponse<MainCategoryResponse> response = new ApiResponse<>();
        response.setMessage("Create Main Category: "+ request.getCategoryName());
        response.setResult(service.createMainCategory(request,file));
        return response;
    }

    @PutMapping("/update/{id}")
    public ApiResponse<MainCategoryResponse> updateCategoryName(
            @PathVariable String id,
            @PathParam("categoryName") String categoryName,
            @RequestPart(value = "file",required = false) MultipartFile file){
        MainCategoryCreationRequest request = MainCategoryCreationRequest.builder()
                .categoryName(categoryName)
                .build();
        ApiResponse<MainCategoryResponse> response = new ApiResponse<>();
        response.setMessage("Update Main Category: "+ request.getCategoryName());
        response.setResult(service.updateCategoryName(id, request,file));
        return response;
    }

    @GetMapping("/getAll")
    public ApiResponse<List<MainCategory>> getAll(){
        ApiResponse<List<MainCategory>> response = new ApiResponse<>();
        response.setMessage("Get all Main Categories: ");
        response.setResult(service.getAllMainCategory());
        return response;
    }

//    @DeleteMapping("/delete/{id}")
//    public ApiResponse<String> deleteMainCategory(@PathVariable String id){
//        ApiResponse<String> response = new ApiResponse<>();
//        response.setResult(service.deleteMainCategory(id));
//        return response;
//    }
}
