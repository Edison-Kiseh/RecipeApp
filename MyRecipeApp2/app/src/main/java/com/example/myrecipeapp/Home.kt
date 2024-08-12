package com.example.myrecipeapp

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myrecipeapp.database.RecipeDatabase
import com.example.myrecipeapp.database.RecipeRepository
import com.example.myrecipeapp.databinding.FragmentHomeBinding
import com.example.myrecipeapp.viewModels.RecipeViewModel
import com.example.myrecipeapp.viewModels.RecipeViewModelFactory

class Home : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewModel: RecipeViewModel
    private lateinit var adapter: RecipeAdapter

    @SuppressLint("ResourceAsColor")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        val recipeDao = RecipeDatabase.getInstance(requireContext()).recipeDao()
        val stepsDao = RecipeDatabase.getInstance(requireContext()).stepDao()
        val ingredientDao = RecipeDatabase.getInstance(requireContext()).ingredientDao()

        val repository = RecipeRepository(recipeDao, stepsDao, ingredientDao)
        val factory = RecipeViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory).get(RecipeViewModel::class.java)

        recyclerView = binding.recycleView
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)

        val navController = findNavController()
        adapter = RecipeAdapter(navController, listOf(), viewModel)
        recyclerView.adapter = adapter

        viewModel.recipes.observe(viewLifecycleOwner, Observer { recipes ->
            if(recipes.isEmpty()){
                binding.emptyMessageText.visibility = View.VISIBLE
                binding.noodles.visibility = View.VISIBLE
            }
            else{
                binding.emptyMessageText.visibility = View.GONE
                binding.noodles.visibility = View.GONE
                adapter.updateData(recipes)
            }
        })

        //fetching the recipes
        viewModel.fetchRecipes()

        binding.fab.setOnClickListener {
            navController.navigate(R.id.action_home2_to_addRecipe)
        }

        binding.searchBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                TODO("Not yet implemented")
                //i haven't and won't implement it because I don't actually need this functionality in project
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Handle the text changes in the search query
                newText?.let { adapter.filter(it) } // Update the adapter's filter with the new text
                return true
            }
        })

        return binding.root
    }
}
