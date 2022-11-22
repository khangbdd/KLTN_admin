package com.example.aposs_admin.ui_controller.manage_category_fragment

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
import com.example.aposs_admin.adapter.CategoryAndSubCategoryImageAdapter
import com.example.aposs_admin.adapter.CategoryAndSubcategoryAdapter
import com.example.aposs_admin.databinding.FragmentDetailCategoryBinding
import com.example.aposs_admin.viewmodels.AllCategoryViewModel

class DetailCategoryFragment : Fragment(), CategoryAndSubcategoryAdapter.OnClickListener {

    private lateinit var binding: FragmentDetailCategoryBinding
    private val viewModel: AllCategoryViewModel by activityViewModels()
    private lateinit var categoryImageAdapter : CategoryAndSubCategoryImageAdapter
    private lateinit var subcategoryAdapter: CategoryAndSubcategoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_detail_category, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this.viewLifecycleOwner
        categoryImageAdapter = CategoryAndSubCategoryImageAdapter()
        subcategoryAdapter = CategoryAndSubcategoryAdapter(this)
        binding.subcategory.adapter = subcategoryAdapter
        binding.listImage.adapter = categoryImageAdapter

        binding.createSubcategory.setOnClickListener {
            findNavController().navigate(DetailCategoryFragmentDirections.actionDetailCategoryFragmentToCreateSubcategoryFragment())
        }



        binding.back.setOnClickListener {
            findNavController().popBackStack()
        }


        return binding.root
    }

    override fun onClick(position: Int) {
        viewModel.setCurrentSubcategory(position)
        findNavController().navigate(DetailCategoryFragmentDirections.actionDetailCategoryFragmentToDetailSubcategoryFragment())
    }
}