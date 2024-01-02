package com.example.servivet.ui.main.fragment

import android.widget.CalendarView
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.servivet.R
import com.example.servivet.databinding.FragmentChangedateBinding
import com.example.servivet.ui.base.BaseFragment
import com.example.servivet.ui.base.BaseViewModel
import com.example.servivet.ui.main.view_model.ChangeDateViewModel

class ChangedateFragment:BaseFragment<FragmentChangedateBinding,ChangeDateViewModel>(R.layout.fragment_changedate) {
    override val binding: FragmentChangedateBinding by viewBinding(FragmentChangedateBinding::bind)
    override val mViewModel: ChangeDateViewModel by viewModels()

    override fun isNetworkAvailable(boolean: Boolean) {
    }

    override fun setupViewModel() {

    }

    override fun setupViews() {
        binding.apply {
            lifecycleOwner=viewLifecycleOwner
            viewModel=mViewModel
            click=mViewModel.ClickAction(binding,requireContext())

        }
        setcal()

    }
    fun setcal(){
        mViewModel.ClickAction(binding,requireContext()).calenderset()
    }

    override fun setupObservers() {
    }

}