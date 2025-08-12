package com.example.beginnerfit.ui.profile


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.beginnerfit.ClearAllUserDataUseCase
import com.example.beginnerfit.MyApplication
import com.example.beginnerfit.R
import com.example.beginnerfit.domain.usecase.profile.BackUpUserDataUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val backUpDataUseCase: BackUpUserDataUseCase,
    private val clearAllUserDataUseCase: ClearAllUserDataUseCase
) : ViewModel() {


    private val _signOutResult = MutableSharedFlow<SignOutResult>()
    val signOutResult: SharedFlow<SignOutResult> get() = _signOutResult



    fun logout() {
        viewModelScope.launch {
            try {
                backUpDataUseCase.invoke()
                clearAllUserDataUseCase.invoke()
                _signOutResult.emit(SignOutResult.Success(MyApplication.getContext().getString(R.string.logout_successful)))
            } catch (e: Exception) {
                _signOutResult.emit(SignOutResult.Failure(MyApplication.getContext().getString(R.string.unexpected_error)))
                println("Logout error: ${e.message}")
            }
        }
    }


}

