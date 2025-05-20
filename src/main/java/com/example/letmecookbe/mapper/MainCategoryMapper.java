package com.example.letmecookbe.mapper;

import com.example.letmecookbe.dto.request.MainCategoryRequest;
import com.example.letmecookbe.dto.response.MainCategoryResponse;
import com.example.letmecookbe.entity.MainCategory;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MainCategoryMapper {
    MainCategory toMainCategory(MainCategoryRequest request);
    MainCategoryResponse toMainCategoryResponse(MainCategory mainCategory);
}
