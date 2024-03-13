package com.woodgrove.android.ui.signup

import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.microsoft.identity.client.exception.MsalException
import com.microsoft.identity.nativeauth.UserAttributes
import com.microsoft.identity.nativeauth.statemachine.errors.SignUpError
import com.microsoft.identity.nativeauth.statemachine.results.SignUpResult
import com.microsoft.identity.nativeauth.statemachine.states.SignUpCodeRequiredState
import com.woodgrove.android.R
import com.woodgrove.android.databinding.FragmentSignupEmailPasswordBinding
import com.woodgrove.android.ui.login.LoginActivity
import com.woodgrove.android.utils.AuthClient
import com.woodgrove.android.utils.PasswordGenerator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class SignupEmailPasswordFragment : Fragment() {

    private lateinit var binding: FragmentSignupEmailPasswordBinding
    private val authClient = AuthClient.getAuthClient()
    private var validationState = arrayListOf(false,false,false)
    private var isFirstFocus = true

    companion object {
        fun getNewInstance() = SignupEmailPasswordFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentSignupEmailPasswordBinding.inflate(inflater, container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeLandingListeners()
    }

    private fun initializeLandingListeners() {
        binding.signupEmailField.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // This method is called before the text is changed.
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // This method is called whenever the text is changed.
                val inputText = s.toString().trim()
                if (!isValidEmail(inputText)) {
                    validationState[0] = false
                    binding.emailFieldLayout.error = "Invalid email address"
                }
                else {
                    validationState[0] = true
                    binding.emailFieldLayout.error = null // Clear the error
                }
                validateInputFields()
            }

            override fun afterTextChanged(s: Editable?) {
                // This method is called after the text is changed.
                val inputText = s.toString().trim()
                if (isValidEmail(inputText)) {
                    CoroutineScope(Dispatchers.Main).launch {
                        val result = authClient.signUp(inputText)
                        if (result is SignUpError && result.isUserAlreadyExists()) {
                            validationState[0] = false
                            binding.emailFieldLayout.error = "Email already exists"
                        }
                    }
                }
            }
        })

        binding.signupPasswordField.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus && isFirstFocus) {
                binding.signupPasswordField.text = Editable.Factory.getInstance()
                    .newEditable(PasswordGenerator.generatePassword(8))
                binding.signupPasswordField.setSelection(binding.signupPasswordField.text?.length ?: 0)
                binding.signupPasswordField.requestFocus()
                binding.signupPasswordField.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                isFirstFocus = false
            }
        }

        binding.signupPasswordField.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // This method is called before the text is changed.
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // This method is called whenever the text is changed.
                val inputText = s.toString().trim()
                if (!isValidPassword(inputText)) {
                    validationState[1] = false
                    binding.passwordFieldLayout.error = "Password must contain at least 8 characters, one uppercase letter, one lowercase letter, and one digit."
                } else {
                    validationState[1] = true
                    binding.passwordFieldLayout.error = null // Clear error
                }
                validateInputFields()
            }

            override fun afterTextChanged(s: Editable?) {
                // This method is called after the text is changed.
            }
        })

        binding.signupNameField.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // This method is called before the text is changed.
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // This method is called whenever the text is changed.
                val inputText = s.toString().trim()
                if (inputText.isEmpty()) {
                    validationState[2] = false
                    binding.nameFieldLayout.error = "Empty name"
                } else {
                    validationState[2] = true
                    binding.nameFieldLayout.error = null // Clear error
                }
                validateInputFields()
            }

            override fun afterTextChanged(s: Editable?) {
                // This method is called after the text is changed.
            }
        })

        binding.signupEmailPasswordNext.setOnClickListener {
            showLoading()
            startSignup()
        }
    }

    private fun validateInputFields() {
        if (validationState.contains(false)) {
            disableNextButton()
        } else {
            enableNextButton()
        }
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
                val password = CharArray(binding.signupPasswordField.length())
                binding.signupPasswordField.text?.getChars(0, binding.signupPasswordField.length(), password, 0)
                val name = binding.signupNameField.text.toString()

                val attributes = UserAttributes.Builder
                    .displayName(name)
                    .build()

                val actionResult = authClient.signUp(
                    username = email,
                    password = password,
                    attributes = attributes
                )

                when (actionResult) {
                    is SignUpResult.CodeRequired -> {
                        hideLoading()
                        navigateNext(actionResult.nextState)
                    }
                    is SignUpError -> {
                        hideLoading()
                        if (actionResult.isInvalidPassword()){
                            showInvalidPasswordError()
                        }
                        else if (actionResult.isInvalidUsername()){
                            showInvalidEmailError()
                        }
                        else if (actionResult.isUserAlreadyExists()) {
                            showUserAlreadyExistsError()
                        }
                        else {
                            showGeneralError(actionResult.errorMessage)
                        }
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
            .replace(R.id.signup_fragmentContainer, newFragment, tag)
            .commitAllowingStateLoss()

        localFragmentManager.executePendingTransactions()
    }

    private fun isValidEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun isValidPassword(password: String): Boolean {
        val pattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$".toRegex()
        return pattern.matches(password)
    }
}