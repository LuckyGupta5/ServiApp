package com.example.servivet.ui.main.fragment

import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.servivet.R
import com.example.servivet.databinding.FragmentChatsBinding
import com.example.servivet.ui.base.BaseFragment
import com.example.servivet.ui.main.adapter.ChatFragmentAdapter
import com.example.servivet.ui.main.view_model.ChatViewModel
import com.example.servivet.utils.ProcessDialog


class ChatsFragment :BaseFragment<FragmentChatsBinding,ChatViewModel>(R.layout.fragment_chats) {
    override val binding: FragmentChatsBinding by viewBinding(FragmentChatsBinding::bind)
    override val mViewModel: ChatViewModel by viewModels()

    override fun isNetworkAvailable(boolean: Boolean) {
    }

    override fun setupViewModel() {

         }

    fun setadapter(){
        binding.idChatRecycle.adapter=ChatFragmentAdapter(requireContext(), ArrayList())
    }

    override fun setupViews() {
        ProcessDialog.dismissDialog()
        binding.apply{
            lifecycleOwner=viewLifecycleOwner
            viewMOdel=mViewModel
            click=mViewModel.ClickAction(requireContext(),binding)
        }
        setBack()
        setadapter()

    }

    override fun setupObservers() {
    }
    private fun setBack() {
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().navigate(R.id.action_chatFragment_to_homeFragment)
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

}