package com.example.letmecookbe.dto.request;
import lombok.*;
import lombok.experimental.FieldDefaults;
import jakarta.validation.constraints.NotBlank;
@Data // Tự động tạo getter/setter và các phương thức cơ bản
@Builder
@NoArgsConstructor // Constructor không đối số
@AllArgsConstructor // Constructor với tất cả đối số
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentRequest {
    @NotBlank(message = "Nội dung bình luận không được để trống.") // Đảm bảo trường không rỗng
    String commentText;
}