package com.example.letmecookbe.service;

import com.example.letmecookbe.entity.Account;
import com.example.letmecookbe.entity.Comment;
import com.example.letmecookbe.entity.Recipe;
import com.example.letmecookbe.repository.AccountRepository;
import com.example.letmecookbe.repository.CommentRepository;
import com.example.letmecookbe.repository.RecipeRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service // Đánh dấu đây là Spring Service
public class CommentService {
    @Autowired private CommentRepository commentRepository;
    @Autowired private AccountRepository accountRepository;
    @Autowired private RecipeRepository recipeRepository;

    // Lấy ID Account của người dùng hiện tại từ Security Context
    private String getCurrentAccountId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName(); // Thường là username/ID từ JWT
        }
        throw new IllegalStateException("Người dùng chưa xác thực.");
    }

    // Tạo Comment mới
    public Comment createComment(String recipeId, String commentText) {
        String currentAccountId = getCurrentAccountId();
        Account account = accountRepository.findById(currentAccountId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy Account: " + currentAccountId));
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy Recipe: " + recipeId));
        Comment newComment = Comment.builder()
                .commentText(commentText)
                .account(account)
                .recipe(recipe)
                .createdAt(LocalDateTime.now())
                .build();
        return commentRepository.save(newComment);
    }

    // Cập nhật nội dung Comment
    public Comment updateComment(String commentId, String newCommentText) {
        String currentAccountId = getCurrentAccountId();
        Comment existingComment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy Comment: " + commentId));
        // Kiểm tra quyền chỉnh sửa
        if (!existingComment.getAccount().getId().equals(currentAccountId)) {
            throw new SecurityException("Bạn không có quyền chỉnh sửa comment này.");
        }
        existingComment.setCommentText(newCommentText);
        return commentRepository.save(existingComment);
    }

    // Xóa Comment
    public void deleteComment(String commentId) {
        String currentAccountId = getCurrentAccountId();
        Comment existingComment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy Comment: " + commentId));
        // Kiểm tra quyền xóa
        if (!existingComment.getAccount().getId().equals(currentAccountId)) {
            throw new SecurityException("Bạn không có quyền xóa comment này.");
        }
        commentRepository.delete(existingComment);
    }

    // Lấy tất cả Comment theo ID Recipe
    public List<Comment> getCommentsByRecipeId(String recipeId) {
        if (!recipeRepository.existsById(recipeId)) {
            throw new EntityNotFoundException("Không tìm thấy Recipe: " + recipeId);
        }
        return commentRepository.findByRecipeId(recipeId);
    }
    // Lấy một Comment theo ID
    public Comment getCommentById(String commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy Comment: " + commentId));
    }
}