package com.example.servivet.ui.main.fragment.testing

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.servivet.databinding.FragmentTestingBinding

class TestingFragment : Fragment() {
    private lateinit var binding: FragmentTestingBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        binding = FragmentTestingBinding.inflate(inflater, container, false)
        initClickEvents()
        return binding.root
    }

    private fun initClickEvents() {
        binding.idSendData.setOnClickListener{
            findNavController().navigate(TestingFragmentDirections.actionTestingFragmentToTestingBottomSheet7(binding.idDataField.text.toString()))
        }
    }

}