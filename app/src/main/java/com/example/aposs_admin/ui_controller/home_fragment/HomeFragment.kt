package com.example.aposs_admin.ui_controller.home_fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.aposs_admin.R
import com.example.aposs_admin.databinding.FragmentHomeBinding
import com.example.aposs_buyer.responsitory.database.AccountDatabase
import com.example.aposs_admin.ui_controller.activity.BankingInformationActivity
import com.example.aposs_admin.ui_controller.activity.CategoryActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlin.reflect.jvm.internal.impl.descriptors.Visibilities

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var binding: FragmentHomeBinding? = null
    private val viewModel: HomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        setUpNavigation()
        return binding?.root!!
    }

    private fun setUpNavigation() {
        binding?.btnBank?.setOnClickListener {
            startActivity(Intent(this.requireContext(), BankingInformationActivity::class.java))
        }

        binding?.btnCategory?.setOnClickListener {
            startActivity(Intent(this.requireContext(), CategoryActivity::class.java))
        }

        binding?.btnProduct?.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToProductsFragment())
        }

        binding?.btnOrder?.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToOrderFragment())
        }

        binding?.btnPredict?.setOnClickListener {
            // navigate to predict
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    private fun setUpOptionsButton() {
        binding?.let {
            with(it) {
                options.setOnClickListener {
                    if (lnOptions.visibility == View.GONE)
                    {
                        lnOptions.visibility = View.VISIBLE
                    } else {
                        lnOptions.visibility = View.GONE
                    }
                }
                parent.setOnClickListener {
                    lnOptions.visibility = View.GONE
                }
                logout.setOnClickListener {
                    val accountDao = AccountDatabase.getInstance(requireContext()).accountDao
                    accountDao.getAccount()?.let { it1 -> accountDao.deleteAccount(it1) }
                    findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToLoginActivity())
                }
            }
        }
    }
}