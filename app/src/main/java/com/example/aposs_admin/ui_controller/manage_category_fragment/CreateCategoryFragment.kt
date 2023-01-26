package com.example.aposs_admin.ui_controller.manage_category_fragment

import android.app.Activity
import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.EditText
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.aposs_admin.R
import com.example.aposs_admin.adapter.CategoryAndSubCategoryImageAdapter
import com.example.aposs_admin.databinding.FragmentCreateCategoryBinding
import com.example.aposs_admin.model.DetailCategory
import com.example.aposs_admin.model.Image
import com.example.aposs_admin.model.dto.NewCategory
import com.example.aposs_admin.model.dto.NewSubcategoryDTO
import com.example.aposs_admin.ui_controller.dialog.LoadingDialog
import com.example.aposs_admin.util.LoadingStatus
import com.example.aposs_admin.viewmodels.AllCategoryViewModel
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.IOException

class CreateCategoryFragment : Fragment(), CategoryAndSubCategoryImageAdapter.OnClickListener {

    private lateinit var binding: FragmentCreateCategoryBinding
    private val viewModel: AllCategoryViewModel by activityViewModels()
    private val args: CreateCategoryFragmentArgs by navArgs()
    private lateinit var imageAdapter: CategoryAndSubCategoryImageAdapter
    private lateinit var currentCategory: DetailCategory
    private val maxImageSupport: Int = 5
    private lateinit var loadingDialog: LoadingDialog
    private val reference: StorageReference = FirebaseStorage.getInstance().reference
    private var listImageURL: MutableList<String> = mutableListOf()
    private var listOldImageUrl: MutableList<Image> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_create_category, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        imageAdapter = CategoryAndSubCategoryImageAdapter(this)
        imageAdapter.isInEditMode = true
        binding.listImage.adapter = imageAdapter
        if (args.id == -1L) {
            createState()
        } else {
            editState()
        }
        binding.totalImage.text = "Danh sách hình ảnh(" + currentCategory.images.size + "/5):"
        binding.currentCategory = currentCategory

