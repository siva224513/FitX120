package com.example.beginnerfit.ui.workoutTrack.workout

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.beginnerfit.MyApplication
import com.example.beginnerfit.R
import com.example.beginnerfit.TrackerViewModel
import com.example.beginnerfit.databinding.FragmentWorkoutDetailBinding
import com.example.beginnerfit.domain.repository.Repository
import com.example.beginnerfit.domain.usecase.workout.SaveDailyStreakUseCase
import com.example.beginnerfit.model.WorkOutDetail
import com.google.android.material.snackbar.Snackbar
import java.io.Serializable
import java.time.LocalDate

class WorkoutDetailFragment : Fragment() {

    companion object {
        fun newInstance(
            workouts: List<WorkOutDetail>,
            dayNumber: Int,
            date: LocalDate
        ): WorkoutDetailFragment {
            return WorkoutDetailFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(WORKOUTS, workouts as Serializable)
                    putString(DATE, date.toString())
                    putInt(DAY_NUMBER, dayNumber)
                }
            }
        }

        const val WORKOUTS = "workouts"
        const val DATE = "date"
        const val DAY_NUMBER = "dayNumber"

        const val PUSH = "push"
        const val PULL = "pull"
        const val LEG = "leg"
        const val REST = "rest"
        const val WORKOUT_FRAGMENT = "WorkoutFragment"

    }

    private lateinit var workouts: MutableList<WorkOutDetail>
    private var dayNumber: Int = 0
    private lateinit var date: String

    private var _binding: FragmentWorkoutDetailBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: WorkoutDetailListAdapter


    private lateinit var viewModel: WorkoutDetailViewModel
    private val sharedViewModel: TrackerViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        println("WorkoutDetailFragment onCreate")
        arguments?.let {
            workouts = it.getSerializable(WORKOUTS) as MutableList<WorkOutDetail>
            dayNumber = it.getInt(DAY_NUMBER)
            date = it.getString(DATE).toString()
        }


        val repository = Repository
        val factory = WorkoutDetailViewModelFactory(SaveDailyStreakUseCase(repository))
        viewModel = ViewModelProvider(this, factory)[WorkoutDetailViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWorkoutDetailBinding.inflate(inflater, container, false)
        println("WorkoutDetailFragment onCreateView")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        println("WorkoutDetailFragment onViewCreate $date")

        if (!sharedViewModel.isEditableDay(date)) {
            binding.workoutDoneButton.setDisabledState(false)
        }

        setupHeader()
        setupRecyclerView()
        setupListeners()
    }

    fun View.setDisabledState(enabled: Boolean) {
        this.isEnabled = enabled
        this.isClickable = enabled
        this.alpha = if (enabled) 1f else 0.5f
    }

    override fun onPause() {
        super.onPause()
        println("WorkoutDetailFragment pause")
    }

    override fun onStop() {
        super.onStop()
        println("WorkoutDetailFragment stop")
    }

    private fun setupHeader() {

        val drawable = ContextCompat.getDrawable(
            MyApplication.getContext(),
            R.drawable.outline_fitness_center_24
        )
        val newColor = ContextCompat.getColor(MyApplication.getContext(), R.color.red)
        if (drawable != null) {
            DrawableCompat.setTint(drawable, newColor)
        }

        binding.workoutIcon.setImageDrawable(drawable)

        val workoutTitle =
            if (workouts.isNotEmpty()) getFormattedCategory(workouts[0].category) else WORKOUTS
        binding.workoutTitle.text = workoutTitle

        binding.exerciseCount.text =
            requireContext().getString(R.string.exercise_count_format, workouts.size)
        binding.workoutDoneButton.visibility =
            if (workouts.isNotEmpty()) View.VISIBLE else View.GONE


    }


    private fun getFormattedCategory(category: String): String {
        return when {
            category.contains(
                PUSH,
                ignoreCase = true
            ) -> requireContext().getString(R.string.push_day_workout)

            category.contains(
                PULL,
                ignoreCase = true
            ) -> requireContext().getString(R.string.pull_day_workout)

            category.contains(
                LEG,
                ignoreCase = true
            ) -> requireContext().getString(R.string.leg_day_workout)

            category.contains(
                REST,
                ignoreCase = true
            ) -> requireContext().getString(R.string.active_rest_day)

            else -> WORKOUTS
        }
    }

    private fun setupRecyclerView() {

        adapter = WorkoutDetailListAdapter(workouts) { workout ->
            openWorkoutFragment(workout)
        }

        binding.workoutDetailRecyclerView.apply {

            layoutManager = LinearLayoutManager(requireContext())

            adapter = this@WorkoutDetailFragment.adapter
        }
    }


    private fun setupListeners() {
        binding.workoutDoneButton.setOnClickListener {

            val isAllCompleted = workouts.all { it.isCompleted == 1 }
            if (isAllCompleted) {
                viewModel.addStreak(dayNumber, date)
                Snackbar.make(binding.root, R.string.workout_completed, Snackbar.LENGTH_SHORT)
                    .show()
                parentFragmentManager.popBackStack()
            } else {
                val dialog = AlertDialog.Builder(requireContext())
                    .setTitle(R.string.incomplete_workout)
                    .setMessage(R.string.workout_complete_message)
                    .setPositiveButton(R.string.ok, null)
                    .show()

                dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                    ?.setTextColor(ContextCompat.getColor(requireContext(), R.color.PastTextColor))

            }
        }
    }

    private fun openWorkoutFragment(workout: WorkOutDetail) {

        val editable = sharedViewModel.isEditableDay(date)
        requireActivity().supportFragmentManager.beginTransaction()
            .setCustomAnimations(
                R.anim.boom_enter,
                R.anim.boom_exit,
                R.anim.boom_enter,
                R.anim.boom_exit
            )
            .replace(R.id.flFragment, WorkoutFragment.newInstance(workout, editable))
            .addToBackStack(WORKOUT_FRAGMENT)
            .commit()
    }


    inner class WorkoutDetailViewModelFactory(
        private val saveDailyStreakUseCase: SaveDailyStreakUseCase
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return WorkoutDetailViewModel(saveDailyStreakUseCase) as T
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
