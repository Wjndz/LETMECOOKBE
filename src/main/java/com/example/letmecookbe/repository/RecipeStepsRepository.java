package com.example.letmecookbe.repository;

import com.example.letmecookbe.entity.RecipeSteps;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecipeStepsRepository extends JpaRepository<RecipeSteps, String> {
}
