package com.example.foodify.bookmarks.view

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.foodify.MainActivityViewModel
import com.example.foodify.adapter.MealsAdapter
import com.example.foodify.bookmarks.viewmodel.BookmarksViewmodel
import com.example.foodify.data.model.MealPreview
import com.example.foodify.databinding.FragmentBookmarksBinding
import com.example.foodify.home.view.MealState
import com.example.foodify.home.view.OnItemClickListener
import com.example.foodify.util.NetworkUtils
import com.example.foodify.util.showErrorSnackBar
import com.example.foodify.util.showInfoSnackBar
import com.example.foodify.util.showProgressDialog
import com.example.foodify.util.showSuccessSnackBar
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class BookmarksFragment : Fragment(), OnItemClickListener{
    private lateinit var _binding: FragmentBookmarksBinding
    private val binding get() = _binding
    private val viewModel: BookmarksViewmodel by viewModel()
    private lateinit var pDialog: SweetAlertDialog
    private lateinit var mealAdapter: MealsAdapter
    private val mainViewModel:MainActivityViewModel by activityViewModel()
    private lateinit var userId: String
    private lateinit var networkUtils: NetworkUtils

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userId = mainViewModel.getCurrentUser()?.uid ?: ""
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBookmarksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.bookmarksMealsRecyclerView.layoutManager =
            GridLayoutManager(binding.root.context, 2)
        mealAdapter = MealsAdapter(this@BookmarksFragment){id->
            val action = BookmarksFragmentDirections.actionBookmarksFragment2ToMealDetailsFragment(id)
            findNavController().navigate(action)
        }
        binding.bookmarksMealsRecyclerView.adapter = mealAdapter
        networkUtils = NetworkUtils(requireContext())
        pDialog = showProgressDialog(binding.root.context)
        viewModel.getSpecificFavMeals(userId)
        observeBookmarks()
        if(networkUtils.hasNetworkConnection()){
            binding.sync.setOnClickListener {
                viewModel.syncBookmarkedMeals(userId)
            }
            binding.backup.setOnClickListener {
                viewModel.backupBookmarkedMeals(userId)
            }

            observeSyncAndBackUpState()
        }else{
            binding.sync.setOnClickListener {
               showErrorSnackBar(requireView(), "No Internet Connection")
            }
            binding.backup.setOnClickListener {
                showErrorSnackBar(requireView(), "No Internet Connection")
            }
        }

    }

    private fun observeSyncAndBackUpState() {
        viewModel.syncState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is SyncState.Success -> {
                    pDialog.dismiss()
                    showSuccessSnackBar(binding.root, state.message)
                    viewModel.getSpecificFavMeals(userId)
                }
                is SyncState.Error -> {
                    pDialog.dismiss()
                    Log.d("aloo", "observeSyncAndBackUpState: ${state.message}")
                    showErrorSnackBar(binding.root, state.message)
                }
                is SyncState.NoChange -> {
                    pDialog.dismiss()
                    showInfoSnackBar(binding.root, state.message)
                }
                SyncState.Loading -> pDialog.show()
                is SyncState.NoBackup -> {
                    pDialog.dismiss()
                    showInfoSnackBar(binding.root, state.message)
                }
            }
        }

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
                }
                is MealState.Error -> showError(state)
                else -> Unit
            }
        }
    }

    private fun showFavMeals(meals: List<MealPreview>) {
        pDialog.dismiss()
        if (meals.isEmpty()){
            binding.emptyView.emptyLayout.visibility = View.VISIBLE
            binding.bookmarksMealsRecyclerView.visibility = View.GONE
            binding.backup.visibility = View.GONE
        }else{
            binding.emptyView.emptyLayout.visibility = View.GONE
            binding.bookmarksMealsRecyclerView.visibility = View.VISIBLE
            binding.backup.visibility = View.VISIBLE
        }
        mealAdapter.submitList(meals)

    }
    private fun handleMessageState(state: MealState.Message) {
        when (state.action) {
            MealState.Action.ADDED_TO_FAVORITES ->Unit
            MealState.Action.REMOVED_FROM_FAVORITES -> {
                viewModel.getSpecificFavMeals(userId)
                showSuccessSnackBar(
                    requireView(),
                    state.message
                )
            }
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