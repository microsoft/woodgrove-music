package com.woodgrove.android.ui.login

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.microsoft.identity.client.exception.MsalException
import com.microsoft.identity.nativeauth.statemachine.errors.SignInError
import com.microsoft.identity.nativeauth.statemachine.results.SignInResult
import com.woodgrove.android.R
import com.woodgrove.android.databinding.FragmentLoginEmailPasswordBinding
import com.woodgrove.android.ui.home.HomeActivity
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeListeners()
        checkToPrefillUsername()
    }

    private fun checkToPrefillUsername() {
        val username = activity?.intent?.getStringExtra(Constants.Intent.USERNAME)
        if (!username.isNullOrBlank()) {
            binding.loginEmailField.setText(username)
        }
    }

    private fun initializeListeners() {
        binding.loginEmailPasswordNext.setOnClickListener {
            showLoading()
            startLogin()
        }
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
                    is SignInError -> {
                        hideLoading()
                        when {
                            actionResult.isUserNotFound() -> {
                                showError("No account was found for this email address.")
                            }
                            actionResult.isInvalidCredentials()-> {
                                showError("Username and password do not match.")
                            }
                            else -> {
                                showGeneralError()
                            }
                        }
                    }
                    else -> {
                        hideLoading()
                        showGeneralError()
                    }
                }
            } catch (exception: MsalException) {
                hideLoading()
                showError(exception.message.toString())
            }
        }
    }

    private fun showError(errorMsg: String) {
        val title = getString(R.string.error_title)
        showDialog(title, errorMsg)
    }

    private fun showGeneralError() {
        val title = getString(R.string.error_title)
        val message = getString(R.string.general_error_message)
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