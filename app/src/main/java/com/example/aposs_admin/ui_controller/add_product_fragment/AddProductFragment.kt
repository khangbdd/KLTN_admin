package com.example.aposs_admin.ui_controller.add_product_fragment

import android.Manifest
import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.CalendarContract.EventDays.query
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContentResolverCompat.query
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.aposs_admin.R
import com.example.aposs_admin.adapter.AddImageAdapter
import com.example.aposs_admin.databinding.FragmentAddProductBinding
import com.example.aposs_admin.model.dto.TokenDTO
import com.example.aposs_admin.ui_controller.dialog.LoadingDialog
import com.example.aposs_admin.util.LoadingStatus
import com.example.aposs_buyer.responsitory.database.AccountDatabase
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.net.URI


@AndroidEntryPoint
class AddProductFragment : Fragment() {

    private var binding: FragmentAddProductBinding? = null
    private val viewModel: AddProductViewModel by viewModels()
    private var dialog: LoadingDialog? = null

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
        val account = AccountDatabase.getInstance(this.requireContext()).accountDao.getAccount()!!
        viewModel.token = TokenDTO(
            account.accessToken,
            account.tokenType,
            account.refreshToken
        )
        dialog = LoadingDialog(requireActivity())
        binding?.back?.setOnClickListener {
            findNavController().popBackStack()
        }
        setUpAddImageButton()
        setUpListImage()
        setUpCategoriesList()
        setUpKindList()
        setUpSaveButton()
        setUpCancelButton()
        return binding?.root!!
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    private fun setUpCategoriesList() {
        val adapter = ArrayAdapter(
            requireContext(), R.layout.item_auto_complete, ArrayList(
                viewModel.listDisplayCategories.value!!
            )
        )
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
        val adapter = ArrayAdapter(
            requireContext(), R.layout.item_auto_complete, ArrayList(
                viewModel.listDisplayKind.value!!
            )
        )
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
        val getContent =
            registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
                uri?.let { imageUri ->
                    val type = getFileExtension(imageUri)
                    viewModel.addImages(imageUri, type)
                }
            }
        binding?.imgAdd?.setOnClickListener {
            getContent.launch("image/*")
        }
    }

    private fun getFileExtension(uri: Uri): String? {
        var cr: ContentResolver = this.requireContext().contentResolver
        var mime: MimeTypeMap = MimeTypeMap.getSingleton()
        return mime.getExtensionFromMimeType(cr.getType(uri))
    }

//    private fun getRealPathFromURI(contentURI: Uri): String? {
//        val result: String?
//        val cursor: Cursor? = this.requireContext().contentResolver.query(contentURI, null, null, null, null)
//        if (cursor == null) { // Source is Dropbox or other similar local file path
//            result = contentURI.path
//        } else {
//            cursor.moveToFirst()
//            val idx: Int = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
//            result = cursor.getString(idx)
//            cursor.close()
//        }
//        return result
//    }

    private fun setUpSaveButton() {
        this.activity?.let {
            ActivityCompat.requestPermissions(
                it, arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.MANAGE_EXTERNAL_STORAGE
                ), 1
            )
        }
        binding?.btnSave?.setOnClickListener {
            if (viewModel.name.value == "" || viewModel.quantity.value == "" || viewModel.price.value == "" || viewModel.kind.value == "") {
                Toast.makeText(this.requireContext(), "Thiếu thông tin", Toast.LENGTH_SHORT)
            } else {
                viewModel.requestCreateNewProduct()
            }
        }
        viewModel.status.observe(viewLifecycleOwner) {
            if (it == LoadingStatus.Success) {
                Toast.makeText(this.requireContext(), "Success", Toast.LENGTH_SHORT)
                dialog?.dismissDialog()
                findNavController().popBackStack()
            } else if (it == LoadingStatus.Loading) {
                Toast.makeText(this.requireContext(), "Loading", Toast.LENGTH_SHORT)
                dialog?.startLoading()
            } else if (it == LoadingStatus.Fail) {
                dialog?.dismissDialog()
                Toast.makeText(this.requireContext(), "Fail", Toast.LENGTH_SHORT)
            }
        }
    }

    private fun setUpCancelButton() {
        binding?.btnCancel?.setOnClickListener {
            findNavController().popBackStack()
        }
    }
}