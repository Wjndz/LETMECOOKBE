package com.example.letmecookbe.service;

import com.example.letmecookbe.dto.request.ReportRequest;
import com.example.letmecookbe.dto.request.ReportStatusUpdateRequest;
import com.example.letmecookbe.dto.response.ReportResponse;
import com.example.letmecookbe.entity.Account;
import com.example.letmecookbe.entity.Report;
import com.example.letmecookbe.entity.Recipe;
import com.example.letmecookbe.entity.Comment;
import com.example.letmecookbe.exception.AppException;
import com.example.letmecookbe.exception.ErrorCode;
import com.example.letmecookbe.mapper.ReportMapper;
import com.example.letmecookbe.repository.AccountRepository;
import com.example.letmecookbe.repository.ReportRepository;
import com.example.letmecookbe.repository.RecipeRepository;
import com.example.letmecookbe.repository.CommentRepository;
import com.example.letmecookbe.enums.ReportStatus;
import com.example.letmecookbe.enums.ReportType;
import com.example.letmecookbe.enums.ReportSeverity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile; // <-- Thêm import này

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportService {

    private final ReportRepository reportRepository;
    private final AccountRepository accountRepository;
    private final RecipeRepository recipeRepository;
    private final CommentRepository commentRepository;
    private final ReportMapper reportMapper;
    private final AuthService authService;
    private final FileStorageService fileStorageService; // <-- Inject FileStorageService trở lại

    /**
     * Tạo một báo cáo mới.
     * Người dùng (ROLE_USER) có thể tạo báo cáo về công thức, bình luận hoặc người dùng khác.
     */
    @PreAuthorize("hasRole('USER')")
    public ReportResponse createReport(ReportRequest request) {
        Account currentUser = authService.getCurrentAccount();
        Account reportedAccountForEntity = null;

        // Xử lý báo cáo công thức
        if (request.getReportType() == ReportType.REPORT_RECIPE) {
            Recipe recipe = recipeRepository.findById(request.getReportedItemId())
                    .orElseThrow(() -> new AppException(ErrorCode.RECIPE_NOT_FOUND));

            if (recipe.getAccount().getId().equals(currentUser.getId())) {
                throw new AppException(ErrorCode.CANNOT_REPORT_OWN_CONTENT);
            }
            reportedAccountForEntity = recipe.getAccount();
        }
        // Xử lý báo cáo bình luận
        else if (request.getReportType() == ReportType.REPORT_COMMENT) {
            Comment comment = commentRepository.findById(request.getReportedItemId())
                    .orElseThrow(() -> new AppException(ErrorCode.COMMENT_NOT_EXIST));

            if (comment.getAccount().getId().equals(currentUser.getId())) {
                throw new AppException(ErrorCode.CANNOT_REPORT_OWN_CONTENT);
            }
            reportedAccountForEntity = comment.getAccount();
        }
        // Xử lý báo cáo người dùng
        else if (request.getReportType() == ReportType.REPORT_USER) {
            Account reportedUser = accountRepository.findById(request.getReportedItemId())
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

            if (reportedUser.getId().equals(currentUser.getId())) {
                throw new AppException(ErrorCode.CANNOT_REPORT_OWN_ACCOUNT);
            }
            reportedAccountForEntity = reportedUser;
        }
        // Xử lý trường hợp loại báo cáo không hợp lệ
        else {
            throw new AppException(ErrorCode.INVALID_KEY);
        }

        // --- Xử lý tải lên ảnh bằng chứng trở lại ---
        String evidenceImageUrl = null;
        if (request.getEvidenceImage() != null && !request.getEvidenceImage().isEmpty()) {
            try {
                evidenceImageUrl = fileStorageService.uploadFile(request.getEvidenceImage());
            } catch (AppException e) {
                log.error("Failed to upload evidence image: {}", e.getMessage());
                throw e;
            } catch (Exception e) {
                log.error("Unexpected error during file upload: {}", e.getMessage());
                throw new AppException(ErrorCode.FILE_UPLOAD_FAILED);
            }
        }
        // ------------------------------------

        // Chuyển đổi DTO sang Entity và thiết lập các trường ban đầu
        Report report = reportMapper.toEntity(request);
        report.setReporterAccount(currentUser);
        report.setReportedAccount(reportedAccountForEntity);
        report.setStatus(ReportStatus.PENDING);
        report.setSeverity(determineSeverity(request.getReason(), request.getReportType()));
        report.setEvidenceImageUrl(evidenceImageUrl); // <-- Set URL ảnh bằng chứng đã tải lên

        Report savedReport = reportRepository.save(report);
        log.info("Report created successfully for item ID: {} with type: {} by user: {}",
                request.getReportedItemId(), request.getReportType(), currentUser.getEmail());

        return reportMapper.toDto(savedReport);
    }

    /**
     * Xác định mức độ nghiêm trọng của báo cáo dựa trên lý do và loại báo cáo.
     */
    private ReportSeverity determineSeverity(String reason, ReportType reportType) {
        switch (reason) {
            case "Spam hoặc quảng cáo":
                return ReportSeverity.MEDIUM;
            case "Ngôn từ thù địch hoặc quấy rối":
                return ReportSeverity.HIGH;
            case "Vi phạm bản quyền":
                return ReportSeverity.CRITICAL;
            case "Lừa đảo hoặc gian lận":
                return ReportSeverity.CRITICAL;
            case "Thông tin sai lệch":
                if (reportType == ReportType.REPORT_RECIPE) {
                    return ReportSeverity.HIGH;
                }
                return ReportSeverity.MEDIUM;
            case "Nội dung không phù hợp":
                return ReportSeverity.LOW;
            case "Nội dung bạo lực":
                return ReportSeverity.HIGH;
            case "Khác":
                return ReportSeverity.LOW;
            default:
                log.warn("Unknown report reason: {}", reason);
                return ReportSeverity.LOW;
        }
    }

    /**
     * Lấy tất cả báo cáo, có thể lọc theo trạng thái và hỗ trợ phân trang.
     * Chỉ Admin (ROLE_ADMIN) mới có quyền truy cập.
     */
    @PreAuthorize("hasRole('ADMIN')")
    public Page<ReportResponse> getAllReports(String status, Pageable pageable) {
        Page<Report> reportsPage;
        if (status != null && !status.isEmpty()) {
            try {
                ReportStatus reportStatus = ReportStatus.valueOf(status.toUpperCase());
                reportsPage = reportRepository.findByStatus(reportStatus, pageable);
            } catch (IllegalArgumentException e) {
                throw new AppException(ErrorCode.INVALID_KEY);
            }
        } else {
            reportsPage = reportRepository.findAll(pageable);
        }
        return reportsPage.map(reportMapper::toDto);
    }

    /**
     * Lấy một báo cáo cụ thể theo ID.
     * Chỉ Admin (ROLE_ADMIN) mới có quyền truy cập.
     */
    @PreAuthorize("hasRole('ADMIN')")
    public ReportResponse getReportById(String reportId) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new AppException(ErrorCode.REPORT_NOT_FOUND));
        return reportMapper.toDto(report);
    }

    /**
     * Cập nhật trạng thái và thêm phản hồi của Admin cho một báo cáo.
     * Chỉ Admin (ROLE_ADMIN) mới có quyền thực hiện.
     */
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public ReportResponse updateReportStatus(String reportId, ReportStatusUpdateRequest request) {
        Account currentAdminAccount = authService.getCurrentAccount();

        Report existingReport = reportRepository.findById(reportId)
                .orElseThrow(() -> new AppException(ErrorCode.REPORT_NOT_FOUND));

        reportMapper.updateReportFromStatusRequest(request, existingReport);
        existingReport.setResolvedByAdmin(currentAdminAccount);
        existingReport.setResolvedAt(LocalDateTime.now());

        Report updatedReport = reportRepository.save(existingReport);
        log.info("Report ID {} status updated to {} by admin {}", reportId, request.getNewStatus(), currentAdminAccount.getEmail());

        return reportMapper.toDto(updatedReport);
    }

    /**
     * Xóa một báo cáo.
     * Chỉ Admin (ROLE_ADMIN) mới có thể xóa.
     */
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteReport(String reportId) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new AppException(ErrorCode.REPORT_NOT_FOUND));

        // --- Xóa ảnh bằng chứng trên Cloudinary trở lại ---
        if (report.getEvidenceImageUrl() != null && !report.getEvidenceImageUrl().isEmpty()) {
            try {
                fileStorageService.deleteFile(report.getEvidenceImageUrl());
            } catch (AppException e) {
                log.error("Failed to delete evidence image for report ID {}: {}", reportId, e.getMessage());
                // Quyết định: có nên ném lỗi tiếp hay chỉ ghi log và vẫn xóa báo cáo?
                // Hiện tại, tôi sẽ cho phép báo cáo vẫn bị xóa dù ảnh không xóa được để tránh kẹt DB.
                // Tùy thuộc vào yêu cầu nghiệp vụ của bạn.
            }
        }
        // -------------------------------------------

        reportRepository.deleteById(reportId);
        log.info("Report ID {} deleted by admin {}", reportId, authService.getCurrentAccount().getEmail());
    }
    @PreAuthorize("hasRole('ADMIN')") // Hoặc quyền phù hợp
    public long getTotalCommentReportsFromReportEntity() {
        // Sử dụng ReportType.REPORT_COMMENT vì đó là giá trị trong DB của bạn
        return reportRepository.countByReportType(ReportType.REPORT_COMMENT);
    }

}