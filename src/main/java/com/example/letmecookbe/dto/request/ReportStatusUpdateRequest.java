package com.example.letmecookbe.dto.request;

import com.example.letmecookbe.enums.ReportStatus; // Import enum ReportStatus
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReportStatusUpdateRequest {
    @NotNull(message = "Trang thái ko được null") // Đảm bảo trạng thái mới không null
    private ReportStatus newStatus; // Trạng thái mới của báo cáo (PENDING, REVIEWED, RESOLVED, REJECTED)
    @Size(max = 1000, message = "Admin response cannot exceed 1000 characters") // Giới hạn độ dài phản hồi
    private String adminResponse; // Phản hồi của admin (tùy chọn)
}