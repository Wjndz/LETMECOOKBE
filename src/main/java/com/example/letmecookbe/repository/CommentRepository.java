package com.example.letmecookbe.repository;

import com.example.letmecookbe.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List; // Import này là cần thiết
@Repository
public interface CommentRepository extends JpaRepository<Comment, String> { // <-- Sửa từ String thành Integer
    // Phương thức này tìm kiếm Comment theo ID của Recipe
    // Recipe ID là String (như trong Recipe.java bạn đã cung cấp)
    Page<Comment> findByRecipe_Id(String recipeId, Pageable pageable); // <-- Đảm bảo tên phương thức và kiểu tham số đúng

    @Modifying
    @Query("DELETE FROM Comment c WHERE c.recipe.id = :recipeId")
    void deleteByRecipeId(String recipeId);

}