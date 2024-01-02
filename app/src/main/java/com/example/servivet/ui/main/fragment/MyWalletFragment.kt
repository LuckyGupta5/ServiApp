package com.example.servivet.ui.main.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.servivet.R
import com.example.servivet.databinding.FragmentMyWalletBinding
import com.example.servivet.ui.base.BaseFragment
import com.example.servivet.ui.main.adapter.TransactionAdapter
import com.example.servivet.ui.main.view_model.MyWalletViewModel

class MyWalletFragment : BaseFragment<FragmentMyWalletBinding,MyWalletViewModel>(R.layout.fragment_my_wallet) {
    override val binding: FragmentMyWalletBinding by viewBinding(FragmentMyWalletBinding::bind)
    override val mViewModel: MyWalletViewModel by viewModels()

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

    fun setadapter(){
        binding.transition.adapter=TransactionAdapter(ArrayList())
    }

    override fun setupObservers() {
    }

}