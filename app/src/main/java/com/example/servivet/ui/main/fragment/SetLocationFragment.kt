package com.example.servivet.ui.main.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.servivet.R
import com.example.servivet.databinding.FragmentSetLocationBinding
import com.example.servivet.ui.base.BaseFragment
import com.example.servivet.ui.main.view_model.location.SetLocationViewModel

class SetLocationFragment : BaseFragment<FragmentSetLocationBinding,SetLocationViewModel>(R.layout.fragment_set_location) {
    override val binding: FragmentSetLocationBinding by viewBinding(FragmentSetLocationBinding::bind)
    override val mViewModel: SetLocationViewModel by viewModels()

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
    }

    override fun setupObservers() {
    }
}