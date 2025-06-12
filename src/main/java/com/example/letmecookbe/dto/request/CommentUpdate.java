package com.example.letmecookbe.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;
import jakarta.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentUpdate {
    @NotBlank(message = "Comment text cannot be empty")
    String commentText;
    String img; // URL hình ảnh mới cho comment (có thể null)
}