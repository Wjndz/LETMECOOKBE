package com.example.letmecookbe.repository;

import com.example.letmecookbe.entity.RecipeIngredients;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecipeIngredientsRepository extends JpaRepository<RecipeIngredients, String> {
}
