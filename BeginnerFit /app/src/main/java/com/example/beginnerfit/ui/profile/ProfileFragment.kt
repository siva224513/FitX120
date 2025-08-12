package com.example.beginnerfit.ui.profile

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat.recreate
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.beginnerfit.AccountSettingFragment
import com.example.beginnerfit.ClearAllUserDataUseCase
import com.example.beginnerfit.MainActivity
import com.example.beginnerfit.R
import com.example.beginnerfit.TrackerActivity
import com.example.beginnerfit.databinding.FragmentProfileBinding
import com.example.beginnerfit.domain.repository.Repository
import com.example.beginnerfit.domain.repository.UserRepository
import com.example.beginnerfit.domain.usecase.profile.BackUpUserDataUseCase
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import androidx.core.content.edit
import com.example.beginnerfit.MyApplication
import kotlinx.serialization.json.Json.Default.configuration


class ProfileFragment : Fragment() {

    companion object {
        const val PREF_NAME = "app_prefs"
        const val KEY_DARK_MODE = "dark_mode"

        const val MALE = "male"
        const val FEMALE = "female"

    }

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!


    private lateinit var viewModel: ProfileViewModel

    val user = UserRepository.getUser()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)

        val repository = Repository
        val backUpDataUseCase = BackUpUserDataUseCase(repository)
        val clearAllUserDataUseCase = ClearAllUserDataUseCase(repository)
        val factory = ProfileViewModelFactory(backUpDataUseCase, clearAllUserDataUseCase)
        viewModel = ViewModelProvider(this, factory)[ProfileViewModel::class.java]
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupProfile()
        setupListeners()
        observeLogoutResult()
    }

    private fun observeLogoutResult() {
        lifecycleScope.launch {
            viewModel.signOutResult.collect { response ->
                when (response) {
                    is SignOutResult.Success -> {
                        Snackbar.make(requireView(), response.message, Snackbar.LENGTH_SHORT)
                            .setDuration(500).show()
                        startActivity(Intent(requireContext(), MainActivity::class.java))
                        requireActivity().finish()
                    }

                    is SignOutResult.Failure -> {
                        Snackbar.make(requireView(), response.message, Snackbar.LENGTH_SHORT)
                            .setDuration(500).show()
                    }
                }

            }

        }
    }

    private fun setupProfile() {

        binding.tvUserName.text = user.name
        binding.tvEmail.text = user.email
        binding.weightText.text = user.currentWeight.toString()
        binding.heightText.text = user.height.toString()
        val bmi = calculateBmi(user.currentWeight!!, user.height!!)
        binding.bmiText.text = String.format("%.1f", bmi)

        val gender = user.gender

        if (gender.equals(MALE, ignoreCase = true)) {
            binding.profileImage.setImageResource(R.drawable.profile_default_image)
        } else if (gender.equals(FEMALE, ignoreCase = true)) {
            binding.profileImage.setImageResource(R.drawable.profile_default_image_female)
        }
    }

    fun calculateBmi(weightKg: Double, heightCm: Double): Double {
        val heightMeters = heightCm / 100.0
        val bmi = weightKg / (heightMeters * heightMeters)
        return bmi
    }


    private fun setupListeners() {


        binding.aboutSection.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .setCustomAnimations(
                    R.anim.boom_enter,
                    R.anim.boom_exit,
                    R.anim.boom_enter,
                    R.anim.boom_exit
                )
                .replace(R.id.flFragment, AboutSectionFragment())
                .addToBackStack(null)
                .commit()
        }

        binding.logoutButton.setOnClickListener {
            showConfirmationDialog(
                getString(R.string.logout),
                getString(R.string.logout_alert)
            ) { viewModel.logout() }
        }

        binding.profileSection.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .setCustomAnimations(
                    R.anim.boom_enter,
                    R.anim.boom_exit,
                    R.anim.boom_enter,
                    R.anim.boom_exit
                )
                .replace(R.id.flFragment, UserDetailsFragment())
                .addToBackStack(null)
                .commit()
        }
        binding.accountSection.setOnClickListener {

            requireActivity().supportFragmentManager.beginTransaction()
                .setCustomAnimations(
                    R.anim.boom_enter,
                    R.anim.boom_exit,
                    R.anim.boom_enter,
                    R.anim.boom_exit
                )
                .replace(R.id.flFragment, AccountSettingFragment())
                .addToBackStack(null)
                .commit()
        }

        val prefs = requireContext().getSharedPreferences(PREF_NAME, MODE_PRIVATE)
        val isDarkMode = prefs.getBoolean(KEY_DARK_MODE, false)


        binding.themeSwitch.isChecked = isDarkMode
        binding.themeSwitch.text =
            if (isDarkMode) getString(R.string.dark_mode) else getString(R.string.light_mode)

        binding.themeSwitch.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit { putBoolean(KEY_DARK_MODE, isChecked) }
            val mode =
                if (isChecked) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO

            AppCompatDelegate.setDefaultNightMode(mode)
            binding.themeSwitch.text =
                if (isChecked) getString(R.string.dark_mode) else getString(R.string.light_mode)

        }

//        checkCurrentTheme()

    }

//    private fun checkCurrentTheme() {
//        val currentNightMode =
//            resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
//
//
//        when (currentNightMode) {
//            Configuration.UI_MODE_NIGHT_NO -> {
//                println("UI_MODE_NIGHT_NO")
//            }
//
//            Configuration.UI_MODE_NIGHT_YES -> {
//                println("UI_MODE_NIGHT_YES")
//            }
//
//        }
//    }


    private fun showConfirmationDialog(
        title: String,
        message: String,
        onConfirm: () -> Unit
    ) {
        val dialog = AlertDialog.Builder(requireContext())
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(getString(R.string.yes)) { _, _ -> onConfirm() }
            .setNegativeButton(getString(R.string.no), null)
            .show()


        dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            ?.setTextColor(ContextCompat.getColor(requireContext(), R.color.PastTextColor))

        dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
            ?.setTextColor(ContextCompat.getColor(requireContext(), R.color.PastTextColor))


    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    inner class ProfileViewModelFactory(
        private val backUpDataUseCase: BackUpUserDataUseCase,
        private val clearAllUserDataUseCase: ClearAllUserDataUseCase
    ) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ProfileViewModel(backUpDataUseCase, clearAllUserDataUseCase) as T
        }
    }
}
