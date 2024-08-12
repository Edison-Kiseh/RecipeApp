package com.example.myrecipeapp

import android.app.AlertDialog
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.myrecipeapp.database.RecipeDatabase
import com.example.myrecipeapp.database.RecipeRepository
import com.example.myrecipeapp.databinding.FragmentRecipeInfoBinding
import com.example.myrecipeapp.viewModels.RecipeViewModel
import com.example.myrecipeapp.viewModels.RecipeViewModelFactory

class RecipeInfo : Fragment() {

    private lateinit var binding: FragmentRecipeInfoBinding
    private lateinit var viewModel: RecipeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val recipeDao = RecipeDatabase.getInstance(requireContext()).recipeDao()
        val stepsDao = RecipeDatabase.getInstance(requireContext()).stepDao()
        val ingredientDao = RecipeDatabase.getInstance(requireContext()).ingredientDao()

        val repository = RecipeRepository(recipeDao, stepsDao, ingredientDao)
        val factory = RecipeViewModelFactory(repository)

        viewModel = ViewModelProvider(this, factory).get(RecipeViewModel::class.java)

        binding = FragmentRecipeInfoBinding.inflate(inflater, container, false)

        // Fetching arguments from the home
        val args = RecipeInfoArgs.fromBundle(requireArguments())
        val recipeId = args.recipeId

        viewModel.recipe.observe(viewLifecycleOwner, Observer { recipe ->
            binding.textField.text = recipe.description
            binding.time.text = manageTime(recipe.prepTime)

            // Setting the recipe image
            if (recipe.image.startsWith("android.resource://")) {
                val resId = recipe.image.substringAfterLast("/").toInt()
                binding.foodImage.setImageResource(resId)
            } else {
                val uri = Uri.parse(recipe.image)
                Glide.with(binding.foodImage.context)
                    .load(uri)
                    .into(binding.foodImage)
            }

            binding.foodName.text = recipe.name
        })

        // Fetching all details based on the recipeId
        viewModel.fetchSingleRecipe(recipeId)
        viewModel.fetchIngredients(recipeId)
        viewModel.fetchSteps(recipeId)

        // Fetching recipe ingredients
        binding.ingredients.setOnClickListener {
            viewModel.ingredients.observe(viewLifecycleOwner, Observer { ingredientsList ->
                binding.viewContainer.removeAllViews()

                if (ingredientsList.isNotEmpty()) {
                    for (ingredient in ingredientsList) {
                        val dotTextView = TextView(requireContext()).apply {
                            text = "\u2022  $ingredient"
                            setPadding(16, 8, 8, 8)
                            setTextSize(18F)
                        }
                        binding.viewContainer.addView(dotTextView)
                    }
                } else {
                    binding.textField.visibility = View.VISIBLE
                    binding.textField.text = "No ingredients to display"
                }

                // Scroll to the bottom of ScrollView
//                binding.scrollView.post {
//                    binding.scrollView.fullScroll(View.FOCUS_DOWN)
//                }

                // Changing the color of the buttons and their text
                updateColours(false)
            })
        }

        // Fetching recipe steps
        binding.stepsButton.setOnClickListener {
            viewModel.steps.observe(viewLifecycleOwner, Observer { stepsList ->
                binding.viewContainer.removeAllViews()

                if (stepsList.isNotEmpty()) {
                    for ((index, step) in stepsList.withIndex()) {
                        val stepTextView = TextView(requireContext()).apply {
                            text = "Step ${index + 1}: $step"
                            setPadding(16, 8, 8, 8)
                            setTextSize(18F)
                        }
                        binding.viewContainer.addView(stepTextView)
                    }
                } else {
                    binding.textField.visibility = View.VISIBLE
                    binding.textField.text = "No steps to display"
                }

                // Scroll to the bottom of ScrollView
//                binding.scrollView.post {
//                    binding.scrollView.fullScroll(View.FOCUS_DOWN)
//                }

                // Changing the color of the buttons and their text
                updateColours(true)
            })
        }

        // Show popup menu on foodImage long click
        binding.options.setOnClickListener() {
            showPopupMenu(it, recipeId)
            true
        }

        return binding.root
    }

    private fun showPopupMenu(view: View, recipeId: Long) {
        val popup = PopupMenu(requireContext(), view)
        val inflater: MenuInflater = popup.menuInflater
        inflater.inflate(R.menu.context_menu, popup.menu)
        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_edit -> {
                    editRecipe(recipeId)
                    true
                }
                R.id.action_delete -> {
                    deleteRecipe(recipeId)
                    true
                }
                else -> false
            }
        }
        popup.show()
    }

    private fun manageTime(prepTime: String): String {
        val time = prepTime.toInt()

        return if (time < 60) {
            "$time mins"
        } else {
            val hours = time / 60
            val minutes = time % 60
            "$hours hr $minutes mins"
        }
    }

    private fun deleteRecipe(recipeId: Long) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Delete Recipe")
        builder.setMessage("Are you sure you want to delete this recipe?")

        // If the user clicks yes
        builder.setPositiveButton("Yes") { dialog, _ ->
            viewModel.deleteRecipe(recipeId)

            Toast.makeText(requireContext(), "The recipe has been deleted", Toast.LENGTH_SHORT).show()
            val navController = findNavController()
            navController.navigate(R.id.action_recipeInfo_to_home2)
            dialog.dismiss()
        }

        // If the user clicks no
        builder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
        }

        // Create and show the dialog
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun editRecipe(recipeId: Long) {
        val navController = findNavController()
        val action = RecipeInfoDirections.actionRecipeInfoToEditRecipe(recipeId)
        navController.navigate(action)
    }

    private fun updateColours(change: Boolean){
        if(change){
            binding.stepsButton.setTextColor(resources.getColor(R.color.black))
            binding.stepsButton.setBackgroundColor(resources.getColor(R.color.VeryLightGreen))

            binding.ingredients.setTextColor(resources.getColor(R.color.white))
            binding.ingredients.setBackgroundColor(resources.getColor(R.color.SeaGreen))
        }else{
            binding.ingredients.setTextColor(resources.getColor(R.color.black))
            binding.ingredients.setBackgroundColor(resources.getColor(R.color.VeryLightGreen))

            binding.stepsButton.setTextColor(resources.getColor(R.color.white))
            binding.stepsButton.setBackgroundColor(resources.getColor(R.color.SeaGreen))
        }
    }
}
