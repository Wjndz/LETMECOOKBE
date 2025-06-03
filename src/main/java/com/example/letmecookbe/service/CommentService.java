package com.example.letmecookbe.service;

import com.example.letmecookbe.dto.request.CommentRequest;
import com.example.letmecookbe.dto.response.CommentResponse;
import com.example.letmecookbe.entity.Account;
import com.example.letmecookbe.entity.Comment;
import com.example.letmecookbe.entity.Recipe;
import com.example.letmecookbe.exception.AppException; // Import AppException của bạn
import com.example.letmecookbe.exception.ErrorCode;  // Import ErrorCode của bạn
import com.example.letmecookbe.mapper.CommentMapper;
import com.example.letmecookbe.repository.AccountRepository;
import com.example.letmecookbe.repository.CommentRepository;
import com.example.letmecookbe.repository.RecipeRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;
import javax.swing.*;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CommentService {

    CommentRepository commentRepository;
    CommentMapper commentMapper;
    AccountRepository accountRepository;
    RecipeRepository recipeRepository;

    // --- 1. Tạo Comment ---
    public CommentResponse createComment(String recipeId, CommentRequest request) {
        var context = SecurityContextHolder.getContext();
        String userEmail = context.getAuthentication().getName(); // Lấy EMAIL từ JWT subject
        // Tìm tài khoản bằng EMAIL
        Account account = accountRepository.findAccountByEmail(userEmail)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED)); // Lỗi này sẽ hiếm xảy ra nếu JWT hợp lệ
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new AppException(ErrorCode.RECIPE_NOT_FOUND));
        Comment comment = commentMapper.toComment(request);
        comment.setAccount(account);
        comment.setRecipe(recipe);
        comment = commentRepository.save(comment);
        return commentMapper.toCommentResponse(comment);
    }

    // --- 2. Chỉnh sửa Comment ---
    public CommentResponse updateComment(String commentId, CommentRequest request) {
        var context = SecurityContextHolder.getContext();
        String currentEmail = context.getAuthentication().getName(); // Đây là email của user hiện tại từ JWT
        //  Tìm account của người dùng hiện tại bằng email để lấy ID của họ
        Account currentUserAccount = accountRepository.findAccountByEmail(currentEmail)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED)); // Xử lý nếu không tìm thấy user
        // Tìm comment cần cập nhật
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new AppException(ErrorCode.COMMENT_NOT_EXIST));
        //So sánh ID của người dùng hiện tại với ID của người tạo comment
        // Lấy thông tin quyền hạn (roles/authorities) của người dùng hiện tại
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // Kiểm tra xem người dùng hiện tại có phải là chủ sở hữu của comment hay không
        boolean isOwner = comment.getAccount().getId().equals(currentUserAccount.getId());
        // Kiểm tra xem người dùng hiện tại có vai trò ADMIN hay không
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));
        // Nếu người dùng không phải là chủ sở hữu và cũng không phải là ADMIN, ném ngoại lệ UNPROHIBITED
        if (!isOwner && !isAdmin) {
            throw new AppException(ErrorCode.UNAUTHORIZED_ACTION);
        }
        // Nếu vượt qua kiểm tra quyền, tiến hành cập nhật comment
        comment.setCommentText(request.getCommentText());
        comment = commentRepository.save(comment);
        return commentMapper.toCommentResponse(comment);
    }

    // --- 3. Xóa Comment ---
    public void deleteComment(String commentId) {
        var context = SecurityContextHolder.getContext();
        String currentUserId = context.getAuthentication().getName();
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new AppException(ErrorCode.COMMENT_NOT_EXIST)); // Sử dụng AppException
        commentRepository.delete(comment);
    }

    // --- 4. Xem Comment của Bài đăng ---
    public List<CommentResponse> getCommentsByRecipe(String recipeId) {
        if (!recipeRepository.existsById(recipeId)) {
            throw new AppException(ErrorCode.RECIPE_NOT_FOUND); // Sử dụng AppException
        }
        List<Comment> comments = commentRepository.findByRecipe_Id(recipeId);
        return commentMapper.toCommentResponseList(comments);
    }
    // --- 5. Xem một Comment cụ thể theo ID ---
    public CommentResponse getCommentById(String commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new AppException(ErrorCode.COMMENT_NOT_EXIST)); // Sử dụng AppException
        return commentMapper.toCommentResponse(comment);
    }

 
}