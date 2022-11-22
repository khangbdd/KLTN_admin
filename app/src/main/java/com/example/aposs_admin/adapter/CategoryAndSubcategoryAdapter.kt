package com.example.aposs_admin.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.aposs_admin.R
import com.example.aposs_admin.databinding.ItemCategoryAndSubcategoryBinding
import com.example.aposs_admin.model.Order

class CategoryAndSubcategoryAdapter(private val onClickListener: OnClickListener): ListAdapter<String, CategoryAndSubcategoryAdapter.CategoryAndSubcategoryViewHolder>(DiffCallBack.instance!!) {

    interface OnClickListener {
        fun onClick(position: Int)
    }

    class DiffCallBack : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(
            oldItem: String,
            newItem: String
        ): Boolean {
            return oldItem == newItem
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(
            oldItem: String,
            newItem: String
        ): Boolean {
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

    inner class CategoryAndSubcategoryViewHolder(val binding: ItemCategoryAndSubcategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(name: String) {
            binding.name.text = name
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CategoryAndSubcategoryViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding: ItemCategoryAndSubcategoryBinding= DataBindingUtil.inflate(layoutInflater, R.layout.item_category_and_subcategory, parent, false)
        return CategoryAndSubcategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryAndSubcategoryViewHolder, position: Int) {
        val currentOrder: String = getItem(position)
        holder.bind(currentOrder)
        holder.itemView.setOnClickListener { onClickListener.onClick(position) }
    }
}