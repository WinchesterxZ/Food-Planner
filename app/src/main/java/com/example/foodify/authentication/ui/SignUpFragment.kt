package com.example.foodify.authentication.ui

import android.graphics.Typeface
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.foodify.R
import com.example.foodify.authentication.viewmodel.SignUpViewModel
import com.example.foodify.data.model.Result
import com.example.foodify.databinding.FragmentSignUpBinding
import com.example.foodify.util.showErrorSnackBar
import com.example.foodify.util.showProgressDialog
import com.google.android.material.textfield.TextInputLayout
import org.koin.androidx.viewmodel.ext.android.viewModel


class SignUpFragment : Fragment() {
    private lateinit var _binding: FragmentSignUpBinding
    private val viewModel: SignUpViewModel by viewModel()
    private lateinit var sDialog: SweetAlertDialog
    private lateinit var pDialog: SweetAlertDialog
    private val binding get() = _binding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignUpBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pDialog = showProgressDialog(binding.root.context)
        observeSignupState()
        binding.signInText.setOnClickListener{
            findNavController().navigate(R.id.action_signUpFragment_to_loginFragment)
        }
        binding.signUpButton.setOnClickListener{
            binding.apply {
                if (validateUsername(usernameTextInputLayout) &&
                    validatePhoneNumber(phoneNumberTextInputLayout) &&
                    validateEmail(emailTextInputLayout) &&
                    validatePassword(passwordTextInputLayout) &&
                    validateConfirmPassword(passwordTextInputLayout, confirmPasswordTextInputLayout)){
                    viewModel.signupWithEmail(emailTextInputLayout.editText?.text.toString(),passwordTextInputLayout.editText?.text.toString(),usernameTextInputLayout.editText?.text.toString())
                }
            }
        }
    }


    private fun showSuccessDialog(){
        sDialog = SweetAlertDialog(binding.root.context,SweetAlertDialog.SUCCESS_TYPE)
        sDialog.titleText = "Account Created Successfully!"
        sDialog.contentText = "A verification mail was sent to your Email"
        sDialog.setCancelable(false)
        sDialog.confirmText = "Login"
        sDialog.setConfirmClickListener {
            sDialog.dismiss()
            findNavController().navigate(R.id.action_signUpFragment_to_loginFragment)
        }
        sDialog.show()
        sDialog.findViewById<Button>(cn.pedant.SweetAlert.R.id.confirm_button)?.apply {
            backgroundTintList = null // Remove default Material background tint
            setTypeface(typeface, Typeface.BOLD) // Make text bold
            setBackgroundResource(R.drawable.custom_button_background) // Apply custo
        }

    }

    private fun observeSignupState() {
        viewModel.signupState.removeObservers(viewLifecycleOwner)
        viewModel.signupState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is Result.Loading -> {
                    pDialog.show()
                }
                is Result.Success -> {
                    if (::pDialog.isInitialized && pDialog.isShowing) {
                        pDialog.dismiss()
                    }
                    showSuccessDialog()
                }
                is Result.Error -> {
                    if (::pDialog.isInitialized && pDialog.isShowing) {
                        pDialog.dismiss()
                    }
                    if(state.exception.message == "This email is already registered."){
                        binding.emailTextInputLayout.error = getString(R.string.email_already_exists)
                        binding.emailTextInputLayout.isErrorEnabled = true
                    }else{
                        state.exception.message?.let { showErrorSnackBar(requireView(),it) }
                    }
                }
                null -> {
                    if (::pDialog.isInitialized && pDialog.isShowing) {
                        pDialog.dismiss()
                    }
                }
            }
        }
    }


    private fun validateUsername(usernameTextInputLayout: TextInputLayout): Boolean {
        val username = usernameTextInputLayout.editText?.text.toString()
        if (username.isBlank()) {
            usernameTextInputLayout.error = getString(R.string.username_required)
            return false
        }
        usernameTextInputLayout.error = null
        return true
    }

    private fun validatePhoneNumber(phoneNumberTextInputLayout: TextInputLayout): Boolean {
        val phoneNumber = phoneNumberTextInputLayout.editText?.text.toString()
        if (phoneNumber.isBlank()) {
            phoneNumberTextInputLayout.error = getString(R.string.phone_number_required)
            return false
        } else if (!Patterns.PHONE.matcher(phoneNumber).matches()) {
            phoneNumberTextInputLayout.error = getString(R.string.invalid_phone_number)
            return false
        }
        phoneNumberTextInputLayout.error = null
        return true
    }

    private fun validateEmail(emailTextInputLayout: TextInputLayout): Boolean {
        val email = emailTextInputLayout.editText?.text.toString()
        if (email.isBlank()) {
            emailTextInputLayout.error = getString(R.string.email_required)
            return false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailTextInputLayout.error = getString(R.string.invalid_email_address)
            return false
        }
        emailTextInputLayout.error = null
        return true
    }

    private fun validatePassword(passwordTextInputLayout: TextInputLayout): Boolean {
        val password = passwordTextInputLayout.editText?.text.toString()
        if (password.isBlank()) {
            passwordTextInputLayout.error = getString(R.string.password_required)
            return false
        } else if (password.length < 6) { // Example minimum length
            passwordTextInputLayout.error = getString(R.string.password_too_short)
            return false
        }
        passwordTextInputLayout.error = null
        return true
    }

    private fun validateConfirmPassword(
        passwordTextInputLayout: TextInputLayout,
        confirmPasswordTextInputLayout: TextInputLayout
    ): Boolean {
        val password = passwordTextInputLayout.editText?.text.toString()
        val confirmPassword = confirmPasswordTextInputLayout.editText?.text.toString()
        if (confirmPassword.isBlank()) {
            confirmPasswordTextInputLayout.error = getString(R.string.password_required)
            return false
        } else if (password != confirmPassword) {
            confirmPasswordTextInputLayout.error = getString(R.string.passwords_do_not_match)
            return false
        }
        confirmPasswordTextInputLayout.error = null
        confirmPasswordTextInputLayout.requestLayout() // Important!
        return true
    }

}