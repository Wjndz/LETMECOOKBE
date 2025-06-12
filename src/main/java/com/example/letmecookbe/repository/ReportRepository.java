// src/main/java/com/example/letmecookbe/repository/ReportRepository.java
package com.example.letmecookbe.repository;

import com.example.letmecookbe.entity.Report; // Cần import entity Report
import com.example.letmecookbe.enums.ReportStatus; // Cần import enum ReportStatus
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository // Đảm bảo annotation này để Spring phát hiện đây là một Repository
public interface ReportRepository extends JpaRepository<Report, String> {
    // JpaRepository<T, ID>
    // T: Kiểu Entity (Report)
    // ID: Kiểu khóa chính của Entity (String vì Report dùng UUID)

    // Phương thức tìm kiếm tùy chỉnh (đã được sử dụng trong ReportService)
    List<Report> findByStatus(ReportStatus status);
}