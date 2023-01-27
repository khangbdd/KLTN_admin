package com.example.aposs_admin.ui_controller.filter_product_fragment

import android.os.Bundle
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
import com.example.aposs_admin.databinding.FragmentFilterProductBinding
import com.example.aposs_admin.databinding.FragmentListProductBinding
import com.example.aposs_admin.ui_controller.create_predict_fragment.CreatePredictViewModel
import com.example.aposs_admin.ui_controller.list_product_fragment.ListProductFragmentArgs
import com.example.aposs_admin.ui_controller.list_product_fragment.ListProductViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FilterProductFragment : Fragment() {
    private val viewModel: FilterProductViewModel by viewModels()
    private var binding: FragmentFilterProductBinding? = null
    private val args: FilterProductFragmentArgs by navArgs()

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
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_filter_product, container, false)
        binding?.viewModel = viewModel
        binding?.lifecycleOwner = viewLifecycleOwner
        viewModel.loadList(args.kindId)
        viewModel.observeSearch(viewLifecycleOwner)
        binding?.rcProducts?.adapter = ProductsAdapter(object: ProductsAdapter.OnClickProductListener{
            override fun onClickProduct(selectedId: Long) {
                findNavController().navigate(FilterProductFragmentDirections.actionFilterProductFragmentToFragmentDetailProduct(selectedId))
            }
        })
        binding?.back?.setOnClickListener {
            findNavController().popBackStack()
        }
        return binding?.root!!
    }
}