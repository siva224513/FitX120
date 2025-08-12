package com.example.beginnerfit.ui.signIn


import android.content.Intent
import android.os.Bundle
import android.text.InputFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.beginnerfit.R
import com.example.beginnerfit.SessionManager
import com.example.beginnerfit.TrackerActivity
import com.example.beginnerfit.domain.repository.UserRepository
import com.example.beginnerfit.databinding.FragmentSignInBinding
import com.example.beginnerfit.domain.repository.Repository
import com.example.beginnerfit.domain.usecase.profile.SetupUserDataFromJsonToDbUseCase
import com.example.beginnerfit.domain.usecase.signin.GetUserUseCase
import com.example.beginnerfit.ui.signup.SignUpFragment
import com.example.beginnerfit.ui.signup.StartupFragment
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class SignInFragment : Fragment() {

    private var _binding: FragmentSignInBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: SignInViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignInBinding.inflate(inflater, container, false)
        val repository = Repository
        val setupUserDataFromJsonToDbUseCase = SetupUserDataFromJsonToDbUseCase(repository)
        val getUserUseCase = GetUserUseCase(repository)
        val factory = SignInViewModelFactory(getUserUseCase, setupUserDataFromJsonToDbUseCase)
        viewModel = ViewModelProvider(this, factory)[SignInViewModel::class.java]
        observeLoginResult()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        restoreValues()
        setDataToViewmodel()
        setupListener()
    }

    private fun restoreValues() {
        binding.apply {
            emailEditText.setText(viewModel.email)
            passwordEditText.setText(viewModel.password)
        }
    }

    private fun observeLoginResult() {
        lifecycleScope.launch {
            viewModel.signInResult.collect { response ->
                when (response) {
                    is SignInResult.Success -> {
                        Snackbar.make(requireView(), response.message, Snackbar.LENGTH_SHORT)
                            .setDuration(500).show()
                        UserRepository.setUser(response.userBackUpData.user)
                        SessionManager.saveUser(requireContext(), response.userBackUpData.user)

                        when {
                            UserRepository.getUser().isProfileCompleted() -> {
                                viewModel.setUpUserDataFromJsonToDb(response.userBackUpData)
                                val intent = Intent(requireContext(), TrackerActivity::class.java)
                                intent.putExtra("shouldShowLoader", true)
                                startActivity(intent)
                                requireActivity().finish()

                            }

                            else -> {
                                parentFragmentManager.beginTransaction()
                                    .setCustomAnimations(
                                        R.anim.boom_enter,
                                        R.anim.boom_exit,
                                        R.anim.boom_enter,
                                        R.anim.boom_exit
                                    )
                                    .replace(R.id.fragment_container, StartupFragment.newInstance())
                                    .commit()
                            }
                        }
                    }

                    is SignInResult.Failure -> {
                        Snackbar.make(requireView(), response.message, Snackbar.LENGTH_SHORT)
                            .setDuration(500).show()
                    }
                }

            }
        }
    }


    private fun setDataToViewmodel() {

        binding.apply {
            emailEditText.filters = arrayOf(InputFilter.LengthFilter(50))
            passwordEditText.filters = arrayOf(InputFilter.LengthFilter(20))
            emailEditText.doAfterTextChanged {
                viewModel.email = it.toString()
                emailErrorText.visibility = View.GONE

            }
            passwordEditText.doAfterTextChanged {
                viewModel.password = it.toString()
                passwordErrorText.visibility = View.GONE
            }
        }
    }

    private fun setupListener() {
        binding.apply {

            registerNow.setOnClickListener {
                parentFragmentManager.beginTransaction()
                    .setCustomAnimations(
                        R.anim.boom_enter,
                        R.anim.boom_exit,
                        R.anim.boom_enter,
                        R.anim.boom_exit
                    )
                    .replace(R.id.fragment_container, SignUpFragment.newInstance())
                    .addToBackStack(SIGN_UP_FRAGMENT)
                    .commit()
            }

            loginButton.setOnClickListener {
                val email = emailEditText.text.toString().trim()
                val password = passwordEditText.text.toString().trim()

                emailErrorText.visibility = View.GONE
                passwordErrorText.visibility = View.GONE

                when {
                    email.isEmpty() -> {
                        emailErrorText.text = getString(R.string.email_required)
                        emailErrorText.visibility = View.VISIBLE
                        emailEditText.requestFocus()
                    }
                    password.isEmpty() -> {
                        passwordErrorText.text = getString(R.string.password_required)
                        passwordErrorText.visibility = View.VISIBLE
                        passwordEditText.requestFocus()
                    }

                    else -> {
                        viewModel.login(email, password)
                    }
                }
            }

        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    companion object {
        fun newInstance() = SignInFragment()

        private const val SIGN_UP_FRAGMENT = "SignUpFragment"


    }

    inner class SignInViewModelFactory(
        val getUserUseCase: GetUserUseCase,
        private val setupUserDataFromJsonToDbUseCase: SetupUserDataFromJsonToDbUseCase
    ) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return SignInViewModel(getUserUseCase, setupUserDataFromJsonToDbUseCase) as T
        }
    }
}
