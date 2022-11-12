package com.example.aposs_admin.ui_controller.product_detail_fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.aposs_admin.R
import com.example.aposs_admin.adapter.DetailProductImageViewPagerAdapter
import com.example.aposs_admin.animation.ZoomOutPageTransformer
import com.example.aposs_admin.databinding.FragmentDetailProductBinding
import com.example.aposs_admin.util.LoadingStatus
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FragmentDetailProduct : Fragment() {

    private val viewModel: DetailProductViewModel by activityViewModels()
    private var binding: FragmentDetailProductBinding? = null
    private val args: FragmentDetailProductArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_detail_product, container, false)
        binding?.viewModel = viewModel
        binding?.lifecycleOwner = viewLifecycleOwner
        val selectedProductId: Long = args.selectedId
        if (selectedProductId != -1L) {
            viewModel.setSelectedProductId(selectedProductId)
        }
        setBackButton()
        setUpViewPager()
        setUpIndicator()
        onDetailProductChange()
        return binding?.root!!
    }


    private fun setUpViewPager() {
        val imagesAdapter =
            DetailProductImageViewPagerAdapter(DetailProductImageViewPagerAdapter.OnClickListener {
                findNavController().navigate(
                    FragmentDetailProductDirections.actionFragmentDetailProductToFullScreenImageFragment(
                        it
                    )
                )
            })
        binding?.images?.setPageTransformer(ZoomOutPageTransformer())
        binding?.images?.adapter = imagesAdapter
    }

    private fun setBackButton() {
        binding?.back?.setOnClickListener {
            findNavController().popBackStack()
        }
    }


    private fun setUpIndicator() {
        binding?.indicator?.setViewPager(binding?.images)
    }

    private fun onDetailProductChange() {
        viewModel.productDetailLoadingState.observe(viewLifecycleOwner) {
            if (it == LoadingStatus.Loading) {
                binding?.detailProgress?.visibility = View.VISIBLE
            } else {
                binding?.detailProgress?.visibility = View.GONE
                if (it == LoadingStatus.Fail) {
                    Toast.makeText(this.requireContext(), "Loading fail", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}