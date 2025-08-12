package com.example.beginnerfit.ui.signup

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.InputFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import com.example.beginnerfit.R
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.transition.Visibility
import com.example.beginnerfit.databinding.FragmentSignUpBinding
import com.example.beginnerfit.domain.repository.Repository
import com.example.beginnerfit.domain.usecase.signup.RegisterUserUseCase
import com.example.beginnerfit.ui.signIn.SignInFragment
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class SignUpFragment : Fragment() {


    companion object {
        fun newInstance() = SignUpFragment()

        private const val SIGN_N_FRAGMENT = "signInFragment"
    }

    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!

    private lateinit var signUpViewModel: SignUpViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentSignUpBinding.inflate(inflater, container, false)
        val repository = Repository
        val factory = SignUpViewModelFactory(RegisterUserUseCase(repository))
        signUpViewModel = ViewModelProvider(this, factory)[SignUpViewModel::class.java]
        observeViewModel()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        restoreValues()
        setDataToViewModel()
        setupListeners()
    }

    private fun setDataToViewModel() {
        val noSpacesFilter = InputFilter { source, _, _, _, _, _ ->
            if (source != null && source.contains(" ")) "" else null
        }
        binding.nameEditText.filters = arrayOf(InputFilter.LengthFilter(30))
        binding.emailEditText.filters = arrayOf(noSpacesFilter, InputFilter.LengthFilter(50))
        binding.passwordEditText.filters = arrayOf(noSpacesFilter, InputFilter.LengthFilter(20))
        binding.confirmPasswordEditText.filters = arrayOf(noSpacesFilter, InputFilter.LengthFilter(20))

        binding.apply {
            nameEditText.doAfterTextChanged {
                signUpViewModel.name = it.toString()
                nameError.visibility = View.GONE
            }
            emailEditText.doAfterTextChanged {
                signUpViewModel.email = it.toString()
                emailError.visibility = View.GONE
            }
            passwordEditText.doAfterTextChanged {
                signUpViewModel.password = it.toString()
                passwordError.visibility = View.GONE
            }
            confirmPasswordEditText.doAfterTextChanged {
                signUpViewModel.confirmPassword = it.toString()
                confirmPasswordError.visibility = View.GONE
            }

        }
    }


    private fun setupListeners() {
        binding.apply {

            signInText.setOnClickListener {
                parentFragmentManager.beginTransaction()
                    .setCustomAnimations(
                        R.anim.boom_enter,
                        R.anim.boom_exit,
                        R.anim.boom_enter,
                        R.anim.boom_exit
                    )
                    .replace(R.id.fragment_container, SignInFragment.newInstance())
                    .addToBackStack(SIGN_N_FRAGMENT)
                    .commit()
            }

            signUpButton.setOnClickListener {
                val name = nameEditText.text.toString().trim()
                val email = emailEditText.text.toString().trim()
                val password = passwordEditText.text.toString()
                val confirmPassword = confirmPasswordEditText.text.toString()

                when {
                    name.isBlank() -> {
                        nameError.visibility = View.VISIBLE
                        nameError.text = getString(R.string.name_required)
                        nameEditText.requestFocus()
                    }
                    name.length < 3 || name.length > 20 -> {
                        nameError.setText(R.string.error_username_length)
                        nameError.visibility = View.VISIBLE
                        nameEditText.requestFocus()
                    }
                    !name.matches(Regex("^[a-zA-Z0-9_]+$")) -> {
                        nameError.setText(R.string.error_username_invalid)
                        nameError.visibility = View.VISIBLE
                        nameEditText.requestFocus()
                    }

                    email.isBlank() -> {
                        emailError.visibility = View.VISIBLE
                        emailError.text = getString(R.string.email_required)
                        emailEditText.requestFocus()
                    }

                    !isValidEmail(email) -> {
                        emailError.visibility = View.VISIBLE
                        emailError.text = getString(R.string.invalid_email)
                        emailEditText.requestFocus()
                    }

                    password.isBlank() -> {
                        passwordError.visibility = View.VISIBLE
                        passwordError.text = getString(R.string.password_required)
                        passwordEditText.requestFocus()
                    }

                    password.length < 6 || password.length > 20 -> {
                        passwordError.text = getString(R.string.password_length_error)
                        passwordError.visibility = View.VISIBLE
                        passwordEditText.requestFocus()
                    }
                    !password.matches(Regex("^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@#\$%^&+=!*]).+$")) -> {
                        passwordError.text = getString(R.string.password_complexity_error)
                        passwordError.visibility = View.VISIBLE
                        passwordEditText.requestFocus()
                    }

                    confirmPassword.isBlank() -> {
                        confirmPasswordError.visibility = View.VISIBLE
                        confirmPasswordError.text = getString(R.string.confirm_password_required)
                        confirmPasswordEditText.requestFocus()
                    }

                    password != confirmPassword -> {
                        confirmPasswordError.visibility = View.VISIBLE
                        confirmPasswordError.text = getString(R.string.password_mismatch)
                        confirmPasswordEditText.requestFocus()
                    }

                    else -> {
                        signUpViewModel.register()
                    }
                }
            }
        }
    }

    fun isValidEmail(email: String): Boolean {
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$".toRegex()
        return email.matches(emailRegex)
    }


    private fun restoreValues() {
        binding.apply {
            nameEditText.setText(signUpViewModel.name)
            emailEditText.setText(signUpViewModel.email)
            passwordEditText.setText(signUpViewModel.password)
            confirmPasswordEditText.setText(signUpViewModel.confirmPassword)
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            signUpViewModel.signUpResult.collect { result ->
                if (result) {
                    Snackbar
                        .make(requireView(), R.string.register_success, Snackbar.LENGTH_SHORT)
                        .setDuration(500).show()
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, SignInFragment())
                        .commit()
                } else {
                    Snackbar.make(requireView(), R.string.register_failed, Snackbar.LENGTH_SHORT)
                        .setDuration(500).show()
                }

            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    inner class SignUpViewModelFactory(val registerUserUseCase: RegisterUserUseCase) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return SignUpViewModel(registerUserUseCase) as T
        }
    }


}