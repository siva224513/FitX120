package com.example.beginnerfit.ui.workoutTrack

import android.os.Bundle
import android.view.ContextThemeWrapper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.beginnerfit.R
import com.example.beginnerfit.TrackerViewModel
import com.example.beginnerfit.databinding.FragmentWorkoutTrackBinding
import com.example.beginnerfit.domain.repository.Repository
import com.example.beginnerfit.domain.repository.UserRepository
import com.example.beginnerfit.domain.usecase.profile.CalculateStreakUseCase
import com.example.beginnerfit.domain.usecase.workout.GetProgramSchedule
import com.example.beginnerfit.domain.usecase.workout.GetProgramWorkOutByIdUseCase
import com.example.beginnerfit.domain.usecase.workout.GetWorkoutWeeksUseCase
import com.example.beginnerfit.model.WorkoutDay
import com.example.beginnerfit.ui.workoutTrack.workout.WorkoutDetailFragment
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.getValue

class WorkoutTrackFragment : Fragment() {


    companion object {
        const val FRAGMENT_NAME = "workoutDetailFragment"
        const val DATE_PATTERN = "dd-MM-yyyy"
    }

    private var _binding: FragmentWorkoutTrackBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: WorkoutTrackViewModel
    private lateinit var adapter: WorkoutListAdapter
    private val sharedViewModel: TrackerViewModel by activityViewModels<TrackerViewModel>()

    private var previousWeekIndex: Int = 0


    private val user = UserRepository.getUser()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWorkoutTrackBinding.inflate(inflater, container, false)

        createViewModel()
        observeViewModel()

        return binding.root
    }

    fun createViewModel() {
        val repository = Repository
        val getWorkoutWeeksUseCase = GetWorkoutWeeksUseCase(
            GetProgramSchedule(repository),
            GetProgramWorkOutByIdUseCase(repository)
        )
        val calculateStreakUseCase = CalculateStreakUseCase(repository)
        val factory = WorkoutTrackViewModelFactory(
            getWorkoutWeeksUseCase,
            calculateStreakUseCase
        )
        viewModel = ViewModelProvider(this, factory)[WorkoutTrackViewModel::class.java]
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setAdapter()

        val today = LocalDate.now()
        val formattedDate = today.format(DateTimeFormatter.ofPattern(DATE_PATTERN))

        viewModel.loadSelectedDate(formattedDate)
        viewModel.calculateStreak()

    }


    private fun setAdapter() {
        adapter = WorkoutListAdapter(emptyList()) { day ->
            openWorkoutDetailFragment(day)
        }
        binding.workoutRecyclerView.adapter = adapter
        binding.workoutRecyclerView.layoutManager = LinearLayoutManager(requireContext())

    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.weeks.observe(viewLifecycleOwner) { weeks ->
                populateWeekTabs(weeks)
                showWeek(viewModel.selectedWeekIndex.value ?: 0, weeks)
            }
        }

        lifecycleScope.launch {
            viewModel.selectedWeekIndex.observe(viewLifecycleOwner) { index ->
                viewModel.weeks.value?.let { weeks ->
                    showWeek(index, weeks)
                    updateTabSelection(index)
                }
            }
        }

        lifecycleScope.launch {
            viewModel.currentStreak.observe(viewLifecycleOwner) { currentStreak ->
                binding.streakCount.text = currentStreak.toString()
            }
        }


    }

    private fun populateWeekTabs(weeks: List<List<WorkoutDay>>) {
        binding.weekTabs.removeAllViews()
        for (i in weeks.indices) {
            val styledContext = ContextThemeWrapper(
                requireContext(),
                if (i == viewModel.selectedWeekIndex.value) R.style.WeekTab_Active else R.style.WeekTab
            )
            val tv = TextView(styledContext).apply {
                text = getString(R.string.week_text, i + 1)
                setOnClickListener {
                    viewModel.setSelectedWeek(i)
                }
            }
            binding.weekTabs.addView(tv)
        }
    }

    private fun updateTabSelection(selectedIndex: Int) {
        for (i in 0 until binding.weekTabs.childCount) {
            val tab = binding.weekTabs.getChildAt(i) as TextView
            if (i == selectedIndex) {
                tab.setTextAppearance(R.style.WeekTab_Active)
                tab.setBackgroundResource(R.drawable.bg_week_tab_active)
            } else {
                tab.setTextAppearance(R.style.WeekTab)
                tab.setBackgroundResource(R.drawable.bg_week_tab_default)
            }
        }
    }

    private fun showWeek(currentIndex: Int, weeks: List<List<WorkoutDay>>) {
        if (currentIndex !in weeks.indices) return

        val context = binding.workoutRecyclerView.context
        val animRes = when {
            currentIndex > previousWeekIndex -> R.anim.layout_animation_right
            currentIndex < previousWeekIndex -> R.anim.layout_animation_left
            else -> null
        }

        animRes?.let {
            val animation = AnimationUtils.loadLayoutAnimation(context, it)
            binding.workoutRecyclerView.layoutAnimation = animation
            binding.workoutRecyclerView.scheduleLayoutAnimation()
        }

        adapter.updateDays(weeks[currentIndex], viewModel.selectedDate.value)
        scrollToSelectedTab(currentIndex)
        previousWeekIndex = currentIndex
    }

    private fun scrollToSelectedTab(index: Int) {
        val tab = binding.weekTabs.getChildAt(index) ?: return
        binding.weekScroll.post {
            val scrollX = tab.left - (binding.weekScroll.width - tab.width) / 2
            binding.weekScroll.smoothScrollTo(scrollX, 0)
        }
    }


    private fun openWorkoutDetailFragment(day: WorkoutDay) {
        requireActivity().supportFragmentManager.beginTransaction()
            .setCustomAnimations(
                R.anim.boom_enter,
                R.anim.boom_exit,
                R.anim.boom_enter,
                R.anim.boom_exit
            )
            .replace(
                R.id.flFragment,
                WorkoutDetailFragment.newInstance(day.workouts, day.dayNumber, day.date)
            )
            .addToBackStack(FRAGMENT_NAME).commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    inner class WorkoutTrackViewModelFactory(
        private val getWorkoutWeeksUseCase: GetWorkoutWeeksUseCase,
        private val calculateStreakUseCase: CalculateStreakUseCase,
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return WorkoutTrackViewModel(getWorkoutWeeksUseCase, calculateStreakUseCase) as T
        }
    }
}
