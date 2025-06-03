package com.example.letmecookbe.dto.request;

import com.example.letmecookbe.enums.DietType;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class UserInfoCreationRequest {
    @NotBlank(message = "Giới tính là bắt buộc")
    String sex;

    @Min(value = 1, message = "Chiều cao phải là số dương")
    int height;

    @Min(value = 1, message = "Cân nặng phải là số dương")
    int weight;

    String healthCondition;

    @Min(value = 1, message = "Tuổi phải là số dương")
    int age;

    @PastOrPresent(message = "Ngày sinh không được trong tương lai")
    LocalDate dob;

    @NotEmpty(message = "Phải chọn ít nhất một loại chế độ ăn")
    List<DietType> dietTypes;
}