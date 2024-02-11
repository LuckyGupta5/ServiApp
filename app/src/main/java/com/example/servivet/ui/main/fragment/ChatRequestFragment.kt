package com.example.servivet.ui.main.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.servivet.R
import com.example.servivet.databinding.FragmentChatRequestBinding
import com.example.servivet.ui.base.BaseFragment
import com.example.servivet.ui.main.adapter.ChatRequestAdapter
import com.example.servivet.ui.main.view_model.ChatRequestViewModel

class ChatRequestFragment : BaseFragment<FragmentChatRequestBinding,ChatRequestViewModel>(R.layout.fragment_chat_request) {
    override val binding: FragmentChatRequestBinding by viewBinding(FragmentChatRequestBinding::bind)
    override val mViewModel: ChatRequestViewModel by viewModels()

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
    }

    private fun setadapter() {
        binding.idChatRequestRecycle.adapter=ChatRequestAdapter(ArrayList())
    }

    override fun setupObservers() {
    }
}