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
}
