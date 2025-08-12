package com.example.beginnerfit.ui.profile

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.beginnerfit.R
import com.example.beginnerfit.databinding.FragmentAboutSectionBinding
import com.example.beginnerfit.databinding.FragmentProfileBinding

class AboutSectionFragment : Fragment() {

    companion object {
        fun newInstance() = AboutSectionFragment()
    }

    private var _binding: FragmentAboutSectionBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AboutSectionViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentAboutSectionBinding.inflate(inflater, container, false)
        return binding.root
    }
}