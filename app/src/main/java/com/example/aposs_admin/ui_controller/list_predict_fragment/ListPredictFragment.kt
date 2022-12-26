package com.example.aposs_admin.ui_controller.list_predict_fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.aposs_admin.R
import com.example.aposs_admin.adapter.PredictionRecordAdapter
import com.example.aposs_admin.databinding.FragmentListPredictBinding
import com.example.aposs_admin.model.dto.PredictionRecordDTO
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ListPredictFragment : Fragment() {

    private var binding: FragmentListPredictBinding? = null
    private val viewModel: ListPredictViewModel by viewModels()

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
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_list_predict, container, false)
        binding?.lifecycleOwner = viewLifecycleOwner
        binding?.viewModel = viewModel
        binding?.rcPredicts?.adapter = PredictionRecordAdapter(object : PredictionRecordAdapter.OnClickListener {
            override fun onClick(predict: PredictionRecordDTO) {
                // fix later
                findNavController().navigate(ListPredictFragmentDirections.actionListPredictFragmentToDetailPredictFragment(
                    1
                ))
            }
        })
        binding?.createNewPredict?.setOnClickListener {
            findNavController().navigate(ListPredictFragmentDirections.actionListPredictFragmentToCreatePredictFragment())
        }
        binding?.back?.setOnClickListener {
            findNavController().popBackStack()
        }
        return binding?.root!!
    }
}