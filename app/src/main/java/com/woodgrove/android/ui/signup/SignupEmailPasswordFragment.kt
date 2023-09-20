package com.woodgrove.android.ui.signup

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputLayout
import com.microsoft.identity.client.UserAttributes
import com.microsoft.identity.client.exception.MsalException
import com.microsoft.identity.client.statemachine.results.Result
import com.microsoft.identity.client.statemachine.results.SignUpResult
import com.microsoft.identity.client.statemachine.results.SignUpUsingPasswordResult
import com.microsoft.identity.client.statemachine.states.SignUpCodeRequiredState
import com.woodgrove.android.R
import com.woodgrove.android.databinding.FragmentSignupEmailPasswordBinding
import com.woodgrove.android.ui.login.LoginActivity
import com.woodgrove.android.utils.AuthClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class SignupEmailPasswordFragment : Fragment() {

    private lateinit var binding: FragmentSignupEmailPasswordBinding
    private val authClient = AuthClient.getAuthClient()

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
        binding.signupEmailField.addTextChangedListener {
            // Clear any previously set errors
            clearErrors(binding.emailFieldLayout)

            // Check if all fields are set, and "next" button can be enabled
            validateInputFields()
        }
        binding.signupPasswordField.addTextChangedListener {
            // Clear any previously set errors
            clearErrors(binding.passwordFieldLayout)

            // Check if all fields are set, and "next" button can be enabled
            validateInputFields()
        }
        binding.signupNameField.addTextChangedListener {
            // Clear any previously set errors
            clearErrors(binding.nameFieldLayout)

            // Check if all fields are set, and "next" button can be enabled
            validateInputFields()
        }
        binding.signupEmailPasswordNext.setOnClickListener {
            showLoading()
            startSignup()
        }
    }

    private fun validateInputFields() {
        val emailValue = binding.signupEmailField.text
        val passwordValue = binding.signupPasswordField.text
        val nameValue = binding.signupNameField.text
        if (!emailValue.isNullOrBlank() && !passwordValue.isNullOrBlank() && !nameValue.isNullOrBlank()) {
            enableNextButton()
        } else {
            disableNextButton()
        }
    }

    private fun clearErrors(component: TextInputLayout) {
        component.error = null
    }

    private fun enableNextButton() {
        binding.signupEmailPasswordNext.isEnabled = true
    }

    private fun disableNextButton() {
        binding.signupEmailPasswordNext.isEnabled = false
    }

    private fun showLoading() {
        binding.signupEmailPasswordNext.text = null
        binding.signupNextLoader.visibility = View.VISIBLE
    }

    private fun hideLoading() {
        binding.signupEmailPasswordNext.text = getString(R.string.next_button)
        binding.signupNextLoader.visibility = View.GONE
    }

    private fun startSignup() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val email = binding.signupEmailField.text.toString()
                val password = binding.signupPasswordField.text.toString()
                val name = binding.signupNameField.text.toString()

                val attributes = UserAttributes.Builder
                    .displayName(name)
                    .build()

                val actionResult = authClient.signUpUsingPassword(
                    username = email,
                    password = password,
                    attributes = attributes
                )

                when (actionResult) {
                    is SignUpResult.CodeRequired -> {
                        hideLoading()
                        navigateNext(actionResult.nextState)
                    }
                    is SignUpResult.UserAlreadyExists -> {
                        hideLoading()
                        showUserAlreadyExistsError()
                    }
                    is SignUpResult.InvalidEmail -> {
                        hideLoading()
                        showInvalidEmailError()
                    }
                    is SignUpResult.InvalidPassword -> {
                        hideLoading()
                        showInvalidPasswordError()
                    }
                    is SignUpResult.InvalidAttributes -> {
                        hideLoading()
                        showInvalidNameError()
                    }
                    is SignUpResult.AttributesRequired, is SignUpResult.Complete -> {
                        hideLoading()
                        showGeneralError("Unexpected result: $actionResult")
                    }
                    is SignUpUsingPasswordResult.AuthNotSupported,
                    is SignUpResult.UnexpectedError, is SignUpResult.BrowserRequired -> {
                        hideLoading()
                        showGeneralError((actionResult as Result.ErrorResult).error.errorMessage)
                    }
                }
            } catch (exception: MsalException) {
                hideLoading()
                showGeneralError(exception.message.toString())
            }
        }
    }

    private fun showInvalidEmailError() {
        binding.emailFieldLayout.error = getString(R.string.invalid_email_error)
    }

    private fun showInvalidPasswordError() {
        binding.passwordFieldLayout.error = getString(R.string.invalid_password_error)
    }

    private fun showInvalidNameError() {
        binding.nameFieldLayout.error = getString(R.string.invalid_name_error)
    }

    private fun showUserAlreadyExistsError() {
        val title = getString(R.string.error_title)
        val message = getString(R.string.user_exists_error_message)
        val builder = AlertDialog.Builder(requireContext())
        val alertDialog = builder
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(R.string.go_to_login) { dialog, id ->
                navigateToLogin()
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

    private fun navigateToLogin() {
        requireActivity().let { parentActivity ->
            // Start new activity
            startActivity(LoginActivity.getStartIntent(requireActivity()))

            val signUpActivity = try {
                parentActivity as SignupActivity
            } catch (e: java.lang.ClassCastException) {
                return
            }
            // Close existing
            signUpActivity.close()
            parentActivity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        }
    }

    private fun navigateNext(authState: SignUpCodeRequiredState) {
        val newFragment = SignupCodeFragment.getNewInstance(authState)

        val localFragmentManager = parentFragmentManager
        localFragmentManager.beginTransaction()
            .setCustomAnimations(
                R.anim.slide_in_right_full,
                R.anim.slide_out_left_full
            )
            .addSharedElement(binding.signupEmailPasswordContainer, "signupCode_next_transitioned")
            .replace(R.id.signup_fragmentContainer, newFragment, tag)
            .commitAllowingStateLoss()

        localFragmentManager.executePendingTransactions()
    }
}