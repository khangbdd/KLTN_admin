package com.example.aposs_admin.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.aposs_admin.R
import com.example.aposs_admin.databinding.ItemAddImageBinding
import com.example.aposs_admin.model.LocalImage
import java.util.zip.Inflater

class AddImageAdapter: ListAdapter<LocalImage, AddImageAdapter.AddImageViewHolder>(DiffCallback.instance!!) {

    class DiffCallback: DiffUtil.ItemCallback<LocalImage>() {
        override fun areItemsTheSame(oldItem: LocalImage, newItem: LocalImage): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: LocalImage, newItem: LocalImage): Boolean {
            return oldItem == newItem
        }
        companion object {
            private var diffCallBack: AddImageAdapter.DiffCallback? = null
            val instance: AddImageAdapter.DiffCallback?
                get() {
                    if (diffCallBack == null) {
                        diffCallBack = AddImageAdapter.DiffCallback()
                    }
                    return diffCallBack
                }
        }
    }

    class AddImageViewHolder(val binding: ItemAddImageBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(src: LocalImage) {
            binding.localImage = src
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddImageViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = DataBindingUtil.inflate<ItemAddImageBinding>(inflater, R.layout.item_add_image, parent, false)
        return AddImageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AddImageViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }
}