package com.example.servivet.ui.main.fragment

import android.content.Intent
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.servivet.R
import com.example.servivet.databinding.FragmentSplashBinding
import com.example.servivet.ui.base.BaseFragment
import com.example.servivet.ui.main.activity.HomeActivity
import com.example.servivet.ui.main.view_model.SplashViewModel
import com.example.servivet.utils.Session


class SplashFragment :
    BaseFragment<FragmentSplashBinding, SplashViewModel>(R.layout.fragment_splash) {
    override val binding: FragmentSplashBinding by viewBinding(FragmentSplashBinding::bind)
    override val mViewModel: SplashViewModel by viewModels()
    override fun isNetworkAvailable(boolean: Boolean) {
    }
    override fun setupViewModel() {
    }
    override fun setupViews() {
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = mViewModel
        }
    }
    override fun setupObservers() {
        mViewModel.getLiveData().observe(viewLifecycleOwner) {
            if (Session.isLogin==true) {
                startActivity(Intent(requireActivity(), HomeActivity::class.java))
                requireActivity().finish()
            } else
                findNavController().navigate(R.id.action_splashFragment_to_choosePreferredLanguageFragment)
        }
    }
}