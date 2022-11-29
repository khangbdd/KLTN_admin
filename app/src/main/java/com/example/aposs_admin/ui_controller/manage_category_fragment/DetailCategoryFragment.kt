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
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.aposs_admin.R
import com.example.aposs_admin.adapter.CategoryAndSubCategoryImageAdapter
import com.example.aposs_admin.adapter.CategoryAndSubcategoryAdapter
import com.example.aposs_admin.databinding.FragmentDetailCategoryBinding
import com.example.aposs_admin.ui_controller.dialog.LoadingDialog
import com.example.aposs_admin.util.LoadingStatus
import com.example.aposs_admin.viewmodels.AllCategoryViewModel

class DetailCategoryFragment : Fragment(), CategoryAndSubcategoryAdapter.OnClickListener, CategoryAndSubCategoryImageAdapter.OnClickListener {

    private lateinit var binding: FragmentDetailCategoryBinding
    private val viewModel: AllCategoryViewModel by activityViewModels()
    private lateinit var categoryImageAdapter : CategoryAndSubCategoryImageAdapter
    private lateinit var subcategoryAdapter: CategoryAndSubcategoryAdapter
    private lateinit var loadingDialog: LoadingDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_detail_category, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this.viewLifecycleOwner
        categoryImageAdapter = CategoryAndSubCategoryImageAdapter(this)
        subcategoryAdapter = CategoryAndSubcategoryAdapter(this)
        binding.subcategory.adapter = subcategoryAdapter
        binding.listImage.adapter = categoryImageAdapter

        binding.createSubcategory.setOnClickListener {
            findNavController().navigate(DetailCategoryFragmentDirections.actionDetailCategoryFragmentToCreateSubcategoryFragment())
        }

        binding.buttonEdit.setOnClickListener {
            findNavController().navigate(DetailCategoryFragmentDirections.actionDetailCategoryFragmentToCreateCategoryFragment(viewModel.currentCategory.value!!.id))
        }

        binding.back.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnDelete.setOnClickListener {
            onOpenDeleteDialog()
        }
        setUpLoadingDialog()
        return binding.root
    }

    override fun onClick(position: Int) {
        viewModel.setCurrentSubcategory(position)
        findNavController().navigate(DetailCategoryFragmentDirections.actionDetailCategoryFragmentToDetailSubcategoryFragment())
    }

    override fun removeImageClick(position: Int) {

    }

    private fun setUpLoadingDialog() {
        // create loading dialog
        loadingDialog = LoadingDialog(this.requireActivity())
        // tracking loading status
        viewModel.uploadCategoryStatus.observe(viewLifecycleOwner) {
            if (it != LoadingStatus.Init){
                if (it == LoadingStatus.Loading) {
                    loadingDialog.startLoading()
                } else {
                    loadingDialog.dismissDialog()
                    if(it == LoadingStatus.Success){
                        Toast.makeText(this.requireContext(), "Xóa danh mục thành công", Toast.LENGTH_LONG).show()
                        viewModel.loadAllCategories()
                        findNavController().popBackStack()
                    }else{
                        Toast.makeText(this.requireContext(), "Xóa danh mục thất bại, thử lại sau!", Toast.LENGTH_LONG).show()
                    }
                    viewModel.updateSubcategoryStatus.value = LoadingStatus.Init
                }
            }
        }
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

        tvtAsk.text = "Bạn chắc chắn muốn xóa danh mục này?"

        btnCancel.setOnClickListener {
            dialogDelete.dismiss()
        }
        btnDelete.isEnabled = true
        btnDelete.setOnClickListener {
            viewModel.currentCategory.value.let {
                if (it != null){
                    if(viewModel.currentSubcategories!!.isNotEmpty()){
                        Toast.makeText(this.requireContext(), "Chỉ có thể xóa danh mục khi không có danh mục con!", Toast.LENGTH_LONG).show()
                    }else{
                        viewModel.deleteCategory(it.id)
                    }
                }
            }
            dialogDelete.dismiss()
        }
        dialogDelete.show()
    }
}