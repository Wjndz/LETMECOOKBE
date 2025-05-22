package com.example.letmecookbe.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class IngredientsCreationRequest {
    @NotBlank(message = "NOT_NULL")
    String ingredientName;

    @NotBlank(message = "NOT_NULL")
    String caloriesPerUnit;
}
