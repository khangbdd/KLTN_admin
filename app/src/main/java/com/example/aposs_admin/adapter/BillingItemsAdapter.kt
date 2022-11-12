package com.example.aposs_admin.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.aposs_admin.R
import com.example.aposs_admin.databinding.ItemOrderBillingBinding
import com.example.aposs_admin.model.OrderBillingItem

class BillingItemsAdapter :
    ListAdapter<OrderBillingItem, BillingItemsAdapter.BillingItemViewHolder>(
        DiffCallBack.instance!!
    ) {
    class DiffCallBack : DiffUtil.ItemCallback<OrderBillingItem>() {
        override fun areItemsTheSame(
            oldItem: OrderBillingItem,
            newItem: OrderBillingItem
        ): Boolean {
            return oldItem == newItem
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(
            oldItem: OrderBillingItem,
            newItem: OrderBillingItem
        ): Boolean {
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BillingItemViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding: ItemOrderBillingBinding= DataBindingUtil.inflate(layoutInflater, R.layout.item_order_billing, parent, false)
        return BillingItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BillingItemViewHolder, position: Int) {
        val currentOrder = getItem(position)
        holder.bind(currentOrder)
    }

    inner class BillingItemViewHolder(private val binding: ItemOrderBillingBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(orderBillingItem: OrderBillingItem?) {
            binding.billingItem = orderBillingItem
        }
    }
}