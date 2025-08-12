package com.example.beginnerfit.ui.foodTrack

import android.os.Bundle
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.beginnerfit.domain.usecase.food.GetFoodScheduleUseCase
import com.example.beginnerfit.domain.usecase.food.GetFoodWeeksUseCase
import com.example.beginnerfit.R
import com.example.beginnerfit.databinding.FragmentFoodTrackBinding
import com.example.beginnerfit.domain.repository.Repository
import com.example.beginnerfit.domain.repository.UserRepository
import com.example.beginnerfit.model.WorkoutDay
import com.example.beginnerfit.ui.foodTrack.food.FoodFragment
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class FoodTrackFragment : Fragment() {

    companion object
    {
       private const val  FOOD_FRAGMENT ="FoodFragment"
    }
    private var _binding: FragmentFoodTrackBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: FoodTrackViewModel
    private lateinit var adapter: FoodDayAdapter
    private var previousWeekIndex: Int = 0
    private val user  = UserRepository.getUser()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFoodTrackBinding.inflate(inflater, container, false)
        val repository = Repository
        val factory =
            FoodTrackWeeksViewModelFactory(GetFoodWeeksUseCase(GetFoodScheduleUseCase(repository)))
        viewModel = ViewModelProvider(this, factory)[FoodTrackViewModel::class.java]
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
             viewModel.loadDates()
        observeViewModel()
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        adapter = FoodDayAdapter(emptyList()) { day ->
            openWorkoutDetailFragment(day)
        }
        binding.foodRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.foodRecyclerView.adapter = adapter
    }

    private fun openWorkoutDetailFragment(date: LocalDate) {
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
        val day = date.format(formatter)!!
        requireActivity().supportFragmentManager.beginTransaction()
            .setCustomAnimations(
                R.anim.boom_enter,
                R.anim.boom_exit,
                R.anim.boom_enter,
                R.anim.boom_exit
            )
            .replace(
                R.id.flFragment,
                FoodFragment.newInstance(day)
            ).addToBackStack(FOOD_FRAGMENT).commit()
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
                viewModel.weeks.value?.let { weeks->
                    showWeek(index,weeks)
                    updateTabSelection(index)
                }
            }
        }
    }

    private fun showWeek(currentIndex: Int, weeks: List<List<LocalDate>>) {
        if (currentIndex !in weeks.indices) return

        val context = binding.foodRecyclerView.context
        val animRes = when {
            currentIndex > previousWeekIndex -> R.anim.layout_animation_right
            currentIndex < previousWeekIndex -> R.anim.layout_animation_left
            else -> null
        }

        animRes?.let {
            val animation = AnimationUtils.loadLayoutAnimation(context, it)
            binding.foodRecyclerView.layoutAnimation = animation
            binding.foodRecyclerView.scheduleLayoutAnimation()
        }

        adapter.updateDays(weeks[currentIndex])
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



    private fun populateWeekTabs(weeks: List<List<LocalDate>>) {
        binding.weekTabs.removeAllViews()
        for (i in weeks.indices) {
            val styledContext = ContextThemeWrapper(
                requireContext(),
                if (i == viewModel.selectedWeekIndex.value) R.style.FoodWeekTab else R.style.WeekTab
            )
            val tv = TextView(styledContext).apply {
                text = "WEEK ${i + 1}"
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
                tab.setTextAppearance(R.style.FoodWeekTab)
                tab.setBackgroundResource(R.drawable.bg_week_tab_active)
            } else {
                tab.setTextAppearance(R.style.WeekTab)
                tab.setBackgroundResource(R.drawable.bg_week_tab_default)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    inner class FoodTrackWeeksViewModelFactory(private val getFoodWeeksUseCase: GetFoodWeeksUseCase) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return FoodTrackViewModel(getFoodWeeksUseCase) as T
        }
    }
}
