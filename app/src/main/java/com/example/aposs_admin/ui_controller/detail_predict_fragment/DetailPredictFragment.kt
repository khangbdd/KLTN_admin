package com.example.aposs_admin.ui_controller.detail_predict_fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.navArgs
import com.example.aposs_admin.R
import com.example.aposs_admin.databinding.FragmentDetailPredictBinding
import dagger.hilt.android.AndroidEntryPoint

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
        return binding?.root!!
    }
}