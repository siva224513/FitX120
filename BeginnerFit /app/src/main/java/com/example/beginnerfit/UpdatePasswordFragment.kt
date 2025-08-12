package com.example.beginnerfit

import android.os.Bundle
import android.text.InputFilter
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.beginnerfit.databinding.FragmentUpdatePasswordBinding
import com.example.beginnerfit.domain.repository.Repository
import com.example.beginnerfit.domain.repository.UserRepository
import com.example.beginnerfit.domain.usecase.signup.UpdateUserProfileInDbUseCase
import kotlinx.coroutines.launch

class UpdatePasswordFragment : Fragment() {

    companion object {
        fun newInstance() = UpdatePasswordFragment()
        val user = UserRepository.getUser()

    }

    private lateinit var viewModel: UpdatePasswordViewModel

    private var _binding: FragmentUpdatePasswordBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUpdatePasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val repository = Repository
        val updateUserProfileInDbUseCase = UpdateUserProfileInDbUseCase(repository)
        val factory = UpdatePasswordViewModelFactory(updateUserProfileInDbUseCase)

        viewModel = ViewModelProvider(this, factory)[UpdatePasswordViewModel::class.java]

        setListener()
    }

    private fun setListener() {

        binding.editPassword.filters = arrayOf(InputFilter.LengthFilter(12))
        binding.editPassword.setText(viewModel.typedPassword)

        updateButtonState(viewModel.typedPassword)
        binding.editPassword.doAfterTextChanged { text ->
            viewModel.typedPassword = text.toString()
            binding.passwordError.visibility = View.GONE
            updateButtonState(viewModel.typedPassword)
        }

        binding.btnUpdatePassword.setOnClickListener {
            val newPassword = viewModel.typedPassword.trim()

            when {
                newPassword.length < 6 || newPassword.length > 20 -> {
                    binding.passwordError.setText(R.string.password_length_error)
                    binding.passwordError.visibility = View.VISIBLE
                    binding.editPassword.requestFocus()
                }

                !newPassword.matches(Regex("^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@#\$%^&+=!]).+$")) -> {
                    binding.passwordError.setText(R.string.password_complexity_error)
                    binding.passwordError.visibility = View.VISIBLE
                    binding.editPassword.requestFocus()
                }

                else -> {
                    lifecycleScope.launch {

                        user.password = newPassword
                        binding.passwordError.visibility = View.GONE
                        val success = viewModel.updatePassword(user)
                        if (success) {
                            SessionManager.saveUser(requireContext(), user)
                            Toast.makeText(
                                requireContext(),
                                R.string.password_updated,
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        } else {
                            Toast.makeText(
                                requireContext(),
                                R.string.password_update_failed,
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }
                    }
                }
            }
        }
    }

    private fun updateButtonState(password: String) {
        val isValid = password.length>=6
        binding.btnUpdatePassword.isEnabled = isValid
        binding.btnUpdatePassword.setBackgroundTintList(
            resources.getColorStateList(
                if (isValid) R.color.red_background else android.R.color.darker_gray,
                null
            )
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    inner class UpdatePasswordViewModelFactory(val updateUserProfileInDbUseCase: UpdateUserProfileInDbUseCase) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return UpdatePasswordViewModel(updateUserProfileInDbUseCase) as T
        }
    }

}
