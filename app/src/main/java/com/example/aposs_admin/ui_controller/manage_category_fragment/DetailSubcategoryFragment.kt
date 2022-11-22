package com.example.aposs_admin.ui_controller.manage_category_fragment

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.aposs_admin.R
import com.example.aposs_admin.adapter.ProductsAdapter
import com.example.aposs_admin.databinding.FragmentDetailSubcategoryBinding
import com.example.aposs_admin.ui_controller.dialog.LoadingDialog
import com.example.aposs_admin.util.LoadingStatus
import com.example.aposs_admin.viewmodels.AllCategoryViewModel

class DetailSubcategoryFragment : Fragment(), ProductsAdapter.OnClickProductListener {
    private lateinit var binding: FragmentDetailSubcategoryBinding
    private val viewModel: AllCategoryViewModel by activityViewModels()
    private lateinit var productsAdapter: ProductsAdapter
    private lateinit var loadingDialog: LoadingDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_detail_subcategory, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        productsAdapter = ProductsAdapter(this)
        binding.rcProducts.adapter = productsAdapter
        binding.back.setOnClickListener {
            viewModel.currentSubcategory.postValue(null)
            findNavController().popBackStack()
        }
        binding.buttonEdit.setOnClickListener {
            findNavController().navigate(DetailSubcategoryFragmentDirections.actionDetailSubcategoryFragmentToCreateSubcategoryFragment(viewModel.currentSubcategory.value!!.id))
        }
        binding.deleteButton.setOnClickListener {
            onOpenDeleteDialog()
        }
        // Inflate the layout for this fragment
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
                    if(it == LoadingStatus.Success){
                        Toast.makeText(this.requireContext(), "Xóa danh mục thành công", Toast.LENGTH_LONG).show()
                        viewModel.loadingALlSubCategories()
                        findNavController().popBackStack()
                    }else{
                        Toast.makeText(this.requireContext(), "Xóa danh mục thất bại, thử lại sau!", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    override fun onClickProduct(selectedId: Long) {

    }

    private fun onOpenDeleteDialog() {
        val dialogDelete = this.context?.let { Dialog(it) }
        dialogDelete?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialogDelete?.setContentView(R.layout.delete_permission_dialog)

        val window = dialogDelete?.window ?: return

        window.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val windowAttribute = window.attributes

        windowAttribute.gravity = Gravity.CENTER
        window.attributes = windowAttribute

        dialogDelete.setCancelable(false)

        val btnDelete: AppCompatButton = dialogDelete.findViewById(R.id.btn_delete)
        val btnCancel: AppCompatButton = dialogDelete.findViewById(R.id.btn_cancel)
        val tvtAsk: TextView = dialogDelete.findViewById(R.id.tv_ask)

        tvtAsk.text = "Bạn chắc chắn muốn xóa danh mục con này?"

        btnCancel.setOnClickListener {
            dialogDelete.dismiss()
        }
        btnDelete.isEnabled = true
        btnDelete.setOnClickListener {
            viewModel.currentSubcategory.value.let {
                if (it != null){
                    if(it.Products.isNotEmpty()){
                        Toast.makeText(this.requireContext(), "Chỉ có thể xóa danh mục khi không còn sản phẩm thuộc danh mục đó!", Toast.LENGTH_LONG).show()
                    }else{
                        viewModel.deleteSubcategory(it.id)
                    }
                }
            }
            dialogDelete.dismiss()
        }
        dialogDelete.show()
    }


}