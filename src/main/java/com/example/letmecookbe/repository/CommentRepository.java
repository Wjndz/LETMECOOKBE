package com.example.letmecookbe.repository;
import com.example.letmecookbe.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
@Repository
public interface CommentRepository extends JpaRepository<Comment, String> {
    // Tìm tất cả Comment theo ID của Recipe
    List<Comment> findByRecipeId(String recipeId);
    List<Comment> findByAccountId(String accountId); // Tìm Comment theo ID của Account
    List<Comment> findByCommentTextContaining(String keyword); // Tìm Comment chứa từ khóa
}