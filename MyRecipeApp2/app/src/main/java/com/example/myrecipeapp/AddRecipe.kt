package com.example.myrecipeapp

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.myrecipeapp.database.IngredientsTable
import com.example.myrecipeapp.database.RecipeDatabase
import com.example.myrecipeapp.database.RecipeTable
import com.example.myrecipeapp.database.StepsTable
import com.example.myrecipeapp.databinding.FragmentAddRecipeBinding
import com.example.myrecipeapp.viewModels.RecipeAddViewModel

class AddRecipe : Fragment() {

    private lateinit var binding: FragmentAddRecipeBinding
    private var stepCount = 0
    private lateinit var viewModel: RecipeAddViewModel
    private var selectedImageUri: Uri? = null
    private val PICK_IMAGE_REQUEST = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddRecipeBinding.inflate(inflater, container, false)

        binding.saveIngredients.setBackgroundColor(resources.getColor(R.color.SeaGreen))
        binding.saveRecipe.setBackgroundColor(resources.getColor(R.color.SeaGreen))
        binding.saveSteps.setBackgroundColor(resources.getColor(R.color.SeaGreen))

        val recipeDao = RecipeDatabase.getInstance(requireContext()).recipeDao()
        val stepsDao = RecipeDatabase.getInstance(requireContext()).stepDao()
        val ingredientDao = RecipeDatabase.getInstance(requireContext()).ingredientDao()

        viewModel = RecipeAddViewModel.create(recipeDao, stepsDao, ingredientDao)

        binding.saveIngredients.setOnClickListener {
            addIngredient()
        }

        binding.saveSteps.setOnClickListener {
            addStep()
        }

        binding.saveRecipe.setOnClickListener {
            saveRecipe()
        }

        binding.foodImageContainer.setOnClickListener {
            openImagePicker()
        }

        return binding.root
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            selectedImageUri = data.data
            binding.foodImage.setImageURI(selectedImageUri)
            binding.clickToAddText.visibility = View.GONE
        }
    }

    private fun addIngredient() {
        val ingredientName = binding.newCheckbox.text.toString().trim()
        if (viewModel.validateIngredientName(ingredientName)) {
            val ingredientLayout = LinearLayout(requireContext()).apply {
                orientation = LinearLayout.HORIZONTAL
                setPadding(0, 4, 0, 16)
            }

            val ingredientTextView = TextView(requireContext()).apply {
                text = "• $ingredientName"
                setPadding(0, 4, 30, 0)
                setTextSize(16F)
            }

            val closeButton = ImageView(requireContext()).apply {
                setImageResource(R.drawable.x)
                layoutParams = LinearLayout.LayoutParams(48, 48)
                setOnClickListener {
                    binding.ingredientsContainer.removeView(ingredientLayout)
                }
            }

            ingredientLayout.addView(ingredientTextView)
            ingredientLayout.addView(closeButton)
            binding.ingredientsContainer.addView(ingredientLayout)
            binding.newCheckbox.text.clear()
        } else {
            Toast.makeText(requireContext(), "Please enter an ingredient name", Toast.LENGTH_SHORT).show()
        }
    }

    private fun addStep() {
        val stepDescription = binding.addStep.text.toString().trim()
        if (viewModel.validateStepDescription(stepDescription)) {
            stepCount++
            addStepLayout(stepCount, stepDescription)
            binding.addStep.text.clear()
        } else {
            Toast.makeText(requireContext(), "Please enter a step first", Toast.LENGTH_SHORT).show()
        }
    }

    private fun addStepLayout(stepNumber: Int, stepDescription: String) {
        val stepLayout = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(0, 4, 0, 16)
        }

        val stepTextView = TextView(requireContext()).apply {
            id = View.generateViewId()
            text = "Step $stepNumber: $stepDescription"
            setPadding(0, 4, 30, 0)
            setTextSize(16F)
        }

        val closeButton = ImageView(requireContext()).apply {
            setImageResource(R.drawable.x)
            layoutParams = LinearLayout.LayoutParams(48, 48)
            setOnClickListener {
                binding.stepsContainer.removeView(stepLayout)
                updateStepNumbers()
            }
        }

        stepLayout.addView(stepTextView)
        stepLayout.addView(closeButton)
        binding.stepsContainer.addView(stepLayout)
    }

    private fun updateStepNumbers() {
        for (i in 0 until binding.stepsContainer.childCount) {
            val stepLayout = binding.stepsContainer.getChildAt(i) as LinearLayout
            val stepTextView = stepLayout.getChildAt(0) as TextView
            val stepDescription = stepTextView.text.toString().substringAfter(": ")
            stepTextView.text = "Step ${i + 1}: $stepDescription"
        }
        stepCount = binding.stepsContainer.childCount
    }

    private fun saveRecipe() {
        val recipeName = binding.recipeNameField.text.toString().trim()
        val prepTime = binding.prepTimeField.text.toString().trim()
        val description = binding.descriptionField.text.toString().trim()

        val ingredientsCount = binding.ingredientsContainer.childCount
        val stepsCount = binding.stepsContainer.childCount

        if (viewModel.validateRecipeFields(recipeName, prepTime, description, ingredientsCount, stepsCount)) {
            val foodImage: String = selectedImageUri?.toString() ?: "android.resource://com.example.myrecipeapp/" + R.drawable.def

            val ingredients = mutableListOf<IngredientsTable>()
            for (i in 0 until ingredientsCount) {
                val ingredientLayout = binding.ingredientsContainer.getChildAt(i) as LinearLayout
                val ingredientTextView = ingredientLayout.getChildAt(0) as TextView
                val ingredientName = ingredientTextView.text.toString().trim().removePrefix("• ")
                ingredients.add(IngredientsTable(name = ingredientName, recipeId = 0))
            }

            val steps = mutableListOf<StepsTable>()
            for (i in 0 until stepsCount) {
                val stepLayout = binding.stepsContainer.getChildAt(i) as LinearLayout
                val stepTextView = stepLayout.getChildAt(0) as TextView
                val stepDescription = stepTextView.text.toString().trim().substringAfter(": ")
                steps.add(StepsTable(name = stepDescription, recipeId = 0))
            }

            val recipe = RecipeTable(
                name = recipeName,
                description = description,
                prepTime = prepTime,
                image = foodImage,
                stepCount = steps.size,
                ingredientCount = ingredients.size
            )

            viewModel.addRecipeWithIngredients(recipe, ingredients, steps)

            Toast.makeText(requireContext(), "Recipe saved successfully!", Toast.LENGTH_SHORT).show()
            val navController = findNavController()
            navController.navigate(R.id.action_addRecipe_to_home2)
        } else {
            Toast.makeText(requireContext(), "Please fill out all fields", Toast.LENGTH_SHORT).show()
        }
    }
}