        binding.addImage.setOnClickListener {
            openGallery()
        }
        binding.back.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.save.setOnClickListener {
            if (args.id == -1L) {
                addNewCategory()
            } else {
                updateOldCategory()
            }
        }
        setUpLoadingDialog()
        // Inflate the layout for this fragment
        return binding.root
    }

    private fun setUpLoadingDialog() {
        // create loading dialog
        loadingDialog = LoadingDialog(this.requireActivity())
        // tracking loading status
        viewModel.uploadCategoryStatus.observe(viewLifecycleOwner) {
            if (it != LoadingStatus.Init) {
                if (it == LoadingStatus.Loading) {
                    loadingDialog.startLoading()
                } else {
                    loadingDialog.dismissDialog()
                    if (it == LoadingStatus.Success) {
                        if (args.id == -1L) {
                            Toast.makeText(
                                this.requireContext(),
                                "Tạo danh mục thành công",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                            viewModel.loadAllCategories()
                            findNavController().popBackStack()
                        } else {
                            Toast.makeText(
                                this.requireContext(),
                                "Cập nhật danh mục thành công",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                            viewModel.loadAllCategories()
                            findNavController().popBackStack()
                        }
                    } else {
                        if (args.id == -1L) {
                            Toast.makeText(
                                this.requireContext(),
                                "Tạo danh mục thất bại",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        } else {
                            Toast.makeText(
                                this.requireContext(),
                                "Cập nhật danh mục thất bại",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }
                    }
                }
                viewModel.uploadCategoryStatus.value = LoadingStatus.Init
            }
        }
    }

    private fun checkValidEditText(editText: EditText): Boolean {
        return if (editText.text.isBlank()) {
            editText.error = "Vui lòng điền đầy đủ thông tin"
            false
        } else {
            true
        }
    }

    private fun getFileExtension(uri: Uri): String? {
        val cr: ContentResolver = requireContext().contentResolver
        val mime: MimeTypeMap = MimeTypeMap.getSingleton()
        return mime.getExtensionFromMimeType(cr.getType(uri))
    }

    private fun addNewCategory() {
        if (checkValidEditText(binding.categoryName)
        ) {
            if (currentCategory.images.isNotEmpty()) {
                viewModel.uploadCategoryStatus.value = LoadingStatus.Loading
                currentCategory.images.stream().forEach { image ->
                    val imgId =
                        System.currentTimeMillis()
                            .toString() + "." + getFileExtension(image.imageUri as Uri)
                    val fileRef: StorageReference = reference.child(imgId)
                    fileRef.putFile(image.imageUri as Uri).addOnSuccessListener {
                        fileRef.downloadUrl.addOnSuccessListener {
                            listImageURL.add(it.toString())
                            if (listImageURL.size == currentCategory.images.size) {
                                val newCategory = NewCategory(
                                    id = -1L,
                                    name = currentCategory.name,
                                    images = listImageURL
                                )
                                viewModel.addNewCategory(newCategory)
                            }
                        }
                    }.addOnFailureListener {
                        Toast.makeText(
                            this.requireContext(),
                            "Tải hình ảnh lên thất bại $it",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        viewModel.uploadCategoryStatus.value = LoadingStatus.Fail
                    }
                }
            } else {
                Toast.makeText(
                    this.requireContext(),
                    "Vui lòng chọn ít nhất 1 hình ảnh cho danh mục",
                    Toast.LENGTH_LONG
                ).show()
            }
        } else {
            Toast.makeText(
                this.requireContext(),
                "Vui lòng điền đầy đủ thông tin",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun updateOldCategory() {
        if (checkValidEditText(binding.categoryName)
        ) {
            if (currentCategory.images.isNotEmpty()) {
                viewModel.uploadCategoryStatus.value = LoadingStatus.Loading
                currentCategory.images.stream().forEach { image ->
                    if (!listOldImageUrl.contains(image)) {
                        val imgId =
                            System.currentTimeMillis()
                                .toString() + "." + getFileExtension(image.imageUri as Uri)
                        val fileRef: StorageReference = reference.child(imgId)
                        fileRef.putFile(image.imageUri as Uri).addOnSuccessListener {
                            fileRef.downloadUrl.addOnSuccessListener { it ->
                                listImageURL.add(0, it.toString())
                                if (listImageURL.size == currentCategory.images.size) {
                                    val newCategory = NewCategory(
                                        id = currentCategory.id,
                                        name = currentCategory.name,
                                        images = listImageURL
                                    )
                                    viewModel.updateOldCategory(newCategory)
                                }
                            }
                        }.addOnFailureListener {
                            Toast.makeText(
                                this.requireContext(),
                                "Tải hình ảnh lên thất bại $it",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                            viewModel.uploadCategoryStatus.value = LoadingStatus.Fail
                        }
                    } else {
                        listImageURL.add(image.imgURL.toString())
                        if (listImageURL.size == currentCategory.images.size) {
                            val newCategory = NewCategory(
                                id = currentCategory.id,
                                name = currentCategory.name,
                                images = listImageURL
                            )
                            viewModel.updateOldCategory(newCategory)
                        }
                    }
                }
            } else {
                Toast.makeText(
                    this.requireContext(),
                    "Vui lòng chọn ít nhất 1 hình ảnh cho danh mục",
                    Toast.LENGTH_LONG
                ).show()
            }
        } else {
            Toast.makeText(
                this.requireContext(),
                "Vui lòng điền đầy đủ thông tin",
                Toast.LENGTH_SHORT
            ).show()
        }
    }


    private fun createState() {
        // reset data
        currentCategory = DetailCategory(
            id = -1L,
            name = "",
            images = mutableListOf()
        )

        // define view
        binding.headerTitle.text = "Tạo danh mục mới"
        binding.categoryName.text = null
        binding.listImage.visibility = View.GONE
    }

    private fun editState() {
        currentCategory = DetailCategory(
            id = viewModel.currentCategory.value!!.id,
            name = viewModel.currentCategory.value!!.name,
            images = viewModel.currentCategory.value!!.images.toMutableList()
        )

        listOldImageUrl = viewModel.currentCategory.value!!.images.toMutableList()

        binding.headerTitle.text = "Chỉnh sửa danh mục"
        binding.categoryName.setText(viewModel.currentCategory.value?.name)
        binding.listImage.visibility = View.VISIBLE
    }

    private fun openGallery() {
        if (currentCategory.images.size >= maxImageSupport) {
            Toast.makeText(
                this.requireContext(),
                "Chỉ có thể thêm tối đa 5 ảnh!",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            val galleryIntent: Intent = Intent()
            galleryIntent.action = Intent.ACTION_GET_CONTENT
            galleryIntent.type = "image/*"
            startActivityForResult(galleryIntent, 2)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 2 && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            try {
                val imageUri = data.data!!
                binding.listImage.visibility = View.VISIBLE
                currentCategory.images.add(0, Image(imageUri))
                imageAdapter.submitList(currentCategory.images)
                binding.totalImage.text =
                    "Danh sách hình ảnh(" + currentCategory.images.size + "/5):"
                binding.listImage.adapter = imageAdapter
            } catch (e: IOException) {
                Toast.makeText(requireContext(), "Tải hình ảnh thất bại!", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    override fun removeImageClick(position: Int) {
        if (currentCategory.images.size > 1) {
            currentCategory.images.removeAt(position)
            binding.totalImage.text =
                "Danh sách hình ảnh(" + currentCategory.images.size + "/5):"
            imageAdapter.notifyItemRemoved(position)
            imageAdapter.notifyItemChanged(position, currentCategory.images.size)
        } else {
            Toast.makeText(
                this.requireContext(),
                "Danh mục phải có ít nhất 1 bức ảnh!",
                Toast.LENGTH_SHORT
            ).show()
        }
    }


}