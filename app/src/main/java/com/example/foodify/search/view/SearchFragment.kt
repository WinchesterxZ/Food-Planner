package com.example.foodify.search.view

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.foodify.MainActivity
import com.example.foodify.MainActivityViewModel
import com.example.foodify.adapter.AreaSearchAdapter
import com.example.foodify.adapter.CategoriesSearchAdapter
import com.example.foodify.adapter.IngredientAdapter
import com.example.foodify.data.model.Area
import com.example.foodify.data.model.Category
import com.example.foodify.data.model.Ingredient
import com.example.foodify.databinding.FragmentSearchBinding
import com.example.foodify.search.viewmodel.SearchViewModel
import com.example.foodify.util.showErrorSnackBar
import com.example.foodify.util.showProgressDialog
import com.google.android.material.chip.Chip
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


class SearchFragment : Fragment() {
    private lateinit var _binding: FragmentSearchBinding
    private val binding get() = _binding
    private val viewModel: SearchViewModel by viewModel()
    private lateinit var categoriesAdapter: CategoriesSearchAdapter
    private lateinit var areasAdapter: AreaSearchAdapter
    private lateinit var ingredientsAdapter: IngredientAdapter
    private lateinit var pDialog: SweetAlertDialog
    private lateinit var searchAutoCompleteTextView: AutoCompleteTextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pDialog = showProgressDialog(requireContext())
        val activityBinding = (requireActivity() as MainActivity).binding
        searchAutoCompleteTextView = activityBinding.toolbar.searchAutoCompleteTextView
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        viewModel.getMealsCategories() // initially load categories
        observeSearchState()
        updateChipsItems()
        setSearchAutoCompleteTextView()
        observeFilteredData()
    }

    private fun setSearchAutoCompleteTextView() {
        searchAutoCompleteTextView.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.setSearchQuery(s.toString())
            }

            override fun afterTextChanged(s: android.text.Editable?) {}
        })
    }
    private fun observeFilteredData() {
        viewModel.filteredCategories.observe(viewLifecycleOwner) { loadCategories(it) }
        viewModel.filteredAreas.observe(viewLifecycleOwner) { loadAreas(it) }
        viewModel.filteredIngredients.observe(viewLifecycleOwner) { loadIngredients(it) }
    }


    private fun updateChipsItems() {
        for (i in 0 until binding.chipGroup.childCount) {
            val chip = binding.chipGroup.getChildAt(i) as Chip
            chip.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    when (chip.id) {
                        binding.chipCategory.id -> viewModel.getMealsCategories()
                        binding.chipArea.id -> viewModel.getMealsAreas()
                        binding.chipIngredient.id -> viewModel.getMealsIngredients()
                    }
                }
            }
        }
    }

    private fun observeSearchState() {
        viewModel.searchState.observe(viewLifecycleOwner) { state ->
            Log.d("zzzz", "observeSearchState:$state ")
            when (state) {
                is SearchState.Loading -> {
                    showLoading()
                }
                is SearchState.SuccessCategories -> {
                    loadCategories(state.categories)
                }
                is SearchState.SuccessAreas -> {
                    loadAreas(state.areas)
                }
                is SearchState.SuccessIngredients -> {
                    loadIngredients(state.ingredients)
                }
                is SearchState.Error -> {
                    when (state.type) {
                        SearchState.ErrorType.LoadError -> {
                            pDialog.dismiss()
                            // Handle error
                            showErrorMessage(state.message)
                        }
                    }
                }
            }
        }
    }

    private fun showErrorMessage(message: String) {
        showErrorSnackBar(requireView(), message)
    }

    private fun loadIngredients(ingredients: List<Ingredient>) {

        pDialog.dismiss()
        ingredientsAdapter = IngredientAdapter(){ingredient->
            val action = SearchFragmentDirections.actionSearchFragmentToSearchResultFragment(
                "ingredient",
                ingredient
            )
            findNavController().navigate(action)
        }
        ingredientsAdapter.submitList(ingredients)
        binding.recyclerView.adapter = ingredientsAdapter
    }

    private fun loadAreas(areas: List<Area>) {
        pDialog.dismiss()
        areasAdapter = AreaSearchAdapter(){area->
            val action = SearchFragmentDirections.actionSearchFragmentToSearchResultFragment(
                "area",
                area
            )
            findNavController().navigate(action)
        }
        areasAdapter.submitList(areas)
        binding.recyclerView.adapter = areasAdapter
    }

    private fun showLoading() {
        pDialog.show()
    }

    private fun loadCategories(categories: List<Category>) {
        pDialog.dismiss()
        categoriesAdapter = CategoriesSearchAdapter(){
            val action = SearchFragmentDirections.actionSearchFragmentToSearchResultFragment(
                "category",
                it
            )
            findNavController().navigate(action)
        }
        categoriesAdapter.submitList(categories)
        binding.recyclerView.adapter = categoriesAdapter

    }
}