package com.example.myrecipeapp.viewModels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myrecipeapp.database.IngredientDao
import com.example.myrecipeapp.database.IngredientsTable
import com.example.myrecipeapp.database.RecipeDao
import com.example.myrecipeapp.database.RecipeRepository
import com.example.myrecipeapp.database.RecipeTable
import com.example.myrecipeapp.database.StepDao
import com.example.myrecipeapp.database.StepsTable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RecipeAddViewModel(private val recipeDao: RecipeDao, private val stepDao: StepDao, private val ingredientDao: IngredientDao) : ViewModel() {

    private val repository: RecipeRepository = RecipeRepository(recipeDao, stepDao, ingredientDao)

    fun validateIngredientName(ingredientName: String): Boolean {
        return ingredientName.isNotEmpty()
    }

    fun validateStepDescription(stepDescription: String): Boolean {
        return stepDescription.isNotEmpty()
    }

    fun validateRecipeFields(recipeName: String, prepTime: String, description: String, ingredientsCount: Int, stepsCount: Int): Boolean {
        return recipeName.isNotEmpty() && prepTime.isNotEmpty() && description.isNotEmpty() && ingredientsCount > 0 && stepsCount > 0
    }

    fun insertRecipe(recipe: RecipeTable) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertRecipe(recipe)
        }
    }

    fun addRecipeWithIngredients(recipe: RecipeTable, ingredients: List<IngredientsTable>, steps: List<StepsTable>) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertRecipeWithIngredients(recipe, ingredients, steps)
        }
    }

    fun updateRecipeWithIngredients(recipe: RecipeTable, ingredients: List<IngredientsTable>, steps: List<StepsTable>) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateRecipe(recipe, ingredients, steps)
        }
    }

    fun insertStep(step: StepsTable) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertStep(step)
        }
    }

    companion object {
        fun create(recipeDao: RecipeDao, stepDao: StepDao, ingredientDao: IngredientDao): RecipeAddViewModel {
            return RecipeAddViewModel(recipeDao, stepDao, ingredientDao)
        }
    }
}