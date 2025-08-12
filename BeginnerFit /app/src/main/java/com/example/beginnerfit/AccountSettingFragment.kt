package com.example.beginnerfit

import android.content.Intent
import android.os.Bundle
import android.text.InputFilter
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.beginnerfit.databinding.DialogConfirmDeleteBinding
import com.example.beginnerfit.databinding.FragmentAccountSettingBinding
import com.example.beginnerfit.domain.repository.Repository
import com.example.beginnerfit.domain.repository.UserRepository
import com.example.beginnerfit.domain.usecase.profile.DeleteUserBackupFileUseCase
import com.example.beginnerfit.ui.profile.SignOutResult
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class AccountSettingFragment : Fragment() {

    companion object {
        fun newInstance() = AccountSettingFragment()
    }

    private lateinit var viewModel: AccountSettingViewModel


    private var _binding: FragmentAccountSettingBinding? = null
    val binding get() = _binding!!


    val user = UserRepository.getUser()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentAccountSettingBinding.inflate(inflater, container, false)

        val repository = Repository
        val clearAllUserDataUseCase = ClearAllUserDataUseCase(repository)
        val deleteUserBackupFileUseCase = DeleteUserBackupFileUseCase(repository)
        val factory =
            AccountSettingViewModelFactory(clearAllUserDataUseCase, deleteUserBackupFileUseCase)
        viewModel = ViewModelProvider(this, factory)[AccountSettingViewModel::class.java]
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setListener()
        observeLogoutResult()
    }

    private fun setListener() {
        binding.optionUsername.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .setCustomAnimations(
                    R.anim.boom_enter,
                    R.anim.boom_exit,
                    R.anim.boom_enter,
                    R.anim.boom_exit
                )
                .replace(R.id.flFragment, ChangeUserNameFragment())
                .addToBackStack(null)
                .commit()
        }

        binding.optionPassword.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .setCustomAnimations(
                    R.anim.boom_enter,
                    R.anim.boom_exit,
                    R.anim.boom_enter,
                    R.anim.boom_exit
                )
                .replace(R.id.flFragment, UpdatePasswordFragment())
                .addToBackStack(null)
                .commit()
        }


        binding.btnDeleteAccount.setOnClickListener {
            val user = UserRepository.getUser()
            showDeleteAccountDialog(user.password)
        }
    }

    private fun showDeleteAccountDialog(correctPassword: String) {
        val dialogBinding = DialogConfirmDeleteBinding.inflate(layoutInflater)

        val alertDialog = AlertDialog.Builder(requireContext())
            .setView(dialogBinding.root)
            .setCancelable(true)
            .create()

        dialogBinding.deleteButton.isEnabled = false

        dialogBinding.passwordInput.filters = arrayOf(InputFilter.LengthFilter(20))

        dialogBinding.passwordInput.doAfterTextChanged {
            val isValid = it.toString() == correctPassword
            dialogBinding.deleteButton.isEnabled = isValid
            dialogBinding.deleteButton.setBackgroundTintList(
                resources.getColorStateList(
                    if (isValid) R.color.red_background else android.R.color.darker_gray,
                    null
                )
            )
        }

        dialogBinding.deleteButton.setOnClickListener {
            alertDialog.dismiss()
            viewModel.deleteAccount(user)
        }

        dialogBinding.cancelButton.setOnClickListener {
            alertDialog.dismiss()
        }

        alertDialog.show()
    }


    private fun observeLogoutResult() {
        lifecycleScope.launch {
            viewModel.deleteAccountResult.collect { response ->
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


    inner class AccountSettingViewModelFactory(
        private val clearAllUserDataUseCase: ClearAllUserDataUseCase,
        private val deleteUserBackupFileUseCase: DeleteUserBackupFileUseCase
    ) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return AccountSettingViewModel(clearAllUserDataUseCase,deleteUserBackupFileUseCase) as T
        }
    }
}