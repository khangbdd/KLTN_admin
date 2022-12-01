package com.example.aposs_admin.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.aposs_admin.R
import com.example.aposs_admin.databinding.ItemAccountBinding

class AccountAdapter(private val onChangePasswordClick: OnChangePasswordClick,
private val onDeleteAccountClick: OnDeleteAccountClick): ListAdapter<String, AccountAdapter.AccountViewHolder>(DiffCallBack.instance!!) {

    interface OnChangePasswordClick {
        fun onChangePasswordClick(account: String)
    }

    interface  OnDeleteAccountClick {
        fun onDeleteAccountCLick(account: String)
    }

    class DiffCallBack: DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return  oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
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

    class AccountViewHolder(val binding: ItemAccountBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(account: String) {
            binding.account = account
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccountViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding : ItemAccountBinding= DataBindingUtil.inflate(inflater, R.layout.item_account, parent, false)
        return AccountViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AccountViewHolder, position: Int) {
        val item: String = getItem(position)
        holder.bind(item)
        holder.binding.deleteAccount.setOnClickListener {
            onDeleteAccountClick.onDeleteAccountCLick(item)
        }
        holder.binding.changePassword.setOnClickListener {
            onChangePasswordClick.onChangePasswordClick(item)
        }
    }
}