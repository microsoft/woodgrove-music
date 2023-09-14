package com.woodgrove.android.ui.landing

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.woodgrove.android.R
import com.woodgrove.android.databinding.FragmentSignupEmailPasswordBinding

class SignupEmailPasswordFragment : Fragment() {

    private lateinit var binding: FragmentSignupEmailPasswordBinding

    companion object {
        fun getNewInstance() = SignupEmailPasswordFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentSignupEmailPasswordBinding.inflate(inflater, container,false)
        return binding.root;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeLandingListeners()
    }

    private fun initializeLandingListeners() {
        binding.signupEmailPasswordNext.setOnClickListener {
            navigateNext()
        }
    }

    private fun navigateNext() {
        val localFragmentManager = parentFragmentManager
        val fragmentTransaction = localFragmentManager.beginTransaction()
        fragmentTransaction.setCustomAnimations(
            R.anim.slide_in_right_full,
            R.anim.slide_out_left_full
        )
        fragmentTransaction.replace(R.id.signup_fragmentContainer, SignupCodeFragment.getNewInstance(), tag)
        fragmentTransaction.commitAllowingStateLoss()
        localFragmentManager.executePendingTransactions()
    }
}