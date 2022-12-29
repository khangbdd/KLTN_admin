package com.example.aposs_admin.ui_controller.detail_predict_fragment

import android.graphics.Color
import android.os.Bundle
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
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.utils.EntryXComparator
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*


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
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_detail_predict, container, false)
        binding?.viewModel = viewModel
        binding?.lifecycleOwner = viewLifecycleOwner
        viewModel.loadDetailPredictDTO(arg.predictID)
        configureLineChart()
        viewModel.listOldSale.observe(viewLifecycleOwner) {
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
            private val mFormat = SimpleDateFormat("dd/MM/yyyy")
            override fun getFormattedValue(value: Float): String {
                val millis = value.toLong()
                return mFormat.format(Date(millis))
            }
        }
    }

    private fun setValueForLineChart() {
        val oldData = viewModel.listOldSale.value!!
        val newData: MutableList<PredictionRecordItemDTO> = (viewModel.detailPredictDTO.value!!.recordItemDTOList as MutableList<PredictionRecordItemDTO>?)!!
        val lineData = LineData(
            createDataSetForLineChart(oldData, "#219653", "Thực tế"),
            createFutureDataSetForLineChart( newData, "#F2935D", "Dự báo")
        )
        binding?.linechart?.data = lineData
    }

    private fun createDataSetForLineChart(sales: MutableList<SaleDTO>, color: String, label: String): LineDataSet {
        val entries = ArrayList<Entry>()
        for (data in sales) {
            val myDate = "${data.date} 00:00:00"
            val sdf = SimpleDateFormat("dd-MM-yyyy HH:mm:ss")
            val date = sdf.parse(myDate)
            val millis = date?.time
            if (millis != null) {
                entries.add(Entry(millis.toFloat(), data.sale.toFloat()));
            }
        }
        Collections.sort(entries, EntryXComparator())
        val dataSet = LineDataSet(entries, label)
        dataSet.color = Color.parseColor(color)
        dataSet.setDrawCircles(false)
        return dataSet
    }

    private fun createFutureDataSetForLineChart(sales: List<PredictionRecordItemDTO>, color: String, label: String): LineDataSet {
        var entries = ArrayList<Entry>()
        for (data in sales) {
            val myDate = "${data.date} 00:00:00"
            val sdf = SimpleDateFormat("dd-MM-yyyy HH:mm:ss")
            val date = sdf.parse(myDate)
            val millis = date?.time
            if (millis != null) {
                entries.add(Entry(millis.toFloat(), data.sale.toFloat()));
            }
        }
        Collections.sort(entries, EntryXComparator())
        val dataSet = LineDataSet(entries, label)
        dataSet.color = Color.parseColor(color)
        dataSet.setDrawCircles(false)
        return dataSet
    }
}