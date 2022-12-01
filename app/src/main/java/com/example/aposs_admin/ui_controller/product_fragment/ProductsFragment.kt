package com.example.aposs_admin.ui_controller.product_fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.aposs_admin.R
import com.example.aposs_admin.adapter.ProductsAdapter
import com.example.aposs_admin.databinding.FragmentProductsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProductsFragment : Fragment(), ViewTreeObserver.OnScrollChangedListener  {

    private var binding: FragmentProductsBinding? = null
    private val viewModel: ProductsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_products, container, false)
        binding?.viewModel = viewModel
        binding?.lifecycleOwner = viewLifecycleOwner
        binding?.rcProducts?.adapter = ProductsAdapter(object: ProductsAdapter.OnClickProductListener{
            override fun onClickProduct(selectedId: Long) {
                findNavController().navigate(ProductsFragmentDirections.actionProductsFragmentToFragmentDetailProduct(selectedId))
            }
        })
        binding?.scrollView?.viewTreeObserver?.addOnScrollChangedListener(this)
        setUpButton()
        setUpSearchFilter()
        return binding?.root!!
    }

    override fun onScrollChanged() {
        // lazy load implementation
        val view: View? = binding?.scrollView?.getChildAt(binding?.scrollView!!.childCount - 1)
        val bottomDetector = view?.bottom?.minus((binding?.scrollView?.height!! + binding?.scrollView?.scrollY!!))
        if (bottomDetector != null) {
            if (bottomDetector <= 0) {
                viewModel.loadProducts()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    private fun setUpButton() {
        binding?.back?.setOnClickListener {
            findNavController().popBackStack()
        }
        binding?.btnAdd?.setOnClickListener {
            findNavController().navigate(ProductsFragmentDirections.actionProductsFragmentToAddProductFragment())
        }
    }

    private fun setUpSearchFilter() {
        binding?.search?.setOnClickListener {
            findNavController().navigate(ProductsFragmentDirections.actionProductsFragmentToSearchFragment())
        }
        viewModel.searchText.observe(viewLifecycleOwner) {
            viewModel.filter()
        }
    }
}