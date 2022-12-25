package com.example.aposs_admin.adapter

import com.example.aposs_admin.model.dto.KindDTO

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

class KindAdapter(private val onClickListener: OnClickListener): ListAdapter<KindDTO, KindAdapter.KindViewHolder>(DiffCallBack.instance!!) {

    interface OnClickListener {
        fun onClick(position: Int)
    }

    class DiffCallBack : DiffUtil.ItemCallback<KindDTO>() {
        override fun areItemsTheSame(
            oldItem: KindDTO,
            newItem: KindDTO
        ): Boolean {
            return oldItem == newItem
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(
            oldItem: KindDTO,
            newItem: KindDTO
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

    inner class KindViewHolder(val binding: ItemCategoryAndSubcategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(kindDTO: KindDTO) {
            binding.name.text = kindDTO.name
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): KindViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding: ItemCategoryAndSubcategoryBinding= DataBindingUtil.inflate(layoutInflater, R.layout.item_category_and_subcategory, parent, false)
        return KindViewHolder(binding)
    }

    override fun onBindViewHolder(holder: KindViewHolder, position: Int) {
        val currentOrder: KindDTO = getItem(position)
        holder.bind(currentOrder)
        holder.itemView.setOnClickListener { onClickListener.onClick(position) }
    }
}