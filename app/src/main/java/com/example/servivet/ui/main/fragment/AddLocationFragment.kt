package com.example.servivet.ui.main.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.servivet.R
import com.example.servivet.databinding.FragmentAddLocationBinding
import com.example.servivet.ui.base.BaseFragment
import com.example.servivet.ui.main.view_model.AddLocationViewModel
import com.example.servivet.utils.CommonUtils.showToast

class AddLocationFragment :
    BaseFragment<FragmentAddLocationBinding, AddLocationViewModel>(R.layout.fragment_add_location) {
    override val binding: FragmentAddLocationBinding by viewBinding(FragmentAddLocationBinding::bind)
    override val mViewModel: AddLocationViewModel by viewModels()

    override fun isNetworkAvailable(boolean: Boolean) {
    }

    override fun setupViewModel() {

    }

    private fun setClick() {
        /*
                binding.savethisAddressLayout.setOnClickListener {
                        if (mViewModel.isCheck.value == true) {
                            mViewModel.isCheck.postValue(false)
                        } else {
                            mViewModel.isCheck.postValue(true)
                        }
                    }
                }
                mViewModel.countryCode = binding.countryCode.selectedCountryCode
        */

    }


    override fun setupViews() {
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = mViewModel
            click = mViewModel.ClickAction()
        }
        setClick()

    }

    override fun setupObservers() {
    }

}