package com.example.myrecipeapp.database

import android.util.Log
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update

@Dao
interface RecipeDao {
    @Insert
    fun insertRecipe(recipe: RecipeTable) : Long

    @Insert
    fun insertIngredient(ingredient: IngredientsTable)

    @Insert
    fun insertStep(step: StepsTable)

    @Query("SELECT * FROM recipe_table ORDER BY recipeId DESC")
    fun getAllRecipes(): List<RecipeTable>

    @Query("SELECT name FROM recipe_table")
    fun getAllRecipesNames(): List<String>

    @Query("SELECT image FROM recipe_table")
    fun getAllRecipesImages(): List<String>

    @Query("SELECT * FROM recipe_table WHERE recipeId = :recipeId")
    fun getRecipeById(recipeId: Long): RecipeTable

    @Query("DELETE FROM recipe_table WHERE recipeId = :recipeId")
    fun deleteRecipe(recipeId: Long)

    @Query("DELETE FROM ingredients_table WHERE recipe_id = :recipeId")
    fun deleteIngredient(recipeId: Long)

    @Query("DELETE FROM steps_table WHERE recipe_id = :recipeId")
    fun deleteSteps(recipeId: Long)

    @Update
    fun update(recipe: RecipeTable)

    @Transaction
    fun deleteRecipeWithContent(recipeId: Long){
        deleteRecipe(recipeId)
        deleteIngredient(recipeId)
        deleteSteps(recipeId)
    }

    @Transaction
    fun updateRecipe(recipe: RecipeTable, ingredients: List<IngredientsTable>, steps: List<StepsTable>){
        Log.e("Updating", "Recipe being updated in the transaction")

        update(recipe)
        deleteIngredient(recipe.recipeId)
        deleteSteps(recipe.recipeId)

        for (ingredient in ingredients) {
            ingredient.recipeId = recipe.recipeId
            insertIngredient(ingredient)
        }
        for (step in steps) {
            step.recipeId = recipe.recipeId
            insertStep(step)
        }
    }


    @Transaction
    fun insertRecipeWithDetails(
        recipe: RecipeTable,
        ingredients: List<IngredientsTable>,
        steps: List<StepsTable>,
    ) {
        val recipeId: Long = insertRecipe(recipe)
        for (ingredient in ingredients) {
            ingredient.recipeId = recipeId
            insertIngredient(ingredient)
        }
        for (step in steps) {
            step.recipeId = recipeId
            insertStep(step)
        }
    }
}
