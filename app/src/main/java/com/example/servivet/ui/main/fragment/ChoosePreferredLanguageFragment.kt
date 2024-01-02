package com.example.servivet.ui.main.fragment

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.servivet.R
import com.example.servivet.databinding.FragmentChoosePreferredLanguageBinding
import com.example.servivet.databinding.FragmentSplashBinding
import com.example.servivet.ui.base.BaseFragment
import com.example.servivet.ui.main.view_model.ChooseLanguageViewModel
import com.example.servivet.ui.main.view_model.SplashViewModel


class ChoosePreferredLanguageFragment : BaseFragment<FragmentChoosePreferredLanguageBinding,ChooseLanguageViewModel>(R.layout.fragment_choose_preferred_language) {

    override val binding: FragmentChoosePreferredLanguageBinding by viewBinding(FragmentChoosePreferredLanguageBinding::bind)
    override val mViewModel: ChooseLanguageViewModel by viewModels()

    override fun isNetworkAvailable(boolean: Boolean) {

    }

    override fun setupViewModel() { if (isAdded)
        binding.apply {
            lifecycleOwner=viewLifecycleOwner
            viewModel=mViewModel
            click=mViewModel.ClickAction()
        }
        setViewPagerAdapter()
    }

    private fun setViewPagerAdapter() {

    }

    override fun setupViews() {
    }

    override fun setupObservers() {

    }

}