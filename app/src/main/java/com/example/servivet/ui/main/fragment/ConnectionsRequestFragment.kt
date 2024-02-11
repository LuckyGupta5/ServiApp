package com.example.servivet.ui.main.fragment

import android.util.Log
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.servivet.R
import com.example.servivet.data.model.connection.connection_list.responnse.MyConnection
import com.example.servivet.databinding.FragmentConnectionsRequestBinding
import com.example.servivet.ui.base.BaseFragment
import com.example.servivet.ui.main.adapter.ConnectionRequestAdapter
import com.example.servivet.ui.main.view_model.AcceptRejectViewModel
import com.example.servivet.ui.main.view_model.ConnectionRequestViewModel
import com.example.servivet.utils.CommonUtils
import com.example.servivet.utils.CommonUtils.showSnackBar
import com.example.servivet.utils.ProcessDialog
import com.example.servivet.utils.Status
import com.example.servivet.utils.StatusCode
import com.google.gson.Gson

class ConnectionsRequestFragment :
    BaseFragment<FragmentConnectionsRequestBinding, ConnectionRequestViewModel>(R.layout.fragment_connections_request) {
    override val binding: FragmentConnectionsRequestBinding by viewBinding(
        FragmentConnectionsRequestBinding::bind
    )
    override val mViewModel: ConnectionRequestViewModel by viewModels()
    private val acceptRejectModel: AcceptRejectViewModel by viewModels()
    private val requestList = ArrayList<MyConnection>()

    override fun isNetworkAvailable(boolean: Boolean) {
    }

    override fun setupViewModel() {
    }

    override fun setupViews() {
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = mViewModel
            click = mViewModel.ClickAction()
        }
        backbtn()
        initAcceptRejectModel()
    }

    fun backbtn() {
        binding.idTopLayout.idBack.setOnClickListener(View.OnClickListener {
            findNavController().popBackStack()
        })
    }

    override fun setupObservers() {
        binding.idTopLayout.idTitle.text = getText(R.string.connections_requests)
        mViewModel.getConnectionListRequest()
        mViewModel.getConnectionRequestData().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    ProcessDialog.dismissDialog()
                    when (it.data!!.code) {
                        StatusCode.STATUS_CODE_SUCCESS -> {
                            requestList.clear()

                            requestList.addAll(it.data.result.connectionRequestList)
                            initRequestAdapter()

                        }

                        StatusCode.STATUS_CODE_FAIL -> {
                            showSnackBar(it.data.message!!)
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

                Status.UNAUTHORIZED -> {
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

    private fun initAcceptRejectModel() {
        acceptRejectModel.getAcceptRejectData().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    ProcessDialog.dismissDialog()
                    when (it.data!!.code) {
                        StatusCode.STATUS_CODE_SUCCESS -> {
                            showSnackBar(it.data.message!!)
                        }

                        StatusCode.STATUS_CODE_FAIL -> {
                            showSnackBar(it.data.message!!)
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

                Status.UNAUTHORIZED -> {
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

    private fun initRequestAdapter() {
        binding.idNoDataFound.root.isVisible = requestList.size <= 0
        binding.requestAdapter =
            ConnectionRequestAdapter(requireContext(), requestList, onItemClick)
    }

    private val onItemClick: (Int, String) -> Unit = { identifier, data ->

        when (identifier) {
            1 -> {
                acceptRejectModel.getAcceptRejectRequest(identifier, data)
            }

            2 -> {
                acceptRejectModel.getAcceptRejectRequest(identifier, data)
            }
        }


    }

}