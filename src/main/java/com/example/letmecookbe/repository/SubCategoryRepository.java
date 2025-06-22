package com.example.letmecookbe.repository;

import com.example.letmecookbe.dto.response.SubCategoryResponse;
import com.example.letmecookbe.entity.SubCategory;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SubCategoryRepository extends JpaRepository<SubCategory, String> {

    boolean existsBySubCategoryName(String subCategoryName);

    List<SubCategory> findAllByMainCategoryId(String id);

    @Query("SELECT COUNT(c) FROM SubCategory c")
    int countAllSubCategories();

    @Modifying
    @Query("DELETE FROM SubCategory sc WHERE sc.mainCategory.id = :mainCategoryId")
    void deleteByMainCategoryId(String mainCategoryId);

    @Query("SELECT sc FROM SubCategory sc " +
            "LEFT JOIN Recipe r ON r.subCategory.id = sc.id " +
            "GROUP BY sc.id, sc.subCategoryName " +
            "ORDER BY COUNT(r) DESC")
    List<SubCategory> findTop6SubCategoriesByRecipeCount(Pageable pageable);


}
