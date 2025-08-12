package com.example.beginnerfit.ui.foodTrack.food


import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.beginnerfit.R
import com.example.beginnerfit.TrackerViewModel
import com.example.beginnerfit.databinding.DialogFoodSelectionBinding
import com.example.beginnerfit.model.FoodLog
import kotlinx.coroutines.launch
import kotlin.getValue


class FoodDialogFragment(
    private val mealType: String,
    private val selectedDate: String,
    private val currentDayId: Int?,
) : DialogFragment() {

    private var _binding: DialogFoodSelectionBinding? = null
    private val binding get() = _binding!!

    private val foodTrackViewModel: FoodViewModel by viewModels<FoodViewModel>(
        ownerProducer = { requireParentFragment() })

    private val sharedViewModel: TrackerViewModel by activityViewModels()


    private lateinit var adapter: FoodAdapter


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogFoodSelectionBinding.inflate(layoutInflater)

        adapter = FoodAdapter { selectedFood ->
            val foodLog = FoodLog(
                id = 0,
                date = selectedDate,
                mealType = mealType,
                foodId = selectedFood.id,
                dayId = currentDayId ?: 0,
            )

            lifecycleScope.launch {
                foodTrackViewModel.insertFoodLog(foodLog)
            }
            dismiss()
        }

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@FoodDialogFragment.adapter
        }

        lifecycleScope.launch {


            sharedViewModel.foodsState.collect { foodsState ->
                when (foodsState) {
                    is FoodResult.Error -> {

                    }

                    FoodResult.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                        binding.recyclerView.visibility = View.GONE
                        binding.searchFoodEditText.visibility = View.GONE
                    }

                    is FoodResult.Success -> {
                        binding.progressBar.visibility = View.GONE
                        binding.recyclerView.visibility = View.VISIBLE
                        binding.searchFoodEditText.visibility = View.VISIBLE
                        adapter.updateAllFood(foodsState.data)
                    }
                }
            }
        }

        binding.searchFoodEditText.doAfterTextChanged {
            adapter.filterByName(it.toString())
        }


        return AlertDialog.Builder(requireContext())
            .setTitle("Select Food for $mealType")
            .setView(binding.root)
            .setNegativeButton("Cancel") { _, _ -> dismiss() }
            .create()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
