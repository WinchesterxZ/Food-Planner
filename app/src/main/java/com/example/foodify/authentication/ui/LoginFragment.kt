package com.example.foodify.authentication.ui
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.credentials.CredentialManager
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.foodify.BuildConfig
import com.example.foodify.main.MainActivity
import com.example.foodify.R
import com.example.foodify.authentication.viewmodel.LoginViewModel
import com.example.foodify.data.model.Result
import com.example.foodify.databinding.FragmentLoginBinding
import com.example.foodify.util.showErrorSnackBar
import com.example.foodify.util.showProgressDialog
import org.koin.androidx.viewmodel.ext.android.viewModel


class LoginFragment : Fragment() {
    private lateinit var _binding: FragmentLoginBinding
    private val binding get() = _binding
    private val viewModel: LoginViewModel by viewModel()
    private lateinit var pDialog: SweetAlertDialog
    private lateinit var credentialManager: CredentialManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(viewModel.isUserLoggedIn()){
            goToMainActivity(true)
        }else{
            viewModel.saveGuestMode(false)
        }
        credentialManager = CredentialManager.create(requireContext())
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{

        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pDialog = showProgressDialog(binding.root.context)
        binding.forgotPasswordText.setOnClickListener{
            findNavController().navigate(R.id.action_loginFragment_to_resetPasswordFragment)
        }
        binding.signUpText.setOnClickListener{
            findNavController().navigate(R.id.action_loginFragment_to_signUpFragment)
        }
        binding.signInAsGuestButton.setOnClickListener {
            goToMainActivity(true)
            viewModel.saveGuestMode(true)
        }
        observeLoginState()

        binding.signInButton.setOnClickListener {
            if (validateEmail() && validatePassword()) {
                viewModel.signInWithEmail(binding.emailTextInputLayout.editText?.text.toString(),binding.passwordTextInputLayout.editText?.text.toString())
            }
        }
        binding.googleSignInButton.setOnClickListener{
            viewModel.signInWithGoogle(
                credentialManager,
                BuildConfig.SERVER_CLIENT_ID,
                requireContext()
            )
        }

    }




    private fun observeLoginState(){
        viewModel.loginState.observe(viewLifecycleOwner) { state ->
            binding.mainView.isEnabled = true
            when (state) {
                is Result.Loading -> {
                   pDialog.show()
                }
                is Result.Success -> {
                    if (::pDialog.isInitialized && pDialog.isShowing) {
                        pDialog.dismiss()
                    }
                    goToMainActivity()
                }
                is Result.Error -> {
                    if (::pDialog.isInitialized && pDialog.isShowing) {
                        pDialog.dismiss()
                    }
                    when (state.exception.message) {
                        "Incorrect email or password. Please try again." -> {
                            binding.passwordTextInputLayout.error = state.exception.message
                        }
                        "Email Is Not Verified", "This account has been disabled." -> {
                            binding.emailTextInputLayout.error = state.exception.message
                        }
                        else -> {
                            state.exception.message?.let { showErrorSnackBar(requireView(),it) }
                        }
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

    private fun goToMainActivity(alreadySignedIn:Boolean = false) {
        val intent = Intent(requireContext(), MainActivity::class.java)
        if (!alreadySignedIn)  intent.putExtra("SHOW_SNACKBAR", true)
        startActivity(intent)
        requireActivity().finish()
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
    private fun validatePassword(): Boolean {
        val password = binding.passwordTextInputLayout.editText?.text.toString()
        return when {
            password.isEmpty() -> {
                binding.passwordTextInputLayout.error = "Password cannot be empty"
                false
            }
            password.length < 6 -> {
                binding.passwordTextInputLayout.error = "Password must be at least 6 characters"
                false
            }
            else -> {
                binding.passwordTextInputLayout.error = null
                true
            }
        }
    }



}