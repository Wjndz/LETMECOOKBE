package com.example.letmecookbe.controller;

import com.example.letmecookbe.dto.request.CommentRequest;
import com.example.letmecookbe.dto.response.CommentResponse;
import com.example.letmecookbe.service.CommentService;
import jakarta.validation.Valid; // Để sử dụng @Valid cho validation của request body
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController // Đánh dấu lớp này là một REST Controller
@RequestMapping("/{recipeId}/comments") // Định nghĩa base URL cho comment của một recipe cụ thể
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CommentController {
    CommentService commentService; // Inject CommentService
    // --- 1. Xem tất cả Comment của một Bài đăng (GET) ---
    // Endpoint: GET /api/recipes/{recipeId}/comments
    @GetMapping
    public ResponseEntity<List<CommentResponse>> getCommentsByRecipe(@PathVariable String recipeId) {
        List<CommentResponse> comments = commentService.getCommentsByRecipe(recipeId);
        return ResponseEntity.ok(comments); // Trả về status 200 OK
    }
    // --- 2. Tạo Comment (POST) ---
    // Endpoint: POST /api/recipes/{recipeId}/comments
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED) // Trả về 201 Created khi tạo thành công
    public ResponseEntity<CommentResponse> createComment(
            @PathVariable String recipeId,
            @RequestBody @Valid CommentRequest request) { // <--- ĐẶT BREAKPOINT Ở ĐÂY
        CommentResponse createdComment = commentService.createComment(recipeId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdComment);
    }
    // --- 3. Chỉnh sửa Comment (PUT) ---
    // Endpoint: PUT /api/recipes/{recipeId}/comments/{commentId}
    @PutMapping("/{commentId}")
    public ResponseEntity<CommentResponse> updateComment(
            @PathVariable String recipeId, // recipeId có thể được dùng để kiểm tra bối cảnh nếu cần
            @PathVariable String commentId,
            @RequestBody @Valid CommentRequest request) {
        CommentResponse updatedComment = commentService.updateComment(commentId, request);
        return ResponseEntity.ok(updatedComment); // Trả về status 200 OK
    }
    // --- 4. Xóa Comment (DELETE) ---
    // Endpoint: DELETE /api/recipes/{recipeId}/comments/{commentId}
    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT) // Trả về 204 No Content khi xóa thành công
    public ResponseEntity<Void> deleteComment(
            @PathVariable String recipeId, // recipeId có thể được dùng để kiểm tra bối cảnh nếu cần
            @PathVariable String commentId) {
        commentService.deleteComment(commentId);
        return ResponseEntity.noContent().build(); // Build response 204
    }
    // --- 5. Xem một Comment cụ thể theo ID (GET) ---
    // Endpoint: GET /api/recipes/{recipeId}/comments/{commentId}
    @GetMapping("/{commentId}")
    public ResponseEntity<CommentResponse> getCommentById(@PathVariable String recipeId, @PathVariable String commentId) {
        CommentResponse comment = commentService.getCommentById(commentId);
        return ResponseEntity.ok(comment); // Trả về status 200 OK
    }
}