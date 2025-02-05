package com.example.foodify.authentication.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.foodify.R
import com.example.foodify.authentication.viewmodel.LoginViewModel
import com.example.foodify.data.model.Result
import com.example.foodify.databinding.FragmentResetPasswordBinding
import com.example.foodify.util.showErrorSnackBar
import com.example.foodify.util.showProgressDialog
import com.example.foodify.util.showSuccessSnackBar
import org.koin.androidx.viewmodel.ext.android.viewModel

class ResetPasswordFragment : Fragment() {
    private lateinit var _binding: FragmentResetPasswordBinding
    private val binding get() = _binding
    private val viewModel: LoginViewModel by viewModel()
    private lateinit var pDialog: SweetAlertDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentResetPasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pDialog = showProgressDialog(binding.root.context)
        binding.resetPasswordButton.setOnClickListener {
            if (validateEmail()) {
                val email = binding.emailTextInputLayout.editText?.text.toString().trim()
                viewModel.sendPasswordResetEmail(email)
                observeResetPasswordState()
            }
        }
        binding.backButton.setOnClickListener{
            findNavController().navigate(R.id.action_resetPasswordFragment_to_loginFragment)
        }
    }

    private fun observeResetPasswordState() {
        viewModel.resetPasswordState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is Result.Loading -> {
                    pDialog.show()
                }
                is Result.Success -> {
                    if (::pDialog.isInitialized && pDialog.isShowing) {
                        pDialog.dismiss()
                    }
                    showSuccessSnackBar(binding.root, "Password reset email sent!")
                }
                is Result.Error -> {
                    if (::pDialog.isInitialized && pDialog.isShowing) {
                        pDialog.dismiss()
                    }
                    showErrorSnackBar(binding.root, "Error: ${state.exception.message}")
                }
                null -> {}
            }
        }
    }

    private fun validateEmail(): Boolean {
        val email = binding.emailTextInputLayout.editText?.text.toString().trim()
        return when {
            email.isEmpty() -> {
                binding.emailTextInputLayout.error = "Email cannot be empty"
                false
            }
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                binding.emailTextInputLayout.error = "Invalid email format"
                false
            }
            else -> {
                binding.emailTextInputLayout.error = null
                true
            }
        }
    }
}