package com.example.aposs_admin.ui_controller.list_kind_fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.aposs_admin.R
import com.example.aposs_admin.adapter.CategoryAndSubcategoryAdapter
import com.example.aposs_admin.adapter.KindAdapter
import com.example.aposs_admin.databinding.FragmentListKindBinding
import com.example.aposs_admin.ui_controller.create_predict_fragment.CreatePredictViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ListKindFragment : Fragment() {

    private val viewModel: ListKindViewModel by viewModels()
    private val createPredictViewModel: CreatePredictViewModel by activityViewModels()
    private var binding: FragmentListKindBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_list_kind, container, false)
        binding?.viewModel = viewModel
        binding?.lifecycleOwner = viewLifecycleOwner
        binding?.rcKinds?.adapter = KindAdapter(
            object : KindAdapter.OnClickListener {
                override fun onClick(position: Int) {
                    val kindID = viewModel.listDisplay.value?.get(position)?.id
                    if (kindID != null) {
                        createPredictViewModel.selectedKindId.value = kindID
                        createPredictViewModel.selectedKindName.value = viewModel.listDisplay.value?.get(position)?.name
                        createPredictViewModel.selectedProductId.value = -1
                        createPredictViewModel.selectedProductName.value = "Chọn sản phẩm dự báo"
                        findNavController().popBackStack()
                    }
                }
            }
        )
        viewModel.setUpObserver(viewLifecycleOwner)
        binding?.back?.setOnClickListener {
            findNavController().popBackStack()
        }
        return binding?.root!!
    }
}