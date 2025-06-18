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
import com.example.letmecookbe.repository.ReportRepository; // THÊM IMPORT NÀY
import com.example.letmecookbe.enums.ReportType; // THÊM IMPORT NÀY (giả sử bạn có enum ReportType)
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import com.example.letmecookbe.enums.CommentStatus;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.ArrayList;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CommentService {

    CommentRepository commentRepository;
    CommentMapper commentMapper;
    AccountRepository accountRepository;
    RecipeRepository recipeRepository;
    private final ReportRepository reportRepository; // THÊM DÒNG NÀY ĐỂ INJECT ReportRepository

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
        comment.setRecipe(recipe);
        comment.setStatus(CommentStatus.APPROVED);
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
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        comment.setCommentText(request.getCommentText());
        comment = commentRepository.save(comment);
        return commentMapper.toCommentResponse(comment);
    }

    // --- 3. Xóa Comment ---
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteComment(String commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new AppException(ErrorCode.COMMENT_NOT_EXIST));
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
    @PreAuthorize("hasRole('ADMIN')")
    public Page<CommentResponse> getAllComments(Pageable pageable,
                                                String searchTerm,
                                                String recipeId,
                                                String status,
                                                String date) {
        Specification<Comment> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            // Filter by searchTerm (commentText or account username)
            if (searchTerm != null && !searchTerm.trim().isEmpty()) {
                Predicate commentTextPredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("commentText")), "%" + searchTerm.toLowerCase() + "%");
                Predicate usernamePredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("account").get("username")), "%" + searchTerm.toLowerCase() + "%");
                predicates.add(criteriaBuilder.or(commentTextPredicate, usernamePredicate));
            }
            // Filter by recipeId
            if (recipeId != null && !recipeId.equals("all") && !recipeId.trim().isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("recipe").get("id"), recipeId));
            }
            //Filter by status
            if (status != null && !status.equals("all") && !status.trim().isEmpty()) {
                try {
                    // Chuyển đổi chuỗi status sang Enum CommentStatus
                    CommentStatus commentStatus = CommentStatus.valueOf(status.toUpperCase());
                    predicates.add(criteriaBuilder.equal(root.get("status"), commentStatus));
                } catch (IllegalArgumentException e) {
                    // Xử lý nếu chuỗi trạng thái không hợp lệ (ví dụ: người dùng gửi "abc" thay vì "APPROVED")
                    // Ở đây, chúng ta chỉ in lỗi ra console và bỏ qua bộ lọc này.
                    System.err.println("Invalid status parameter: " + status);
                }
            }
            if (date != null && !date.trim().isEmpty()) {
                try {
                    LocalDate searchDate = LocalDate.parse(date); // Phân tích chuỗi ngày (ví dụ "2024-06-17") thành LocalDate
                    predicates.add(criteriaBuilder.equal(criteriaBuilder.function("DATE", LocalDate.class, root.get("createdAt")), searchDate));
                } catch (DateTimeParseException e) {
                    // Xử lý nếu định dạng ngày không hợp lệ
                    System.err.println("Invalid date format: " + date);
                }
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
        Page<Comment> commentsPage = commentRepository.findAll(spec, pageable);
        return commentsPage.map(commentMapper::toCommentResponse);
    }

    //7. Cập nhật trạng thái Comment
    @PreAuthorize("hasRole('ADMIN')")
    public CommentResponse updateCommentStatus(String commentId, String newStatusString) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new AppException(ErrorCode.COMMENT_NOT_EXIST));
        try {
            CommentStatus newStatus = CommentStatus.valueOf(newStatusString.toUpperCase());
            comment.setStatus(newStatus);
        } catch (IllegalArgumentException e) {
            throw new AppException(ErrorCode.INVALID_COMMENT_STATUS);
        }
        comment = commentRepository.save(comment);
        return commentMapper.toCommentResponse(comment);
    }

    // --- 8. Lấy tổng số lượng comment theo trạng thái báo cáo (ví dụ: PENDING) ---
    @PreAuthorize("hasRole('ADMIN')") // Chỉ ADMIN mới có quyền này
    public long countCommentsByStatus(CommentStatus status) {
        return commentRepository.countByStatus(status);
    }

    // --- 9. Lấy tổng số lượng TẤT CẢ comment trong hệ thống ---
    @PreAuthorize("hasRole('ADMIN')") // Hoặc quyền phù hợp
    public long getTotalCommentsCount() {
        return commentRepository.count();
    }

    // --- 10. Lấy tổng số lượng báo cáo comment từ entity Report ---
    @PreAuthorize("hasRole('ADMIN')") // Chỉ ADMIN mới có quyền này
    public long getTotalCommentReportsFromReportEntity() {
        // Đảm bảo ReportType.COMMENT là giá trị enum chính xác trong ReportType của bạn
        return reportRepository.countByReportType(ReportType.REPORT_COMMENT);
    }
}