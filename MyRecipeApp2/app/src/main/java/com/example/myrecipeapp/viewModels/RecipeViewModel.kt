package com.example.myrecipeapp.viewModels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myrecipeapp.database.IngredientsTable
import com.example.myrecipeapp.database.RecipeRepository
import com.example.myrecipeapp.database.RecipeTable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RecipeViewModel(private val repository: RecipeRepository) : ViewModel() {

    private val _recipes = MutableLiveData<List<RecipeTable>>()
    val recipes: LiveData<List<RecipeTable>> get() = _recipes

    fun fetchRecipes() {
        viewModelScope.launch(Dispatchers.IO) {
            Log.d("ViewModel", "Fetch recipes function called")
            val recipeList = repository.getAllRecipes()
            _recipes.postValue(recipeList)
        }
    }

    private val _recipe = MutableLiveData<RecipeTable>()
    val recipe: LiveData<RecipeTable> get() = _recipe

    fun fetchSingleRecipe(recipeId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val singleRecipe = repository.getRecipeById(recipeId)
            _recipe.postValue(singleRecipe)
        }
    }

    private val _ingredients = MutableLiveData<List<String>>()
    val ingredients: LiveData<List<String>> get() = _ingredients

    fun fetchIngredients(recipeId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val ingredientsList = repository.getIngredients(recipeId)
            _ingredients.postValue(ingredientsList)
        }
    }

    private val _steps = MutableLiveData<List<String>>()
    val steps: LiveData<List<String>> get() = _steps

    var stepsCount = 0

    fun fetchSteps(recipeId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val stepsList = repository.getSteps(recipeId)
            _steps.postValue(stepsList)
            stepsCount = stepsList.size
        }
    }


    fun deleteRecipe(recipeId: Long){
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteRecipe(recipeId)
        }
    }
}

