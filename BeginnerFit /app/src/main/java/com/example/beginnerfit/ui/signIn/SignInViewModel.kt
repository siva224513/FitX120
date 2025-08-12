package com.example.beginnerfit.ui.signIn


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.beginnerfit.MyApplication
import com.example.beginnerfit.R
import com.example.beginnerfit.domain.usecase.profile.SetupUserDataFromJsonToDbUseCase
import com.example.beginnerfit.domain.usecase.signin.GetUserUseCase
import com.example.beginnerfit.model.UserBackUpData
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class SignInViewModel(
    private val getUserUseCase: GetUserUseCase,
    private val setupUserDataFromJsonToDbUseCase: SetupUserDataFromJsonToDbUseCase
) : ViewModel() {

    var email: String = ""
    var password: String = ""

    private val _signInResult = MutableSharedFlow<SignInResult>()
    val signInResult = _signInResult.asSharedFlow()

    fun login(email: String, password: String) {
        viewModelScope.launch {
            try {
                val userBackUpData = getUserUseCase.invoke(email, password)
                println(userBackUpData.toString())
                if (userBackUpData != null) {
                    _signInResult.emit(
                        SignInResult.Success(
                            userBackUpData,
                            MyApplication.getContext().getString(
                                R.string.login_successful
                            )
                        )
                    )

                } else {
                    _signInResult.emit(
                        SignInResult.Failure(
                            MyApplication.getContext().getString(R.string.invalid_email_or_password)
                        )
                    )
                }
            } catch (e: Exception) {

                _signInResult.emit(
                    SignInResult.Failure(
                        MyApplication.getContext().getString(R.string.unexpected_error)
                    )
                )
            }
        }
    }

    fun setUpUserDataFromJsonToDb(userBackUpData: UserBackUpData) {
        viewModelScope.launch {
            setupUserDataFromJsonToDbUseCase.invoke(userBackUpData)
        }
    }


}
