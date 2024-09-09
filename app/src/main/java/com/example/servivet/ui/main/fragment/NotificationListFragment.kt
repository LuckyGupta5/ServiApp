package com.example.servivet.ui.main.fragment

import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.servivet.R
import com.example.servivet.databinding.FragmentNotificationListBinding
import com.example.servivet.ui.base.BaseFragment
import com.example.servivet.ui.main.view_model.NotificationListViewModel

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

    }


}