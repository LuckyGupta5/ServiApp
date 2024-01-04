package com.example.servivet.ui.main.fragment

import android.widget.CompoundButton
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.servivet.R
import com.example.servivet.databinding.FragmentSettingsBinding
import com.example.servivet.ui.base.BaseFragment
import com.example.servivet.ui.main.view_model.SettingsViewModel

class SettingsFragment:BaseFragment<FragmentSettingsBinding,SettingsViewModel>(R.layout.fragment_settings) {
    override val binding: FragmentSettingsBinding by viewBinding(FragmentSettingsBinding::bind)
    override val mViewModel: SettingsViewModel by viewModels()

    override fun isNetworkAvailable(boolean: Boolean) {
    }

    override fun setupViewModel() {
    }


    override fun setupViews() {
        binding.apply {
            lifecycleOwner=viewLifecycleOwner
            viewModel=mViewModel
            click=mViewModel.ClickAction(requireActivity())
        }
    }

    override fun setupObservers() {
    }
}