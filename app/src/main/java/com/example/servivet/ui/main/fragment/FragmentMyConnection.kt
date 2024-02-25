package com.example.servivet.ui.main.fragment

import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.servivet.R
import com.example.servivet.data.model.connection.connection_list.responnse.MyConnection
import com.example.servivet.databinding.FragmentMyConnectionBinding
import com.example.servivet.ui.base.BaseFragment
import com.example.servivet.ui.main.adapter.MyConnectionAdapter
import com.example.servivet.ui.main.view_model.AcceptRejectViewModel
import com.example.servivet.ui.main.view_model.MyConnectionModelView
import com.example.servivet.utils.CommonUtils
import com.example.servivet.utils.CommonUtils.showSnackBar
import com.example.servivet.utils.ProcessDialog
import com.example.servivet.utils.Status
import com.example.servivet.utils.StatusCode

class FragmentMyConnection :
    BaseFragment<FragmentMyConnectionBinding, MyConnectionModelView>(R.layout.fragment_my_connection) {
    override val binding: FragmentMyConnectionBinding by viewBinding(FragmentMyConnectionBinding::bind)
    override val mViewModel: MyConnectionModelView by viewModels()
    private val connectionList = ArrayList<MyConnection>()
    private val acceptRejectModel: AcceptRejectViewModel by viewModels()

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
        setadapter()
        initAcceptRejectModel()

    }

    private fun setadapter() {

    }

    private fun backbtn() {
        binding.idTopLayout.idBack.setOnClickListener(View.OnClickListener {
            requireActivity().onBackPressed()
        })
    }


    override fun setupObservers() {
        binding.idTopLayout.idTitle.text = getString(R.string.my_connections)

        mViewModel.getConnectionListRequest()
        mViewModel.getConnectionData().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    ProcessDialog.dismissDialog()
                    when (it.data!!.code) {
                        StatusCode.STATUS_CODE_SUCCESS -> {
                            connectionList.clear()
                            connectionList.addAll(it.data.result.myConnectionList)
                            initConnectionAdapter()

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
                          //  showSnackBar(it.data.message!!)
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


    private fun initConnectionAdapter() {
        binding.idNoDataFound.root.isVisible = connectionList.size <= 0
        binding.listAdapter = MyConnectionAdapter(requireContext(), connectionList, onItemClick)
    }


    private val onItemClick: (Int, String) -> Unit = { identifire, data ->

        when (identifire) {
            2 -> {
                acceptRejectModel.getAcceptRejectRequest(identifire, data)
            }
        }


    }

}