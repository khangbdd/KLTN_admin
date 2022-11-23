package com.example.aposs_admin.ui_controller.account_manage_fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.aposs_admin.R
import com.example.aposs_admin.adapter.AccountAdapter
import com.example.aposs_admin.databinding.FragmentAccountManageBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AccountManageFragment : Fragment() {

    private val viewModel: AccountManageViewModel by viewModels()
    private var binding: FragmentAccountManageBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_account_manage, container, false)
        binding?.viewModel = viewModel
        binding?.lifecycleOwner = viewLifecycleOwner
        setUpButton()
        setUpListAccount()
        return binding?.root!!
    }

    private fun setUpListAccount() {
        binding?.rcAccount?.adapter = AccountAdapter(object: AccountAdapter.OnChangePasswordClick {
            override fun onChangePasswordClick(account: String) {
                // call viewModel
            }
        }, object : AccountAdapter.OnDeleteAccountClick{
            override fun onDeleteAccountCLick(account: String) {
                viewModel.deleteAccount(account)
            }

        })
    }

    private fun setUpButton() {
        binding?.back?.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}