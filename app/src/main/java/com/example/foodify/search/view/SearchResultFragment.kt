package com.example.foodify.search.view

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.foodify.MainActivity
import com.example.foodify.MainActivityViewModel
import com.example.foodify.adapter.MealsAdapter
import com.example.foodify.authentication.ui.AuthActivity
import com.example.foodify.data.model.MealPreview
import com.example.foodify.databinding.FragmentSearchResultBinding
import com.example.foodify.home.view.MealState
import com.example.foodify.home.view.OnItemClickListener
import com.example.foodify.search.viewmodel.SearchResultViewModel
import com.example.foodify.util.showErrorSnackBar
import com.example.foodify.util.showLoginDialog
import com.example.foodify.util.showProgressDialog
import com.example.foodify.util.showSuccessSnackBar
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchResultFragment : Fragment(),OnItemClickListener {
    private lateinit var _binding: FragmentSearchResultBinding
    private val viewModel: SearchResultViewModel by viewModel()
    private val mainViewModel:MainActivityViewModel by activityViewModel()
    private val args: SearchResultFragmentArgs by navArgs()
    private val binding get() = _binding
    private lateinit var pDialog: SweetAlertDialog
    private lateinit var mealsAdapter: MealsAdapter
    private lateinit var userId: String
    private lateinit var searchType: String
    private lateinit var searchQuery: String
    private lateinit var searchAutoCompleteTextView: AutoCompleteTextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userId= mainViewModel.getCurrentUser()?.uid?: ""
        searchType = args.searchType
        searchQuery = args.searchName
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        // Inflate the layout for this fragment
        _binding = FragmentSearchResultBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        searchAutoCompleteTextView = (requireActivity() as MainActivity).binding.toolbar.searchAutoCompleteTextView
        searchAutoCompleteTextView.hint = "Filter Your Search Result"
        _binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        mealsAdapter = MealsAdapter(this@SearchResultFragment){id->
            val action = SearchResultFragmentDirections.actionSearchResultFragmentToMealDetailsFragment(id)
            findNavController().navigate(action)
        }
        binding.recyclerView.adapter = mealsAdapter
        callMealsApi()
        pDialog = showProgressDialog(requireContext())
        observeMealState()
        observeFilteredData()
        setSearchAutoCompleteTextView()
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
        viewModel.filteredMeals.observe(viewLifecycleOwner) { loadMeals(it) }
    }

    private fun callMealsApi() {
        when(searchType){
            "category" -> viewModel.getMealsByCategory(searchQuery,userId)
            "area" -> viewModel.getMealsByArea(searchQuery,userId)
            "ingredient" -> viewModel.getMealsByIngredient(searchQuery,userId)
            "name" -> viewModel.getMealsByName(searchQuery,userId)
        }
    }

    private fun observeMealState() {
        viewModel.mealState.observe(viewLifecycleOwner){state->
                Log.d("a3a3a3a3", "observeHomeData: $state")
                when (state) {
                    is MealState.Loading -> showLoading()
                    is MealState.Success -> {
                        loadMeals(state.meals)
                    }
                    is MealState.Message -> handleMessageState(state)
                    is MealState.Error -> showError(state)
                    else -> Unit
            }
        }
    }

    private fun loadMeals(meals: List<MealPreview>) {
        pDialog.dismiss()
        if(meals.isEmpty()){
            binding.noData.visibility = View.VISIBLE
            binding.recyclerView.visibility = View.GONE
        }else{
            binding.noData.visibility = View.GONE
            binding.recyclerView.visibility = View.VISIBLE
        }
        mealsAdapter.submitList(meals)
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
            else->Unit
        }
    }

    private fun showLoading() {
        pDialog.show()
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBookmarkClick(meal: MealPreview) {
        if(userId.isNotEmpty()){
            viewModel.toggleFavButton(meal)
            if(meal.isFav){
                viewModel.addMeal(meal,userId)
            }else{
                viewModel.removeMeal(meal.idMeal)
            }
            mealsAdapter.notifyDataSetChanged()
        }else{
            showLoginDialog(requireContext()){
                val intent = Intent(requireContext(), AuthActivity::class.java)
                startActivity(intent)
                requireActivity().finish()
            }
        }

    }

}