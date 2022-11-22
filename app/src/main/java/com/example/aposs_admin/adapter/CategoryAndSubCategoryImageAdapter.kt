package com.example.aposs_admin.adapter

import android.annotation.SuppressLint
import android.provider.ContactsContract.Data
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.aposs_admin.R
import com.example.aposs_admin.databinding.ItemCategoryAndSubcategoryBinding
import com.example.aposs_admin.databinding.ItemCategoryAndSubcategoryImageBinding
import com.example.aposs_admin.model.Image

class CategoryAndSubCategoryImageAdapter: ListAdapter<Image, CategoryAndSubCategoryImageAdapter.CategoryAndSubcategoryImageViewHolder>(DiffCallBack.instance!!) {

    class DiffCallBack : DiffUtil.ItemCallback<Image>() {
        override fun areItemsTheSame(
            oldItem: Image,
            newItem: Image
        ): Boolean {
            return oldItem == newItem
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(
            oldItem: Image,
            newItem: Image
        ): Boolean {
            return oldItem.imgURL == newItem.imgURL
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

    inner class CategoryAndSubcategoryImageViewHolder(val binding: ItemCategoryAndSubcategoryImageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(image: Image) {
            binding.image = image
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CategoryAndSubcategoryImageViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding: ItemCategoryAndSubcategoryImageBinding = DataBindingUtil.inflate(layoutInflater, R.layout.item_category_and_subcategory_image, parent, false)
        return CategoryAndSubcategoryImageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryAndSubcategoryImageViewHolder, position: Int) {
        val currentOrder: Image = getItem(position)
        holder.bind(currentOrder)
    }
}