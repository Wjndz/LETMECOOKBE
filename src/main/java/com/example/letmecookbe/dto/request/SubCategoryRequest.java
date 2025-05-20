package com.example.letmecookbe.dto.request;

import com.example.letmecookbe.entity.MainCategory;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.validator.constraints.NotBlank;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SubCategoryRequest {
    @NotBlank(message = "NOT_NULL")
    String subCategoryName;
    String subCategoryImg;
    @NotBlank(message = "NOT_NULL")
    String categoryId;
}
