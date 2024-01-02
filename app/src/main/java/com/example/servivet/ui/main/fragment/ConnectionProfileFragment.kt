package com.example.servivet.ui.main.fragment

import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.servivet.R
import com.example.servivet.databinding.FragmentConnectionProfileBinding
import com.example.servivet.ui.base.BaseFragment
import com.example.servivet.ui.main.view_model.ConnectionProfileViewModel

class ConnectionProfileFragment : BaseFragment<FragmentConnectionProfileBinding, ConnectionProfileViewModel>(R.layout.fragment_connection_profile) {
    override val binding: FragmentConnectionProfileBinding by viewBinding(FragmentConnectionProfileBinding::bind)
    override val mViewModel: ConnectionProfileViewModel by viewModels()

    override fun isNetworkAvailable(boolean: Boolean) {
    }

    override fun setupViewModel() {
    }

    override fun setupViews() {
    }

    override fun setupObservers() {
    }
}