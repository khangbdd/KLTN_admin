package com.example.aposs_admin.adapter

import android.annotation.SuppressLint
import android.provider.ContactsContract.Data
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.aposs_admin.R
import com.example.aposs_admin.databinding.ItemCategoryAndSubcategoryBinding
import com.example.aposs_admin.databinding.ItemCategoryAndSubcategoryImageBinding
import com.example.aposs_admin.model.Image

class CategoryAndSubCategoryImageAdapter(val onClickListener: OnClickListener): ListAdapter<Image, CategoryAndSubCategoryImageAdapter.CategoryAndSubcategoryImageViewHolder>(DiffCallBack.instance!!) {

    var isInEditMode: Boolean = false

    interface OnClickListener {
        fun removeImageClick(position: Int)
    }

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
        val currentImage: Image = getItem(position)
        if(isInEditMode){
            holder.binding.removeImage.visibility = View.VISIBLE
            holder.binding.removeImage.setOnClickListener {
                onClickListener.removeImageClick(position)
            }
        }else{
            holder.binding.removeImage.visibility = View.GONE
        }
        holder.bind(currentImage)
    }
}