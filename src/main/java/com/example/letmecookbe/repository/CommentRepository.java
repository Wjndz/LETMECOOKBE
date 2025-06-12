package com.example.letmecookbe.repository;

import com.example.letmecookbe.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List; // Import này là cần thiết
@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer> { // <-- Sửa từ String thành Integer
    // Phương thức này tìm kiếm Comment theo ID của Recipe
    // Recipe ID là String (như trong Recipe.java bạn đã cung cấp)
    List<Comment> findByRecipe_Id(String recipeId); // <-- Đảm bảo tên phương thức và kiểu tham số đúng
}