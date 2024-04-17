package com.example.servivet.ui.main.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.servivet.R
import com.example.servivet.databinding.FragmentNotificationListBinding
import com.example.servivet.ui.base.BaseFragment
import com.example.servivet.ui.main.view_model.NotificationListViewModel


class NotificationListFragment : BaseFragment<FragmentNotificationListBinding,NotificationListViewModel>(R.layout.fragment_notification_list) {
    override val binding: FragmentNotificationListBinding
        get() = TODO("Not yet implemented")
    override val mViewModel: NotificationListViewModel
        get() = TODO("Not yet implemented")

    override fun isNetworkAvailable(boolean: Boolean) {
    }

    override fun setupViewModel() {
    }

    override fun setupViews() {
    }

    override fun setupObservers() {
    }


}