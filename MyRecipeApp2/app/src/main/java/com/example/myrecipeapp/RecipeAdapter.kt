package com.example.myrecipeapp

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.example.myrecipeapp.database.RecipeTable
import com.bumptech.glide.Glide
import com.example.myrecipeapp.viewModels.RecipeViewModel

class RecipeAdapter(
    private val navController: NavController,
    private var recipes: List<RecipeTable>,
    private val viewModel: RecipeViewModel
) : RecyclerView.Adapter<RecipeAdapter.MyViewHolder>() {

    private var filteredRecipes: List<RecipeTable> = recipes // Initially, show all recipes

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.recipe_card_item, parent, false)
        return MyViewHolder(v, navController)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val recipe = filteredRecipes[position]
        holder.bind(recipe)
    }

    override fun getItemCount(): Int {
        return filteredRecipes.size
    }

    inner class MyViewHolder(itemView: View, private val navController: NavController) : RecyclerView.ViewHolder(itemView) {
        private val itemImage: ImageView = itemView.findViewById(R.id.home_recipe_image)
        private val itemTitle: TextView = itemView.findViewById(R.id.home_recipe_name)
        private val itemStepCount: TextView = itemView.findViewById(R.id.steps_number)
        private val itemIngCount: TextView = itemView.findViewById(R.id.ingredient_number)

        fun bind(recipe: RecipeTable) {
            val textTitle = if (recipe.name.length > 21) {
                recipe.name.substring(0, 21) + "..."
            } else {
                recipe.name
            }
            itemTitle.text = textTitle
            itemStepCount.text = "Steps: ${recipe.stepCount}"
            itemIngCount.text = "Ingredients: ${recipe.ingredientCount}"

            if (recipe.image.startsWith("android.resource://")) {
                val resId = recipe.image.substringAfterLast("/").toInt()
                itemImage.setImageResource(resId)
            } else {
                val uri = Uri.parse(recipe.image)
                Glide.with(itemImage.context)
                    .load(uri)
                    .into(itemImage)
            }

            itemView.setOnClickListener {
                val action = HomeDirections.actionHome2ToRecipeInfo(recipe.recipeId)
                navController.navigate(action)
            }

        }
    }

    fun updateData(newRecipes: List<RecipeTable>) {
        recipes = newRecipes
        filter("") // Refresh the filtered list
    }

    fun removeItem(position: Int) {
        recipes = recipes.filterIndexed { index, _ -> index != position }
        filter("") // Refresh the filtered list
    }

    fun filter(query: String) {
        filteredRecipes = if (query.isEmpty()) {
            recipes // Show all recipes if query is empty
        } else {
            recipes.filter { it.name.contains(query, ignoreCase = true) } // Filter recipes by name
        }
        notifyDataSetChanged()
    }
}
