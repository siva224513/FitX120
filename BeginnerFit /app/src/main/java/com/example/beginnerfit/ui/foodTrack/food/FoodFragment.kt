package com.example.beginnerfit.ui.foodTrack.food

import android.os.Bundle
import android.text.InputFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.app.ActionBar
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.beginnerfit.R
import com.example.beginnerfit.TrackerViewModel
import com.example.beginnerfit.databinding.FragmentFoodBinding
import com.example.beginnerfit.databinding.MealTrackerCardBinding
import com.example.beginnerfit.domain.repository.Repository
import com.example.beginnerfit.domain.repository.UserRepository
import com.example.beginnerfit.domain.usecase.dashboard.CalculateMaintenanceCalorieUseCase
import com.example.beginnerfit.domain.usecase.food.*
import com.example.beginnerfit.domain.usecase.food.SaveSleepUseCase
import com.example.beginnerfit.domain.usecase.signup.UpdateUserProfileInDbUseCase
import com.example.beginnerfit.domain.usecase.water.GetWaterCountUseCase
import com.example.beginnerfit.domain.usecase.water.SaveWaterCountUseCase
import com.example.beginnerfit.domain.usecase.weight.CalculateWeightProgressUseCase
import com.example.beginnerfit.domain.usecase.weight.GetWeightForDayIdUseCase
import com.example.beginnerfit.domain.usecase.weight.SaveWeightUseCase
import com.example.beginnerfit.model.Food
import com.example.beginnerfit.model.Nutrition
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class FoodFragment : Fragment() {


    companion object {
        fun newInstance(day: String): FoodFragment {
            return FoodFragment().apply {
                arguments = Bundle().apply { putString(ARG_DATE, day) }
            }
        }

        private const val ARG_DATE = "date"

        private const val BREAK_FAST = "Breakfast"
        private const val LUNCH = "Lunch"
        private const val MORNING_SNACK = "MorningSnack"
        private const val EVENING_SNACK = "EveningSnack"
        private const val DINNER = "Dinner"

    }

    private var _binding: FragmentFoodBinding? = null
    private val binding get() = _binding!!

    private lateinit var foodViewModel: FoodViewModel
    private lateinit var selectedDate: String

    private val mealAdapters = mutableMapOf<String, MealFoodAdapter>()


    private var currentDayId: Int? = null
    private val sharedViewModel: TrackerViewModel by activityViewModels()
    private val user = UserRepository.getUser()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        selectedDate = requireArguments().getString(ARG_DATE)!!




        println("selected date is in foodFragment: $selectedDate")

        val repository = Repository
        val factory = FoodViewModelFactory(
            CalculateMaintenanceCalorieUseCase(),
            InsertFoodLogUseCase(repository),
            GetAllFoodLogsByDate(repository),
            GetTotalNutritionForMealUseCase(),
            selectedDate,
            GetWaterCountUseCase(repository),
            SaveWaterCountUseCase(repository),
            GetWeightForDayIdUseCase(repository),
            SaveWeightUseCase(repository),
            CalculateWeightProgressUseCase(),
            UpdateUserProfileInDbUseCase(repository),
            SaveSleepUseCase(repository),
            GetSleepLogUseCase(repository),
            DeleteFoodLogUseCase(repository)
        )
        foodViewModel = ViewModelProvider(this, factory)[FoodViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFoodBinding.inflate(inflater, container, false)
        setDayId()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (!sharedViewModel.isEditableDay(selectedDate, "dd-MM-yyyy")) {
            disableAllInputs()
        }

        binding.textDateInfo.text = selectedDate


        setupRecycleView()
        setupMealButtons()
        observeFoodLogs()
        setupWaterTracker()
        setupWeightTracker()
        setupSleepTracker()

    }

    private fun setupRecycleView() {

        val allFoods = sharedViewModel.allFoods
        mealAdapters[BREAK_FAST] = MealFoodAdapter(allFoods) { foodLog ->
            foodViewModel.deleteFoodLog(foodLog) { success ->
                if (success) {
                    Snackbar.make(
                        binding.root,
                        R.string.food_deleted_updated,
                        Snackbar.LENGTH_SHORT
                    ).show()
                } else {
                    Snackbar.make(binding.root, R.string.food_deleted_failed, Snackbar.LENGTH_SHORT)
                        .show()
                }
            }
            updateMealNutritionUI()
        }.also {
            binding.breakfastTracker.recyclerViewMealFoods.apply {
                adapter = it
                layoutManager = LinearLayoutManager(requireContext())
            }
        }
        mealAdapters[LUNCH] = MealFoodAdapter(allFoods) { foodLog ->
            foodViewModel.deleteFoodLog(foodLog) {}
            updateMealNutritionUI()
        }.also {
            binding.lunchTracker.recyclerViewMealFoods.apply {
                adapter = it
                layoutManager = LinearLayoutManager(requireContext())
            }
        }

        mealAdapters[DINNER] = MealFoodAdapter(allFoods) { foodLog ->
            foodViewModel.deleteFoodLog(foodLog) {}
            updateMealNutritionUI()
        }.also {
            binding.dinnerTracker.recyclerViewMealFoods.apply {
                adapter = it
                layoutManager = LinearLayoutManager(requireContext())
            }
        }

        mealAdapters[MORNING_SNACK] = MealFoodAdapter(allFoods) { foodLog ->
            foodViewModel.deleteFoodLog(foodLog) {}
            updateMealNutritionUI()
        }.also {
            binding.morningSnackTracker.recyclerViewMealFoods.apply {
                adapter = it
                layoutManager = LinearLayoutManager(requireContext())
            }
        }

        mealAdapters[EVENING_SNACK] = MealFoodAdapter(allFoods) { foodLog ->
            foodViewModel.deleteFoodLog(foodLog) {}
            updateMealNutritionUI()
        }.also {
            binding.eveningSnackTracker.recyclerViewMealFoods.apply {
                adapter = it
                layoutManager = LinearLayoutManager(requireContext())
            }

        }
    }

    private fun setupSleepTracker() {

        lifecycleScope.launch {
            foodViewModel.isSleepAchieved.collectLatest { isAchieved ->
                binding.cbSleepCompleted.isChecked = isAchieved

            }
        }

        binding.cbSleepCompleted.setOnCheckedChangeListener { _, isChecked ->
            foodViewModel.saveSleep(isChecked, currentDayId)
            val snackBar = Snackbar.make(
                binding.root,
                R.string.sleep_updated,
                Snackbar.LENGTH_SHORT
            )
            snackBar.view.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.workout_set_card_today)
            snackBar.show()


        }

    }

    private fun disableAllInputs() {
        binding.breakfastTracker.btnAddMeal.setDisabledState(false)
        binding.lunchTracker.btnAddMeal.setDisabledState(false)
        binding.dinnerTracker.btnAddMeal.setDisabledState(false)
        binding.morningSnackTracker.btnAddMeal.setDisabledState(false)
        binding.eveningSnackTracker.btnAddMeal.setDisabledState(false)

        binding.btnWaterAdd.setDisabledState(false)
        binding.btnWaterRemove.setDisabledState(false)

        binding.btnSaveWeight.setDisabledState(false)
        binding.etWeightInput.setDisabledState(false)

        binding.cbSleepCompleted.setDisabledState(false)
    }

    fun View.setDisabledState(enabled: Boolean) {
        this.isEnabled = enabled
        this.isClickable = enabled
        this.alpha = if (enabled) 1f else 0.5f
    }


    private fun setupWeightTracker() {


        binding.etWeightInput.filters = arrayOf(InputFilter.LengthFilter(5))
        val goal = foodViewModel.getGoal(user.startWeight!!, user.targetWeight!!)
        lifecycleScope.launch {
            foodViewModel.weight.collectLatest { weight ->
                val weightProgress =
                    foodViewModel.calculateWeightProgress(weight, user).coerceAtLeast(0)


                val percentage =
                    ((weightProgress / goal) * 100).coerceIn(0.0, 100.0).roundToInt()


                binding.tvWeightProgress.text =
                    getString(R.string.weight_progress, weightProgress.toDouble(), goal)
                binding.weightProgressBar.progress = percentage


            }
        }

        binding.etWeightInput.doAfterTextChanged {
            binding.weightError.visibility = View.GONE
        }


        binding.btnSaveWeight.setOnClickListener {
            val input = binding.etWeightInput.text.toString().trim()
            val weight = input.toDoubleOrNull()

            when {
                input.isEmpty() -> {
                    binding.weightError.text = getString(R.string.error_weight_length)
                    binding.weightError.visibility = View.VISIBLE
                    binding.etWeightInput.requestFocus()
                }

                weight == null -> {
                    binding.weightError.text = getString(R.string.error_weight_null)
                    binding.weightError.visibility = View.VISIBLE
                    binding.etWeightInput.requestFocus()
                }

                weight <= 30 -> {
                    binding.weightError.text =getString(R.string.error_weight_min)
                    binding.weightError.visibility = View.VISIBLE
                    binding.etWeightInput.requestFocus()
                }

                weight >= 200 -> {
                    binding.weightError.text = getString(R.string.error_weight_max)
                    binding.weightError.visibility = View.VISIBLE
                    binding.etWeightInput.requestFocus()
                }


                else -> {
                    binding.weightError.visibility = View.GONE

                    lifecycleScope.launch {
                        val updated = foodViewModel.saveWeight(weight, currentDayId)

                        if (updated) {
                            Snackbar.make(
                                binding.root,
                                "Weight updated to $weight kg",
                                Snackbar.LENGTH_SHORT
                            ).show()

                            user.currentWeight = weight
                            user.maintenanceCalorie =
                                foodViewModel.calculateMaintenanceCalorie(weight, user)
                            foodViewModel.updateUserProfile(user)

                        } else {
                            Snackbar.make(binding.root, "Weight not updated", Snackbar.LENGTH_SHORT)
                                .show()
                        }
                    }
                }
            }
        }


    }


    private fun setupWaterTracker() {
        val glasses = listOf(
            binding.glass1,
            binding.glass2,
            binding.glass3,
            binding.glass4,
            binding.glass5,
            binding.glass6,
            binding.glass7,
            binding.glass8
        )


        binding.btnWaterAdd.setOnClickListener {
            val reachedMax = foodViewModel.addGlass(currentDayId)
            if (reachedMax) {
                Snackbar.make(binding.root, R.string.water_reached_max, Snackbar.LENGTH_SHORT).show()

            }
        }


        binding.btnWaterRemove.setOnClickListener {
            val reachedMin = foodViewModel.removeGlass(currentDayId)
            if (reachedMin) {
                Snackbar.make(binding.root, R.string.water_reached_min, Snackbar.LENGTH_SHORT).show()

            }
        }

        lifecycleScope.launch {
            foodViewModel.waterCount.collectLatest { count ->
                binding.tvWaterProgress.text = getString(R.string.water_progress,count)
                glasses.forEachIndexed { index, imageView ->
                    imageView.setImageResource(
                        if (index < count) R.drawable.outline_water_full_24
                        else R.drawable.outline_water_loss_24
                    )
                }
            }
        }
    }



    private fun setDayId() {
        lifecycleScope.launch {
            currentDayId = Repository.getDayIdByDate(selectedDate) ?: 1


            currentDayId?.let {
                foodViewModel.loadWaterCount(it)
                foodViewModel.loadWeight(it)
                foodViewModel.loadSleepLog(it)
            }


        }
    }

    private fun setupMealButtons() = with(binding) {
        breakfastTracker.btnAddMeal.setOnClickListener { showFoodDialog("Breakfast") }
        lunchTracker.btnAddMeal.setOnClickListener { showFoodDialog("Lunch") }
        dinnerTracker.btnAddMeal.setOnClickListener { showFoodDialog("Dinner") }
        morningSnackTracker.btnAddMeal.setOnClickListener { showFoodDialog("MorningSnack") }
        eveningSnackTracker.btnAddMeal.setOnClickListener { showFoodDialog("EveningSnack") }
    }


    private fun observeFoodLogs() {
        lifecycleScope.launch {
            foodViewModel.foodLogs.collectLatest {
                updateMealNutritionUI()
            }
        }

    }

    private fun showFoodDialog(mealType: String) {

        FoodDialogFragment(mealType, selectedDate, currentDayId).show(
            childFragmentManager,
            "FoodDialog"
        )
    }


    private fun updateMealNutritionUI() {
        lifecycleScope.launch {
            foodViewModel.loadFoodLogs(selectedDate)
            val allFoods = sharedViewModel.allFoods

            val mealTypes =
                listOf("Breakfast", "Lunch", "Dinner", "MorningSnack", "EveningSnack")

            for (mealType in mealTypes) {
                val layout = getLayoutForMeal(mealType)
                updateSingleMealCard(mealType, layout, allFoods)
            }
        }
    }

    private fun getLayoutForMeal(mealType: String): MealTrackerCardBinding {
        return when (mealType) {
            "Breakfast" -> binding.breakfastTracker
            "Lunch" -> binding.lunchTracker
            "Dinner" -> binding.dinnerTracker
            "MorningSnack" -> binding.morningSnackTracker
            "EveningSnack" -> binding.eveningSnackTracker
            else -> throw IllegalArgumentException("Unknown meal type: $mealType")
        }
    }

    private fun updateSingleMealCard(
        mealType: String,
        layout: MealTrackerCardBinding,
        allFoods: List<Food>
    ) {

        val logs = foodViewModel.foodLogs.value.filter { it.mealType == mealType }

        layout.foodListContainer.visibility = if (logs.isNotEmpty()) View.VISIBLE else View.GONE
        mealAdapters[mealType]?.submitList(logs)
        val nutrition = foodViewModel.getTotalNutritionFromDb(logs, allFoods)
        updateMealCard(layout, mealType, nutrition)
    }


    private fun updateMealCard(
        layout: MealTrackerCardBinding,
        mealType: String,
        nutrition: Nutrition
    ) {
        layout.tvMealName.text = mealType
        layout.tvCalories.text = getString(R.string.nutrition_calorie,nutrition.calorie)
        layout.tvProteinValue.text = getString(R.string.nutrition_macro,nutrition.protein)
        layout.tvCarbsValue.text =getString(R.string.nutrition_macro,nutrition.carbs.toDouble())
        layout.tvFatValue.text =getString(R.string.nutrition_macro,nutrition.fat)
        layout.tvFiberValue.text = getString(R.string.nutrition_macro,nutrition.fiber)

    }




    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    inner class FoodViewModelFactory(
        private val calculateMaintenanceCalorieUseCase: CalculateMaintenanceCalorieUseCase,
        private val insertFoodLogUseCase: InsertFoodLogUseCase,
        private val getAllFoodLogsByDate: GetAllFoodLogsByDate,
        private val getTotalNutritionForMealUseCase: GetTotalNutritionForMealUseCase,
        private val date: String,
        private val getWaterCountUseCase: GetWaterCountUseCase,
        private val saveWaterCountUseCase: SaveWaterCountUseCase,
        private val getWeightForDayIdUseCase: GetWeightForDayIdUseCase,
        private val saveWeightUseCase: SaveWeightUseCase,
        private val calculateWeightProgressUseCase: CalculateWeightProgressUseCase,
        private val updateUserProfileInDbUseCase: UpdateUserProfileInDbUseCase,
        private val sleepUseCase: SaveSleepUseCase,
        private val getSleepLogUseCase: GetSleepLogUseCase,
        private val deleteFoodLogUseCase: DeleteFoodLogUseCase
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return FoodViewModel(
                calculateMaintenanceCalorieUseCase,
                insertFoodLogUseCase,
                getAllFoodLogsByDate,
                getTotalNutritionForMealUseCase,
                date,
                getWaterCountUseCase,
                saveWaterCountUseCase,
                getWeightForDayIdUseCase,
                saveWeightUseCase,
                calculateWeightProgressUseCase,
                updateUserProfileInDbUseCase,
                sleepUseCase,
                getSleepLogUseCase,
                deleteFoodLogUseCase
            ) as T
        }
    }
}
