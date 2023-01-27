package com.example.aposs_admin.ui_controller.filter_subcategory_fragment

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
import com.example.aposs_admin.adapter.KindAdapter
import com.example.aposs_admin.databinding.FragmentFilterSubcategoryBinding
import com.example.aposs_admin.databinding.FragmentListKindBinding
import com.example.aposs_admin.ui_controller.create_predict_fragment.CreatePredictViewModel
import com.example.aposs_admin.ui_controller.list_kind_fragment.ListKindViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FilterSubcategoryFragment : Fragment() {

    private val viewModel: FilterSubcategoryViewModel by viewModels()
    private var binding: FragmentFilterSubcategoryBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_filter_subcategory, container, false)
        binding?.viewModel = viewModel
        binding?.lifecycleOwner = viewLifecycleOwner
        binding?.rcKinds?.adapter = KindAdapter(
            object : KindAdapter.OnClickListener {
                override fun onClick(position: Int) {
                    val kindID = viewModel.listDisplay.value?.get(position)?.id
                    if (kindID != null) {
                        findNavController().navigate(FilterSubcategoryFragmentDirections.actionFilterSubcategoryFragmentToFilterProductFragment(kindID))
                    }
                }
            }
        )
        viewModel.setUpObserver(viewLifecycleOwner)
        binding?.back?.setOnClickListener {
            findNavController().popBackStack()
        }
        return binding!!.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}