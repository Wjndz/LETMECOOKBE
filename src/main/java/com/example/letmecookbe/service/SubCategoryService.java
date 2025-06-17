package com.example.letmecookbe.service;

import com.example.letmecookbe.dto.request.SubCategoryCreationRequest;
import com.example.letmecookbe.dto.request.SubCategoryUpdateRequest;
import com.example.letmecookbe.dto.response.SubCategoryResponse;
import com.example.letmecookbe.entity.*;
import com.example.letmecookbe.exception.AppException;
import com.example.letmecookbe.exception.ErrorCode;
import com.example.letmecookbe.mapper.SubCategoryMapper;
import com.example.letmecookbe.repository.*;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SubCategoryService {
    MainCategoryRepository MainRepository;
    SubCategoryRepository SubRepository;
    SubCategoryMapper subCategoryMapper;
    RecipeRepository recipeRepository;
    RecipeDeletionService recipeDeletionService;
    private final FileStorageService fileStorageService;

    @PreAuthorize("hasRole('ADMIN')")
    public SubCategoryResponse createSubCategory(String id,SubCategoryCreationRequest request, MultipartFile image )  {
        MainCategory main = MainRepository.findById(id).orElseThrow(
                ()-> new AppException(ErrorCode.MAIN_CATEGORY_NOT_EXIST)
        );
        if(SubRepository.existsBySubCategoryName(request.getSubCategoryName())){
            throw new AppException(ErrorCode.SUB_CATEGORY_EXISTED);
        }
        String subImageUrl = fileStorageService.uploadFile(image);
        SubCategory sub = subCategoryMapper.toSubCategory(request);
        sub.setMainCategory(main);
        sub.setSubCategoryImg(subImageUrl);
        SubCategory savedSub = SubRepository.save(sub);
        return subCategoryMapper.toSubCategoryResponse(savedSub);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public SubCategoryResponse updateSubCategoryName(String id, SubCategoryUpdateRequest request, MultipartFile image )  {
        SubCategory sub = SubRepository.findById(id).orElseThrow(
                ()-> new AppException(ErrorCode.SUB_CATEGORY_NOT_EXIST)
        );
        if(!request.getSubCategoryName().isBlank()){
            if(SubRepository.existsBySubCategoryName(request.getSubCategoryName())){
                throw new AppException(ErrorCode.SUB_CATEGORY_EXISTED);
            }
            sub.setSubCategoryName(request.getSubCategoryName());
        }
        if (image != null && !image.isEmpty()) {
            String subImageUrl = fileStorageService.uploadFile(image);
            sub.setSubCategoryImg(subImageUrl);
        }
        if(!request.getCategoryId().isBlank())
            sub.setMainCategory(MainRepository.findById(request.getCategoryId()).orElseThrow(
                    ()-> new AppException(ErrorCode.MAIN_CATEGORY_NOT_EXIST)
            ));
        SubCategory savedSub = SubRepository.save(sub);
        return subCategoryMapper.toSubCategoryResponse(savedSub);
    }

    @PreAuthorize("hasAuthority('GET_SUB_CATEGORY_BY_MAIN_CATEGORY')")
    public List<SubCategory> getSubCategoryByManCategoryId(String id){
        if(!MainRepository.existsById(id)){
            throw new AppException(ErrorCode.MAIN_CATEGORY_NOT_EXIST);
        }
        return SubRepository.findAllByMainCategoryId(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public String deleteSubCategory(String id) {
        SubCategory subCategory = SubRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.SUB_CATEGORY_NOT_EXIST));

        try {
            List<Recipe> recipes = recipeRepository.findRecipeBySubCategoryId(id);

            recipeDeletionService.deleteRecipesAndRelatedData(recipes);

            SubRepository.deleteById(id);

            return "Xóa SubCategory và tất cả dữ liệu liên quan thành công: " + subCategory.getSubCategoryName();

        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi xóa SubCategory: " + e.getMessage(), e);
        }
    }

}
