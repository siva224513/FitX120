package com.example.beginnerfit

import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.text.InputFilter
import android.text.InputType
import android.text.Spannable
import android.text.Spanned
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.beginnerfit.databinding.FragmentChangeUserNameBinding
import com.example.beginnerfit.domain.repository.Repository
import com.example.beginnerfit.domain.repository.UserRepository
import com.example.beginnerfit.domain.usecase.signup.UpdateUserProfileInDbUseCase
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class ChangeUserNameFragment : Fragment() {

    companion object {
        fun newInstance() = ChangeUserNameFragment()

        const val USER_NAME_MAX_LENGTH = 20
    }

    private lateinit var viewModel: ChangeUserNameViewModel
    private var _binding: FragmentChangeUserNameBinding? = null
    private val binding get() = _binding!!


    val user = UserRepository.getUser()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChangeUserNameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val repository = Repository
        val updateUserProfileInDbUseCase = UpdateUserProfileInDbUseCase(repository)

        val factory = ChangeUserNameViewModelFactory(updateUserProfileInDbUseCase)

        viewModel = ViewModelProvider(this, factory)[ChangeUserNameViewModel::class.java]

        setListeners()
    }


    fun disableKeyboard() {
//        val v = binding.editUsername
//        v.clearFocus()
//        val imm = requireContext().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
//        imm.hideSoftInputFromWindow(v.windowToken, 0)

        Toast.makeText(
            requireContext(),
            R.string.error_username_max,
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun setListeners() {

        binding.editUsername.filters = arrayOf(InputFilter.LengthFilter(USER_NAME_MAX_LENGTH))
        binding.editUsername.setText(viewModel.username)

        updateButtonState(viewModel.username)

        binding.editUsername.doAfterTextChanged {
            binding.userNameError.visibility = View.GONE
            viewModel.username = it.toString()
            if (it.toString().length == USER_NAME_MAX_LENGTH) {
                disableKeyboard()
            }
            updateButtonState(viewModel.username)
        }


        binding.btnUpdateUserName.setOnClickListener {
            val newUsername = viewModel.username.trim()

            when {
                newUsername.length < 3 || newUsername.length > 20 -> {
                    binding.userNameError.setText(R.string.error_username_length)
                    binding.userNameError.visibility = View.VISIBLE
                    binding.editUsername.requestFocus()
                }

                newUsername == user.name -> {
                    binding.userNameError.setText(R.string.error_username_same)
                    binding.userNameError.visibility = View.VISIBLE
                    binding.editUsername.requestFocus()
                }

                !newUsername.matches(Regex("^[a-zA-Z0-9_]+$")) -> {
                    binding.userNameError.setText(R.string.error_username_invalid)
                    binding.userNameError.visibility = View.VISIBLE
                    binding.editUsername.requestFocus()
                }

                else -> {
                    user.name = newUsername
                    binding.userNameError.visibility = View.GONE
                    lifecycleScope.launch {
                        val isUpdated = viewModel.updateUserName(user)

                        if (isUpdated) {
                            Snackbar.make(
                                requireView(),getString(R.string.username_updated,user.name),
                                Snackbar.LENGTH_SHORT
                            ).show()

                            SessionManager.saveUser(requireContext(), user)
                            parentFragmentManager.popBackStack()
                        } else {
                            Snackbar.make(
                                requireView(), getString(R.string.username_update_failed),
                                Snackbar.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }

    }

    private fun updateButtonState(username: String) {
        val isValid = username.isNotBlank() && user.name != username
        binding.btnUpdateUserName.isEnabled = isValid
        binding.btnUpdateUserName.setBackgroundTintList(
            resources.getColorStateList(
                if (isValid) R.color.red_background else android.R.color.darker_gray,
                null
            )
        )
    }

    inner class ChangeUserNameViewModelFactory(val updateUserProfileInDbUseCase: UpdateUserProfileInDbUseCase) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ChangeUserNameViewModel(updateUserProfileInDbUseCase) as T
        }
    }

}



