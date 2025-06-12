package com.example.letmecookbe.enums;

import lombok.Getter;

@Getter // Lombok annotation để tự động tạo getter
public enum Difficulty {
    EASY("Dễ"),         // Giá trị enum trong code và giá trị hiển thị/lưu trong DB
    MEDIUM("Trung bình"),
    HARD("Khó");

    private final String value; // Trường để lưu trữ giá trị tiếng Việt

    Difficulty(String value) {
        this.value = value;
    }
}

