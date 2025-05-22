package com.example.letmecookbe.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Ingredients {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;
    String ingredientName;
    String caloriesPerUnit;
}
