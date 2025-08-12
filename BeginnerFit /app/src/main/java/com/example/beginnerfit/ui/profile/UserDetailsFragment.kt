package com.example.beginnerfit.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.beginnerfit.R
import com.example.beginnerfit.databinding.FragmentUserDetailsBinding
import com.example.beginnerfit.databinding.ItemUserDetailCardBinding
import com.example.beginnerfit.domain.repository.UserRepository

class UserDetailsFragment : Fragment() {

    private var _binding: FragmentUserDetailsBinding? = null
    private val binding get() = _binding!!

    val user = UserRepository.getUser()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentUserDetailsBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUserCardValue(binding.itemName, getString(R.string.label_name), user.name)
        setUserCardValue(binding.itemEmail, getString(R.string.label_email), user.email)
        setUserCardValue(binding.itemAge, getString(R.string.label_age), user.age?.toString())
        setUserCardValue(binding.itemGender, getString(R.string.label_gender), user.gender)
        setUserCardValue(binding.itemHeight, getString(R.string.label_height), user.height?.toString())
        setUserCardValue(binding.itemCurrentWeight, getString(R.string.label_current_weight), user.currentWeight?.toString())
        setUserCardValue(binding.itemTargetWeight, getString(R.string.label_target_weight), user.targetWeight?.toString())
        setUserCardValue(binding.itemPlan, getString(R.string.label_program_plan), user.programPlan)
        setUserCardValue(binding.itemStartDate, getString(R.string.label_start_date), user.programStartDate)
    }

    private fun setUserCardValue(
        view: ItemUserDetailCardBinding,
        label: String,
        value: String?
    ) {

        view.tvLabel.text = label
        view.tvValue.text = value
    }

}