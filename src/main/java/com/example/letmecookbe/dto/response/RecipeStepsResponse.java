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
public class RecipeStepsResponse {
    int step;
    String id;
    String description;
    String waitingTime;
    String recipeId;
    String recipeName;
    String recipeStepImage;
}
