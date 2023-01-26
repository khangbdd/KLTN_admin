package com.example.aposs_admin.ui_controller.order_detail_fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.aposs_admin.R
import com.example.aposs_admin.adapter.BillingItemsAdapter
import com.example.aposs_admin.databinding.FragmentOrderDetailBinding
import com.example.aposs_admin.local_storage.entity.Account
import com.example.aposs_admin.model.dto.TokenDTO
import com.example.aposs_admin.util.OrderStatus
import com.example.aposs_buyer.responsitory.database.AccountDatabase
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class OrderDetailFragment : Fragment() {

    private val viewModel: OrderDetailViewModel by viewModels()
    private var binding: FragmentOrderDetailBinding? = null
    private var orderDetailBillingItem: BillingItemsAdapter? = null
    private val args: OrderDetailFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_order_detail, container, false)
        binding?.viewModel = viewModel
        binding?.lifecycleOwner = viewLifecycleOwner
        viewModel.detailOrder.value = args.order
        orderDetailBillingItem = BillingItemsAdapter()
        binding?.billingItems?.adapter = orderDetailBillingItem
        viewModel.detailOrder.value?.let {
            setShowButtonByStatus(it.status)
        }
        binding?.statusString?.text = viewModel.detailOrder.value!!.statusString
        binding?.back?.setOnClickListener {
           findNavController().popBackStack()
        }

        binding?.cancel?.setOnClickListener {
            viewModel.detailOrder.value?.let {
                viewModel.setOrderStatus(it.id, OrderStatus.Cancel) {
                    binding?.statusString?.text = viewModel.detailOrder.value!!.statusString
                }
                setShowButtonByStatus(OrderStatus.Cancel)
            }
        }
        binding?.ratingNow?.setOnClickListener {
            viewModel.detailOrder.value?.let {
                if (it.status === OrderStatus.Pending) {
                    viewModel.setOrderStatus(it.id, OrderStatus.Confirmed)
                    {
                        binding?.statusString?.text = viewModel.detailOrder.value!!.statusString
                    }
                    setShowButtonByStatus(OrderStatus.Confirmed)
//                    orderViewModel.loadOrder(OrderStatus.Pending)
                }
                if (it.status === OrderStatus.Confirmed) {
                    viewModel.setOrderStatus(it.id, OrderStatus.Delivering)
                    {
                        binding?.statusString?.text = viewModel.detailOrder.value!!.statusString
                    }
                    setShowButtonByStatus(OrderStatus.Delivering)
//                    orderViewModel.loadOrder(OrderStatus.Confirmed)
                }
            }
        }
        if (viewModel.detailOrder.value?.isOnlinePayment != null) {
            if (!viewModel.detailOrder.value!!.isOnlinePayment) {
                binding?.paymentStatusValue?.visibility = View.GONE
            }
        }
        loadToken()
        setVisibilities()
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
            viewModel.token = TokenDTO(
                account.accessToken, account.tokenType, account.refreshToken
            )
        }
    }

    private fun setShowButtonByStatus(status: OrderStatus) {
        if (status === OrderStatus.Success) {
            binding!!.ratingNow.visibility = View.GONE
            binding!!.cancel.visibility = View.GONE
            //            binding.cancel.setVisibility(View.GONE);
        } else if (status === OrderStatus.Pending) {
            binding!!.ratingNow.text = "Xác nhận đơn"
            binding!!.ratingNow.visibility = View.VISIBLE
            binding!!.cancel.visibility = View.VISIBLE
            //            binding.cancel.setVisibility(View.VISIBLE);
        } else if (status === OrderStatus.Confirmed) {
            binding!!.ratingNow.text = "Vận chuyển đơn"
            //            binding.cancel.setVisibility(View.VISIBLE);
            binding!!.ratingNow.visibility = View.VISIBLE
            binding!!.cancel.visibility = View.VISIBLE
        } else if (status === OrderStatus.Delivering) {
            binding!!.ratingNow.visibility = View.GONE
            binding!!.cancel.visibility = View.GONE
            //            binding.cancel.setVisibility(View.GONE);
        } else if (status === OrderStatus.Cancel) {
            binding!!.ratingNow.visibility = View.GONE
            binding!!.cancel.visibility = View.GONE
            //            binding.cancel.setVisibility(View.GONE);
        }
    }

    private fun setVisibilities() {
        if (viewModel.detailOrder.value?.status == OrderStatus.Cancel) {
            binding?.reason?.visibility = View.VISIBLE
            binding?.reasonString?.visibility = View.VISIBLE
            return
        }
        binding?.reason?.visibility = View.GONE
        binding?.reasonString?.visibility = View.GONE
    }
}