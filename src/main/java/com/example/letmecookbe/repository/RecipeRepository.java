package com.example.letmecookbe.repository;

import com.example.letmecookbe.entity.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, String> {
    @Query("select c from Recipe c where c.subCategory.id = :subCategoryId")
    List<Recipe> findRecipeBySubCategoryId(String subCategoryId);

    @Query("select c from Recipe c where c.title LIKE %:KeyWord% or c.subCategory.subCategoryName LIKE %:KeyWord%")
    List<Recipe> findRecipeByKeyword(String KeyWord);

    @Query("select c from Recipe c where c.account.id = :accountId")
    List<Recipe> findRecipeByAccountId(String accountId);

    @Query("SELECT c FROM Recipe c  WHERE c.status = 'APPROVED' ORDER BY c.totalLikes DESC  LIMIT 5")
    List<Recipe> findTop5RecipesByTotalLikes();

    @Query("SELECT c FROM Recipe c " +
            "WHERE c.subCategory.id = :subCategoryId AND c.status ='APPRROVED' " +
            "AND DATE(c.createAt) = CURRENT_DATE " +
            "ORDER BY c.totalLikes DESC")
    List<Recipe> findRecipesBySubCategoryIdCreatedTodayOrderByLikes(String subCategoryId);

    @Query("SELECT COUNT(c) FROM Recipe c WHERE c.account.id = :accountId")
    int countRecipesByAccountId(String accountId);
}
