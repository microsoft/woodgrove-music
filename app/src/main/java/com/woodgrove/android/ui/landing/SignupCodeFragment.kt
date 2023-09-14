package com.woodgrove.android.ui.landing

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.woodgrove.android.R
import com.woodgrove.android.databinding.FragmentSignupCodeBinding
import com.woodgrove.android.ui.HomeActivity

class SignupCodeFragment : Fragment() {

    private lateinit var binding: FragmentSignupCodeBinding

    companion object {
        fun getNewInstance() = SignupCodeFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentSignupCodeBinding.inflate(inflater, container,false)
        return binding.root;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeLandingListeners()
    }

    private fun initializeLandingListeners() {
        binding.signupCodeNext.setOnClickListener {
            navigateNext()
        }
    }

    private fun navigateNext() {
        val homeActivityIntent = HomeActivity.getStartIntent(requireContext())
        homeActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(homeActivityIntent)
    }
}