package com.example.servivet.ui.main.fragment.testing

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.example.servivet.R
import com.example.servivet.databinding.FragmentTestingBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class TestingBottomSheet : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentTestingBottomSheetBinding

    private val argument:TestingBottomSheetArgs by navArgs()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentTestingBottomSheetBinding.inflate(inflater, container, false)

        getArgumentData()
        return binding.root
    }

    private fun getArgumentData() {

        binding.idGetData.text = argument.data

    }

}