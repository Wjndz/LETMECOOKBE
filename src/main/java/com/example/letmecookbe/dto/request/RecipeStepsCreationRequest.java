package com.example.letmecookbe.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.apache.logging.log4j.message.Message;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RecipeStepsCreationRequest {
    @NotBlank(message = "NOT_NULL")
    int step;

    @NotBlank(message = "NOT_NULL")
    String recipeId;

    @NotBlank(message = "NOT_NULL")
    @Size(min = 2, message ="RECIPE_STEPS_DESCRIPTION_INVALID")
    String description;

    @NotBlank(message = "NOT_NULL")
    String waitingTime;

    String recipeImage;

}
