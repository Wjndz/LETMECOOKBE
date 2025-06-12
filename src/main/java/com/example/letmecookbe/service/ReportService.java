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
import com.example.letmecookbe.repository.AccountRepository; // Có thể bỏ nếu chỉ dùng qua AuthService
import com.example.letmecookbe.repository.ReportRepository;
import com.example.letmecookbe.repository.RecipeRepository;
import com.example.letmecookbe.repository.CommentRepository;
import com.example.letmecookbe.enums.ReportStatus;
import com.example.letmecookbe.enums.ReportType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
// Các import Spring Security không còn cần thiết nếu đã chuyển logic sang AuthService
// import org.springframework.security.core.context.SecurityContextHolder;
// import org.springframework.security.core.Authentication;
// import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportService {

    private final ReportRepository reportRepository;
    private final AccountRepository accountRepository; // Giữ lại nếu có các logic khác cần tìm kiếm Account trực tiếp, không liên quan đến user hiện tại
    private final RecipeRepository recipeRepository;
    private final CommentRepository commentRepository;
    private final ReportMapper reportMapper;
    private final AuthService authService; // Đảm bảo AuthService đã được inject và có phương thức getCurrentAccount()


    /**
     * Tạo một báo cáo mới.
     */
    @Transactional
    public ReportResponse createReport(ReportRequest request) {
        // Lấy thông tin người dùng hiện tại từ AuthService
        Account currentUser = authService.getCurrentAccount(); // SỬ DỤNG AUTHSERVICE

        // Kiểm tra sự tồn tại của đối tượng bị báo cáo (Recipe hoặc Comment) và ngăn chặn tự báo cáo
        if (request.getReportType() == ReportType.REPORT_RECIPE) {
            Recipe recipe = recipeRepository.findById(request.getReportedItemId())
                    .orElseThrow(() -> new AppException(ErrorCode.RECIPE_NOT_FOUND));

            if (recipe.getAccount().getId().equals(currentUser.getId())) {
                throw new AppException(ErrorCode.CANNOT_REPORT_OWN_CONTENT);
            }
        } else if (request.getReportType() == ReportType.REPORT_COMMENT) {
            Comment comment = commentRepository.findById(request.getReportedItemId())
                    .orElseThrow(() -> new AppException(ErrorCode.COMMENT_NOT_EXIST)); // Đã sửa ErrorCode

            if (comment.getAccount().getId().equals(currentUser.getId())) {
                throw new AppException(ErrorCode.CANNOT_REPORT_OWN_CONTENT);
            }
        } else {
            throw new AppException(ErrorCode.INVALID_KEY);
        }

        Report report = reportMapper.toEntity(request);
        report.setReporterAccount(currentUser);
        report.setStatus(ReportStatus.PENDING);

        Report savedReport = reportRepository.save(report);
        log.info("Report created successfully for item ID: {} by user: {}", request.getReportedItemId(), currentUser.getEmail()); // Sử dụng email từ Account object

        return reportMapper.toDto(savedReport);
    }

    /**
     * Lấy tất cả báo cáo, có thể lọc theo trạng thái.
     */
    public List<ReportResponse> getAllReports(String status) {
        List<Report> reports;
        if (status != null && !status.isEmpty()) {
            try {
                ReportStatus reportStatus = ReportStatus.valueOf(status.toUpperCase());
                reports = reportRepository.findByStatus(reportStatus);
            } catch (IllegalArgumentException e) {
                throw new AppException(ErrorCode.INVALID_KEY);
            }
        } else {
            reports = reportRepository.findAll();
        }

        return reports.stream()
                .map(reportMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Lấy một báo cáo cụ thể theo ID.
     */
    public ReportResponse getReportById(String reportId) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new AppException(ErrorCode.REPORT_NOT_FOUND));
        return reportMapper.toDto(report);
    }

    /**
     * Cập nhật trạng thái và thêm phản hồi của Admin cho một báo cáo.
     */
    @Transactional
    public ReportResponse updateReportStatus(String reportId, ReportStatusUpdateRequest request) {
        // Lấy thông tin admin hiện tại từ AuthService
        Account currentAdminAccount = authService.getCurrentAccount(); // SỬ DỤNG AUTHSERVICE

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
     * Xóa một báo cáo. Chỉ Admin mới có thể xóa.
     */
    @Transactional
    public void deleteReport(String reportId) {
        // Lấy thông tin admin hiện tại từ AuthService (nếu cần cho log)
        // String currentAdminIdentifier = authService.getCurrentUserIdentifier(); // Có thể dùng nếu chỉ cần identifier dạng String

        if (!reportRepository.existsById(reportId)) {
            throw new AppException(ErrorCode.REPORT_NOT_FOUND);
        }

        reportRepository.deleteById(reportId);
        log.info("Report ID {} deleted by admin {}", reportId, authService.getCurrentAccount().getEmail()); // SỬ DỤNG AUTHSERVICE
    }
}