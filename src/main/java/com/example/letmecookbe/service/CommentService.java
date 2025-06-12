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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

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
    @PreAuthorize("hasAuthority('CREATE_COMMENT')")
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
        comment = commentRepository.save(comment);
        return commentMapper.toCommentResponse(comment);
    }

    // --- 2. Chỉnh sửa Comment ---
    @PreAuthorize("hasAuthority('UPDATE_COMMENT')")
    public CommentResponse updateComment(String commentId, CommentRequest request) {
        var context = SecurityContextHolder.getContext();
        String currentEmail = context.getAuthentication().getName(); // Đây là email của user hiện tại từ JWT

        // Bước 1: Tìm account của người dùng hiện tại bằng email để lấy ID của họ
        Account currentUserAccount = accountRepository.findAccountByEmail(currentEmail)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED)); // Xử lý nếu không tìm thấy user (hiếm khi xảy ra nếu JWT hợp lệ)

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new AppException(ErrorCode.COMMENT_NOT_EXIST));

        // Bước 2: So sánh ID của người dùng hiện tại với ID của người tạo comment
        // Bây giờ chúng ta đang so sánh Integer với Integer (comment.getAccount().getId() vs currentUserAccount.getId())
    

        comment.setCommentText(request.getCommentText());
        comment = commentRepository.save(comment);
        return commentMapper.toCommentResponse(comment);
    }

    // --- 3. Xóa Comment ---
    @PreAuthorize("hasAuthority('DELETE_COMMENT')")
    public void deleteComment(String commentId) {
        var context = SecurityContextHolder.getContext();
        String currentUserId = context.getAuthentication().getName();
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new AppException(ErrorCode.COMMENT_NOT_EXIST)); // Sử dụng AppException
        commentRepository.delete(comment);
    }

    // --- 4. Xem Comment của Bài đăng ---
    @PreAuthorize("hasAuthority('GET_COMMENTS_BY_RECIPE')")
    public Page<CommentResponse> getCommentsByRecipe(String recipeId, Pageable pageable) { // Đã sửa đổi chữ ký
        if (!recipeRepository.existsById(recipeId)) {
            throw new AppException(ErrorCode.RECIPE_NOT_FOUND);
        }
        // Gọi phương thức findByRecipe_Id từ CommentRepository với Pageable
        Page<Comment> commentsPage = commentRepository.findByRecipe_Id(recipeId, pageable);

        // Sử dụng map để chuyển đổi Page<Comment> sang Page<CommentResponse>
        return commentsPage.map(commentMapper::toCommentResponse);
    }


    // --- 5. Xem một Comment cụ thể theo ID ---
    @PreAuthorize("hasAuthority('GET_COMMENT_BY_ID')")
    public CommentResponse getCommentById(String commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new AppException(ErrorCode.COMMENT_NOT_EXIST)); // Sử dụng AppException
        return commentMapper.toCommentResponse(comment);
    }

 
}