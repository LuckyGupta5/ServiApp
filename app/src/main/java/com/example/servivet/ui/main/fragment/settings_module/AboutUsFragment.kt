package com.example.servivet.ui.main.fragment.settings_module

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.servivet.R
import com.example.servivet.databinding.FragmentAboutUsBinding
import com.example.servivet.databinding.FragmentSettingsBinding
import com.example.servivet.ui.base.BaseFragment
import com.example.servivet.ui.main.view_model.SettingsViewModel
import com.example.servivet.ui.main.view_model.settings_viewmodel.AboutUsViewModel


class AboutUsFragment :
    BaseFragment<FragmentAboutUsBinding, AboutUsViewModel>(R.layout.fragment_about_us) {
    override val binding: FragmentAboutUsBinding by viewBinding(FragmentAboutUsBinding::bind)
    override val mViewModel: AboutUsViewModel by viewModels()


    override fun isNetworkAvailable(boolean: Boolean) {
    }

    override fun setupViewModel() {
    }

    override fun setupViews() {
    }

    override fun setupObservers() {
    }


}