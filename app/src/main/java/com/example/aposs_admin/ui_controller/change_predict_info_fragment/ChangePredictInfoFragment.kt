package com.example.aposs_admin.ui_controller.change_predict_info_fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.aposs_admin.R
import com.example.aposs_admin.databinding.FragmentChangePredictInfoBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChangePredictInfoFragment : Fragment() {

    private val viewModel: ChangePredictInfoViewModel by viewModels()
    private var binding: FragmentChangePredictInfoBinding? = null
    private val args: ChangePredictInfoFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_change_predict_info, container, false)
        binding?.viewModel = viewModel
        binding?.lifecycleOwner = viewLifecycleOwner
        viewModel.setCurrentIdAndNameAndDescription(args.id, args.name, args.description)
        binding?.btnSave?.setOnClickListener {
            viewModel.updatePredict {
                Toast.makeText(requireContext(), "Cập nhật thành công", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            }
        }
        binding?.back?.setOnClickListener {
            findNavController().popBackStack()
        }
        return binding?.root!!
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}