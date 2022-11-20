package com.example.aposs_admin.ui_controller.manage_category_fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.aposs_admin.R
import com.example.aposs_admin.adapter.CategoryAndSubcategoryAdapter
import com.example.aposs_admin.databinding.FragmentAllCategoryBinding
import com.example.aposs_admin.viewmodels.AllCategoryViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AllCategoryFragment : Fragment(), CategoryAndSubcategoryAdapter.OnClickListener {

    private lateinit var binding: FragmentAllCategoryBinding
    private val viewModel: AllCategoryViewModel by activityViewModels()
    private lateinit var categoryAdapter: CategoryAndSubcategoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_all_category, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        categoryAdapter = CategoryAndSubcategoryAdapter(this)
        binding.allCategories.adapter = categoryAdapter
        binding.back.setOnClickListener {
            requireActivity().finish()
        }
        binding.createNewCategory.setOnClickListener {
            findNavController().navigate(AllCategoryFragmentDirections.actionAllCategoryFragmentToCreateCategoryFragment())
        }
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onClick(position: Int) {
        viewModel.setCurrentCategory(position)
        findNavController().navigate(AllCategoryFragmentDirections.actionAllCategoryFragmentToDetailCategoryFragment())
    }
}