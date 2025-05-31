package com.example.letmecookbe.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RecipeUpdateRequest {
    String title;
    String description;
    String image;
    String difficulty;
    String cookingTime;
    String subCategoryId;
}
