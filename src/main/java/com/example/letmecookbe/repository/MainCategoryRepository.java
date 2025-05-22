package com.example.letmecookbe.repository;

import com.example.letmecookbe.entity.MainCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MainCategoryRepository extends JpaRepository<MainCategory, String> {
    boolean existsByCategoryName(String categoryName);
}
