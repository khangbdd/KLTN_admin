package com.example.aposs_admin.ui_controller.detail_predict_fragment

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.aposs_admin.R
import com.example.aposs_admin.databinding.FragmentDetailPredictBinding
import com.example.aposs_admin.model.dto.PredictionRecordItemDTO
import com.example.aposs_admin.model.dto.SaleDTO
import com.example.aposs_admin.ui_controller.dialog.DeletePredictYesNoDialog
import com.example.aposs_admin.util.PredictionStatus
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.EntryXComparator
import dagger.hilt.android.AndroidEntryPoint
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


@AndroidEntryPoint
class DetailPredictFragment : Fragment() {

    private var binding: FragmentDetailPredictBinding? = null
    private val viewModel: DetailPredictViewModel by viewModels()
    private val arg: DetailPredictFragmentArgs by navArgs()
    private var isDonePrepare: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_detail_predict, container, false)
        binding?.viewModel = viewModel
        binding?.lifecycleOwner = viewLifecycleOwner
        viewModel.loadDetailPredictDTO(arg.predictID) {
            if (it == PredictionStatus.Success) {
                binding?.linechart?.visibility = View.VISIBLE
                binding?.noLinechart?.visibility = View.GONE
            } else {
                binding?.linechart?.visibility = View.GONE
                binding?.noLinechart?.visibility = View.VISIBLE
            }
            configVisibilities()
        }
        configureLineChart()
//        viewModel.startIndex.observe(viewLifecycleOwner) {
//            if (it != -1) {
//                showChartIfNeed()
//            }
//        }
        viewModel.detailPredictDTO.observe(viewLifecycleOwner) {
            if (it.recordItemDTOList!!.isNotEmpty()) {
                showChartIfNeed()
            }
        }
        viewModel.listOldSale.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                showChartIfNeed()
            }
        }
        viewModel.listFullDate.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                showChartIfNeed()
            }
        }
        viewModel.listIndexOfPredict.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                showChartIfNeed()
            }
        }
        viewModel.listIndexOfSale.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                showChartIfNeed()
            }
        }
        binding?.back?.setOnClickListener {
            findNavController().popBackStack()
        }
        binding?.btnDelete?.setOnClickListener {
            fragmentManager?.beginTransaction()
                ?.add(DeletePredictYesNoDialog(requireContext(), "Xác nhận xóa dự báo ${viewModel.detailPredictDTO.value!!.name}?", viewModel.detailPredictDTO.value!!, findNavController()), null)
                ?.commit()
        }
        binding?.btnEdit?.setOnClickListener {
            val passingData = viewModel.detailPredictDTO.value!!
            findNavController().navigate(DetailPredictFragmentDirections.actionDetailPredictFragmentToChangePredictInfoFragment(passingData.name?:"", passingData.description?:"", passingData.id))
        }
        viewModel.detailPredictDTO.observe(viewLifecycleOwner) {
            configVisibilities()
        }
        return binding?.root!!
    }

    private fun showChartIfNeed() {
        if (isDonePrepare < 5) {
            isDonePrepare++
        }
        if (isDonePrepare == 5) {
            setValueForLineChart()
        }
    }

    private fun configureLineChart() {
        val desc = Description()
        desc.text = "Sale Prediction"
        desc.textSize = 16F
        binding?.linechart?.description = desc
        val xAxis: XAxis? = binding?.linechart?.xAxis
        xAxis?.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return viewModel.listFullDate.value!![value.toInt()]
            }
        }
    }

    private fun setValueForLineChart() {
        val oldData = viewModel.listOldSale.value!!
        val newData: MutableList<PredictionRecordItemDTO> =
            (viewModel.detailPredictDTO.value!!.recordItemDTOList as MutableList<PredictionRecordItemDTO>?)!!
        val lineData = LineData(
            createDataSetForLineChart(oldData, "#219653", "Thực tế"),
            createFutureDataSetForLineChart(newData, "#FD0909", "Dự báo")
        )
        binding?.linechart?.layoutParams?.height = ViewGroup.LayoutParams.MATCH_PARENT
        binding?.linechart?.layoutParams?.width = ViewGroup.LayoutParams.MATCH_PARENT
        binding?.linechart?.data = lineData
        binding?.linechart?.setVisibleXRangeMaximum(40F)
        binding?.linechart?.notifyDataSetChanged()
        binding?.linechart?.moveViewToX(viewModel.listFullDate.value!!.size.toFloat())
    }

    private fun createDataSetForLineChart(
        sales: MutableList<SaleDTO>,
        color: String,
        label: String
    ): LineDataSet {
        val entries = ArrayList<Entry>()
        var index = 0
        for (data in sales) {
            entries.add(Entry(viewModel.listIndexOfSale.value!![index].toFloat(), data.sale.toFloat()))
            index++
        }
        Collections.sort(entries, EntryXComparator())
        val dataSet = LineDataSet(entries, label)
        dataSet.color = Color.parseColor(color)
        dataSet.setDrawCircles(true)
        dataSet.setDrawValues(true)
        return dataSet
    }

    private fun createFutureDataSetForLineChart(
        sales: List<PredictionRecordItemDTO>,
        color: String,
        label: String
    ): LineDataSet {
        val entries = ArrayList<Entry>()
        var index = 0
        for (data in sales) {
            entries.add(Entry(viewModel.listIndexOfPredict.value!![index].toFloat(), data.sale.toFloat()))
            index++
        }
        Collections.sort(entries, EntryXComparator())
        val dataSet = LineDataSet(entries, label)
        dataSet.color = Color.parseColor(color)
        dataSet.setDrawCircles(true)
        dataSet.setDrawValues(true)
        return dataSet
        return LineDataSet(entries, label)
    }

    private fun configVisibilities() {
        if (viewModel.detailPredictDTO.value?.description != null) {
            binding?.tvDescriptionInfo?.setTypeface(null, Typeface.NORMAL)
        }
        if (viewModel.detailPredictDTO.value?.productID == -1L) {
            binding?.tvProduct?.visibility = View.GONE
            binding?.tvProductInfo?.visibility = View.GONE
        }
    }

}