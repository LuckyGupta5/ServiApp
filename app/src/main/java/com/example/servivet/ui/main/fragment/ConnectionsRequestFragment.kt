package com.example.servivet.ui.main.fragment

import android.text.Editable
import android.text.TextWatcher
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
import com.example.servivet.ui.main.view_model.ConnectionRequestListViewModel
import com.example.servivet.utils.CommonUtils
import com.example.servivet.utils.CommonUtils.showSnackBar
import com.example.servivet.utils.ProcessDialog
import com.example.servivet.utils.Status
import com.example.servivet.utils.StatusCode

class ConnectionsRequestFragment :
    BaseFragment<FragmentConnectionsRequestBinding, ConnectionRequestListViewModel>(R.layout.fragment_connections_request) {
    override val binding: FragmentConnectionsRequestBinding by viewBinding(
        FragmentConnectionsRequestBinding::bind
    )
    override val mViewModel: ConnectionRequestListViewModel by viewModels()
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
        initEditText()
        backbtn()
        initAcceptRejectModel()
    }

    fun backbtn() {
        binding.idTopLayout.idBack.setOnClickListener(View.OnClickListener {
            findNavController().popBackStack()
        })
    }

    private fun initEditText() {
        binding.idTopLayout.idSearch.setOnClickListener {
            binding.idTopLayout.idSearchLayout.isVisible = true
        }
        binding.idTopLayout.idCloseSearch.setOnClickListener {
            binding.idTopLayout.idSearchLayout.isVisible = false
            setupObservers()
        }
        binding.idTopLayout.idSearchText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.requestAdapter?.filter(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
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
                            findNavController().popBackStack()
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
        binding.requestAdapter = ConnectionRequestAdapter(requireContext(), requestList, onItemClick)
    }

    private val onItemClick: (Int, String) -> Unit = { identifier, data ->

        when (identifier) {
            1 -> {
                acceptRejectModel.getAcceptRejectRequest(identifier, data)
            }

            2 -> {
                acceptRejectModel.getAcceptRejectRequest(identifier, data)
            }

            3 -> {
                binding.idNoDataFound.root.isVisible = data.toInt() <= 0
            }
        }


    }

}