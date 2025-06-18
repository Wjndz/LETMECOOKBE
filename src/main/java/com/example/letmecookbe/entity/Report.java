package com.example.letmecookbe.entity;
import com.example.letmecookbe.enums.ReportSeverity;
import com.example.letmecookbe.enums.ReportStatus;
import com.example.letmecookbe.enums.ReportType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import java.time.LocalDateTime;

@Entity
@Table(name = "reports")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    Account reporterAccount;
    @Enumerated(EnumType.STRING)
    ReportType reportType;
    String reportedItemId;
    String reason;
    String description;
    String evidenceImageUrl;
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    ReportStatus status;
    LocalDateTime createdAt;
    String adminResponse;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resolved_by_admin_id")
    Account resolvedByAdmin;
    LocalDateTime resolvedAt;
    @Enumerated(EnumType.STRING) // Lưu dưới dạng chuỗi trong DB
    private ReportSeverity severity;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reported_account_id") // ID của người bị báo cáo
    private Account reportedAccount; // Trường mới
    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}