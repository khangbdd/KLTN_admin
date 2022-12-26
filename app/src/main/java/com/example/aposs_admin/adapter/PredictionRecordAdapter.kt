package com.example.aposs_admin.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.aposs_admin.R
import com.example.aposs_admin.databinding.ItemCategoryAndSubcategoryBinding
import com.example.aposs_admin.databinding.ItemPredictBinding
import com.example.aposs_admin.model.dto.KindDTO
import com.example.aposs_admin.model.dto.PredictionRecordDTO
import com.example.aposs_admin.util.PredictionStatus

class PredictionRecordAdapter(private val onClickListener: OnClickListener): ListAdapter<PredictionRecordDTO, PredictionRecordAdapter.PredictionRecordViewHolder>(DiffCallBack.instance!!) {

    interface OnClickListener {
        fun onClick(predict: PredictionRecordDTO)
    }

    class DiffCallBack : DiffUtil.ItemCallback<PredictionRecordDTO>() {
        override fun areItemsTheSame(
            oldItem: PredictionRecordDTO,
            newItem: PredictionRecordDTO
        ): Boolean {
            return oldItem == newItem
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(
            oldItem: PredictionRecordDTO,
            newItem: PredictionRecordDTO
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

    inner class PredictionRecordViewHolder(val binding: ItemPredictBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(predictionRecordDTO: PredictionRecordDTO) {
            binding.predictRecord = predictionRecordDTO
            if (predictionRecordDTO.isActive.not()) {
                binding.root.visibility = View.GONE
                return
            }
            predictionRecordDTO.predictionStatus?.let { setStatus(it) }
            setProductOrKind(predictionRecordDTO)
        }

        private fun setStatus(status: PredictionStatus) {
            if (status == PredictionStatus.Processing)
            {
                binding.tvStatusInfo.text = "Đang tính toán"
                binding.tvStatusInfo.setTextColor(Color.parseColor("#219653"));
                return
            }
            if (status == PredictionStatus.Cancel)
            {
                binding.tvStatusInfo.text = "Đã hủy"
                binding.tvStatusInfo.setTextColor(Color.parseColor("#5D7EF2F2"));
                return
            }
            if (status == PredictionStatus.Success)
            {
                binding.tvStatusInfo.text = "Hoàn thành"
                binding.tvStatusInfo.setTextColor(Color.parseColor("#F2935D"));
                return
            }
            if (status == PredictionStatus.Error)
            {
                binding.tvStatusInfo.text = "Lỗi tính toán"
                binding.tvStatusInfo.setTextColor(Color.parseColor("#863333F2"));
                return
            }
        }

        private fun setProductOrKind(predictionRecordDTO: PredictionRecordDTO) {
            if (predictionRecordDTO.productID != -1L) {
                binding.tvProductInfo.text = predictionRecordDTO.productName
                binding.tvProduct.text = "Sản phẩm: "
            } else {
                binding.tvProductInfo.text = predictionRecordDTO.subcategoryName
                binding.tvProduct.text = "Danh mục: "
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PredictionRecordViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding: ItemPredictBinding = DataBindingUtil.inflate(layoutInflater, R.layout.item_predict, parent, false)
        return PredictionRecordViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PredictionRecordViewHolder, position: Int) {
        val currentPredict: PredictionRecordDTO = getItem(position)
        holder.bind(currentPredict)
        holder.itemView.setOnClickListener { onClickListener.onClick(currentPredict) }
    }
}