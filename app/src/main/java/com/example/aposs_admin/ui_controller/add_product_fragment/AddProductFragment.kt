package com.example.aposs_admin.ui_controller.add_product_fragment

import android.Manifest
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.aposs_admin.R
import com.example.aposs_admin.adapter.AddImageAdapter
import com.example.aposs_admin.databinding.FragmentAddProductBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class AddProductFragment : Fragment() {

    private var binding: FragmentAddProductBinding? = null
    private val viewModel: AddProductViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_add_product, container, false)
        binding?.lifecycleOwner = viewLifecycleOwner
        binding?.viewModel = viewModel
        binding?.back?.setOnClickListener {
            findNavController().popBackStack()
        }
        setUpAddImageButton()
        setUpListImage()
        setUpCategoriesList()
        setUpKindList()
        setUpSaveButton()
        return binding?.root!!
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    private fun setUpCategoriesList() {
        val adapter = ArrayAdapter(requireContext(), R.layout.item_auto_complete, ArrayList(
            viewModel.listDisplayCategories.value!!
        ))
        binding?.atcCategory?.setAdapter(adapter)
        binding?.atcCategory?.setOnItemClickListener { parent, view, position, id ->
            val it = viewModel.listCategories.value?.get(position)?.id
            if (it.toString() != "") {
                viewModel.filterKind(it.toString()[0].toString().toLong())
            }
        }
        viewModel.listDisplayCategories.observe(viewLifecycleOwner) {
            adapter.clear()
            adapter.addAll(ArrayList(it))
            adapter.notifyDataSetChanged()
        }
    }

    private fun setUpKindList() {
        val adapter = ArrayAdapter(requireContext(), R.layout.item_auto_complete, ArrayList(
            viewModel.listDisplayKind.value!!
        ))
        binding?.atcKind?.setAdapter(adapter)
        viewModel.listDisplayKind.observe(viewLifecycleOwner) {
            adapter.clear()
            adapter.addAll(ArrayList(it))
            adapter.notifyDataSetChanged()
        }
    }

    private fun setUpListImage() {
        binding?.rcImages?.adapter = AddImageAdapter()
    }

    private fun setUpAddImageButton() {
         val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let { imageUri ->
                // Suppose you have an ImageView that should contain the image:
                viewModel.addImages(imageUri)
                viewModel.listImagesPath.value?.add("/storage/emulated/0/Download/download.jpeg")
            }
        }
        binding?.imgAdd?.setOnClickListener {
            getContent.launch("image/*")
        }
    }

    private fun setUpSaveButton(){
        this.activity?.let {
            ActivityCompat.requestPermissions(
                it, arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.MANAGE_EXTERNAL_STORAGE
                ), 1
            )
        }
        binding?.btnSave?.setOnClickListener {
            viewModel.loadImageToFirebase()
        }
    }

}