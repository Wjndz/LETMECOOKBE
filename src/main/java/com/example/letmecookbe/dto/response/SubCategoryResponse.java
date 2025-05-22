package com.example.letmecookbe.dto.response;

import com.example.letmecookbe.entity.MainCategory;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SubCategoryResponse {
    String subCategoryName;
    String subCategoryImg;
    String categoryId;
}
