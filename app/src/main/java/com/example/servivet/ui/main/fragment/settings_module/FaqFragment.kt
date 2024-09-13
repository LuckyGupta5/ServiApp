package com.example.servivet.ui.main.fragment.settings_module

import android.text.Editable
import android.text.TextWatcher
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.servivet.R
import com.example.servivet.data.model.setting.faq_list.response.FaqListResult
import com.example.servivet.data.model.setting.faq_type_list.response.FaqTypeListResult
import com.example.servivet.databinding.FragmentFaqBinding
import com.example.servivet.ui.base.BaseFragment
import com.example.servivet.ui.main.adapter.FaqAdapter
import com.example.servivet.ui.main.adapter.FaqTypeAdapter
import com.example.servivet.ui.main.view_model.FaqViewModel
import com.example.servivet.utils.CommonUtils.showSnackBar
import com.example.servivet.utils.CommonUtils.showToast
import com.example.servivet.utils.ProcessDialog
import com.example.servivet.utils.Status
import com.example.servivet.utils.StatusCode

class FaqFragment :
    BaseFragment<FragmentFaqBinding, FaqViewModel>(R.layout.fragment_faq),
    FaqTypeAdapter.FaqTypeInterFace {
    override val binding: FragmentFaqBinding by viewBinding(FragmentFaqBinding::bind)
    override val mViewModel: FaqViewModel by viewModels()
    lateinit var adapter: FaqAdapter

    override fun isNetworkAvailable(boolean: Boolean) {
    }

    override fun setupViewModel() {
        initEditText()
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = mViewModel
            click = mViewModel.ClickAction()

            binding.searchTab.setOnClickListener {
                binding.idSearchLayout.isVisible = true
                binding.idTopLayout.isVisible = false
            }
            binding.closeSearch.setOnClickListener {
                binding.idSearchLayout.isVisible = false
                binding.idTopLayout.isVisible = true
                mViewModel.hitFaqTypeListApi()
            }
        }
        mViewModel.hitFaqTypeListApi()
    }

    override fun setupViews() {

    }

    override fun setupObservers() {
        mViewModel.faqTypeResponse.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    ProcessDialog.dismissDialog()
                    when (it.data?.code) {
                        StatusCode.STATUS_CODE_SUCCESS -> {
                            setFaqTypeAdapter(it.data.result)
                            mViewModel.hitFaqTypeListApi("")
                        }

                        StatusCode.STATUS_CODE_FAIL -> {
                            showToast(it.data.message?:"Something went wrong")
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

        mViewModel.faqListResponse.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    ProcessDialog.dismissDialog()
                    when (it.data?.code) {
                        StatusCode.STATUS_CODE_SUCCESS -> {
                            setFaqListAdapter(it.data.result)
                        }

                        StatusCode.STATUS_CODE_FAIL -> {
                            showToast(it.data.message?:"Something went wrong")
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
                    ProcessDialog.dismissDialog()
                    it.message?.let {
                        showSnackBar(it)
                    }
                }
            }
        }
    }

    private fun setFaqListAdapter(list: ArrayList<FaqListResult>) {
        adapter = FaqAdapter(requireContext(), list)
        binding.faqRecycler.adapter = adapter

    }

    private fun setFaqTypeAdapter(list: ArrayList<FaqTypeListResult>) {
        list.add(0, FaqTypeListResult("", "All"))
        binding.faqTypeRecycler.adapter = FaqTypeAdapter(requireContext(), list, this)
    }

    override fun onSubCatSelected(id: String) {
        mViewModel.hitFaqTypeListApi(id)
    }

    private fun initEditText() {
        binding.idSearchText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (::adapter.isInitialized) {
                    filter(s.toString())
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }


    fun filter(text: String) {
        val listOfFaq = mViewModel.faqListResponse.value?.data?.result ?: emptyList()
        val filteredList =
            listOfFaq.filter { it.title.trim().lowercase().contains(text.trim().lowercase()) }
        adapter.updateData(filteredList)
    }
}

