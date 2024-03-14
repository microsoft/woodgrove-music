package com.woodgrove.android.ui.login

import android.app.AlertDialog
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputLayout
import com.microsoft.identity.client.exception.MsalException
import com.microsoft.identity.nativeauth.statemachine.errors.SignInError
import com.microsoft.identity.nativeauth.statemachine.results.SignInResult
import com.woodgrove.android.R
import com.woodgrove.android.databinding.FragmentLoginEmailPasswordBinding
import com.woodgrove.android.ui.HomeActivity
import com.woodgrove.android.utils.AuthClient
import com.woodgrove.android.utils.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class LoginEmailPasswordFragment : Fragment() {

    private lateinit var binding: FragmentLoginEmailPasswordBinding
    private val authClient = AuthClient.getAuthClient()

    companion object {
        fun getNewInstance() = LoginEmailPasswordFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentLoginEmailPasswordBinding.inflate(inflater, container,false)
        return binding.root;
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeLandingListeners()
        checkToPrefillUsername()
    }

    private fun checkToPrefillUsername() {
        val username = activity?.intent?.getStringExtra(Constants.Intent.USERNAME)
        if (!username.isNullOrBlank()) {
            binding.loginEmailField.setText(username)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initializeLandingListeners() {
        binding.loginEmailField.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.loginEmailField.setAutofillHints(View.AUTOFILL_HINT_USERNAME)
            }
        }

        binding.loginPasswordField.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.loginPasswordField.setAutofillHints(View.AUTOFILL_HINT_PASSWORD)
            }
        }



        binding.loginEmailField.addTextChangedListener {
            // Clear any previously set errors
            clearErrors(binding.emailFieldLayout)

            // Check if all fields are set, and "next" button can be enabled
            validateInputFields()
        }
        binding.loginPasswordField.addTextChangedListener {
            // Clear any previously set errors
            clearErrors(binding.passwordFieldLayout)

            // Check if all fields are set, and "next" button can be enabled
            validateInputFields()
        }
        binding.loginEmailPasswordNext.setOnClickListener {
            showLoading()
            startLogin()
        }
    }

    private fun validateInputFields() {
        val emailValue = binding.loginEmailField.text
        val passwordValue = binding.loginPasswordField.text
        if (!emailValue.isNullOrBlank() && !passwordValue.isNullOrBlank()) {
            enableNextButton()
        } else {
            disableNextButton()
        }
    }

    private fun clearErrors(component: TextInputLayout) {
        component.error = null
    }

    private fun enableNextButton() {
        binding.loginEmailPasswordNext.isEnabled = true
    }

    private fun disableNextButton() {
        binding.loginEmailPasswordNext.isEnabled = false
    }

    private fun showLoading() {
        binding.loginEmailPasswordNext.text = null
        binding.loginNextLoader.visibility = View.VISIBLE
    }

    private fun hideLoading() {
        binding.loginEmailPasswordNext.text = getString(R.string.next_button)
        binding.loginNextLoader.visibility = View.GONE
    }

    private fun startLogin() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val email = binding.loginEmailField.text.toString()
                val password = CharArray(binding.loginPasswordField.length())
                binding.loginPasswordField.text?.getChars(0, binding.loginPasswordField.length(), password, 0)

                val actionResult = authClient.signIn(
                    username = email,
                    password = password
                )

                when (actionResult) {
                    is SignInResult.Complete -> {
                        hideLoading()
                        navigateNext()
                    }
                    is SignInResult.CodeRequired -> {
                        hideLoading()
                        showGeneralError("Unexpected result: $actionResult")
                    }
                    is SignInError -> {
                        hideLoading()
                        showGeneralError(actionResult.errorMessage)
                    }
                }
            } catch (exception: MsalException) {
                hideLoading()
                showGeneralError(exception.message.toString())
            }
        }
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

    private fun navigateNext() {
        startActivity(HomeActivity.getStartIntent(requireContext()))
        activity?.finish()
    }
}