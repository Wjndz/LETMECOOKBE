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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import org.springframework.data.domain.Page; // Đảm bảo import này
import org.springframework.data.domain.Pageable; // Đảm bảo import này
// import java.util.List; // Có thể bỏ nếu không còn phương thức nào trả về List

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

    /**
     * Tạo một báo cáo mới.
     */
    @Transactional
    public ReportResponse createReport(ReportRequest request) {
        Account currentUser = authService.getCurrentAccount();

        if (request.getReportType() == ReportType.REPORT_RECIPE) {
            Recipe recipe = recipeRepository.findById(request.getReportedItemId())
                    .orElseThrow(() -> new AppException(ErrorCode.RECIPE_NOT_FOUND));

            if (recipe.getAccount().getId().equals(currentUser.getId())) {
                throw new AppException(ErrorCode.CANNOT_REPORT_OWN_CONTENT);
            }
        } else if (request.getReportType() == ReportType.REPORT_COMMENT) {
            Comment comment = commentRepository.findById(request.getReportedItemId())
                    .orElseThrow(() -> new AppException(ErrorCode.COMMENT_NOT_EXIST));

            if (comment.getAccount().getId().equals(currentUser.getId())) {
                throw new AppException(ErrorCode.CANNOT_REPORT_OWN_CONTENT);
            }
        } else {
            throw new AppException(ErrorCode.INVALID_KEY);
        }

        Report report = reportMapper.toEntity(request);
        report.setReporterAccount(currentUser);
        report.setStatus(ReportStatus.PENDING);
        // report.setCreatedAt(LocalDateTime.now()); // Đảm bảo trường này được set nếu không dùng @CreationTimestamp

        Report savedReport = reportRepository.save(report);
        log.info("Report created successfully for item ID: {} by user: {}", request.getReportedItemId(), currentUser.getEmail());

        return reportMapper.toDto(savedReport);
    }

    /**
     * Lấy tất cả báo cáo, có thể lọc theo trạng thái và hỗ trợ phân trang.
     */
    // Đã thay đổi chữ ký phương thức và kiểu trả về
    public Page<ReportResponse> getAllReports(String status, Pageable pageable) {
        Page<Report> reportsPage;
        if (status != null && !status.isEmpty()) {
            try {
                ReportStatus reportStatus = ReportStatus.valueOf(status.toUpperCase());
                reportsPage = reportRepository.findByStatus(reportStatus, pageable); // Truyền Pageable vào repository
            } catch (IllegalArgumentException e) {
                throw new AppException(ErrorCode.INVALID_KEY);
            }
        } else {
            reportsPage = reportRepository.findAll(pageable); // Truyền Pageable vào repository
        }

        return reportsPage.map(reportMapper::toDto); // Chuyển đổi Page<Entity> sang Page<DTO>
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
     * Xóa một báo cáo. Chỉ Admin mới có thể xóa.
     */
    @Transactional
    public void deleteReport(String reportId) {
        if (!reportRepository.existsById(reportId)) {
            throw new AppException(ErrorCode.REPORT_NOT_FOUND);
        }

        reportRepository.deleteById(reportId);
        log.info("Report ID {} deleted by admin {}", reportId, authService.getCurrentAccount().getEmail());
    }
}