package com.example.aposs_admin.adapter

import android.annotation.SuppressLint
import android.opengl.Visibility
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.aposs_admin.R
import com.example.aposs_admin.databinding.ItemOrderBillingBinding
import com.example.aposs_admin.databinding.ItemOrderBinding
import com.example.aposs_admin.model.Order
import com.example.aposs_admin.util.OrderStatus
import com.example.aposs_admin.util.PaymentStatus

class OrderAdapter(
    private val onClickListener: OnClickListener,
    private val onChangeStatusClick: OnChangeStatusClick,
    private val onConfirmPaymentClick: OnConfirmPaymentClick
) :
    ListAdapter<Order, OrderAdapter.OrderViewHolder>(
        DiffCallBack.instance!!
    ) {
    interface OnChangeStatusClick {
        fun onChangeStatusClick(orderId: Long, orderStatus: OrderStatus?)
    }

    interface OnConfirmPaymentClick {
        fun onConfirmPaymentClick(orderId: Long, orderStatus: OrderStatus?)
    }

    class DiffCallBack : DiffUtil.ItemCallback<Order>() {
        override fun areItemsTheSame(oldItem: Order, newItem: Order): Boolean {
            return oldItem == newItem
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: Order, newItem: Order): Boolean {
            return oldItem.id == newItem.id
        }

        companion object {
            private var diffCallBack: DiffCallBack? = null
            val instance: DiffCallBack?
                get() {
                    if (diffCallBack == null) {
                        diffCallBack = DiffCallBack()
                    }
                    return diffCallBack
                }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding: ItemOrderBinding = DataBindingUtil.inflate(layoutInflater, R.layout.item_order, parent, false)
        return OrderViewHolder(binding, onChangeStatusClick)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val currentOrder: Order = getItem(position)
        holder.bind(currentOrder)
        holder.itemView.setOnClickListener { onClickListener.onClick(currentOrder) }
    }

    interface OnClickListener {
        fun onClick(order: Order?)
    }

    inner class OrderViewHolder(
        var binding: ItemOrderBinding,
        var onChangeStatusClick: OnChangeStatusClick
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(order: Order?) {
            binding.order = order
            val adapter = BillingItemsAdapter()
            binding.billingItems.adapter = adapter
            if (order!!.status === OrderStatus.Pending) {
                binding.ratingNow.visibility = View.VISIBLE
                binding.ratingNow.text = "Xác nhận đơn"
            } else if (order!!.status === OrderStatus.Confirmed) {
                binding.ratingNow.visibility = View.VISIBLE
                binding.ratingNow.text = "Đã vận chuyển đơn"
            } else if (order!!.status === OrderStatus.Delivering) {
                binding.ratingNow.visibility = View.GONE
            } else if (order!!.status === OrderStatus.Success) {
                binding.ratingNow.visibility = View.GONE
            } else if (order!!.status === OrderStatus.Cancel) {
                binding.ratingNow.visibility = View.GONE
            }
            binding.ratingNow.setOnClickListener {
                if (order!!.status === OrderStatus.Pending) {
                    onChangeStatusClick.onChangeStatusClick(order!!.id, OrderStatus.Confirmed)
                } else if (order!!.status === OrderStatus.Confirmed) {
                    onChangeStatusClick.onChangeStatusClick(order!!.id, OrderStatus.Delivering)
                }
            }
            binding.completePayment.setOnClickListener {
                onConfirmPaymentClick.onConfirmPaymentClick(order!!.id, order.status)
            }
            if (order != null) {
                if (!order.isOnlinePayment) {
                    binding.completePayment.visibility = View.GONE
                    binding.isPay.visibility = View.GONE
                    binding.isPayValue.visibility = View.GONE
                } else {
                    binding.isPay.visibility = View.VISIBLE
                    binding.isPayValue.visibility = View.VISIBLE
                    binding.completePayment.visibility = View.VISIBLE
                    when (order.paymentStatus) {
                        PaymentStatus.Completed -> {
                            binding.completePayment.visibility = View.GONE
                        }
                        PaymentStatus.Waiting -> {
                            binding.completePayment.visibility = View.VISIBLE
                        }
                        else -> {
                            binding.completePayment.visibility = View.VISIBLE
                        }
                    }
                }
            }


        }
    }
}