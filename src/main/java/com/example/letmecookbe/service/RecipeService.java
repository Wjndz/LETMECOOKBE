package com.example.letmecookbe.service;

import com.example.letmecookbe.dto.request.RecipeCreationRequest;
import com.example.letmecookbe.dto.request.RecipeUpdateRequest;
import com.example.letmecookbe.dto.response.RecipeResponse;
import com.example.letmecookbe.entity.Account;
import com.example.letmecookbe.entity.Recipe;
import com.example.letmecookbe.entity.SubCategory;
import com.example.letmecookbe.enums.RecipeStatus;
import com.example.letmecookbe.exception.AppException;
import com.example.letmecookbe.exception.ErrorCode;
import com.example.letmecookbe.mapper.RecipeMapper;
import com.example.letmecookbe.repository.AccountRepository;
import com.example.letmecookbe.repository.RecipeRepository;
import com.example.letmecookbe.repository.SubCategoryRepository;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RecipeService {
    RecipeRepository RecipeRepository;
    RecipeMapper recipeMapper;
    SubCategoryRepository subCategoryRepository;
    AccountRepository accountRepository;
    RecipeDeletionService recipeDeletionService;
    private final FileStorageService fileStorageService;

    private String getAccountIdFromContext() {
        var context = SecurityContextHolder.getContext();
        String email = context.getAuthentication().getName();
        Account account = accountRepository.findAccountByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_FOUND));
        return account.getId();
    }

    @PreAuthorize("hasAuthority('CREATE_RECIPE')")
    public RecipeResponse createRecipe(String subCategoryId, RecipeCreationRequest request, MultipartFile file){
        SubCategory subCategory = subCategoryRepository.findById(subCategoryId).orElseThrow(
                () -> new AppException(ErrorCode.SUB_CATEGORY_NOT_EXIST)
        );
        Account account = accountRepository.findById(getAccountIdFromContext()).orElseThrow(
                ()->new RuntimeException("Account not found")
        );
        Recipe recipe = recipeMapper.toRecipe(request);
        String recipeImg = fileStorageService.uploadFile(file);
        recipe.setImg(recipeImg);
        recipe.setSubCategory(subCategory);
        recipe.setAccount(account);
        recipe.setStatus(String.valueOf(RecipeStatus.PENDING));
        Recipe savedRecipe = RecipeRepository.save(recipe);
        return recipeMapper.toRecipeResponse(savedRecipe);
    }

    @PreAuthorize("hasAuthority('UPDATE_RECIPE')")
    public RecipeResponse updateRecipe(String id, RecipeUpdateRequest updateRequest, MultipartFile file){
        Recipe recipe = RecipeRepository.findById(id).orElseThrow(
                () -> new AppException(ErrorCode.RECIPE_NOT_FOUND)
        );


        if(!updateRequest.getTitle().isBlank())
            recipe.setTitle(updateRequest.getTitle());

        if(!updateRequest.getDescription().isBlank())
            recipe.setDescription(updateRequest.getDescription());

        if(!updateRequest.getDifficulty().isBlank()) // Sửa lại theo enum
            recipe.setDifficulty(updateRequest.getDifficulty());

        if(!updateRequest.getCookingTime().isBlank())
            recipe.setCookingTime(updateRequest.getCookingTime());

        if( file!= null && !file.isEmpty() ){
            String recipeImg = fileStorageService.uploadFile(file);
            recipe.setImg(recipeImg);
        }

        if(!updateRequest.getSubCategoryId().isBlank()){
            SubCategory sub = subCategoryRepository.findById(updateRequest.getSubCategoryId()).orElseThrow(
                    ()-> new AppException(ErrorCode.SUB_CATEGORY_NOT_EXIST)
            );
            recipe.setSubCategory(sub);
        }

        Recipe savedRecipe = RecipeRepository.save(recipe);
        return recipeMapper.toRecipeResponse(savedRecipe);
    }


    @PreAuthorize("hasAuthority('GET_ALL_RECIPE')")
    public Page<RecipeResponse> getAllRecipe(Pageable pageable) {
        Page<Recipe> recipePage = RecipeRepository.findAll(pageable);
        if (recipePage.isEmpty()) {
            throw new AppException(ErrorCode.LIST_EMPTY);
        }
        return recipePage.map(recipeMapper::toRecipeResponse);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    public List<RecipeResponse> getRecipeByAccountId(){
        List<Recipe> accountRecipes = RecipeRepository.findRecipeByAccountId(getAccountIdFromContext());
        if (accountRecipes.isEmpty()) {
            throw new AppException(ErrorCode.LIST_EMPTY);
        }
        return accountRecipes.stream()
                .map(recipeMapper::toRecipeResponse)
                .collect(Collectors.toList());
    }

    @PreAuthorize("hasAuthority('GET_RECIPE_BY_SUB_CATEGORY')")
    public Page<RecipeResponse> getRecipeBySubCategoryId(String id, Pageable pageable){
        if(!subCategoryRepository.existsById(id)){
            throw new AppException(ErrorCode.SUB_CATEGORY_NOT_EXIST);
        }

        Page<Recipe> recipePage = RecipeRepository.findRecipeBySubCategoryIdWithPagination(id, pageable);

        if (recipePage.isEmpty()) {
            throw new AppException(ErrorCode.LIST_EMPTY);
        }

        return recipePage
                .map(recipe -> "APPROVED".equalsIgnoreCase(recipe.getStatus()) ? recipe : null)
                .map(recipe -> recipe != null ? recipeMapper.toRecipeResponse(recipe) : null);
    }



    public List<RecipeResponse> findRecipeByKeyword(String keyword){
        List<Recipe> recipes = RecipeRepository.findRecipeByKeyword(keyword);
        return  recipes.stream()
                .map(recipeMapper::toRecipeResponse)
                .collect(Collectors.toList());
    }

    @PreAuthorize("hasAuthority('DELETE_RECIPE')")
    @Transactional
    public String deleteRecipe(String id){
        Recipe recipe = RecipeRepository.findById(id).orElseThrow(
                () -> new AppException(ErrorCode.RECIPE_NOT_FOUND)
        );
        try {
            // Truyền 1 recipe vào list
            recipeDeletionService.deleteRecipesAndRelatedData(List.of(recipe));

            return "Xóa Recipe và tất cả dữ liệu liên quan thành công" ;

        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi xóa Recipe: " + e.getMessage(), e);
        }

    }

    @PreAuthorize("hasAuthority('LIKE')")
    public RecipeResponse Like(String id) {
        Recipe recipe = RecipeRepository.findById(id).orElseThrow(
                () -> new AppException(ErrorCode.RECIPE_NOT_FOUND)
        );

        recipe.setTotalLikes(recipe.getTotalLikes() + 1);
        Recipe updatedRecipe = RecipeRepository.save(recipe);
        return recipeMapper.toRecipeResponse(updatedRecipe);
    }

//    public RecipeResponse disLike(String id) {
//        Recipe recipe = RecipeRepository.findById(id).orElseThrow(
//                () -> new AppException(ErrorCode.RECIPE_NOT_FOUND)
//        );
//        recipe.setTotalLikes(recipe.getTotalLikes() - 1);
//        Recipe updatedRecipe = RecipeRepository.save(recipe);
//        return recipeMapper.toRecipeResponse(updatedRecipe);
//    }

    @PreAuthorize("hasRole('ADMIN')")
    public RecipeResponse changeStatusToApprove(String id){
        Recipe recipe = RecipeRepository.findById(id).orElseThrow(
                () -> new AppException(ErrorCode.RECIPE_NOT_FOUND)
        );
        recipe.setStatus(String.valueOf(RecipeStatus.APPROVED));
        Recipe updatedRecipe = RecipeRepository.save(recipe);
        return recipeMapper.toRecipeResponse(updatedRecipe);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public RecipeResponse changeStatusToNotApproved(String id){
        Recipe recipe = RecipeRepository.findById(id).orElseThrow(
                () -> new AppException(ErrorCode.RECIPE_NOT_FOUND)
        );
        recipe.setStatus(String.valueOf(RecipeStatus.NOT_APPROVED));
        Recipe updatedRecipe = RecipeRepository.save(recipe);
        return recipeMapper.toRecipeResponse(updatedRecipe);
    }

    @PreAuthorize("hasAnyAuthority('TOP_5_RECIPE')")
    public List<RecipeResponse> getTop5RecipesByTotalLikes(){
        List<Recipe> recipeList = RecipeRepository.findTop5RecipesByTotalLikes();
        return recipeList.stream()
                .map(recipeMapper::toRecipeResponse)
                .collect(Collectors.toList());
    }

    @PreAuthorize("hasAnyAuthority(('GET_RECIPE_BY_SUB_TODAY'))")
    public List<RecipeResponse> GetRecipesBySubCategoryIdTodayOrderByLikes(String subCategoryId){
        List<Recipe> recipeList = RecipeRepository.findRecipesBySubCategoryIdTodayOrderByLikes(subCategoryId);
        return recipeList.stream()
                .map(recipeMapper::toRecipeResponse)
                .collect(Collectors.toList());
    }

    public int countRecipeBySubCategoryId(){
        int count= RecipeRepository.countRecipesByAccountId(getAccountIdFromContext());
        return count;
    }

    @PreAuthorize("hasAnyAuthority('COUNT_REICPE_BY_ACCOUNT')")
    public int countRecipeByUserId(String accountId) {
        if (!accountRepository.existsById(accountId)) {
            throw new AppException(ErrorCode.ACCOUNT_NOT_FOUND);
        }
        int count = RecipeRepository.countRecipesByAccountId(accountId);
        if (count < 0) {
            throw new AppException(ErrorCode.LIST_EMPTY);
        }
        return count;
    }

    @PreAuthorize("hasAnyAuthority('COUNT_REICPE_BY_SUB_CATEGORY')")
    public int countRecipeBySubCategoryId(String subCategoryId){
        int count= RecipeRepository.countRecipesBySubCategoryId(subCategoryId);
        if (count < 0) {
            throw new AppException(ErrorCode.LIST_EMPTY);
        }
        return count;
    }

    @PreAuthorize("hasAnyAuthority('COUNT_APPROVED_REICPE')")
    public int countApprovedRecipes(){
        int count= RecipeRepository.countApprovedRecipes();
        if (count < 0) {
            throw new AppException(ErrorCode.LIST_EMPTY);
        }
        return count;
    }


    @PreAuthorize("hasAnyAuthority('COUNT_REICPE_BY_MAIN_CATEGORY')")
    public int countRecipesByMainCategory(String mainCategoryId){
        int count= RecipeRepository.countRecipesByMainCategoryId(mainCategoryId);
        if (count < 0) {
            throw new AppException(ErrorCode.LIST_EMPTY);
        }
        return count;
    }
}
