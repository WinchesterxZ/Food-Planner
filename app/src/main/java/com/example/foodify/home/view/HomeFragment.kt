package com.example.foodify.home.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.EditorInfo.*
import android.view.inputmethod.InputMethodManager
import android.widget.AutoCompleteTextView
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import cn.pedant.SweetAlert.SweetAlertDialog
import coil.load
import com.example.foodify.MainActivity
import com.example.foodify.MainActivityViewModel
import com.example.foodify.adapter.CategoryAdapter
import com.example.foodify.adapter.MealsAdapter
import com.example.foodify.authentication.ui.AuthActivity
import com.example.foodify.data.model.Category
import com.example.foodify.data.model.MealPreview
import com.example.foodify.databinding.FragmentHomeBinding
import com.example.foodify.home.viewmodel.HomeViewModel
import com.example.foodify.util.showErrorSnackBar
import com.example.foodify.util.showLoginDialog
import com.example.foodify.util.showProgressDialog
import com.example.foodify.util.showSuccessSnackBar
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeFragment : Fragment(), OnItemClickListener {
    private lateinit var _binding: FragmentHomeBinding
    private val binding get() = _binding
    private val viewModel: HomeViewModel by viewModel()
    private lateinit var categoriesAdapter: CategoryAdapter
    private lateinit var mealsAdapter: MealsAdapter
    private lateinit var pDialog: SweetAlertDialog
    private lateinit var searchAutoCompleteTextView: AutoCompleteTextView
    private lateinit var hint: String
    private val mainViewModel: MainActivityViewModel by activityViewModel()
    private lateinit var userId: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userId = mainViewModel.getCurrentUser()?.uid ?: ""
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.categoriesRecyclerView.layoutManager =
            LinearLayoutManager(binding.root.context, LinearLayoutManager.HORIZONTAL, false)
        binding.popularMealsRecyclerView.layoutManager = GridLayoutManager(binding.root.context, 2)
        pDialog = showProgressDialog(binding.root.context)
        val activityBinding = (requireActivity() as MainActivity).binding
        searchAutoCompleteTextView = activityBinding.toolbar.searchAutoCompleteTextView
        hint = searchAutoCompleteTextView.hint.toString()
        searchAutoCompleteTextView.hint = "Search Meal By Name"
        viewModel.loadHomeData()
        observeHomeData()
    }

    override fun onResume() {
        super.onResume()
        setSearchAutoCompleteTextView()
    }

    private fun setSearchAutoCompleteTextView() {
        // Remove any previous listener before setting a new one
        searchAutoCompleteTextView.setOnEditorActionListener(null)

        searchAutoCompleteTextView.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val query = searchAutoCompleteTextView.text.toString().trim()

                if (query.isNotEmpty() && isAdded) { // Check if fragment is still attached
                    val action = HomeFragmentDirections.actionHomeFragmentToSearchResultFragment(
                        "name", query
                    )
                    findNavController().navigate(action)
                }

                // Hide the keyboard after search
                searchAutoCompleteTextView.clearFocus()
                val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(searchAutoCompleteTextView.windowToken, 0)

                true
            } else {
                false
            }
        }
    }


    private fun observeHomeData() {
        viewModel.mealState.observe(viewLifecycleOwner) { state ->
            Log.d("a3a3a3a3", "observeHomeData: $state")
            when (state) {
                is MealState.Loading -> showLoading()
                is MealState.Success -> {
                    showCategories(state.categories)
                    showMeals(state.meals)
                    showRandomMeal(state.randomMeal)
                }

                is MealState.Message -> handleMessageState(state)
                is MealState.Error -> showError(state)
                else -> Unit
            }
        }
    }

    private fun handleMessageState(state: MealState.Message) {
        when (state.action) {
            MealState.Action.ADDED_TO_FAVORITES -> showSuccessSnackBar(requireView(), state.message)
            MealState.Action.REMOVED_FROM_FAVORITES -> showSuccessSnackBar(
                requireView(),
                state.message
            )
        }
        viewModel.resetState()
    }

    private fun showError(state: MealState.Error) {
        pDialog.dismiss()
        when (state.type) {
            MealState.ErrorType.LoadError -> {
                showErrorSnackBar(requireView(), state.message)
            }

            MealState.ErrorType.AddToFavoriteError -> showErrorSnackBar(
                requireView(),
                state.message
            )

            MealState.ErrorType.DeleteError -> showErrorSnackBar(requireView(), state.message)

        }
    }

    private fun showCategories(categories: List<Category>) {
        pDialog.dismiss()
        categoriesAdapter = CategoryAdapter(categories) { meal ->
            val action = HomeFragmentDirections.actionHomeFragmentToSearchResultFragment(
                "category",
                meal.strCategory
            )
            findNavController().navigate(action)
        }
        binding.categoriesRecyclerView.adapter = categoriesAdapter

    }

    private fun showMeals(meals: List<MealPreview>) {
        pDialog.dismiss()
        mealsAdapter = MealsAdapter(this@HomeFragment) { id ->
            val action = HomeFragmentDirections.actionHomeFragment2ToMealDetailsFragment(id)
            findNavController().navigate(action)
        }
        mealsAdapter.submitList(meals)
        binding.popularMealsRecyclerView.adapter = mealsAdapter

    }

    private fun showLoading() {
        binding.randomMealLayout.progressBar.visibility = View.VISIBLE
        pDialog.show()

    }

    private fun showRandomMeal(meal: MealPreview) {
        pDialog.dismiss()
        binding.randomMealLayout.mealName.text = meal.strMeal
        binding.randomMealLayout.mealImage.load(meal.strMealThumb) {
            listener(
                onSuccess = { _, _ ->
                    run {
                        binding.randomMealLayout.progressBar.visibility = View.GONE
                        Log.d("a3a3a", "showRandomMeal: zzzzzzzz")
                    }
                },
                onError = { _, _ -> binding.randomMealLayout.progressBar.visibility = View.GONE }
            )
        }
        binding.randomMealLayout.bookmark.setOnClickListener {
            if (userId.isNotEmpty()) {
                viewModel.toggleFavButton(meal)
                binding.randomMealLayout.bookmark.setImageResource(
                    if (meal.isFav) {
                        com.example.foodify.R.drawable.ic_bookmark_fill
                    } else {
                        com.example.foodify.R.drawable.ic_bookmark
                    }
                )
                viewModel.addMeal(meal)
            } else {
                showLoginDialog(requireContext()) {
                    goToLoginActivity()
                }
            }

        }


        binding.randomMealLayout.root.setOnClickListener {
            val action =
                HomeFragmentDirections.actionHomeFragment2ToMealDetailsFragment(meal.idMeal)
            findNavController().navigate(action)
        }
    }

    private fun goToLoginActivity() {
        val intent = Intent(requireContext(), AuthActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBookmarkClick(meal: MealPreview) {
        if (userId.isNotEmpty()) {
            viewModel.toggleFavButton(meal)
            if (meal.isFav) {
                viewModel.addMeal(meal)
            } else {
                viewModel.removeMeal(meal.idMeal)
            }
            mealsAdapter.notifyDataSetChanged()
        } else {
            showLoginDialog(requireContext()) {
                goToLoginActivity()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d("ooo", "onDestroy: $hint")
        searchAutoCompleteTextView.hint = hint
    }


}