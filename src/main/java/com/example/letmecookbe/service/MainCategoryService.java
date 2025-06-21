package com.example.letmecookbe.service;

import com.example.letmecookbe.dto.request.MainCategoryCreationRequest;
import com.example.letmecookbe.dto.response.MainCategoryResponse;
import com.example.letmecookbe.entity.*;
import com.example.letmecookbe.exception.AppException;
import com.example.letmecookbe.exception.ErrorCode;
import com.example.letmecookbe.mapper.MainCategoryMapper;
import com.example.letmecookbe.repository.*;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MainCategoryService {
    MainCategoryRepository repository;
    MainCategoryMapper mainCategoryMapper;
    RecipeRepository recipeRepository;
    SubCategoryRepository subCategoryRepository;
    RecipeDeletionService recipeDeletionService;
    private final FileStorageService fileStorageService;


    @PreAuthorize("hasRole('ADMIN')")
    public MainCategoryResponse createMainCategory(MainCategoryCreationRequest mainCategory, MultipartFile file) {
        if (repository.existsByCategoryName(mainCategory.getCategoryName())) {
            throw new AppException(ErrorCode.CATEGORY_EXISTED);
        }
        String categoryImg = fileStorageService.uploadFile(file);
        MainCategory main = mainCategoryMapper.toMainCategory(mainCategory);
        main.setCategoryImg(categoryImg);
        MainCategory savedMain = repository.save(main);
        return mainCategoryMapper.toMainCategoryResponse(savedMain);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public MainCategoryResponse updateCategoryName(String id, MainCategoryCreationRequest mainCategory, MultipartFile file) {
        MainCategory main = repository.findById(id).orElseThrow(
                ()-> new AppException(ErrorCode.MAIN_CATEGORY_NOT_EXIST));
        if(!mainCategory.getCategoryName().isBlank()){
            main.setCategoryName(mainCategory.getCategoryName());
        }
        if (file != null && !file.isEmpty()) {
            String categoryImg = fileStorageService.uploadFile(file);
            main.setCategoryImg(categoryImg);
        }
        MainCategory savedMain = repository.save(main);
        return mainCategoryMapper.toMainCategoryResponse(savedMain);
    }

    @PreAuthorize("hasAuthority('GET_ALL_MAIN_CATEGORY')")
    public List<MainCategory> getAllMainCategory(){
        if(repository.findAll().isEmpty())
            throw new AppException(ErrorCode.LIST_EMPTY);
        return repository.findAll();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public String deleteMainCategory(String id) {
        MainCategory mainCategory = repository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.MAIN_CATEGORY_NOT_EXIST));

        try {
            // 1. Lấy tất cả SubCategory thuộc MainCategory này
           List<SubCategory> subCategories = subCategoryRepository.findAllByMainCategoryId(id);

            // 2. Với mỗi SubCategory, xử lý tất cả Recipe của nó
            for (SubCategory subCategory : subCategories) {
                List<Recipe> recipes = recipeRepository.findRecipeBySubCategoryId(subCategory.getId());

                // 3. Với mỗi Recipe, xóa các dữ liệu liên quan theo thứ tự
                recipeDeletionService.deleteRecipesAndRelatedData(recipes);
            }

            // 5. Xóa tất cả SubCategory (vì SubCategory.category_id → MainCategory.id)
            if (!subCategories.isEmpty()) {
                subCategoryRepository.deleteAll(subCategories);
            }
            // 6. Cuối cùng xóa MainCategory
            repository.deleteById(id);

            return "Xóa MainCategory và tất cả dữ liệu liên quan thành công: " + mainCategory.getCategoryName();

        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi xóa MainCategory: " + e.getMessage(), e);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    public int countAllMainCategories(){
        int count= repository.countAllMainCategories();
        if (count < 0) {
            throw new AppException(ErrorCode.LIST_EMPTY);
        }
        return count;
    }

}
