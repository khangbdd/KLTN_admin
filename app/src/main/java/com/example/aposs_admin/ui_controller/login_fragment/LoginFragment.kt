package com.example.aposs_admin.ui_controller.login_fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.aposs_admin.R
import com.example.aposs_admin.databinding.FragmentLoginBinding
import com.example.aposs_admin.local_storage.entity.Account
import com.example.aposs_admin.ui_controller.activity.HomeActivity
import com.example.aposs_admin.ui_controller.dialog.LoadingDialog
import com.example.aposs_admin.util.LoginState
import com.example.aposs_buyer.responsitory.database.AccountDatabase
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private var binding: FragmentLoginBinding? = null
    private val viewModel: LogInViewModel by viewModels()
    private var dialog: LoadingDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false)
        binding?.viewModel = viewModel
        clearAccount()
        dialog = LoadingDialog(requireActivity())
        toastMessageChange()
        setCheckingEmail()
        setCheckingPassword()
        onLoginStateChange()
        return binding?.root!!
    }

    private fun clearAccount() {
        val currentAccount = AccountDatabase.getInstance(this.requireContext()).accountDao.getAccount()
        if (currentAccount != null) {
            AccountDatabase.getInstance(this.requireContext()).accountDao.deleteAccount(currentAccount)
        }
    }

    private fun onLoginStateChange() {
        viewModel.loginState.observe(this.viewLifecycleOwner) {
            if (it == LoginState.Wait) {
                dialog?.dismissDialog()
            } else {
                if (it == LoginState.Loading) {
                    dialog?.startLoading()
                } else {
                    dialog?.dismissDialog()
                    if (viewModel.token != null && viewModel.email.value != null && viewModel.password.value != null) {
                        val account: Account =
                            Account(
                                viewModel.email.value!!,
                                viewModel.password.value!!,
                                viewModel.token!!.accessToken,
                                viewModel.token!!.tokenType,
                                viewModel.token!!.refreshToken
                            )
                        AccountDatabase.getInstance(this.requireContext()).accountDao.insertAccount(
                            account
                        )
                        startActivity(Intent(this.context, HomeActivity::class.java))
                    } else {
                        Toast.makeText(this.context, "An error occur!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun setCheckingEmail() {
        viewModel.email.observe(this.viewLifecycleOwner) {
            viewModel.isValidEmail()
            binding?.emailLayout?.error = viewModel.emailErrorMessage;
        }
    }

    private fun setCheckingPassword() {
        viewModel.password.observe(this.viewLifecycleOwner) {
            viewModel.isValidPassword()
            binding?.passwordLayout?.error = viewModel.passwordErrorMessage
        }
    }

    private fun toastMessageChange() {
        viewModel.toastMessage.observe(this.viewLifecycleOwner) {
            Toast.makeText(this.requireContext(), it, Toast.LENGTH_SHORT).show()
        }
    }
}