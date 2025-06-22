package com.example.letmecookbe.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RecipeResponse {
    String id;
    String title;
    String description;
    String cookingTime;
    int totalLikes;
    String difficulty;
    String status;
    String image;
    String subCategoryId;
    String subCategoryName;
    String accountId;
    String accountName;
    LocalDateTime createAt;
}
