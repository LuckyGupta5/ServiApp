package com.example.servivet.ui.main.fragment.settings_module

import android.Manifest
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.servivet.R
import com.example.servivet.data.model.setting.address_list.SettingAddress
import com.example.servivet.databinding.FragmentSettingLocationListBinding
import com.example.servivet.ui.base.BaseFragment
import com.example.servivet.ui.main.adapter.SettingAddressListAdapter
import com.example.servivet.ui.main.view_model.SettingLocationListViewModel
import com.example.servivet.utils.CommonUtils
import com.example.servivet.utils.CommonUtils.showSnackBar
import com.example.servivet.utils.Constants
import com.example.servivet.utils.ProcessDialog
import com.example.servivet.utils.Status
import com.example.servivet.utils.StatusCode
import com.example.servivet.utils.requestMultiplePermissions


class SettingLocationListFragment :
    BaseFragment<FragmentSettingLocationListBinding, SettingLocationListViewModel>(R.layout.fragment_setting_location_list) {
    override val binding: FragmentSettingLocationListBinding by viewBinding(
        FragmentSettingLocationListBinding::bind
    )
    override val mViewModel: SettingLocationListViewModel by viewModels()
    private val lPermission = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )
    private var addressData = SettingAddress()
    private var isAddAddress = false

    private var checkLocationPermission = false

    override fun isNetworkAvailable(boolean: Boolean) {
    }

    override fun setupViewModel() {
    }

    override fun setupViews() {
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = mViewModel
            click = mViewModel.ClickAction()
            setClickEvents()
        }

        mViewModel.hitAddressListApi()
    }

    private fun setClickEvents() {
        binding.idAddAddress.setOnClickListener {
            checkLocationIsTrue()
            isAddAddress = true

        }
    }

    private fun setAdapter(list: ArrayList<SettingAddress>) {
        if (list != null && list.isNotEmpty()) {
            binding.saveAddressRecycler.visibility = View.VISIBLE
            binding.noDataLayout.visibility = View.GONE
            binding.saveAddressRecycler.adapter =
                SettingAddressListAdapter(requireContext(), list, onItemClick)
        } else {
            binding.saveAddressRecycler.visibility = View.GONE
            binding.noDataLayout.visibility = View.VISIBLE
        }

    }

    private val onItemClick: (String, SettingAddress) -> Unit = { key, data ->
        when (key) {
            "update" -> {
                addressData = data
                isAddAddress = false

                checkLocationIsTrue()

//                var bundle= Bundle()
//                bundle.putString("action","update")
//                bundle.putSerializable(Constants.DATA,data)
//                findNavController().navigate(R.id.action_settingLocationListFragment_to_settingAddLocationFragment,bundle)

            }

            "delete" -> {
                mViewModel.saveAddressRequest.addressActionType = "delete"
                mViewModel.saveAddressRequest.addressId = data._id
                mViewModel.saveAddressRequest.city = data.city
                mViewModel.saveAddressRequest.fullAddress = data.fullAddress
                mViewModel.saveAddressRequest.latitute = data.location!!.coordinates[0].toString()
                mViewModel.saveAddressRequest.longitute = data.location!!.coordinates[1].toString()
                mViewModel.saveAddressRequest.postalCode = data.postalCode.toString()
                mViewModel.hitSaveAddressAPI()
            }

            "makedefault" -> {
                mViewModel.saveAddressRequest.addressActionType = "makedefault"
                mViewModel.saveAddressRequest.addressId = data._id
                mViewModel.saveAddressRequest.city = data.city
                mViewModel.saveAddressRequest.fullAddress = data.fullAddress
                mViewModel.saveAddressRequest.latitute = data.location!!.coordinates[0].toString()
                mViewModel.saveAddressRequest.longitute = data.location.coordinates[1].toString()
                mViewModel.saveAddressRequest.postalCode = data.postalCode.toString()
                mViewModel.hitSaveAddressAPI()
            }
        }

    }


    override fun setupObservers() {
        mViewModel.addressListResponse.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    ProcessDialog.dismissDialog()

                    when (it.data!!.code) {
                        StatusCode.STATUS_CODE_SUCCESS -> {
                            setAdapter(it.data.result.address)
                        }

                        StatusCode.STATUS_CODE_FAIL -> {
                            showSnackBar(it.data.message)
                        }

                    }
                }

                Status.LOADING -> {
                    ProcessDialog.dismissDialog()
                }

                Status.ERROR -> {
                    ProcessDialog.dismissDialog()
                    it.message?.let {
                        showSnackBar(it)
                    }
                }

                else -> {}
            }
        }

        mViewModel.saveAddressResponse.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    ProcessDialog.dismissDialog()
                    when (it.data?.code) {
                        StatusCode.STATUS_CODE_SUCCESS -> {
                            mViewModel.hitAddressListApi()
                            showSnackBar(it.data.message)
                        }

                        StatusCode.STATUS_CODE_FAIL -> {
                            showSnackBar(it.data.message)
                        }

                        else -> {
                            showSnackBar(it.data!!.message)
                        }
                    }
                }

                Status.LOADING -> {
                    ProcessDialog.startDialog(requireContext())
                }

                Status.ERROR -> {
                    ProcessDialog.dismissDialog()

                    it.message?.let {
                        showSnackBar(it)
                    }
                }

                com.example.servivet.utils.Status.UNAUTHORIZED -> {
                    CommonUtils.logoutAlert(
                        requireContext(),
                        "Session Expired",
                        "Unauthorized User",
                        requireActivity()
                    )
                }
            }
        }
    }


    private fun checkLocationIsTrue() {
        if (!checkLocationPermission) {
            checkLocation()
        } else {
            openMap()
        }
    }

    private fun checkLocation() {
        checkLocationPermission =
            requestMultiplePermissions(requireContext(), lPermission, "Please allow permission")
    }

    private fun openMap() {
        if (isAddAddress) {
            var bundle = Bundle()
            bundle.putString("action", "add")
            findNavController().navigate(
                R.id.action_settingLocationListFragment_to_settingAddLocationFragment,
                bundle
            )
        } else {
            var bundle = Bundle()
            bundle.putString("action", "update")
            bundle.putSerializable(Constants.DATA, addressData)
            findNavController().navigate(
                R.id.action_settingLocationListFragment_to_settingAddLocationFragment,
                bundle
            )
        }


    }


}