package com.example.servivet.ui.main.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.servivet.R
import com.example.servivet.databinding.FragmentCouponsBinding
import com.example.servivet.ui.base.BaseFragment
import com.example.servivet.ui.main.adapter.CouponAdapter
import com.example.servivet.ui.main.view_model.CouponViewModel

class CouponsFragment : BaseFragment<FragmentCouponsBinding,CouponViewModel>(R.layout.fragment_coupons) {

    override val binding: FragmentCouponsBinding by viewBinding(FragmentCouponsBinding::bind)
    override val mViewModel: CouponViewModel by viewModels()

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
        setAdapter()

    }

    override fun setupObservers() {
    }
    fun setAdapter(){
        binding.recyclerview.adapter=CouponAdapter(requireContext(), ArrayList())
    }

}