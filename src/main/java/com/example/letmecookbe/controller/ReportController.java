// src/main/java/com/example/letmecookbe/controller/ReportController.java
package com.example.letmecookbe.controller;

import com.example.letmecookbe.dto.request.ReportRequest;
import com.example.letmecookbe.dto.request.ReportStatusUpdateRequest;
import com.example.letmecookbe.dto.response.ApiResponse; // Giả định bạn có lớp ApiResponse chung
import com.example.letmecookbe.dto.response.ReportResponse;
import com.example.letmecookbe.service.ReportService; // Import ReportService
import jakarta.validation.Valid; // Để kích hoạt validation cho DTO
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; // Để phân quyền
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController // Đánh dấu đây là một REST Controller
@RequestMapping("/reports") // Base URL cho tất cả các API liên quan đến báo cáo
@RequiredArgsConstructor // Lombok: Tự động tạo constructor với các dependency final
@Slf4j // Lombok: Để sử dụng logger
public class ReportController {

    private final ReportService reportService; // Inject ReportService

    /**
     * API: Gửi báo cáo mới (Người dùng)
     * POST /api/reports
     * Body: ReportRequest (reportType, reportedItemId, reason)
     */
    @PostMapping
    @PreAuthorize("isAuthenticated()") // Chỉ người dùng đã đăng nhập mới có thể gửi báo cáo
    public ResponseEntity<ApiResponse<ReportResponse>> createReport(@RequestBody @Valid ReportRequest request) {
        log.info("Received request to create a report: {}", request);
        ReportResponse response = reportService.createReport(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<ReportResponse>builder()
                        .code(HttpStatus.CREATED.value())
                        .message("Báo cáo đã được gửi thành công.")
                        .result(response)
                        .build()
        );
    }

    /**
     * API: Xem tất cả các báo cáo (Admin)
     * GET /api/reports?status={status} (Optional)
     * Params: status (PENDING, RESOLVED, REJECTED)
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')") // Chỉ admin mới có quyền xem tất cả báo cáo
    public ResponseEntity<ApiResponse<List<ReportResponse>>> getAllReports(@RequestParam(required = false) String status) {
        log.info("Received request to get all reports with status: {}", status);
        List<ReportResponse> reports = reportService.getAllReports(status);
        return ResponseEntity.ok(
                ApiResponse.<List<ReportResponse>>builder()
                        .code(HttpStatus.OK.value())
                        .message("Lấy danh sách báo cáo thành công.")
                        .result(reports)
                        .build()
        );
    }

    /**
     * API: Xem chi tiết một báo cáo theo ID (Admin)
     * GET /api/reports/{reportId}
     */
    @GetMapping("/{reportId}")
    @PreAuthorize("hasRole('ADMIN')") // Chỉ admin mới có quyền xem chi tiết báo cáo
    public ResponseEntity<ApiResponse<ReportResponse>> getReportById(@PathVariable String reportId) {
        log.info("Received request to get report by ID: {}", reportId);
        ReportResponse report = reportService.getReportById(reportId);
        return ResponseEntity.ok(
                ApiResponse.<ReportResponse>builder()
                        .code(HttpStatus.OK.value())
                        .message("Lấy chi tiết báo cáo thành công.")
                        .result(report)
                        .build()
        );
    }

    /**
     * API: Cập nhật trạng thái và phản hồi báo cáo (Admin)
     * PUT /api/reports/{reportId}/status
     * Body: ReportStatusUpdateRequest (newStatus, adminResponse)
     */
    @PutMapping("/{reportId}/status")
    @PreAuthorize("hasRole('ADMIN')") // Chỉ admin mới có quyền cập nhật trạng thái báo cáo
    public ResponseEntity<ApiResponse<ReportResponse>> updateReportStatus(
            @PathVariable String reportId,
            @RequestBody @Valid ReportStatusUpdateRequest request) {
        log.info("Received request to update report status for ID {}: {}", reportId, request);
        ReportResponse updatedReport = reportService.updateReportStatus(reportId, request);
        return ResponseEntity.ok(
                ApiResponse.<ReportResponse>builder()
                        .code(HttpStatus.OK.value())
                        .message("Cập nhật trạng thái báo cáo thành công.")
                        .result(updatedReport)
                        .build()
        );
    }

    /**
     * API: Xóa một báo cáo (Admin)
     * DELETE /api/reports/{reportId}
     */
    @DeleteMapping("/{reportId}")
    @PreAuthorize("hasRole('ADMIN')") // Chỉ admin mới có quyền xóa báo cáo
    public ResponseEntity<ApiResponse<Void>> deleteReport(@PathVariable String reportId) {
        log.info("Received request to delete report by ID: {}", reportId);
        reportService.deleteReport(reportId);
        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .code(HttpStatus.OK.value())
                        .message("Báo cáo đã được xóa thành công.")
                        .build()
        );
    }
}