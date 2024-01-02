package com.example.servivet.ui.main.fragment

import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.servivet.R
import com.example.servivet.data.model.home.response.HomeServiceCategory
import com.example.servivet.databinding.FragmentServishBinding
import com.example.servivet.ui.base.BaseFragment
import com.example.servivet.ui.main.adapter.HomeServiceAdapter
import com.example.servivet.ui.main.adapter.ServiceAdapter
import com.example.servivet.ui.main.view_model.ServicesViewModel
import com.example.servivet.utils.Session


class ServicesFragment : BaseFragment<FragmentServishBinding, ServicesViewModel>(R.layout.fragment_servish){
    override val binding: FragmentServishBinding by viewBinding(FragmentServishBinding::bind)
    override val mViewModel: ServicesViewModel by viewModels()

    override fun isNetworkAvailable(boolean: Boolean) {
    }

    override fun setupViewModel() {
        binding.apply {
            lifecycleOwner=viewLifecycleOwner
            viewModel=mViewModel
            click=mViewModel.ClickAction()
        }

        setAdapter("2",Session.category)

    }

    private fun setAdapter(type: String, category: ArrayList<HomeServiceCategory>,) {
        binding.serviceRecycler.adapter=ServiceAdapter(requireContext(), type,category)
    }

    override fun setupViews() {
        setAdapter("2", Session.category)
    }

    override fun setupObservers() {
    }

}