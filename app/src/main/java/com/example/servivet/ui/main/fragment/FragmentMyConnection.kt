package com.example.servivet.ui.main.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.servivet.R
import com.example.servivet.databinding.FragmentConnectionsRequestBinding
import com.example.servivet.databinding.FragmentMyConnectionBinding
import com.example.servivet.ui.base.BaseFragment
import com.example.servivet.ui.main.adapter.MyConnectionAdapter
import com.example.servivet.ui.main.view_model.MyConnectionModelView

class FragmentMyConnection : BaseFragment<FragmentMyConnectionBinding,MyConnectionModelView>(R.layout.fragment_my_connection) {
    override val binding: FragmentMyConnectionBinding by viewBinding(FragmentMyConnectionBinding::bind)
    override val mViewModel: MyConnectionModelView by viewModels()

    override fun isNetworkAvailable(boolean: Boolean) {
    }

    override fun setupViewModel() {
    }

    override fun setupViews() {
        binding.apply {
            lifecycleOwner=viewLifecycleOwner
            viewModel=mViewModel
            click=mViewModel.ClickAction()

        }
        backbtn()
        setadapter()
    }

    private fun setadapter() {
        binding.recyclerview.adapter=MyConnectionAdapter(ArrayList())
    }

    private fun backbtn() {
        binding.idTopLayout.idBack.setOnClickListener(View.OnClickListener {
            requireActivity().onBackPressed()
        })
    }


    override fun setupObservers() {
        binding.idTopLayout.idTitle.text =getString(R.string.my_connections)
    }

}