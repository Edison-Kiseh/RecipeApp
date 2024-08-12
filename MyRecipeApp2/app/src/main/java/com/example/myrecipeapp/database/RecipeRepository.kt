package com.example.myrecipeapp.database

import android.util.Log

class RecipeRepository(private val recipeDao: RecipeDao, private val stepDao: StepDao, private val ingredientDao: IngredientDao) {

    suspend fun insertRecipe(recipe: RecipeTable) {
        recipeDao.insertRecipe(recipe)
    }

    fun getAllRecipes(): List<RecipeTable>{
        Log.d("Repository", "Fetch recipes function called")
        return recipeDao.getAllRecipes()
    }

    fun getRecipeById(recipeId: Long): RecipeTable {
        return recipeDao.getRecipeById(recipeId)
    }

    fun getSteps(recipeId: Long): List<String> {
        return stepDao.getSteps(recipeId)
    }

    fun insertStep(step: StepsTable) {
        stepDao.insertStep(step)
    }

    fun insertIngredient(ingredient: IngredientsTable) {
        ingredientDao.insertIngredient(ingredient)
    }

    fun getIngredients(recipeId: Long) : List<String>{
        return ingredientDao.getAllIngredients(recipeId)
    }

    //deletion
    fun deleteRecipe(recipeId: Long){
        recipeDao.deleteRecipeWithContent(recipeId)
    }

    fun deleteSteps(recipeId: Long){
        stepDao.deleteSteps(recipeId)
    }

    fun deleteIngredients(recipeId: Long){
        ingredientDao.deleteIngredients(recipeId)
    }

    fun insertRecipeWithIngredients(recipe: RecipeTable, ingredients: List<IngredientsTable>, steps: List<StepsTable>) {
        recipeDao.insertRecipeWithDetails(recipe, ingredients, steps)
    }

    fun updateRecipe(recipe: RecipeTable, ingredients: List<IngredientsTable>, steps: List<StepsTable>){
        Log.e("Update function", "Update called in repository")
        recipeDao.updateRecipe(recipe, ingredients, steps)
    }

    fun getNumberOfSteps(recipeId: Long): Int {
        val number = stepDao.getNumberOfSteps(recipeId)

        return number
    }
}
