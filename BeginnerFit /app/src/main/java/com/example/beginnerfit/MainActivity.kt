package com.example.beginnerfit


import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.example.beginnerfit.domain.repository.UserRepository
import com.example.beginnerfit.ui.signIn.SignInFragment
import com.example.beginnerfit.ui.signup.StartupFragment
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {
    private val viewModel: MainViewModel by viewModels()


    companion object
    {
        const val PREF_NAME = "app_prefs"
        const val IS_FIRST_RUN = "is_first_run"
    }


    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)

        installSplashScreen().setKeepOnScreenCondition {
            viewModel.loading.value
        }

        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            val user = viewModel.user
            if (user != null) {
                UserRepository.setUser(user)
                if (user.isProfileCompleted()) {
                    startActivity(Intent(this, TrackerActivity::class.java))
                    finish()
                } else {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, StartupFragment.newInstance()).commit()
                }
            } else {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, SignInFragment()).commit()
            }
        }

    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (v is EditText) {
                val outRect = Rect()
                v.getGlobalVisibleRect(outRect)
                if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                    v.clearFocus()
                    val imm = this.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(v.windowToken, 0)

                }
            }
        }
        return super.dispatchTouchEvent(event)
    }






}
