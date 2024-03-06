package com.woodgrove.android.ui.signup

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.microsoft.identity.client.exception.MsalException
import com.microsoft.identity.nativeauth.statemachine.errors.SignInError
import com.microsoft.identity.nativeauth.statemachine.errors.SignUpError
import com.microsoft.identity.nativeauth.statemachine.results.SignInResult
import com.microsoft.identity.nativeauth.statemachine.results.SignUpResendCodeResult
import com.microsoft.identity.nativeauth.statemachine.results.SignUpResult
import com.microsoft.identity.nativeauth.statemachine.states.SignInCodeRequiredState
import com.microsoft.identity.nativeauth.statemachine.states.SignInContinuationState
import com.microsoft.identity.nativeauth.statemachine.states.SignUpCodeRequiredState
import com.woodgrove.android.R
import com.woodgrove.android.databinding.FragmentSignupCodeBinding
import com.woodgrove.android.ui.HomeActivity
import com.woodgrove.android.utils.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SignupCodeFragment : Fragment() {

    private lateinit var binding: FragmentSignupCodeBinding
    private lateinit var authState: SignUpCodeRequiredState

    companion object {
        fun getNewInstance(authState: SignUpCodeRequiredState): SignupCodeFragment {
            val bundle = Bundle()
            bundle.putParcelable(Constants.STATE, authState)
            val fragment = SignupCodeFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        authState = (this.arguments?.getParcelable(Constants.STATE) as? SignUpCodeRequiredState)!!


        binding = FragmentSignupCodeBinding.inflate(inflater, container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeLandingListeners()
    }

    private fun initializeLandingListeners() {
        binding.signupCodeCodeFieldLayout.setCodeInputLengthChanged { codeLength ->
            // Check if all fields are set, and "next" button can be enabled
            validateInputFields(codeLength)
        }
        binding.signupCodeNext.setOnClickListener {
            showLoading()
            validateCode()
        }
    }

    private fun validateInputFields(codeLength: Int) {
        if (codeLength == 8) {
            enableNextButton()
        } else {
            disableNextButton()
        }
    }

    private fun enableNextButton() {
        binding.signupCodeNext.isEnabled = true
    }

    private fun disableNextButton() {
        binding.signupCodeNext.isEnabled = false
    }

    private fun showLoading() {
        binding.signupCodeNext.text = null
        binding.signupCodeNextLoader.visibility = View.VISIBLE
    }

    private fun hideLoading() {
        binding.signupCodeNext.text = getString(R.string.verify)
        binding.signupCodeNextLoader.visibility = View.GONE
    }

    private fun clearCode() {
        binding.signupCodeCodeFieldLayout.clearCode()
    }

    private fun validateCode() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val code = binding.signupCodeCodeFieldLayout.getCodeInputValue()
                val actionResult = authState.submitCode(code)

                when (actionResult) {
                    is SignUpResult.Complete -> {
                        signIn(actionResult.nextState)
                    }
                    is SignUpResult.PasswordRequired -> {
                        showInvalidCodeError()
                        clearCode()
                    }
                    is SignUpResult.AttributesRequired -> {
                        showGeneralError("Unexpected result: $actionResult")
                    }
                    is  SignUpError -> {
                        showGeneralError(actionResult.errorMessage)
                    }
                }
            } catch (exception: MsalException) {
                showGeneralError(exception.message.toString())
            }
            hideLoading()
        }
    }

    private fun showInvalidCodeError() {
        val title = getString(R.string.error_title)
        val message = getString(R.string.invalid_code_error)
        val builder = AlertDialog.Builder(requireContext())
        val alertDialog = builder
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(R.string.resend_code) { dialog, id ->
                dialog.dismiss()
                resendCode()
                clearCode()
                showLoading()
            }
            .setNegativeButton(R.string.dismiss) { dialog, id ->
                dialog.dismiss()
            }
            .create()

        alertDialog.show()
    }

    private fun showGeneralError(errorMsg: String?) {
        val title = getString(R.string.error_title)
        val message = getString(R.string.general_error_message, errorMsg)
        showDialog(title, message)
    }

    private fun showDialog(title: String, message: String) {
        val builder = AlertDialog.Builder(requireContext())
        val alertDialog = builder
            .setTitle(title)
            .setMessage(message)
            .setNegativeButton(R.string.dismiss) { dialog, id ->
                dialog.dismiss()
            }
            .create()

        alertDialog.show()
    }

    private fun showToast(message: String) {
        Toast.makeText(
            requireContext(),
            message,
            Toast.LENGTH_LONG
        ).show()
    }

    private fun resendCode() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val actionResult = authState.resendCode()

                when (actionResult) {
                    is SignUpResendCodeResult.Success -> {
                        authState = actionResult.nextState
                        showToast(getString(R.string.code_sent))
                    }
                    is SignUpError -> {
                        showGeneralError(actionResult.errorMessage)
                    }
                }
            } catch (exception: MsalException) {
                showGeneralError(exception.message.toString())
            }
            hideLoading()
        }
    }

    private fun signIn(authState: SignInContinuationState) {
        showLoading()
        CoroutineScope(Dispatchers.Main).launch {
            val actionResult = authState.signIn()
            when (actionResult) {
                is SignInResult.Complete -> {
                    hideLoading()
                    navigateNext()
                    requireActivity().finish()
                }
                is SignInResult.CodeRequired,
                is SignInResult.PasswordRequired -> {
                    hideLoading()
                    showGeneralError("Unexpected result: $actionResult")
                }
                is SignInError -> {
                    hideLoading()
                    showGeneralError(actionResult.errorMessage)
                }
            }
        }
    }

    private fun navigateNext() {
        startActivity(HomeActivity.getStartIntent(requireContext()))
        activity?.finish()
    }
}