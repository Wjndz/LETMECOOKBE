package com.example.letmecookbe.service;

import com.example.letmecookbe.dto.request.CommentRequest;
import com.example.letmecookbe.dto.response.CommentResponse;
import com.example.letmecookbe.entity.Account;
import com.example.letmecookbe.entity.Comment;
import com.example.letmecookbe.entity.Recipe;
import com.example.letmecookbe.exception.AppException;
import com.example.letmecookbe.exception.ErrorCode;
import com.example.letmecookbe.mapper.CommentMapper;
import com.example.letmecookbe.repository.AccountRepository;
import com.example.letmecookbe.repository.CommentRepository;
import com.example.letmecookbe.repository.RecipeRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable; // Đã thêm import này
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

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
        String userEmail = context.getAuthentication().getName();

        Account account = accountRepository.findAccountByEmail(userEmail)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new AppException(ErrorCode.RECIPE_NOT_FOUND));

        Comment comment = commentMapper.toComment(request);
        comment.setAccount(account);
        comment.setRecipe(recipe); // Đã thêm dòng này để liên kết comment với recipe
        comment = commentRepository.save(comment);
        return commentMapper.toCommentResponse(comment);
    }

    // --- 2. Chỉnh sửa Comment ---
    @PreAuthorize("hasAuthority('UPDATE_COMMENT')")
    public CommentResponse updateComment(String commentId, CommentRequest request) {
        var context = SecurityContextHolder.getContext();
        String currentEmail = context.getAuthentication().getName();

        Account currentUserAccount = accountRepository.findAccountByEmail(currentEmail)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new AppException(ErrorCode.COMMENT_NOT_EXIST));

        // Kiểm tra quyền: Chỉ người tạo comment mới được phép chỉnh sửa
        if (!comment.getAccount().getId().equals(currentUserAccount.getId())) {
            throw new AppException(ErrorCode.UNAUTHORIZED); // Sử dụng ErrorCode.UNAUTHORIZED
        }

        comment.setCommentText(request.getCommentText());
        comment = commentRepository.save(comment);
        return commentMapper.toCommentResponse(comment);
    }

    // --- 3. Xóa Comment ---
    @PreAuthorize("hasAuthority('DELETE_COMMENT')")
    public void deleteComment(String commentId) {
        var context = SecurityContextHolder.getContext();
        String currentEmail = context.getAuthentication().getName();
        Account currentUserAccount = accountRepository.findAccountByEmail(currentEmail)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new AppException(ErrorCode.COMMENT_NOT_EXIST));

        // Kiểm tra quyền: Chỉ người tạo comment mới được phép xóa (hoặc thêm kiểm tra vai trò ADMIN ở đây)
        if (!comment.getAccount().getId().equals(currentUserAccount.getId())) {
            throw new AppException(ErrorCode.UNAUTHORIZED); // Sử dụng ErrorCode.UNAUTHORIZED
        }

        commentRepository.delete(comment);
    }

    // --- 4. Xem Comment của Bài đăng (có phân trang) ---
    @PreAuthorize("hasAuthority('GET_COMMENTS_BY_RECIPE')")
    public Page<CommentResponse> getCommentsByRecipe(String recipeId, Pageable pageable) {
        if (!recipeRepository.existsById(recipeId)) {
            throw new AppException(ErrorCode.RECIPE_NOT_FOUND);
        }
        Page<Comment> commentsPage = commentRepository.findByRecipe_Id(recipeId, pageable);
        return commentsPage.map(commentMapper::toCommentResponse);
    }


    // --- 5. Xem một Comment cụ thể theo ID ---
    @PreAuthorize("hasAuthority('GET_COMMENT_BY_ID')")
    public CommentResponse getCommentById(String commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new AppException(ErrorCode.COMMENT_NOT_EXIST));
        return commentMapper.toCommentResponse(comment);
    }

    // --- 6. Xem tất cả Comment (có phân trang) ---
    @PreAuthorize("hasAuthority('GET_ALL_COMMENTS')")
    public Page<CommentResponse> getAllComments(Pageable pageable) { // Thay đổi kiểu trả về và thêm tham số Pageable
        Page<Comment> commentsPage = commentRepository.findAll(pageable); // Sử dụng findAll với Pageable
        if (commentsPage.isEmpty()) {
            throw new AppException(ErrorCode.LIST_EMPTY);
        }
        return commentsPage.map(commentMapper::toCommentResponse);
    }
}