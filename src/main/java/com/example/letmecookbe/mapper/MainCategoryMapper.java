package com.example.letmecookbe.mapper;

import com.example.letmecookbe.dto.request.MainCategoryCreationRequest;
import com.example.letmecookbe.dto.response.MainCategoryResponse;
import com.example.letmecookbe.entity.MainCategory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MainCategoryMapper {
    MainCategory toMainCategory(MainCategoryCreationRequest request);

    MainCategoryResponse toMainCategoryResponse(MainCategory mainCategory);
}
