package com.example.aposs_admin.ui_controller.list_product_fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.example.aposs_admin.R
import com.example.aposs_admin.databinding.FragmentListProductBinding
import com.example.aposs_admin.ui_controller.list_kind_fragment.ListKindViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ListProductFragment : Fragment() {

    private val viewModel: ListProductViewModel by viewModels()
    private var binding: FragmentListProductBinding? = null
    private val args: ListProductFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
        return binding?.root!!
    }
}