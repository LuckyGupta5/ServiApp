package com.example.servivet.ui.main.fragment

import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.servivet.R
import com.example.servivet.data.model.notification_list.response.NotificationList
import com.example.servivet.databinding.FragmentNotificationListBinding
import com.example.servivet.ui.base.BaseFragment
import com.example.servivet.ui.main.adapter.NotificationListAdapter
import com.example.servivet.ui.main.view_model.NotificationListViewModel
import com.example.servivet.utils.CommonUtils.showSnackBar
import com.example.servivet.utils.CommonUtils.showToast
import com.example.servivet.utils.ProcessDialog
import com.example.servivet.utils.Status
import com.example.servivet.utils.StatusCode

class NotificationListFragment :
    BaseFragment<FragmentNotificationListBinding, NotificationListViewModel>(R.layout.fragment_notification_list) {

    override val binding: FragmentNotificationListBinding by viewBinding(
        FragmentNotificationListBinding::bind
    )
    override val mViewModel: NotificationListViewModel by activityViewModels()

    override fun isNetworkAvailable(boolean: Boolean) {

    }

    override fun setupViewModel() {

    }

    override fun setupViews() {
        binding.idBackPress.setOnClickListener {
            findNavController().popBackStack(R.id.homeFragment, false)
        }
    }


    override fun setupObservers() {
        mViewModel.getConnectionListRequest()
        mViewModel.getConnectionRequestData().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    ProcessDialog.dismissDialog()
                    when (it.data?.code) {
                        StatusCode.STATUS_CODE_SUCCESS -> {
                            if (it.data.result.data.isNotEmpty()) {
                                initAdapter(it.data.result.data)
                            } else {
                                //todo : add no data found screen
                                showToast("No data found")
                            }
                        }

                        StatusCode.STATUS_CODE_FAIL -> {
                            showToast(it.data.message)
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

                Status.UNAUTHORIZED -> {
                    ProcessDialog.dismissDialog()
                    it.message?.let {
                        showSnackBar(it)
                    }
                }
            }
        }
    }

    private fun initAdapter(data: List<NotificationList>) {
        val adapter = NotificationListAdapter()
        binding.notificationRecycler.adapter = adapter
        adapter.addItems(data as MutableList<NotificationList>)
    }
}