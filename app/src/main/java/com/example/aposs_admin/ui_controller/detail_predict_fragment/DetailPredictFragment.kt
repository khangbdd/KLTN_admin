package com.example.aposs_admin.ui_controller.detail_predict_fragment

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.aposs_admin.R
import com.example.aposs_admin.databinding.FragmentDetailPredictBinding
import com.example.aposs_admin.model.dto.PredictionRecordItemDTO
import com.example.aposs_admin.model.dto.SaleDTO
import com.example.aposs_admin.util.PredictionStatus
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
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
        }
        configureLineChart()
        viewModel.startIndex.observe(viewLifecycleOwner) {
            setValueForLineChart()
        }
        binding?.back?.setOnClickListener {
            findNavController().popBackStack()
        }
        return binding?.root!!
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
        binding?.linechart?.setVisibleXRangeMaximum(10F)
        binding?.linechart?.setMaxVisibleValueCount(5)
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
            entries.add(Entry(index.toFloat(), data.sale.toFloat()))
            index++
        }
        Collections.sort(entries, EntryXComparator())
        val dataSet = LineDataSet(entries, label)
        dataSet.color = Color.parseColor(color)
        dataSet.setDrawCircles(false)
        return dataSet
    }

    private fun createFutureDataSetForLineChart(
        sales: List<PredictionRecordItemDTO>,
        color: String,
        label: String
    ): LineDataSet {
        val entries = ArrayList<Entry>()
        if (viewModel.startIndex.value != -1) {
            var startIndex = viewModel.startIndex.value!!
            Log.i("DetailPredictFragment", startIndex.toString())
            for (data in sales) {
                entries.add(Entry(startIndex.toFloat(), data.sale.toFloat()))
                startIndex++
            }
            Collections.sort(entries, EntryXComparator())
            val dataSet = LineDataSet(entries, label)
            dataSet.color = Color.parseColor(color)
            dataSet.setDrawCircles(false)
            return dataSet
        }
        return LineDataSet(entries, label)
    }
}