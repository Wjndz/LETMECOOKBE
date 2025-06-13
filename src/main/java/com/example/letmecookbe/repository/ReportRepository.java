// src/main/java/com/example/letmecookbe/repository/ReportRepository.java
package com.example.letmecookbe.repository;

import com.example.letmecookbe.entity.Report;
import com.example.letmecookbe.enums.ReportStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report, String> {

    // PHƯƠNG THỨC NÀY PHẢI NHẬN CẢ ReportStatus VÀ Pageable
    Page<Report> findByStatus(ReportStatus status, Pageable pageable);
}