package com.example.beginnerfit.ui.signup


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.beginnerfit.R
import com.example.beginnerfit.databinding.StartupLayoutBinding


class StartupFragment : Fragment() {

    private var _binding: StartupLayoutBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = StartupLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListener()
    }


    private fun setupListener() {
        binding.startUpButton.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .setCustomAnimations(
                    R.anim.boom_enter,
                    R.anim.boom_exit,
                    R.anim.boom_enter,
                    R.anim.boom_exit
                )
                .replace(
                R.id.fragment_container,
                ProfileCompletionFragment.newInstance()
            ).commitNow()
        }

    }

    companion object {
        fun newInstance(): StartupFragment {
            return StartupFragment()
        }
    }

}