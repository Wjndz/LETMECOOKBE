package com.example.letmecookbe.service;

import com.example.letmecookbe.dto.request.MainCategoryCreationRequest;
import com.example.letmecookbe.dto.response.MainCategoryResponse;
import com.example.letmecookbe.entity.MainCategory;
import com.example.letmecookbe.exception.AppException;
import com.example.letmecookbe.exception.ErrorCode;
import com.example.letmecookbe.mapper.MainCategoryMapper;
import com.example.letmecookbe.repository.MainCategoryRepository;
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
    MainCategoryMapper mainCategoryMapper;

    public MainCategoryResponse createMainCategory(MainCategoryCreationRequest mainCategory) {
        if (repository.existsByCategoryName(mainCategory.getCategoryName())) {
            throw new AppException(ErrorCode.CATEGORY_EXISTED);
        }
        MainCategory main = mainCategoryMapper.toMainCategory(mainCategory);
        MainCategory savedMain = repository.save(main);
        return mainCategoryMapper.toMainCategoryResponse(savedMain);
    }

    public MainCategoryResponse updateCategoryName(String id, MainCategoryCreationRequest mainCategory) {
        MainCategory main = repository.findById(id).orElseThrow(
                ()-> new AppException(ErrorCode.MAIN_CATEGORY_NOT_EXIST));
        main.setCategoryName(mainCategory.getCategoryName());
        MainCategory savedMain = repository.save(main);
        return mainCategoryMapper.toMainCategoryResponse(savedMain);
    }

    public List<MainCategory> getAllMainCategory(){
        if(repository.findAll().isEmpty())
            throw new AppException(ErrorCode.LIST_EMPTY);
        return repository.findAll();
    }
}
