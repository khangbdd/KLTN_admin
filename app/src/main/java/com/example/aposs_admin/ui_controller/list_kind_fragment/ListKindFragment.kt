package com.example.aposs_admin.ui_controller.list_kind_fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.aposs_admin.R
import com.example.aposs_admin.adapter.CategoryAndSubcategoryAdapter
import com.example.aposs_admin.databinding.FragmentListKindBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ListKindFragment : Fragment() {

    private val viewModel: ListKindViewModel by viewModels()
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

        binding?.rcKinds?.adapter = CategoryAndSubcategoryAdapter(
            object : CategoryAndSubcategoryAdapter.OnClickListener {
                override fun onClick(position: Int) {
                    val kindID = viewModel.listDisplay.value?.get(position)?.id
                    // set to viewModel of
                }
            }
        )
        viewModel.setUpObserver(viewLifecycleOwner)
        return binding?.root!!
    }
}