package com.example.aposs_admin.ui_controller.create_predict_fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.example.aposs_admin.R
import com.example.aposs_admin.databinding.FragmentCreateCategoryBinding


class CreatePredictFragment : Fragment() {

    private var binding: FragmentCreateCategoryBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_create_predict, container, false)
        return binding?.root!!
    }
}