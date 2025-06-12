package com.example.letmecookbe.dto.response;
import com.example.letmecookbe.enums.ReportStatus;
import com.example.letmecookbe.enums.ReportType;
import lombok.*;
import lombok.experimental.FieldDefaults;
import java.time.LocalDateTime;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReportResponse {
    String id;
    // Thông tin người báo cáo (chỉ username hoặc email để tránh lộ password/nhạy cảm)
    String reporterAccountUsername;
    String reporterAccountEmail;
    ReportType reportType; // Loại đối tượng bị báo cáo
    String reportedItemId; // ID của đối tượng bị báo cáo

    String reason; // Lý do báo cáo

    ReportStatus status;

    LocalDateTime createdAt;

    String adminResponse;

    // Thông tin admin đã xử lý (chỉ username hoặc email)
    String resolvedByAdminUsername;
    String resolvedByAdminEmail;

    LocalDateTime resolvedAt;
}