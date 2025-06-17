package com.example.letmecookbe.controller;

import com.example.letmecookbe.dto.request.CommentRequest;
import com.example.letmecookbe.dto.response.ApiResponse; // Đảm bảo đã import
import com.example.letmecookbe.dto.response.CommentResponse;
import com.example.letmecookbe.service.CommentService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Sort;
import java.util.List; // Vẫn giữ import nếu bạn có các phương thức khác trả về List

@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CommentController {
    CommentService commentService;

    // --- 1. Xem tất cả Comment của một Bài đăng (GET) ---
    @GetMapping("/recipe/{recipeId}") // Đường dẫn mới để lấy comment theo recipe
    public ResponseEntity<Page<CommentResponse>> getCommentsByRecipe(
            @PathVariable String recipeId,
            @PageableDefault(size = 10, page = 0, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<CommentResponse> comments = commentService.getCommentsByRecipe(recipeId, pageable);
        return ResponseEntity.ok(comments);
    }

    // --- 2. Tạo Comment (POST) ---
    @PostMapping("/{recipeId}")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ApiResponse<CommentResponse>> createComment(
            @PathVariable String recipeId,
            @RequestBody @Valid CommentRequest request) {
        CommentResponse createdComment = commentService.createComment(recipeId, request);
        ApiResponse<CommentResponse> response = new ApiResponse<>();
        response.setMessage("Comment created successfully");
        response.setResult(createdComment);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // --- 3. Chỉnh sửa Comment (PUT) ---
    @PutMapping("/{recipeId}/{commentId}")
    public ResponseEntity<ApiResponse<CommentResponse>> updateComment(
            @PathVariable String commentId,
            @RequestBody @Valid CommentRequest request) {
        CommentResponse updatedComment = commentService.updateComment(commentId, request);
        ApiResponse<CommentResponse> response = new ApiResponse<>();
        response.setMessage("Comment updated successfully");
        response.setResult(updatedComment);
        return ResponseEntity.ok(response);
    }

    // --- 4. Xóa Comment (DELETE) ---
    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteComment(@PathVariable String commentId) {
        commentService.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }

    // --- 5. Xem một Comment cụ thể theo ID (GET) ---
    @GetMapping("/{commentId}")
    public ResponseEntity<ApiResponse<CommentResponse>> getCommentById(@PathVariable String commentId) {
        CommentResponse comment = commentService.getCommentById(commentId);
        ApiResponse<CommentResponse> response = new ApiResponse<>();
        response.setMessage("Get comment by ID");
        response.setResult(comment);
        return ResponseEntity.ok(response);
    }

    // --- MỚI THÊM: 6. Xem tất cả Comment trong hệ thống (có phân trang) ---
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<Page<CommentResponse>>> getAllComments(
            @PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam(required = false) String searchTerm, // <--- Đã có
            @RequestParam(required = false) String recipeId,   // <--- Đã có
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String date) {

        ApiResponse<Page<CommentResponse>> response = new ApiResponse<>();
        response.setMessage("Get all comments with pagination and filters");
        response.setResult(commentService.getAllComments(pageable, searchTerm, recipeId, status, date));
        return ResponseEntity.ok(response);
    }
    @PatchMapping("/{commentId}/status")// Chỉ Admin mới được thay đổi trạng thái
    public ResponseEntity<ApiResponse<CommentResponse>> updateCommentStatus(
            @PathVariable String commentId,
            @RequestParam("status") String newStatus) { // Dùng @RequestParam hoặc @RequestBody nếu bạn muốn một object
        CommentResponse updatedComment = commentService.updateCommentStatus(commentId, newStatus);
        ApiResponse<CommentResponse> response = new ApiResponse<>();
        response.setMessage("Comment status updated successfully");
        response.setResult(updatedComment);
        return ResponseEntity.ok(response);
    }

}