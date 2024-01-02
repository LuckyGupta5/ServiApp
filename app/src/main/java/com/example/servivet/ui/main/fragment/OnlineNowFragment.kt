package com.example.servivet.ui.main.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.servivet.R
import com.example.servivet.databinding.FragmentOnlineNowBinding
import com.example.servivet.ui.base.BaseFragment
import com.example.servivet.ui.main.adapter.HomeOnlineNowAdapter
import com.example.servivet.ui.main.view_model.OnlineNowViewModel

class OnlineNowFragment : BaseFragment<FragmentOnlineNowBinding,OnlineNowViewModel>(R.layout.fragment_online_now) {
    override val binding: FragmentOnlineNowBinding by viewBinding(FragmentOnlineNowBinding::bind)
    override val mViewModel: OnlineNowViewModel by viewModels()

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
        setadapterterOnlinenow("2")
        binding.serviceRecycler.isVisible=true
    }

  fun setadapterterOnlinenow(type:String){
      binding.serviceRecycler.adapter=HomeOnlineNowAdapter(type,requireContext(),ArrayList())

    }
    override fun setupObservers() {
    }
}