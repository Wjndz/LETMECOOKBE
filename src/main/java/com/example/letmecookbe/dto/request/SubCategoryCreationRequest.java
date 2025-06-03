package com.example.letmecookbe.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.validator.constraints.NotBlank;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SubCategoryCreationRequest {
    @NotBlank(message = "NOT_NULL")
    String subCategoryName;
    String subCategoryImg;
}
