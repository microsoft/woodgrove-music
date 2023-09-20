package com.woodgrove.android.ui.login

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputLayout
import com.woodgrove.android.R
import com.woodgrove.android.databinding.FragmentLoginEmailPasswordBinding
import com.woodgrove.android.utils.AuthClient
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

        initializeLandingListeners()
    }

    private fun initializeLandingListeners() {
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
}