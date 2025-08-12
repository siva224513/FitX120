package com.example.beginnerfit.ui.workoutTrack.workout

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope


import androidx.recyclerview.widget.LinearLayoutManager
import com.example.beginnerfit.NetworkMonitor
import com.example.beginnerfit.R
import com.example.beginnerfit.databinding.FragmentWorkoutBinding
import com.example.beginnerfit.domain.repository.Repository
import com.example.beginnerfit.domain.usecase.workout.AdjustFutureWorkoutsSetsUseCase
import com.example.beginnerfit.domain.usecase.workout.SaveWorkoutLogUseCase
import com.example.beginnerfit.model.WorkOutDetail
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch


class WorkoutFragment : Fragment() {

    companion object {
        fun newInstance(workout: WorkOutDetail, editable: Boolean): WorkoutFragment {
            return WorkoutFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(WORKOUT, workout)
                    putBoolean(EDITABLE_DAY, editable)
                }
            }
        }

        private const val WORKOUT = "workout"
        private const val EDITABLE_DAY = "editableDay"
        private const val NO_INTERNET_HTML = """
                <html>
                <body style="display:flex;justify-content:center;align-items:center;height:100vh;margin:0;">
                  <div style="text-align:center;font-family:sans-serif;font-size:18px;color:red;">
                    No internet connection.<br>Please check your network.
                  </div>
                </body>
                </html>
                """
    }

    private var _binding: FragmentWorkoutBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: WorkoutViewModel
    private lateinit var workout: WorkOutDetail

    private lateinit var adapter: WorkoutAdapter

    private lateinit var networkMonitor: NetworkMonitor


    private var editableDay = false
    private val setList = mutableListOf<WorkoutSet>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        workout = arguments?.getParcelable(WORKOUT)!!
        editableDay = arguments?.getBoolean(EDITABLE_DAY)!!

        println("WorkoutFragment : $workout")

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWorkoutBinding.inflate(inflater, container, false)

        val repository = Repository
        val saveWorkoutLogUseCase = SaveWorkoutLogUseCase(repository)
        val adjustFutureWorkoutsSetsUseCase = AdjustFutureWorkoutsSetsUseCase(repository)
        val factory = WorkoutViewModelFactory(
            saveWorkoutLogUseCase,
            adjustFutureWorkoutsSetsUseCase
        )
        viewModel = ViewModelProvider(this, factory)[WorkoutViewModel::class.java]

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        if (!editableDay) {
            binding.Save.setDisabledState(false)
        }
        setWorkoutView()
        setupRecyclerView()
        setListeners()
    }

    fun View.setDisabledState(enabled: Boolean) {
        this.isEnabled = enabled
        this.isClickable = enabled
        this.alpha = if (enabled) 1f else 0.5f
    }

    private fun showWorkoutContent(isConnected: Boolean) {
        binding.webView.apply {
            settings.javaScriptEnabled = true
            webChromeClient = WebChromeClient()

            if (isConnected) {
                loadData(workout.url, "text/html", "utf-8")
            } else {
                loadData(
                    NO_INTERNET_HTML,
                    "text/html",
                    "utf-8"
                )
                Snackbar.make(binding.root, R.string.no_internet_connection, Snackbar.LENGTH_SHORT)
                    .show()
            }
        }
    }


    private fun setWorkoutView() {

        networkMonitor = NetworkMonitor(requireContext())
        networkMonitor.startMonitoring()

        val isConnected = networkMonitor.isInternetAvailable()
        showWorkoutContent(isConnected)

        networkMonitor.isConnected.observe(viewLifecycleOwner) { isConnected ->
            showWorkoutContent(isConnected)
        }

        binding.workoutTitle.text = workout.name
        binding.sets.text = workout.sets.toString()
        binding.reps.text = workout.reps
    }

    private fun setupRecyclerView() {
        val totalSets = workout.sets
        setList.clear()

        val weightList = workout.weight.split("-").mapNotNull { it.toDoubleOrNull() }

        val repsList = workout.reps.split("-").mapNotNull { it.toIntOrNull() }

        val targetWeightList = workout.weightUsed.split("-").mapNotNull { it.toDoubleOrNull() }

        val completedRepsList = workout.completedReps.split("-").mapNotNull { it.toIntOrNull() }

        setList.clear()
        repeat(totalSets) { index ->
            val kg = if (index < weightList.size) weightList[index] else 0.0
            val reps = if (index < repsList.size) repsList[index] else 0

            val weightUsed = if (index < targetWeightList.size) targetWeightList[index] else 0.0
            val completedReps = if (index < completedRepsList.size) completedRepsList[index] else 0

            setList.add(WorkoutSet(index + 1, kg, reps, weightUsed, completedReps))
        }

        adapter = WorkoutAdapter(setList)

        binding.recyclerViewWorkoutSets.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@WorkoutFragment.adapter

        }
        adapter.setEditable(editableDay)

    }


    private fun setListeners() {
        binding.Save.setOnClickListener {
            val weightUsed = setList.joinToString("-") { it.weightUsed.toString() }
            val completedReps = setList.joinToString("-") { it.completedReps.toString() }
            lifecycleScope.launch {
                val result =
                    viewModel.saveWorkoutLog(workout.id, workout.dayId, completedReps, weightUsed)
                workout.weightUsed = result.weightUsed
                workout.completedReps = result.completedReps
                workout.isCompleted = result.isCompleted

                Snackbar.make(
                    requireView(),
                    R.string.workout_saved_successfully,
                    Snackbar.LENGTH_SHORT
                )
                    .setDuration(500).show()

                viewModel.adjustFutureWorkoutsSets(workout)

                parentFragmentManager.popBackStack()

            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        networkMonitor.stopMonitoring()
        _binding = null
    }

    inner class WorkoutViewModelFactory(
        private val saveWorkoutLogUseCase: SaveWorkoutLogUseCase,
        private val adjustFutureWorkoutsSetsUseCase: AdjustFutureWorkoutsSetsUseCase
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return WorkoutViewModel(saveWorkoutLogUseCase, adjustFutureWorkoutsSetsUseCase) as T
        }
    }
}
