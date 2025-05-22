package com.example.letmecookbe.repository;

import com.example.letmecookbe.entity.Ingredients;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IngredientsRepository extends JpaRepository<Ingredients, String> {
    boolean existsByIngredientName(String ingredientName);
}
