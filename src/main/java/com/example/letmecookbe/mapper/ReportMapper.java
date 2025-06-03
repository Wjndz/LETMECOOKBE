// src/main/java/com/example/letmecookbe/mapper/ReportMapper.java
package com.example.letmecookbe.mapper;

import com.example.letmecookbe.dto.request.ReportRequest;
import com.example.letmecookbe.dto.request.ReportStatusUpdateRequest;
import com.example.letmecookbe.dto.response.ReportResponse;
import com.example.letmecookbe.entity.Account; // Cần import Account entity
import com.example.letmecookbe.entity.Report;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface ReportMapper {

    // --- Ánh xạ từ ReportRequest (DTO) sang Report (Entity) ---
    // Chỉ rõ các trường cần bỏ qua vì chúng được xử lý riêng (tạo tự động / set trong service)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "reporterAccount", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    // Các trường adminResponse, resolvedByAdmin, resolvedAt không có trong ReportRequest,
    // MapStruct sẽ tự động bỏ qua chúng (giữ nguyên giá trị mặc định/null)
    Report toEntity(ReportRequest request);

    // --- Ánh xạ từ Report (Entity) sang ReportResponse (DTO) ---
    // Ánh xạ các trường lồng nhau từ Account sang các trường phẳng trong DTO
    @Mapping(source = "reporterAccount.username", target = "reporterAccountUsername")
    @Mapping(source = "reporterAccount.email", target = "reporterAccountEmail")
    @Mapping(source = "resolvedByAdmin.username", target = "resolvedByAdminUsername")
    @Mapping(source = "resolvedByAdmin.email", target = "resolvedByAdminEmail")
    ReportResponse toDto(Report report);

    // --- Cập nhật Report (Entity) từ ReportStatusUpdateRequest (DTO) ---
    // Chỉ rõ các trường cần bỏ qua vì chúng không được phép thay đổi qua DTO này
    // (id, reporterAccount, reportType, reportedItemId, reason, createdAt)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "reporterAccount", ignore = true)
    @Mapping(target = "reportType", ignore = true)
    @Mapping(target = "reportedItemId", ignore = true)
    @Mapping(target = "reason", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    // Các trường resolvedByAdmin, resolvedAt sẽ được set thủ công trong Service
    // khi cập nhật trạng thái (để biết admin nào xử lý, thời gian xử lý)
    @Mapping(target = "resolvedByAdmin", ignore = true)
    @Mapping(target = "resolvedAt", ignore = true)
    @Mapping(source = "newStatus", target = "status") // Ánh xạ trạng thái mới
    void updateReportFromStatusRequest(ReportStatusUpdateRequest request, @MappingTarget Report report);
}