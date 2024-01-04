package com.example.servivet.ui.base

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.example.servivet.utils.LogUtil
import com.example.servivet.utils.network.NetworkStatus
import com.example.servivet.utils.network.NetworkStatusHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


abstract class BaseFragment<Binding : ViewBinding, ViewModel : BaseViewModel>(layoutID: Int) :
    Fragment(layoutID) {
    protected abstract val binding: Binding
    protected abstract val mViewModel: ViewModel
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //observeConnectionLiveData()
            CoroutineScope(Dispatchers.Main).launch {
                if(isAdded)
            setupViews()
            setupViewModel()
            setupObservers()
        }


    }

    open fun hasPermission(str: String?): Boolean {
        return Build.VERSION.SDK_INT < 23 || requireActivity().checkSelfPermission(str!!) == PackageManager.PERMISSION_GRANTED
    }

    open fun requestPermissionsSafely(strArr: Array<String?>?, i: Int) {
        if (Build.VERSION.SDK_INT >= 23) {
            requestPermissions(strArr!!, i)
        }
    }

    private fun observeConnectionLiveData() {
        NetworkStatusHelper(requireContext()).observe(viewLifecycleOwner) {
            when (it) {
                NetworkStatus.Available -> {
                    isNetworkAvailable(true)
                    LogUtil.e("Network", "Network Connection Established")
                }

                NetworkStatus.Unavailable -> {
                    LogUtil.e("Network", "No Internet")
                    isNetworkAvailable(false)

                }
            }
        }


    }

    abstract fun isNetworkAvailable(boolean: Boolean)
    abstract fun setupViewModel()
    abstract fun setupViews()
    abstract fun setupObservers()

    fun alertDailog(context: Context, message: String) {
        var alertDialog = AlertDialog.Builder(context) //set icon
            .setIcon(android.R.drawable.ic_dialog_alert) //set title
            .setTitle("") //set message
            .setMessage(message) //set positive button
            .setPositiveButton("ok") { dialogInterface, i -> //set what would happen when positive button is clicked
                requireActivity().finish()
            }
            .show()
    }


}