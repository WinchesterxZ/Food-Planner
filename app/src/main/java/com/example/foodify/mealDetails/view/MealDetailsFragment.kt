package com.example.foodify.mealDetails.view

import MealDetails
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import cn.pedant.SweetAlert.SweetAlertDialog
import coil.load
import com.example.foodify.R
import com.example.foodify.adapter.IngredientsAdapter
import com.example.foodify.adapter.InstructionsAdapter
import com.example.foodify.data.model.MealPreview
import com.example.foodify.databinding.FragmentMealDetailsBinding
import com.example.foodify.mealDetails.viewModel.MealDetailsState
import com.example.foodify.mealDetails.viewModel.MealDetailsViewModel
import com.example.foodify.util.showErrorSnackBar
import com.example.foodify.util.showProgressDialog
import com.example.foodify.util.showSuccessSnackBar
import com.google.android.material.datepicker.MaterialDatePicker
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MealDetailsFragment : Fragment() {
    private lateinit var _binding: FragmentMealDetailsBinding
    private val binding get() = _binding
    private val args: MealDetailsFragmentArgs by navArgs()
    private val viewModel: MealDetailsViewModel by viewModel()
    private lateinit var pDialog: SweetAlertDialog
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMealDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getMealDetails(args.mealId)
        binding.ingredientRecyclerview.layoutManager = LinearLayoutManager(requireContext())
        binding.instructionsRecyclerview.layoutManager = LinearLayoutManager(requireContext())
        //setupToolbar()
        pDialog = showProgressDialog(requireContext())
        observeMealState()
        binding.navBack.setOnClickListener{
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

    }




    private fun observeMealState() {
        viewModel.mealDetailsState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is MealDetailsState.Loading -> {
                    pDialog.show()
                }

                is MealDetailsState.Success -> {
                    showMealDetails(state.meals)
                    Log.d("a3a3a3a3", "observeMealState: ${state.meals.isFav} + ${state.meals.id} + ${state.meals.instructions}, ${state.meals.ingredients}")
                }
                is MealDetailsState.Message -> handleMessageState(state)
                is MealDetailsState.Error -> showError(state)

                else -> Unit
            }
        }
    }

    private fun handleMessageState(state: MealDetailsState.Message) {
        when (state.action) {
            MealDetailsState.Action.ADDED_TO_FAVORITES -> showSuccessSnackBar(requireView(), state.message)
            MealDetailsState.Action.REMOVED_FROM_FAVORITES -> showSuccessSnackBar(
                requireView(),
                state.message
            )
            MealDetailsState.Action.ADDED_TO_CALENDER -> showSuccessSnackBar(requireView(), state.message)
            MealDetailsState.Action.REMOVED_FROM_CALENDER -> showSuccessSnackBar(requireView(), state.message)
        }
        viewModel.resetState()
    }

    private fun showError(state: MealDetailsState.Error) {
        pDialog.dismiss()

        when (state.type) {
            MealDetailsState.ErrorType.LoadError -> {
                showErrorSnackBar(requireView(), state.message)
            }

            MealDetailsState.ErrorType.AddToFavoriteError -> showErrorSnackBar(
                requireView(),
                state.message
            )
            MealDetailsState.ErrorType.DeleteFromFavoriteError -> showErrorSnackBar(requireView(), state.message)
            MealDetailsState.ErrorType.DeleteFromCalenderError -> showErrorSnackBar(requireView(), state.message)
            MealDetailsState.ErrorType.AddToCalenderError -> showErrorSnackBar(requireView(), state.message)

        }
    }
    private fun handleCalenderClick(meal: MealDetails) {
        binding.calenderIcon.setImageResource(if(meal.mealPlan.isNullOrEmpty()) R.drawable.calender else R.drawable.calender_fill)
        binding.calenderIcon.setOnClickListener {
               if (meal.mealPlan.isNullOrEmpty()){
                   val datePicker = MaterialDatePicker.Builder.datePicker().build()
                   datePicker.show(parentFragmentManager, "MaterialDatePicker")
                   datePicker.addOnPositiveButtonClickListener { selection ->
                       val date = convertLongToDate(selection)
                       Log.d("teste", "handleCalenderClick: $date")
                       viewModel.addMealToCalender(
                           MealPreview(
                               idMeal = meal.id,
                               strMeal = meal.name,
                               strMealThumb = meal.thumbnail,
                               isFav = meal.isFav,
                               userId = meal.userId,
                               mealPlan = date
                           )
                       )
                       binding.calenderIcon.setImageResource(R.drawable.calender_fill)
                   }
               }else{
                   viewModel.removeMealFromCalender(meal.id)
                   binding.calenderIcon.setImageResource(R.drawable.calender)
               }
        }
    }
    private fun convertLongToDate(timestamp: Long): String {
        val date = Date(timestamp)
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(date)
    }
    private fun handleBookmarkClick(meal: MealDetails) {
        binding.bookmarkIcon.setImageResource(if (meal.isFav) R.drawable.ic_bookmark_fill else R.drawable.ic_bookmark)
        binding.bookmarkIcon.setOnClickListener {
            viewModel.toggleFavButton(meal)
            if (meal.isFav) {
                viewModel.addMealToBookMark(
                    MealPreview(
                        idMeal = meal.id,
                        strMeal = meal.name,
                        strMealThumb = meal.thumbnail,
                        isFav = true,
                        userId = meal.userId,
                        mealPlan = meal.mealPlan
                    )
                )
                binding.bookmarkIcon.setImageResource(R.drawable.ic_bookmark_fill)
            } else {
                viewModel.removeMealFromBookMark(meal.id)
                binding.bookmarkIcon.setImageResource(R.drawable.ic_bookmark)
            }
        }
    }
    private fun showMealDetails(meal: MealDetails) {
        pDialog.dismiss()
        binding.meadImage.load(meal.thumbnail){
            listener(
                onStart ={binding.mealImageProgressbar.visibility = View.VISIBLE} ,
                onSuccess = { _, _ -> binding.mealImageProgressbar.visibility = View.GONE },
                onError = { _, _ -> binding.mealImageProgressbar.visibility = View.GONE }
            )

        }
        binding.mealName.text = meal.name
        binding.areaName.text = meal.area
        binding.categoryName.text = meal.category
        val adapter = IngredientsAdapter()
        adapter.submitList(meal.ingredients)
        binding.ingredientRecyclerview.adapter = adapter
        binding.instructionsRecyclerview.adapter = InstructionsAdapter(meal.instructions)
        binding.mealVideo.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: YouTubePlayer) {
                super.onReady(youTubePlayer)
                meal.youtube?.let { url ->
                    val videoId = extractYouTubeVideoId(url)
                    if (videoId != null) {
                        youTubePlayer.loadVideo(videoId, 0F)
                    } else {
                        Log.e("YouTubePlayer", "Invalid YouTube URL: $url")
                    }
                }
            }
        })
        handleBookmarkClick(meal)
        handleCalenderClick(meal)

    }
    private fun extractYouTubeVideoId(url: String): String? {
        val regex = ".*(?:youtu.be/|v/|embed/|watch\\?v=|\\?v=)([^&]+).*".toRegex()
        return regex.find(url)?.groupValues?.get(1)
    }

    /*
     private  fun setupToolbar(){
      activityBinding = (requireActivity() as MainActivity).binding
      toolbar = activityBinding.toolbar.root
      originalToolbarHeight = toolbar.layoutParams.height
      originalToolbarBackground = toolbar.background
      view?.post {
          toolbar.layoutParams.height = requireView().height / 3
          toolbar.requestLayout()
      }
      toolbar.menu.clear()
      toolbar.inflateMenu(R.menu.meal_details_menu)
      // Find the Up button and set background
      toolbar.navigationIcon?.let {
          val drawable = ContextCompat.getDrawable(requireContext(), R.drawable.image_circular_background)
          toolbar.navigationIcon = drawable
      }
      toolbar.setNavigationOnClickListener {
          requireActivity().onBackPressedDispatcher.onBackPressed()
      }
    }


    private fun setUpToolbarCalender(meal: MealDetails) {
        val calender = toolbar.menu.findItem(R.id.action_calender)
        val calenderView = LayoutInflater.from(toolbar.context)
            .inflate(R.layout.custom_calender_item, toolbar, false)
        calender.actionView = calenderView
        // Set background & icon
        calenderView.setBackgroundResource(R.drawable.image_circular_background)
        val calenderIcon = calenderView.findViewById<AppCompatImageView>(R.id.custom_icon_calender)
        //calenderIcon.setImageResource(if (meal.isFav) R.drawable.ic_bookmark_fill else R.drawable.ic_bookmark)

        // Handle clicks
        calenderView.setOnClickListener {

        }
    }

    private fun setUpToolbarFavorite(meal: MealDetails) {
        val bookmark = toolbar.menu.findItem(R.id.action_bookmark)
        val bookmarkView = LayoutInflater.from(toolbar.context)
            .inflate(R.layout.custom_bookmark_item, toolbar, false)
        bookmark.actionView = bookmarkView
       // Set background & icon

        bookmarkView.setBackgroundResource(R.drawable.image_circular_background)
        val bookmarkIcon = bookmarkView.findViewById<AppCompatImageView>(R.id.custom_icon)
        bookmarkIcon.setImageResource(if (meal.isFav) R.drawable.ic_bookmark_fill else R.drawable.ic_bookmark)

        // Handle clicks
        bookmarkView.setOnClickListener {
            viewModel.toggleFavButton(meal)
            if (meal.isFav) {
                viewModel.addMeal(
                    MealPreview(
                        idMeal = meal.id,
                        strMeal = meal.name,
                        strMealThumb = meal.thumbnail,
                        isFav = true,
                        userId = meal.userId
                    )
                )
                bookmarkIcon.setImageResource(R.drawable.ic_bookmark_fill)
            } else {
                viewModel.removeMeal(meal.id)
                bookmarkIcon.setImageResource(R.drawable.ic_bookmark)
            }
        }
    }
     override fun onDestroyView() {
        super.onDestroyView()
        toolbar.menu.clear()
        toolbar.layoutParams.height = originalToolbarHeight
        toolbar.background = originalToolbarBackground
        activityBinding.toolbar.toolbarBackground.visibility = View.GONE
    }
     */


}