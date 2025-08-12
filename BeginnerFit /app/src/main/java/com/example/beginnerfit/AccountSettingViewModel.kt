package com.example.beginnerfit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.beginnerfit.domain.usecase.profile.DeleteUserBackupFileUseCase
import com.example.beginnerfit.model.User
import com.example.beginnerfit.ui.profile.SignOutResult
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

class AccountSettingViewModel(
    private val clearAllUserDataUseCase: ClearAllUserDataUseCase,
    private val deleteUserBackupFileUseCase: DeleteUserBackupFileUseCase
) :
    ViewModel() {


    private val _deleteAccountResult = MutableSharedFlow<SignOutResult>()
    val deleteAccountResult: SharedFlow<SignOutResult> get() = _deleteAccountResult


    fun deleteAccount(user: User) {
        viewModelScope.launch {
            try {
                val deleted = deleteUserBackupFileUseCase.invoke(user)
                if (deleted == false) {
                    throw IllegalStateException("Backup File Not Deleted..")
                }

                clearAllUserDataUseCase.invoke()
                _deleteAccountResult.emit(SignOutResult.Success("Account Deleted Successfully"))
            } catch (e: Exception) {
                _deleteAccountResult.emit(SignOutResult.Failure("Account Deletion Failed"))
            }
        }
    }
}