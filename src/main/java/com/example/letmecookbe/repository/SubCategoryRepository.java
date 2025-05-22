package com.example.letmecookbe.repository;

import com.example.letmecookbe.dto.response.SubCategoryResponse;
import com.example.letmecookbe.entity.SubCategory;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubCategoryRepository extends JpaRepository<SubCategory, String> {

    boolean existsBySubCategoryName(String subCategoryName);

    List<SubCategory> findAllByMainCategoryId(String id);
}
