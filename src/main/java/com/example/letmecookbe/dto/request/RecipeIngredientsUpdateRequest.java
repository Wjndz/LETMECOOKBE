package com.example.letmecookbe.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RecipeIngredientsUpdateRequest {
    @NotBlank(message = "NOT_NULL")
    String ingredientId;
    int Quantity;
}
