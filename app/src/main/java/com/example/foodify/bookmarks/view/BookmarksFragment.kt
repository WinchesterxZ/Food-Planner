package com.example.foodify.bookmarks.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.foodify.adapter.MealsAdapter
import com.example.foodify.bookmarks.viewmodel.BookmarksViewmodel
import com.example.foodify.data.model.MealPreview
import com.example.foodify.databinding.FragmentBookmarksBinding
import com.example.foodify.home.view.HomeFragmentDirections
import com.example.foodify.home.view.MealState
import com.example.foodify.home.view.OnBookmarkClickListener
import com.example.foodify.util.showErrorSnackBar
import com.example.foodify.util.showProgressDialog
import com.example.foodify.util.showSuccessSnackBar
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.math.log

class BookmarksFragment : Fragment(), OnBookmarkClickListener{
    private lateinit var _binding: FragmentBookmarksBinding
    private val binding get() = _binding
    private val viewModel: BookmarksViewmodel by viewModel()
    private lateinit var pDialog: SweetAlertDialog
    private lateinit var mealAdapter: MealsAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBookmarksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.bookmarksMealsRecyclerView.layoutManager = GridLayoutManager(binding.root.context, 2)
        pDialog = showProgressDialog(binding.root.context)
        viewModel.getSpecificFavMeals()
        observeBookmarks()

    }

    private fun observeBookmarks() {
        viewModel.mealState.observe(viewLifecycleOwner) { state ->
            Log.d("a3a3a3a3", "Fav: $state")
            when (state) {
                is MealState.Loading -> showLoading()
                is MealState.Success -> {
                    showFavMeals(state.meals)
                    Log.d("a3a3a3a3", "showFavMeals: ${state.meals.size}")
                }
                is MealState.Message -> {
                    handleMessageState(state)
                    viewModel.getSpecificFavMeals()
                }
                is MealState.Error -> showError(state)
                else -> Unit
            }
        }
    }

    private fun showFavMeals(meals: List<MealPreview>) {
        pDialog.dismiss()
        if (meals.isEmpty()){
            Log.d("a3a3a3a3", "showFavMeals: Empty")
            (binding.bookmarksMealsRecyclerView.adapter as? MealsAdapter)?.submitList(emptyList())
            binding.emptyView.emptyLayout.visibility = View.VISIBLE
            return
        }
        binding.emptyView.emptyLayout.visibility = View.GONE
        mealAdapter = MealsAdapter(this@BookmarksFragment){id->
            val action = BookmarksFragmentDirections.actionBookmarksFragment2ToMealDetailsFragment(id)
            findNavController().navigate(action)
        }
        mealAdapter.submitList(meals)
        Log.d("a3a3a3a3", "showFavMeals: ${meals.size}")
        binding.bookmarksMealsRecyclerView.adapter = mealAdapter

    }
    private fun handleMessageState(state: MealState.Message) {
        when (state.action) {
            MealState.Action.ADDED_TO_FAVORITES ->Unit
            MealState.Action.REMOVED_FROM_FAVORITES -> showSuccessSnackBar(
                requireView(),
                state.message
            )
        }
    }

    private fun showLoading() {
        pDialog.show()
    }
    private fun showError(state: MealState.Error) {
        pDialog.dismiss()
        when (state.type) {
            MealState.ErrorType.LoadError -> {
                showErrorSnackBar(requireView(), state.message)
            }
            MealState.ErrorType.DeleteError -> showErrorSnackBar(requireView(), state.message)
            MealState.ErrorType.AddToFavoriteError -> Unit
        }
    }

    override fun onBookmarkClick(meal: MealPreview) {
        viewModel.toggleFavButton(meal)
        viewModel.removeMeal(meal.idMeal)

    }

}