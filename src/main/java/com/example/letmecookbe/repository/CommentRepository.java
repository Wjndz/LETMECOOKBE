package com.example.letmecookbe.repository;

import com.example.letmecookbe.entity.Comment;
import com.example.letmecookbe.enums.CommentStatus; // CRUCIAL: Import the enum!
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, String>, JpaSpecificationExecutor<Comment> {
    Page<Comment> findByRecipe_Id(String recipeId, Pageable pageable);
    Page<Comment> findByStatus(CommentStatus status, Pageable pageable); // Use the imported enum!
    Page<Comment> findByStatusAndRecipe_Id(CommentStatus status, String recipeId, Pageable pageable); // Use the imported enum!
    Page<Comment> findByCommentTextContainingIgnoreCase(String commentText, Pageable pageable);
    long countByStatus(CommentStatus status);
}