// src/main/java/com/example/letmecookbe/enums/ReportStatus.java
package com.example.letmecookbe.enums;

import lombok.Getter;

@Getter
public enum ReportStatus {
    PENDING("Pending"),     // Đang chờ xem xét
    RESOLVED("Resolved"),   // Đã giải quyết / xử lý
    REJECTED("Rejected");   // Đã từ chối

    private final String value;

    ReportStatus(String value) {
        this.value = value;
    }

    // Phương thức tĩnh để chuyển đổi từ String sang Enum
    public static ReportStatus fromValue(String text) {
        for (ReportStatus b : ReportStatus.values()) {
            if (String.valueOf(b.value).equalsIgnoreCase(text)) {
                return b;
            }
        }
        throw new IllegalArgumentException("No enum constant with value " + text);
    }
}