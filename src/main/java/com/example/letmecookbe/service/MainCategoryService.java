package com.example.letmecookbe.service;

import com.example.letmecookbe.dto.request.MainCategoryRequest;
import com.example.letmecookbe.dto.response.MainCategoryResponse;
import com.example.letmecookbe.entity.MainCategory;
import com.example.letmecookbe.exception.AppException;
import com.example.letmecookbe.exception.ErrorCode;
import com.example.letmecookbe.mapper.MainCategoryMapper;
import com.example.letmecookbe.repository.MainCategoryRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MainCategoryService {
    MainCategoryRepository repository;
    @Qualifier("mainCategoryMapperImpl")
    MainCategoryMapper mapper;

    public MainCategoryResponse createMainCategory(MainCategoryRequest mainCategory) {
        if (repository.existsByCategoryName(mainCategory.getCategoryName())) {
            throw new AppException(ErrorCode.CATEGORY_EXISTED);
        }
        MainCategory main = mapper.toMainCategory(mainCategory);
        MainCategory savedMain = repository.save(main);
        return mapper.toMainCategoryResponse(savedMain);
    }

    public MainCategoryResponse updateCategoryName(String id, MainCategoryRequest mainCategory) {
        MainCategory main = repository.findById(id).orElseThrow(
                ()-> new AppException(ErrorCode.MAIN_CATEGORY_NOT_EXIST));
        main.setCategoryName(mainCategory.getCategoryName());
        MainCategory savedMain = repository.save(main);
        return mapper.toMainCategoryResponse(savedMain);
    }

    public List<MainCategory> getAllMainCategory(){
        if(repository.findAll().isEmpty())
            throw new AppException(ErrorCode.LIST_EMPTY);
        return repository.findAll();
    }
}
