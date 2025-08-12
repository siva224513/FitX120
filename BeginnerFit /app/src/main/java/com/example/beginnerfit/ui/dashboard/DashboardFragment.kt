package com.example.beginnerfit.ui.dashboard

import CalendarAdapter
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableRow
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.example.beginnerfit.R
import com.example.beginnerfit.TrackerViewModel
import com.example.beginnerfit.databinding.FragmentDashboardBinding
import com.example.beginnerfit.databinding.TrackerItemBinding
import com.example.beginnerfit.domain.repository.Repository
import com.example.beginnerfit.domain.repository.UserRepository
import com.example.beginnerfit.domain.usecase.dashboard.CalculateDailyTargetUseCase
import com.example.beginnerfit.domain.usecase.dashboard.GetProgramWeeksUseCase
import com.example.beginnerfit.domain.usecase.dashboard.GetProgressDataUseCase
import com.example.beginnerfit.domain.usecase.food.GetAllFoodLogsByDate
import com.example.beginnerfit.domain.usecase.food.GetTotalNutritionForMealUseCase
import com.example.beginnerfit.domain.usecase.weight.GetWeightForDayIdUseCase
import com.example.beginnerfit.model.Food
import com.example.beginnerfit.model.Nutrition
import com.example.beginnerfit.ui.foodTrack.food.FoodResult
import com.mikhaellopez.circularprogressbar.CircularProgressBar
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private val sharedViewModel: TrackerViewModel by activityViewModels()

    private lateinit var viewModel: DashboardViewModel
    private lateinit var calendarAdapter: CalendarAdapter

    private var currentDayId = 1
    private val monthFormatter = DateTimeFormatter.ofPattern("MMM yyyy", Locale.getDefault())


    private val user = UserRepository.getUser()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)


        val repository = Repository

        val getProgramWeeksUseCase = GetProgramWeeksUseCase(repository)
        val getTotalNutritionForMealUseCase = GetTotalNutritionForMealUseCase()
        val getAllFoodLogsByDate = GetAllFoodLogsByDate(repository)
        val getWeightForDayIdUseCase = GetWeightForDayIdUseCase(repository)
        val calculateDailyTargetUseCase = CalculateDailyTargetUseCase()
        val getProgressDataUseCase =
            GetProgressDataUseCase(repository, GetTotalNutritionForMealUseCase())
        val factory = DashboardViewModelFactory(
            getProgramWeeksUseCase,
            getTotalNutritionForMealUseCase,
            getAllFoodLogsByDate,
            getWeightForDayIdUseCase,
            calculateDailyTargetUseCase,
            getProgressDataUseCase
        )
        viewModel = ViewModelProvider(this, factory)[DashboardViewModel::class.java]


        return binding.root


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding.textName.text = user.name
        val date = LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy")).toString()
        binding.textDateInfo.text = date
        loadUI()

    }


    private fun loadUI() {
        setupCalendarRecycler()
        observeCalendarDates()
        observeSelectedDate()
        setListeners()
    }


    private fun setupCalendarRecycler() {
        calendarAdapter = CalendarAdapter(
            emptyList(),
            viewModel.selectedDate.value
        ) { date ->
            viewModel.setSelectedDate(date)
        }

        binding.calendarRecyclerView.apply {
            layoutManager = GridLayoutManager(requireContext(), 7)
            adapter = calendarAdapter
        }
    }

    private fun observeCalendarDates() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.programDates.collectLatest { weeks ->
                if (weeks.isNotEmpty()) {
                    val currentWeek = viewModel.getCurrentWeek()
                    calendarAdapter.updateDates(currentWeek)
                    updateMonthLabelFromWeek(currentWeek)
                }
            }
        }
    }

    private fun observeSelectedDate() {
        lifecycleScope.launch {
            viewModel.selectedDate.collect { date ->
                val formattedDate = viewModel.getFormattedDate(date)
                currentDayId = viewModel.getDayId(formattedDate) ?: 1
                val currentWeek = viewModel.getCurrentWeek()
                calendarAdapter.updateDates(currentWeek)
                updateMonthLabelFromWeek(currentWeek)
                updateUIForSelectedDate(formattedDate)
            }
        }
    }


    private fun setListeners() {
        binding.leftArrow.setOnClickListener {
            val week = viewModel.goToPreviousWeek()
            calendarAdapter.updateDates(week)
            updateMonthLabelFromWeek(week)
        }
        binding.rightArrow.setOnClickListener {
            val week = viewModel.goToNextWeek()
            calendarAdapter.updateDates(week)
            updateMonthLabelFromWeek(week)
        }
    }

    private fun updateMonthLabelFromWeek(week: List<LocalDate?>) {
        val firstDate = week.firstOrNull { it != null }
        binding.tvMonthYear.text = firstDate?.let { monthFormatter.format(it) } ?: "Program"
    }

    private fun updateUIForSelectedDate(date: String) {
        println("updateUIForSelectedDate is called with date: $date")
        observeWeight()
        updateProgressBar(date)

    }

    private fun observeWeight() {
        lifecycleScope.launch {
            binding.startingWeightValue.text =
                getString(R.string.label_starting_weight, user.startWeight)
            val currentWeightOfTheDay = viewModel.getCurrentWeightOfTheDay(currentDayId)
            val currentWeight =
                if (currentWeightOfTheDay == 0.0) user.startWeight else currentWeightOfTheDay

            binding.currentWeightValue.text =
                getString(R.string.label_starting_weight, user.currentWeight)
            binding.targetWeightValue.text =
                getString(R.string.label_starting_weight, user.targetWeight)
        }
    }

    private fun updateProgressBar(date: String) {

        lifecycleScope.launch {
            sharedViewModel.foodsState.collectLatest { state ->

                when (state) {
                    is FoodResult.Error -> {}
                    FoodResult.Loading -> {
                        binding.progressBarLoading.visibility = View.VISIBLE
                        binding.scrollContent.visibility = View.GONE
                        binding.cardViewGreeting.visibility = View.GONE

                    }
                    is FoodResult.Success -> {
                        binding.progressBarLoading.visibility = View.GONE
                        binding.scrollContent.visibility = View.VISIBLE
                        binding.cardViewGreeting.visibility = View.VISIBLE

                        val nutrition = viewModel.getNutritionForDate(date, state.data)
                        val dailyTarget = viewModel.calculateDailyTarget(
                            user.maintenanceCalorie!!,
                            user.programPlan!!
                        )
                        updateCaloriesProgress(nutrition, dailyTarget)
                        updateNutrientTrackers(nutrition, dailyTarget)
                        populateTable(date, state.data)
                    }
                }

            }

        }

    }

    private fun updateNutrientTrackers(nutrition: Nutrition, dailyTarget: Nutrition) {


        updateTracker(
            binding.proteinTracker,
            R.string.protein,
            R.drawable.protein_image,
            dailyTarget.protein,
            nutrition.protein
        )

        updateTracker(
            binding.carbTracker,
            R.string.carbs,
            R.drawable.carbs_image,
            dailyTarget.carbs.toDouble(),
            nutrition.carbs.toDouble()
        )


        updateTracker(
            binding.fatTracker,
            R.string.fat,
            R.drawable.fat_image,
            dailyTarget.fat,
            nutrition.fat
        )
        updateTracker(
            binding.fiberTracker,
            R.string.fiber,
            R.drawable.fibre_image,
            dailyTarget.fiber,
            nutrition.fiber
        )
    }

    private fun updateTracker(
        tracker: TrackerItemBinding,
        label: Int,
        icon: Int,
        target: Double,
        current: Double
    ) {
        tracker.apply {
            Label.setText(label)
            total.text = target.toInt().toString()
            eatenFibre.text = formatValue(current)
            Progressbar.progressMax = target.toFloat()
            Progressbar.progress = current.toFloat()
            trackerIcon.setImageResource(icon)
        }

        animateProgress(tracker.Progressbar, current.toFloat(), tracker.eatenFibre)

    }


    private fun updateCaloriesProgress(nutrition: Nutrition, dailyTarget: Nutrition) {
        binding.apply {

            caloriesProgressBar.apply {
                progressMax = dailyTarget.calorie.toFloat()
                progress = nutrition.calorie.toFloat()
            }
            val remainingCalories = (dailyTarget.calorie - nutrition.calorie).coerceAtLeast(0)
            remainingCaloriesValue.text = remainingCalories.toString()

            animateProgress(caloriesProgressBar, nutrition.calorie.toFloat(), eatenCaloriesValue)
            animateProgress(dailyTarget.calorie, remainingCalories, remainingCaloriesValue)
        }

    }

    private fun animateProgress(
        target: Int,
        value: Int,
        textView: TextView
    ) {
        val animator = ValueAnimator.ofInt(target, value)
        animator.duration = 700
        animator.addUpdateListener { animation ->
            textView.text = animation.animatedValue.toString()
        }
        animator.start()
    }

    private fun populateTable(date: String, foodList: List<Food>) {

        lifecycleScope.launch {
            val tableLayout = binding.progressTable

            val childCount = tableLayout.childCount

            if (childCount > 1) {
                tableLayout.removeViews(1, childCount - 1)
            }

            val days = viewModel.progressData(currentDayId, user, date, foodList)

            if (!days.isEmpty()) {

                days.forEachIndexed { index, day ->
                    val row = TableRow(requireContext())
                    row.addView(createTextView(day.day))
                    row.addView(createTextView(day.calories))
                    row.addView(
                        createTextView(
                            if (day.workout) getString(R.string.done) else getString(
                                R.string.not_done
                            )
                        )
                    )
                    row.addView(createTextView(day.water))
                    row.addView(createTextView(day.weight))
                    row.addView(
                        createTextView(
                            if (day.sleep) getString(R.string.done) else getString(
                                R.string.not_done
                            )
                        )
                    )
                    tableLayout.addView(row)
                }
            } else {
                val row = TableRow(requireContext())

                val textView = TextView(requireContext())
                textView.apply {
                    this.setText(R.string.no_data_available)
                    setPadding(8, 8, 8, 8)
                    gravity = Gravity.CENTER_HORIZONTAL
                }

                row.addView(textView)
                row.setBackgroundResource(R.drawable.table_card)
                tableLayout.addView(row)
            }
        }
    }

    private fun createTextView(text: String): TextView {
        val textView = TextView(requireContext())
        textView.apply {
            this.text = text
            setPadding(16, 8, 16, 8)
            gravity = Gravity.CENTER
        }
        return textView
    }


    private fun animateProgress(
        caloriesProgressBar: CircularProgressBar,
        value: Float,
        textView: TextView,
    ) {
        ObjectAnimator.ofFloat(
            caloriesProgressBar,
            "progress",
            0f,
            value
        ).apply {
            duration = 700
            start()
        }
        ValueAnimator.ofInt(0, value.toInt()).apply {
            duration = 700
            addUpdateListener {
                textView.text = it.animatedValue.toString()
            }
            start()
        }

    }


    private fun formatValue(value: Double): String = getString(R.string.formatted_value, value)

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    inner class DashboardViewModelFactory(
        private val getProgramWeeksUseCase: GetProgramWeeksUseCase,
        private val getTotalNutritionForMealUseCase: GetTotalNutritionForMealUseCase,
        private val getAllFoodLogsByDate: GetAllFoodLogsByDate,
        private val getWeightForDayIdUseCase: GetWeightForDayIdUseCase,
        private val calculateDailyTargetUseCase: CalculateDailyTargetUseCase,
        private val getProgressDataUseCase: GetProgressDataUseCase
    ) : ViewModelProvider.Factory {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return DashboardViewModel(
                getProgramWeeksUseCase,
                getTotalNutritionForMealUseCase,
                getAllFoodLogsByDate,
                getWeightForDayIdUseCase,
                calculateDailyTargetUseCase,
                getProgressDataUseCase
            ) as T
        }
    }
}
