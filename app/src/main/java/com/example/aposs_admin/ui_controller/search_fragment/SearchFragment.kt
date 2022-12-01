package com.example.aposs_admin.ui_controller.search_fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.aposs_admin.R
import com.example.aposs_admin.adapter.ProductsAdapter
import com.example.aposs_admin.databinding.FragmentProductsBinding
import com.example.aposs_admin.databinding.FragmentSearchBinding
import com.example.aposs_admin.ui_controller.product_fragment.ProductsFragmentDirections
import com.example.aposs_admin.ui_controller.product_fragment.ProductsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchFragment : Fragment(), ViewTreeObserver.OnScrollChangedListener  {

    private var binding: FragmentSearchBinding? = null
    private val viewModel: SearchViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_search, container, false)
        binding?.viewModel = viewModel
        binding?.lifecycleOwner = viewLifecycleOwner
        binding?.rcProducts?.adapter = ProductsAdapter(object: ProductsAdapter.OnClickProductListener{
            override fun onClickProduct(selectedId: Long) {
                findNavController().navigate(SearchFragmentDirections.actionSearchFragmentToFragmentDetailProduct(selectedId))
            }
        })
        viewModel.searchText.observe(viewLifecycleOwner) {
            viewModel.onSearchTextChange()
        }
        setUpNestedScrollView()
        setUpButton()
        return binding?.root!!
    }

    private fun setUpButton() {
        binding?.back?.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun onScrollChanged() {
        val view: View? = binding?.scrollView?.getChildAt(binding?.scrollView!!.childCount - 1)
        val bottomDetector = view?.bottom?.minus((binding?.scrollView?.height!! + binding?.scrollView?.scrollY!!))
        if (bottomDetector != null) {
            if (bottomDetector <= 0) {
                viewModel.loadListForDisplay()
            }
        }
    }

    private fun setUpNestedScrollView() {
        binding?.scrollView?.viewTreeObserver?.addOnScrollChangedListener(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}