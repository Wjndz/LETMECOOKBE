package com.example.letmecookbe.repository;

import com.example.letmecookbe.entity.RecipeSteps;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecipeStepsRepository extends JpaRepository<RecipeSteps, String> {
}
