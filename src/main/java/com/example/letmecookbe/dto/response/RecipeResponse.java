package com.example.letmecookbe.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

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
    String totalLikes;
    String difficulty;
    String status;
    String image;
    String subCategoryId;
    String accountId;
}
