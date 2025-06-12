package com.example.letmecookbe.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RecipeCreationRequest {
    @NotBlank(message = "NOT_NULL")
    @Size(min = 2, message ="RECIPE_TITLE_INVALID")
    String title;

    @NotBlank(message = "NOT_NULL")
    @Size(min = 2, message ="RECIPE_DESCRIPTION_INVALID")
    String description;

//    String img;

    @NotBlank(message = "NOT_NULL")
    String difficulty;

    @NotBlank(message = "NOT_NULL")
    String cookingTime;

    String status;
//    @NotBlank(message = "NOT_NULL")
//    String subCategoryId;
//
//    @NotBlank(message = "NOT_NULL")
//    String accountId;
}
