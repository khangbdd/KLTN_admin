package com.example.aposs_admin.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.aposs_admin.R
import com.example.aposs_admin.databinding.ProductItemBinding
import com.example.aposs_admin.model.HomeProduct
import com.google.android.gms.common.util.DataUtils


class ProductsAdapter(private val onClickProductListener: OnClickProductListener) :
    ListAdapter<HomeProduct, ProductsAdapter.ProductViewHolder>(DiffCallback.instance!!) {

    interface OnClickProductListener{
        fun onClickProduct(selectedId: Long)
    }

    class DiffCallback : DiffUtil.ItemCallback<HomeProduct>() {
        override fun areItemsTheSame(oldItem: HomeProduct, newItem: HomeProduct): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: HomeProduct, newItem: HomeProduct): Boolean {
            return oldItem.id == newItem.id
        }

        companion object {
            private var diffCallBack: DiffCallback? = null
            val instance: DiffCallback?
                get() {
                    if (diffCallBack == null) {
                        diffCallBack = DiffCallback()
                    }
                    return diffCallBack
                }
        }
    }

    class ProductViewHolder(private val binding: ProductItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(product: HomeProduct) {
            binding.product = product
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = DataBindingUtil.inflate<ProductItemBinding>(
            layoutInflater,
            R.layout.product_item,
            parent,
            false
        )
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val item: HomeProduct = getItem(position)
        holder.bind(item)
        holder.itemView.setOnClickListener {
            onClickProductListener.onClickProduct(item.id)
        }
    }
}