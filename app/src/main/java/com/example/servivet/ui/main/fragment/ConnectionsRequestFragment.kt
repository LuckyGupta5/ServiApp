package com.example.servivet.ui.main.fragment

import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.servivet.R
import com.example.servivet.databinding.FragmentConnectionsRequestBinding
import com.example.servivet.ui.base.BaseFragment
import com.example.servivet.ui.main.adapter.ConnectionRequestAdapter
import com.example.servivet.ui.main.view_model.ConnectionRequestViewModel

class ConnectionsRequestFragment : BaseFragment<FragmentConnectionsRequestBinding,ConnectionRequestViewModel>(R.layout.fragment_connections_request) {
    override val binding: FragmentConnectionsRequestBinding by viewBinding(FragmentConnectionsRequestBinding::bind)
    override val mViewModel: ConnectionRequestViewModel by viewModels()

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
        setadapter()
        backbtn()
    }

    private fun setadapter() {
        binding.recyclerview.adapter=ConnectionRequestAdapter(requireContext(),ArrayList())
    }

    fun backbtn(){
        binding.idTopLayout.idBack.setOnClickListener(View.OnClickListener {
            findNavController().popBackStack()
        })
    }
    override fun setupObservers() {
        binding.idTopLayout.idTitle.text=getText(R.string.connections_requests)
    }

}