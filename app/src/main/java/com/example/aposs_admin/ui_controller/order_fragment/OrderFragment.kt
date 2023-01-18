package com.example.aposs_admin.ui_controller.order_fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.aposs_admin.R
import com.example.aposs_admin.adapter.OrderAdapter
import com.example.aposs_admin.databinding.FragmentOrderBinding
import com.example.aposs_admin.local_storage.entity.Account
import com.example.aposs_admin.model.dto.TokenDTO
import com.example.aposs_admin.model.Order
import com.example.aposs_admin.util.OrderStatus
import com.example.aposs_buyer.responsitory.database.AccountDatabase
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OrderFragment : Fragment() {

    private var binding: FragmentOrderBinding? = null
    private val viewModel: OrderViewModel by viewModels()
    private var orderAdapter: OrderAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_order, container, false)
        binding?.viewModel = viewModel
        binding?.lifecycleOwner = viewLifecycleOwner
        loadToken()
        orderAdapter = OrderAdapter(object : OrderAdapter.OnClickListener {
            override fun onClick(order: Order?) {
                val directions = order?.let {
                    OrderFragmentDirections.actionOrderFragmentToOrderDetailFragment(
                        it
                    )
                }
                if (directions != null) {
                    findNavController().navigate(directions)
                }
            }
        }, object : OrderAdapter.OnChangeStatusClick {
            override fun onChangeStatusClick(orderId: Long, orderStatus: OrderStatus?) {
                if (orderStatus != null) {
                    viewModel.setOrderStatus(orderId, orderStatus)
                }
            }
        }, object : OrderAdapter.OnConfirmPaymentClick {
            override fun onConfirmPaymentClick(orderId: Long, orderStatus: OrderStatus?) {
                if (orderStatus != null) {
                    viewModel.confirmCompletedPayment(orderId, orderStatus)
                }
            }
        })
        binding?.back?.setOnClickListener {
            findNavController().popBackStack()
        }
        binding?.orders?.adapter = orderAdapter
        binding?.orders?.layoutManager =
            LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false)
        viewModel.lstDisplay.observe(viewLifecycleOwner) { orders ->
            if (orderAdapter != null) {
                orderAdapter!!.submitList(null)
                orderAdapter!!.submitList(ArrayList(orders))
            }
        }
        viewModel.lstDisplay.observe(viewLifecycleOwner) {
            configVisibilitiesOfListOrderStatus(it.size)
        }
        initBottomBar()
        return binding?.root!!
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    private fun loadToken() {
        val account: Account? =
            AccountDatabase.getInstance(requireContext()).accountDao.getAccount()
        if (account != null) {
            viewModel.loadToken(
                TokenDTO(
                    account.accessToken, account.tokenType, account.refreshToken
                )
            )
        }
    }

    private fun initBottomBar() {
        binding!!.bottomBar.addOnTabSelectedListener(object :TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.text) {
                    "Chờ xác nhận" -> {
                        viewModel.loadOrder(OrderStatus.Pending)
                    }
                    "Xác nhận" -> {
                        binding!!.bottomBar.animate()
                        viewModel.loadOrder(OrderStatus.Confirmed)
                    }
                    "Đang giao" -> {
                        viewModel.loadOrder(OrderStatus.Delivering)
                    }
                    "Đã giao" -> {
                        viewModel.loadOrder(OrderStatus.Success)
                    }
                    "Đã hủy" -> {
                        viewModel.loadOrder(OrderStatus.Cancel)
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                // do nothing
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                // do nothing
            }

        })
    }

    private fun configVisibilitiesOfListOrderStatus(currentSize: Int) {
        if (currentSize == 0) {
            binding?.orders?.visibility = View.GONE
            binding?.noOrder?.visibility = View.VISIBLE
            return
        }
        binding?.noOrder?.visibility = View.GONE
        binding?.orders?.visibility = View.VISIBLE
    }

}