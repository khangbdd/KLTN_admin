package com.example.aposs_admin.ui_controller.full_image_fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.aposs_admin.R
import com.example.aposs_admin.adapter.DetailProductImageViewPagerAdapter
import com.example.aposs_admin.animation.ZoomOutPageTransformer
import com.example.aposs_admin.databinding.FragmentFullScreenImageBinding
import com.example.aposs_admin.ui_controller.product_detail_fragment.DetailProductViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FullScreenImageFragment : Fragment() {

    private val args: FullScreenImageFragmentArgs by navArgs()
    private var binding: FragmentFullScreenImageBinding? = null
    private val viewModel: DetailProductViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val currentPosition = args.imageIndex
        binding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.fragment_full_screen_image,
            container,
            false
        )
        binding?.viewModel = viewModel
        binding?.lifecycleOwner = this
        setUpViewPager(currentPosition)
        setUpIndicator()
        setUpBackButton()
        setUpLeftRightController()
        return binding?.root!!
    }

    private fun setUpViewPager(currentPosition: Int) {
        val imagesAdapter =
            DetailProductImageViewPagerAdapter(DetailProductImageViewPagerAdapter.OnClickListener {
                //Do nothing
            })
        binding?.images?.setPageTransformer(ZoomOutPageTransformer())
        binding?.images?.adapter = imagesAdapter
        binding?.images?.currentItem = currentPosition
    }

    private fun setUpIndicator() {
        binding?.indicator?.setViewPager(binding?.images)
    }
    private fun setUpBackButton(){
        binding?.back?.setOnClickListener {
            findNavController().popBackStack()
        }
    }
    private fun setUpLeftRightController() {
        binding?.goToLeftImage?.setOnClickListener {
            if (binding!!.images.currentItem != 0) {
                binding!!.images.currentItem -= 1
            } else {
                binding!!.images.currentItem = viewModel.selectedProductImages.value!!.size - 1
            }
        }
        binding?.goToRightImage?.setOnClickListener {
            if (binding!!.images.currentItem != viewModel.selectedProductImages.value!!.size - 1) {
                binding!!.images.currentItem += 1
            } else {
                binding!!.images.currentItem = 0
            }
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}