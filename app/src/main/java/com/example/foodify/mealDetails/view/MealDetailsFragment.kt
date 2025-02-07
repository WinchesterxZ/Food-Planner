package com.example.foodify.mealDetails.view

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.example.foodify.databinding.FragmentMealDetailsBinding
import com.example.foodify.mealDetails.viewModel.MealDetailsState
import com.example.foodify.mealDetails.viewModel.MealDetailsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class MealDetailsFragment : Fragment() {
    private lateinit var _binding: FragmentMealDetailsBinding
    private val binding get() = _binding
    private val args: MealDetailsFragmentArgs by navArgs()
    private val viewModel: MealDetailsViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentMealDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.textView.text = args.mealId
        viewModel.getMealDetails(args.mealId)
        observeMealState()

    }

    private fun observeMealState() {
        viewModel.mealDetailsState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is MealDetailsState.Loading -> {
                    // showLoading()
                }

                is MealDetailsState.Success -> {
                    Log.d("a3a3a3a3", "observeMealState: ${state.meals.isFav} + ${state.meals.userId} + ${state.meals.name}")
                }
                //showMealDetails(state.meal)
                is MealDetailsState.Error -> {
                    //showError(state.message)
                }

                else -> Unit
            }
        }
    }


}