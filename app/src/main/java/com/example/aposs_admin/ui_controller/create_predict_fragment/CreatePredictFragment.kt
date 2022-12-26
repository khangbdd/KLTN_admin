package com.example.aposs_admin.ui_controller.create_predict_fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.aposs_admin.R
import com.example.aposs_admin.databinding.FragmentCreateCategoryBinding
import com.example.aposs_admin.databinding.FragmentCreatePredictBinding


class CreatePredictFragment : Fragment() {

    private var binding: FragmentCreatePredictBinding? = null
    private val viewModel: CreatePredictViewModel by activityViewModels()

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
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_create_predict, container, false)
        binding?.lifecycleOwner =viewLifecycleOwner
        binding?.viewModel = viewModel
        navigateSetup()
        setUpMeter()
        setUpObserver()
        setUpChangeInNote()
        binding?.btnPredict?.setOnClickListener {
            createPredict()
        }
        binding?.back?.setOnClickListener {
            findNavController().popBackStack()
        }
        return binding?.root!!
    }

    private fun navigateSetup() {
        binding?.btnSelectKind?.setOnClickListener {
            findNavController().navigate(CreatePredictFragmentDirections.actionCreatePredictFragmentToListKindFragment())
        }
        binding?.btnSelectProduct?.setOnClickListener {
            if (viewModel.selectedKindId.value!! != -1L) {
                findNavController().navigate(
                    CreatePredictFragmentDirections.actionCreatePredictFragmentToListProductFragment(
                        viewModel.selectedKindId.value!!
                    )
                )
            }
        }
    }
    private val listSelection = arrayListOf("Month", "Day")
    private fun setUpMeter() {
        val adapter =  ArrayAdapter(this.requireContext(), R.layout.item_auto_complete , listSelection)
        binding?.selector?.setAdapter(adapter)
    }

    private fun setUpObserver() {
        binding?.selector?.setOnItemClickListener { parent, view, position, id ->
            viewModel.refreshLimit(listSelection[position])
        }
    }

    private fun setUpChangeInNote() {
        viewModel.selectedKindId.observe(viewLifecycleOwner) {
            viewModel.refreshLimit(binding?.selector?.text.toString())
        }
        viewModel.limitNumberOfDate.observe(viewLifecycleOwner) {
            if (binding?.selector?.text.toString() == "Day") {
                binding?.tvNoteDate?.text = "*Tối đa $it ngày kể từ hôm nay."
            }
        }
        viewModel.limitNumberOfMonth.observe(viewLifecycleOwner) {
            if (binding?.selector?.text.toString() == "Month") {
                binding?.tvNoteDate?.text = "*Tối đa $it tháng kể từ hôm nay."
            }
        }
        viewModel.selectedProductId.observe(viewLifecycleOwner) {
            viewModel.refreshLimit(binding?.selector?.text.toString())
        }
    }

    private fun createPredict() {
        if (binding?.selector?.text.toString() == "" ||viewModel.numberOfDate.value.toString() == "" || viewModel.selectedKindId.value!! == -1L)
        {
            Toast.makeText(this.requireContext(), "Thiếu thông tin", Toast.LENGTH_SHORT).show()
            return
        }
        if (viewModel.numberOfDate.value.toString().toInt() > viewModel.limitNumberOfDate.value!! && binding?.selector?.text.toString() == "Day") {
            Toast.makeText(this.requireContext(), "Vượt giới hạn dự báo", Toast.LENGTH_SHORT).show()
            return
        }
        if (viewModel.numberOfDate.value.toString().toInt() > viewModel.limitNumberOfMonth.value!! && binding?.selector?.text.toString() == "Month") {
            Toast.makeText(this.requireContext(), "Vượt giới hạn dự báo", Toast.LENGTH_SHORT).show()
            return
        }
        viewModel.createPredict(binding?.selector?.text.toString()) {
            Toast.makeText(this.requireContext(), "Tạo dự báo thành công", Toast.LENGTH_SHORT).show()
            findNavController().popBackStack()
        }
    }
}