package com.example.foodify.calender.view

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.foodify.main.MainActivityViewModel
import com.example.foodify.adapter.MealPlanAdapter
import com.example.foodify.bookmarks.view.SyncState
import com.example.foodify.calender.viewmodel.CalenderViewModel
import com.example.foodify.data.model.MealPreview
import com.example.foodify.databinding.FragmentCalenderBinding
import com.example.foodify.adapter.listeners.OnItemClickListener
import com.example.foodify.util.NetworkUtils
import com.example.foodify.util.showErrorSnackBar
import com.example.foodify.util.showInfoSnackBar
import com.example.foodify.util.showProgressDialog
import com.example.foodify.util.showSuccessSnackBar
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.math.log

class CalenderFragment : Fragment(), OnItemClickListener {
    private lateinit var _binding: FragmentCalenderBinding
    private val binding get() = _binding
    private lateinit var calender: Calendar
    private val mainViewModel: MainActivityViewModel by activityViewModel()
    private val viewModel: CalenderViewModel by viewModel()
    private lateinit var pDialog: SweetAlertDialog
    private lateinit var userId: String
    private lateinit var adapter: MealPlanAdapter
    private lateinit var networkUtils: NetworkUtils

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userId = mainViewModel.getCurrentUser()?.uid ?: ""
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCalenderBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        calender = Calendar.getInstance()
        pDialog = showProgressDialog(requireContext())
        networkUtils = NetworkUtils(requireContext())
        setupRecyclerView()

        binding.calendarView.date = calender.timeInMillis
        binding.calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            calender.set(year, month, dayOfMonth)
            val selectedDate = formatDate(calender)
            viewModel.getMealsByDay(userId, selectedDate)
        }

        val currentDate = formatDate(calender)
        viewModel.getMealsByDay(userId, currentDate)
        observeMealState()

        if(networkUtils.hasNetworkConnection()){
            binding.sync.setOnClickListener {
                viewModel.syncCalendarMeals(userId)
            }
            binding.backup.setOnClickListener {
                viewModel.backupCalendarMeals(userId)
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
                    viewModel.getMealsByDay(userId,formatDate(calender))
                }
                is SyncState.Error -> {
                    pDialog.dismiss()
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

    private fun setupRecyclerView() {
        adapter = MealPlanAdapter(this@CalenderFragment) {  }
        binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.recyclerView.adapter = adapter
    }

    private fun observeMealState() {
        viewModel.mealState.observe(viewLifecycleOwner) { state ->
            Log.d("aaaa", "observeMealState: $state")
            when (state) {
                is MealPlanState.Error -> showError(state)
                is MealPlanState.Loading -> pDialog.show()
                is MealPlanState.Message -> handleMessageState(state)
                is MealPlanState.Success -> showMealDetails(state.meals)
                else -> Unit
            }
        }
    }

    private fun showMealDetails(meals: List<MealPreview>) {
        pDialog.dismiss()
        Log.d("aaaa", "showMealDetails: $meals")
        if(meals.isEmpty()){
            binding.backup.visibility= View.GONE
        }else{
            binding.backup.visibility= View.VISIBLE
        }
        adapter.submitList(meals)
    }

    private fun showError(state: MealPlanState.Error) {
        pDialog.dismiss()
        when (state.type) {
            MealPlanState.ErrorType.LoadError -> showErrorSnackBar(requireView(), state.message)
            MealPlanState.ErrorType.ADDError -> showErrorSnackBar(requireView(), state.message)
            MealPlanState.ErrorType.DeleteError -> showErrorSnackBar(requireView(), state.message)
        }
    }

    private fun handleMessageState(state: MealPlanState.Message) {
        when (state.action) {
            MealPlanState.Action.ADDED_TO_CALENDER -> Unit
            MealPlanState.Action.REMOVED_FROM_CALENDER ->{
                showSuccessSnackBar(requireView(), state.message)
                viewModel.getMealsByDay(userId,formatDate(calender))
            }

        }
    }


    private fun formatDate(calendar: Calendar): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(calendar.time)
    }

    override fun onBookmarkClick(meal: MealPreview) {
        viewModel.removeMeal(meal.idMeal)

    }
}
