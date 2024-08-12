package com.example.myrecipeapp.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface IngredientDao {
    @Insert
    fun insertIngredient(ingredient: IngredientsTable)

    @Query("SELECT name FROM ingredients_table WHERE recipe_id = :recipeId")
    fun getAllIngredients(recipeId: Long): List<String>

    @Query("DELETE FROM ingredients_table WHERE recipe_id = :recipeId")
    fun deleteIngredients(recipeId: Long)
}