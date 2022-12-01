package com.example.aposs_admin.ui_controller.manage_category_fragment

import android.app.Activity.RESULT_OK
import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.webkit.MimeTypeMap
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.aposs_admin.R
import com.example.aposs_admin.databinding.FragmentCreateSubcategoryBinding
import com.example.aposs_admin.model.dto.NewSubcategoryDTO
import com.example.aposs_admin.ui_controller.dialog.LoadingDialog
import com.example.aposs_admin.util.LoadingStatus
import com.example.aposs_admin.viewmodels.AllCategoryViewModel
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.IOException
import java.net.URI


class CreateSubcategoryFragment : Fragment() {

    private lateinit var binding: FragmentCreateSubcategoryBinding
    private val viewModel: AllCategoryViewModel by activityViewModels()
    private val args: CreateSubcategoryFragmentArgs by navArgs()
    private lateinit var loadingDialog: LoadingDialog
    private var currentSelectedCategoryId: Long = 0L
    private var imageUri: Uri? = null
    private val reference: StorageReference = FirebaseStorage.getInstance().reference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_create_subcategory, container, false)
        // Inflate the layout for this fragment
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        binding.back.setOnClickListener {
            findNavController().popBackStack()
        }

        setUpCategoriesList()

        binding.addImage.setOnClickListener {
            openGallery()
        }

        viewModel.updateSubcategoryStatus.value = LoadingStatus.Init
        currentSelectedCategoryId = viewModel.currentCategory.value!!.id
        if(args.subcategoryID == -1L){
            setCreateState()
        }else{
            setEditState()
        }

        binding.save.setOnClickListener {
            if (args.subcategoryID == -1L){
                addNewSubcategory()
            }else{
                updateSubcategory()
            }

        }

        setUpLoadingDialog()
        return binding.root
    }


    private fun setUpLoadingDialog() {
        // create loading dialog
        loadingDialog = LoadingDialog(this.requireActivity())
        // tracking loading status
        viewModel.updateSubcategoryStatus.observe(viewLifecycleOwner) {
            if (it != LoadingStatus.Init){
                if (it == LoadingStatus.Loading) {
                    loadingDialog.startLoading()
                } else {
                    loadingDialog.dismissDialog()
                    if (it == LoadingStatus.Success){
                        if(args.subcategoryID == -1L){
                            Toast.makeText(this.requireContext(), "Tạo danh mục thành công", Toast.LENGTH_SHORT)
                                .show()
                            viewModel.loadingALlSubCategories()
                            findNavController().popBackStack()
                        }else{
                            Toast.makeText(this.requireContext(), "Cập nhật danh mục thành công", Toast.LENGTH_SHORT)
                                .show()
                            viewModel.loadingALlSubCategories()
                            findNavController().popBackStack()
                        }
                    }else{
                        if(args.subcategoryID == -1L){
                            Toast.makeText(this.requireContext(), "Tạo danh mục con thất bại", Toast.LENGTH_SHORT)
                                .show()
                        }else{
                            Toast.makeText(this.requireContext(), "Cập nhật danh mục thất bại", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                }
                viewModel.updateSubcategoryStatus.value = LoadingStatus.Init
            }
        }
    }

    private fun getImageReference(url: String): String {
        val uri = URI(url)
        val path: String = uri.path
        return path.substring(path.lastIndexOf('/') + 1)
    }

    private fun updateSubcategory(){
        if(checkValidEditText(binding.subcategoryName)){
            viewModel.updateSubcategoryStatus.postValue(LoadingStatus.Loading)
            val imageURL = viewModel.currentSubcategory.value!!.image.imgURL
            if(imageUri != null){
                val imgId = getImageReference(imageURL)
                val fileRef: StorageReference = reference.child(imgId)

                fileRef.putFile(imageUri as Uri).addOnSuccessListener {
                    fileRef.downloadUrl.addOnSuccessListener {
                        Toast.makeText(this.requireContext(), "Tải hình ảnh lên thành công", Toast.LENGTH_SHORT)
                            .show()
                        val newSubcategoryDTO = NewSubcategoryDTO(
                            name = binding.subcategoryName.text.toString(),
                            categoryId = currentSelectedCategoryId,
                            image = it.toString()
                        )
                        viewModel.updateSubcategory(args.subcategoryID, newSubcategoryDTO, binding.category.text.toString())
                    }
                }.addOnFailureListener {
                    Toast.makeText(this.requireContext(), "Tải hình ảnh lên thất bại $it", Toast.LENGTH_SHORT)
                        .show()
                    viewModel.updateSubcategoryStatus.value = LoadingStatus.Fail
                }

            }else{
                val newSubcategoryDTO = NewSubcategoryDTO(
                    name = binding.subcategoryName.text.toString(),
                    categoryId = currentSelectedCategoryId,
                    image = imageURL
                )
                viewModel.updateSubcategory(args.subcategoryID, newSubcategoryDTO, binding.category.text.toString())
            }
        } else {
            Toast.makeText(this.requireContext(), "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun addNewSubcategory() {
        if (checkValidEditText(binding.subcategoryName)
        ) {
            if (imageUri != null) {
                viewModel.updateSubcategoryStatus.value = LoadingStatus.Loading
                val imgId =
                    System.currentTimeMillis().toString() + "." + getFileExtension(imageUri as Uri)
                val fileRef: StorageReference = reference.child(imgId)

                fileRef.putFile(imageUri as Uri).addOnSuccessListener {
                    fileRef.downloadUrl.addOnSuccessListener {
                        Toast.makeText(this.requireContext(), "Tải hình ảnh lên thành công", Toast.LENGTH_SHORT)
                            .show()
                        val newSubcategoryDTO = NewSubcategoryDTO(
                            name = binding.subcategoryName.text.toString(),
                            categoryId = currentSelectedCategoryId,
                            image = it.toString()
                        )
                        viewModel.addNewSubCategory(newSubcategoryDTO)
                    }
                }.addOnFailureListener {
                    Toast.makeText(this.requireContext(), "Tải hình ảnh lên thất bại $it", Toast.LENGTH_SHORT)
                        .show()
                    viewModel.updateSubcategoryStatus.value = LoadingStatus.Fail
                }
            }else{
                Toast.makeText(this.requireContext(), "Vui lòng chọn hình ảnh danh mục", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this.requireContext(), "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getFileExtension(uri: Uri): String? {
        val cr: ContentResolver = requireContext().contentResolver
        val mime: MimeTypeMap = MimeTypeMap.getSingleton()
        return mime.getExtensionFromMimeType(cr.getType(uri))
    }

    private fun checkValidEditText(editText: EditText): Boolean {
        return if (editText.text.isBlank()) {
            editText.error = "Vui lòng điền đầy đủ thông tin"
            false
        } else {
            true
        }
    }

    private fun setUpCategoriesList() {
        val adapter = ArrayAdapter(requireContext(), R.layout.item_auto_complete, viewModel.allCategoriesName.value!!)
        binding.category.setAdapter(adapter)
        binding.category.setOnItemClickListener { _, _, position, _ ->
            currentSelectedCategoryId = viewModel.allCategories.value!![position].id
        }
    }

    private fun setEditState(){
        binding.headerTitle.text = "Chỉnh sửa danh mục"
        binding.subcategoryName.setText(viewModel.currentSubcategory.value!!.name)
        binding.category.setText(viewModel.currentSubcategory.value!!.categoryName, false)
        binding.subcategoryImageLayout.visibility = View.VISIBLE
        binding.addImage.text = "Chọn ảnh khác"
        Glide.with(requireContext())
            .load(viewModel.currentSubcategory.value!!.image.imageUri)
            .apply(
                RequestOptions().placeholder(R.drawable.animation_loading)
            )
            .into(binding.subcategoryImage)
    }

    private fun setCreateState(){
        binding.headerTitle.text = "Tạo danh mục mới"
        binding.subcategoryName.text = null
        binding.category.setText(viewModel.currentCategory.value?.name, false)
        binding.subcategoryImageLayout.visibility = View.GONE
    }

    private fun openGallery() {
        val galleryIntent: Intent = Intent()
        galleryIntent.action = Intent.ACTION_GET_CONTENT
        galleryIntent.type = "image/*"
        startActivityForResult(galleryIntent, 2)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 2 && resultCode == RESULT_OK && data != null && data.data != null) {

            try {
                imageUri = data.data
                binding.subcategoryImageLayout.visibility = View.VISIBLE
                binding.subcategoryImage.setImageURI(imageUri)
                binding.addImage.text = "Chọn ảnh khác"
            }catch (e: IOException){
                Toast.makeText(requireContext(), "Tải hình ảnh thất bại!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}