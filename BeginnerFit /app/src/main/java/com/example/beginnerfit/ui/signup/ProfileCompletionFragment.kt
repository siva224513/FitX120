package com.example.beginnerfit.ui.signup


import android.content.Context.INPUT_METHOD_SERVICE
import android.content.Intent
import android.os.Bundle
import android.text.InputFilter
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.beginnerfit.R
import com.example.beginnerfit.TrackerActivity
import com.example.beginnerfit.domain.repository.UserRepository
import com.example.beginnerfit.databinding.FragmentProfileCompletionBinding
import com.example.beginnerfit.domain.repository.Repository
import com.example.beginnerfit.domain.usecase.dashboard.CalculateMaintenanceCalorieUseCase
import com.example.beginnerfit.domain.usecase.profile.UpdateUserToFileUseCase
import com.example.beginnerfit.domain.usecase.signup.CompleteUserProfileUseCase
import com.example.beginnerfit.domain.usecase.workout.GetWorkoutsForDayUseCase
import com.example.beginnerfit.domain.usecase.workout.workoutprogram.GetLegsOneWorkoutUseCase
import com.example.beginnerfit.domain.usecase.workout.workoutprogram.GetLegsTwoWorkoutUseCase
import com.example.beginnerfit.domain.usecase.workout.workoutprogram.GetPullOneWorkoutUseCase
import com.example.beginnerfit.domain.usecase.workout.workoutprogram.GetPullTwoWorkoutUseCase
import com.example.beginnerfit.domain.usecase.workout.workoutprogram.GetPushOneWorkoutUseCase
import com.example.beginnerfit.domain.usecase.workout.workoutprogram.GetPushTwoWorkoutUseCase
import com.example.beginnerfit.domain.usecase.workout.workoutprogram.GetRestDayWorkoutUseCase
import com.example.beginnerfit.domain.usecase.workout.workoutprogram.InsertProgramScheduleUseCase
import com.example.beginnerfit.domain.usecase.workout.workoutprogram.InsertWorkoutDayDetailsUseCase
import com.example.beginnerfit.domain.usecase.workout.workoutprogram.InsertWorkoutWeeksUseCase
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class ProfileCompletionFragment() : Fragment() {
    private lateinit var viewModel: ProfileCompletionViewModel
    private val user = UserRepository.getUser()

    private var _binding: FragmentProfileCompletionBinding? = null
    private val binding get() = _binding!!

    companion object {
        fun newInstance(): ProfileCompletionFragment {
            return ProfileCompletionFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileCompletionBinding.inflate(inflater, container, false)

        createViewModel()

        return binding.root
    }

    private fun createViewModel() {
        val repository = Repository

        val calculateMaintenanceCalorieUseCase = CalculateMaintenanceCalorieUseCase()
        val completeUserProfileUseCase =
            CompleteUserProfileUseCase(calculateMaintenanceCalorieUseCase)
        val updateUserToFileUseCase = UpdateUserToFileUseCase(repository)


        val getWorkoutsForDayUseCase = GetWorkoutsForDayUseCase(
            GetPushOneWorkoutUseCase(repository),
            GetPushTwoWorkoutUseCase(repository),
            GetPullOneWorkoutUseCase(repository),
            GetPullTwoWorkoutUseCase(repository),
            GetLegsOneWorkoutUseCase(repository),
            GetLegsTwoWorkoutUseCase(repository),
            GetRestDayWorkoutUseCase(repository),
            repository
        )

        val insertWorkoutWeeksUseCase = InsertWorkoutWeeksUseCase(
            getWorkoutsForDayUseCase,
            InsertProgramScheduleUseCase(repository),
            InsertWorkoutDayDetailsUseCase(repository)
        )

        val factory = ProfileCompletionViewModelFactory(
            completeUserProfileUseCase,
            insertWorkoutWeeksUseCase,
            updateUserToFileUseCase
        )
        viewModel = ViewModelProvider(this, factory)[ProfileCompletionViewModel::class.java]
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.genderSpinner.adapter = createAdapter(listOf("Select Gender", "Male", "Female"))
        setupProgramTypeSpinner()
        setDataToViewModel()
        setListeners()
    }

    private fun setupProgramTypeSpinner() {
        val programTypes = resources.getStringArray(R.array.program_types).toList()
        val adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, programTypes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.programTypeSpinner.adapter = adapter
    }


    private fun setListeners() {


        binding.apply {
            ageEditText.doAfterTextChanged {
                viewModel.userAge = it.toString()
                ageErrorTextView.visibility = View.GONE
            }
            ageEditText.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    genderSpinner.performClick()
                    val imm = requireContext().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(ageEditText.windowToken, 0)
                    ageEditText.clearFocus()
                    true
                } else false
            }
            heightEditText.doAfterTextChanged {
                viewModel.userHeight = it.toString()
                heightErrorTextView.visibility = View.GONE
            }
            currentWeightEditText.doAfterTextChanged {
                viewModel.currentWeight = it.toString()
                currentWeightErrorTextView.visibility = View.GONE
            }
            targetWeightEditText.doAfterTextChanged {
                viewModel.targetWeight = it.toString()
                targetWeightErrorTextView.visibility = View.GONE
            }

            targetWeightEditText. setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    programTypeSpinner.performClick()
                    val imm = requireContext().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(targetWeightEditText.windowToken, 0)
                    targetWeightEditText.clearFocus()
                    true
                } else false
            }

            genderSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?, view: View?, position: Int, id: Long
                ) {
                    viewModel.genderPosition = position
                    if (position > 0) {
                        genderErrorTextView.visibility = View.GONE
                        heightEditText.postDelayed({
                            heightEditText.requestFocus()
                            val imm = requireContext()
                                .getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                            imm.showSoftInput(heightEditText, InputMethodManager.SHOW_IMPLICIT)
                        }, 100)
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {

                }

            }

            programTypeSpinner.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        viewModel.programTypePosition = position
                        if (position > 0) {
                            programTypeErrorTextView.visibility = View.GONE
                        }
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {

                    }
                }

            profileCompletionButton.setOnClickListener {
                if (validateForm()) {

                    lifecycleScope.launch {
                        val updated = viewModel.completeUser(user)

                        if (updated) {
                            binding.progressbar.visibility = View.VISIBLE
                            binding.programContainer.visibility = View.GONE

                            delay(100)
                            viewModel.generateWorkoutProgram()

                            binding.progressbar.visibility = View.GONE

                            val intent = Intent(requireContext(), TrackerActivity::class.java)
                            startActivity(intent)
                            requireActivity().finish()


                        } else {
                            Snackbar.make(
                                requireView(),
                                "Failed to update profile",
                                Snackbar.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }

        }
    }

    private fun validateForm(): Boolean {
        var isValid = true

        binding.apply {
            ageErrorTextView.visibility = View.GONE
            genderErrorTextView.visibility = View.GONE
            heightErrorTextView.visibility = View.GONE
            currentWeightErrorTextView.visibility = View.GONE
            targetWeightErrorTextView.visibility = View.GONE

            val age = ageEditText.text.toString().trim()
            val height = heightEditText.text.toString().trim()
            val currentWeight = currentWeightEditText.text.toString().trim()
            val targetWeight = targetWeightEditText.text.toString().trim()

            if (age.isEmpty() || age.toIntOrNull() == null || age.toInt() !in 10..100) {
                ageErrorTextView.text = getString(R.string.error_age_invalid)
                ageErrorTextView.visibility = View.VISIBLE
                isValid = false
            }

            if (genderSpinner.selectedItemPosition == 0) {
                genderErrorTextView.text = getString(R.string.error_gender_empty)
                genderErrorTextView.visibility = View.VISIBLE
                isValid = false
            }

            if(programTypeSpinner.selectedItemPosition ==0){
                programTypeErrorTextView.text = getString(R.string.error_program_type)
                programTypeErrorTextView.visibility = View.VISIBLE
                isValid = false
            }

            if (height.isEmpty() || height.toFloatOrNull() == null || height.toFloat() !in 50f..250f) {
                heightErrorTextView.text = getString(R.string.error_height_invalid)
                heightErrorTextView.visibility = View.VISIBLE
                isValid = false
            }

            if (currentWeight.isEmpty() || currentWeight.toFloatOrNull() == null || currentWeight.toFloat() !in 20f..300f) {
                currentWeightErrorTextView.text = getString(R.string.error_current_weight_invalid)
                currentWeightErrorTextView.visibility = View.VISIBLE
                isValid = false
            }

            if (targetWeight.isEmpty() || targetWeight.toFloatOrNull() == null || targetWeight.toFloat() !in 20f..300f) {
                targetWeightErrorTextView.text = getString(R.string.error_target_weight_invalid)
                targetWeightErrorTextView.visibility = View.VISIBLE
                isValid = false
            } else if (targetWeight.toFloat() == currentWeight.toFloat()) {
                targetWeightErrorTextView.text = getString(R.string.error_target_weight_same)
                targetWeightErrorTextView.visibility = View.VISIBLE
                isValid = false
            }
        }
        return isValid
    }


    private fun setDataToViewModel() {

        binding.apply {
            ageEditText.filters = arrayOf(InputFilter.LengthFilter(2))
            heightEditText.filters = arrayOf(InputFilter.LengthFilter(5))
            currentWeightEditText.filters = arrayOf(InputFilter.LengthFilter(5))
            targetWeightEditText.filters = arrayOf(InputFilter.LengthFilter(5))
        }

        binding.apply {
            ageEditText.setText(viewModel.userAge)
            heightEditText.setText(viewModel.userHeight)
            currentWeightEditText.setText(viewModel.currentWeight)
            targetWeightEditText.setText(viewModel.targetWeight)
            genderSpinner.setSelection(viewModel.genderPosition)
            programTypeSpinner.setSelection(viewModel.programTypePosition)
        }
    }


    private fun createAdapter(items: List<String>): ArrayAdapter<String> {
        return ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, items).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
    }

    inner class ProfileCompletionViewModelFactory(
        private val completeUserProfileUseCase: CompleteUserProfileUseCase,
        private val insertWorkoutWeeksUseCase: InsertWorkoutWeeksUseCase,
        private val updateUserToFileUseCase: UpdateUserToFileUseCase
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ProfileCompletionViewModel(
                completeUserProfileUseCase,
                insertWorkoutWeeksUseCase,
                updateUserToFileUseCase
            ) as T
        }
    }
}
