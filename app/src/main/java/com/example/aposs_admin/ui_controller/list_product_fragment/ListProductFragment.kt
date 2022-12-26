package com.example.aposs_admin.ui_controller.list_product_fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.aposs_admin.R
import com.example.aposs_admin.adapter.ProductsAdapter
import com.example.aposs_admin.databinding.FragmentListProductBinding
import com.example.aposs_admin.ui_controller.create_predict_fragment.CreatePredictViewModel
import com.example.aposs_admin.ui_controller.list_kind_fragment.ListKindViewModel
import com.example.aposs_admin.ui_controller.product_fragment.ProductsFragmentDirections
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ListProductFragment : Fragment() {

    private val viewModel: ListProductViewModel by viewModels()
    private val createPredictViewModel: CreatePredictViewModel by activityViewModels()
    private var binding: FragmentListProductBinding? = null
    private val args: ListProductFragmentArgs by navArgs()

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
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_list_product, container, false)
        binding?.viewModel = viewModel
        binding?.lifecycleOwner = viewLifecycleOwner
        viewModel.loadList(args.kindId)
        viewModel.observeSearch(viewLifecycleOwner)
        binding?.rcProducts?.adapter = ProductsAdapter(object: ProductsAdapter.OnClickProductListener{
            override fun onClickProduct(selectedId: Long) {
                createPredictViewModel.selectedProductId.value = selectedId
                createPredictViewModel.selectedProductName.value = viewModel.getNameById(selectedId)
                findNavController().popBackStack()
            }
        })
        binding?.back?.setOnClickListener {
            findNavController().popBackStack()
        }
        return binding?.root!!
    }
}