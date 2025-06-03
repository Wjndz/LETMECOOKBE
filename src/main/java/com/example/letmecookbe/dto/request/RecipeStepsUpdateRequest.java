package com.example.letmecookbe.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RecipeStepsUpdateRequest {
    int step;
    String description;
    String recipeStepsImg;
    String WaitingTime;
}
