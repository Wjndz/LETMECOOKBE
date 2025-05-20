package com.example.letmecookbe.mapper;

import com.example.letmecookbe.dto.request.SubCategoryRequest;
import com.example.letmecookbe.dto.response.SubCategoryResponse;
import com.example.letmecookbe.entity.SubCategory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SubCategoryMapper {
    SubCategory toSubCategory(SubCategoryRequest request);

    @Mapping(source = "mainCategory.id", target = "categoryId")
    SubCategoryResponse toSubCategoryResponse(SubCategory subCategory);
}
