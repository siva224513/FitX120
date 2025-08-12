package com.example.beginnerfit


import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.EditText

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.beginnerfit.databinding.ActivityDashBoardBinding
import com.example.beginnerfit.domain.repository.Repository
import com.example.beginnerfit.domain.usecase.food.GetAllFoodsUseCase
import com.example.beginnerfit.ui.dashboard.DashboardFragment
import com.example.beginnerfit.ui.foodTrack.FoodTrackFragment
import com.example.beginnerfit.ui.profile.ProfileFragment
import com.example.beginnerfit.ui.workoutTrack.WorkoutTrackFragment

class TrackerActivity : AppCompatActivity() {

    private lateinit var trackerViewModel: TrackerViewModel
    private lateinit var binding: ActivityDashBoardBinding

    private val dashboardFragment = DashboardFragment()

    private val workoutTrackFragment = WorkoutTrackFragment()
    private val foodTrackFragment = FoodTrackFragment()
    private val profileFragment = ProfileFragment()
    private var lastSelectedItemId: Int = R.id.navigation_dashboard


    companion object {
        const val SELECTED_TAB_KEY = "selected_tab"
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(SELECTED_TAB_KEY, lastSelectedItemId)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDashBoardBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val repository = Repository
        val factory = TrackerViewModelFactory(GetAllFoodsUseCase(repository))
        trackerViewModel = ViewModelProvider(this, factory)[TrackerViewModel::class.java]


        lastSelectedItemId =
            savedInstanceState?.getInt(SELECTED_TAB_KEY) ?: R.id.navigation_dashboard


        setCurrentFragment(lastSelectedItemId)

        binding.navView.setOnItemSelectedListener { item ->
            switchFragment(item.itemId)
            true
        }
    }

    private fun setCurrentFragment(itemId: Int) {
        val fragment = getFragment(itemId)
        supportFragmentManager.beginTransaction().replace(R.id.flFragment, fragment).commit()
    }


    private fun switchFragment(itemId: Int) {
        val targetFragment = getFragment(itemId)
        val fragmentManager = supportFragmentManager

        fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        val transaction = fragmentManager.beginTransaction()

        transaction.apply {
            if (itemId < lastSelectedItemId) {
                setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
            } else {
                setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right)
            }
        }.replace(R.id.flFragment, targetFragment).commit()

        lastSelectedItemId = itemId
    }

    private fun getFragment(itemId: Int): Fragment {

        val fragment = when (itemId) {
            R.id.navigation_dashboard -> dashboardFragment
            R.id.navigation_track_workout -> workoutTrackFragment
            R.id.navigation_track_food -> foodTrackFragment
            R.id.navigation_profile -> profileFragment
            else -> dashboardFragment
        }
        return fragment
    }

    override fun onBackPressed() {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.flFragment)

        if (supportFragmentManager.backStackEntryCount > 0) {
            super.onBackPressed()
        } else if (currentFragment != dashboardFragment) {
           binding.navView.selectedItemId = R.id.navigation_dashboard
            switchFragment(binding.navView.selectedItemId)
        } else {
            super.onBackPressed()
        }
    }



    override fun onResume() {
        super.onResume()
    }


    override fun onPause() {
        super.onPause()
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

    inner class TrackerViewModelFactory(private val getAllFoodsUseCase: GetAllFoodsUseCase) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return TrackerViewModel(getAllFoodsUseCase) as T
        }
    }

}





