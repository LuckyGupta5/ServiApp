package com.example.servivet.ui.main.fragment

import android.os.Build
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.annotation.RequiresApi
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
        initEditText()
        binding.idTopLayout.idSearch.setOnClickListener {
            binding.idTopLayout.idSearchLayout.visibility = View.VISIBLE
            binding.idTopLayout.idSearch.visibility = View.GONE
        }

        binding.idTopLayout.idCloseSearch.setOnClickListener {
            binding.idTopLayout.idSearchLayout.visibility = View.GONE
            binding.idTopLayout.idSearch.visibility = View.VISIBLE
        }
    }

    private fun initEditText() {
        binding.idTopLayout.idSearchText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                mViewModel.request["search"] = s.toString()
                mViewModel.getConnectionListRequest()
            }
            override fun afterTextChanged(s: Editable?) {

            }
        })


    }

    private fun setadapter() {

    }

    private fun backbtn() {
        binding.idTopLayout.idBack.setOnClickListener(View.OnClickListener {
            requireActivity().onBackPressed()


        })
        binding.idTopLayout.idSearchLayout.setOnClickListener(View.OnClickListener {
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

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initAcceptRejectModel() {
        acceptRejectModel.getAcceptRejectData().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    ProcessDialog.dismissDialog()
                    when (it.data!!.code) {
                        StatusCode.STATUS_CODE_SUCCESS -> {
                          showSnackBar(getString(R.string.connection_remove_successfully))
                            findNavController().popBackStack()
                        }

                        StatusCode.STATUS_CODE_FAIL -> {
                            showSnackBar(it.data.message!!)
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