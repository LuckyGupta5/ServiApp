package com.example.servivet.ui.main.fragment

import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.servivet.R
import com.example.servivet.data.model.chat_models.request_list.response.ChatListResponse
import com.example.servivet.data.model.chat_models.request_list.response.Chatlist
import com.example.servivet.databinding.FragmentChatsBinding
import com.example.servivet.ui.base.BaseFragment
import com.example.servivet.ui.main.adapter.ChatFragmentAdapter
import com.example.servivet.ui.main.view_model.ChatViewModel
import com.example.servivet.utils.ProcessDialog
import com.example.servivet.utils.SocketManager
import com.google.gson.Gson
import io.socket.client.Socket
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject


class ChatsFragment : BaseFragment<FragmentChatsBinding, ChatViewModel>(R.layout.fragment_chats) {
    override val binding: FragmentChatsBinding by viewBinding(FragmentChatsBinding::bind)
    override val mViewModel: ChatViewModel by viewModels()
    private lateinit var socket: Socket
    private lateinit var chatListResponse: ChatListResponse
    private var chatList = ArrayList<Chatlist>()

    override fun isNetworkAvailable(boolean: Boolean) {
    }

    override fun setupViewModel() {

    }


    override fun setupViews() {
        ProcessDialog.dismissDialog()
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewMOdel = mViewModel
            click = mViewModel.ClickAction(requireContext(), binding)
        }
        initEditText()
        setBack()
        initSocket()
    }

    private fun initEditText() {
        binding.idSearchText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
               binding.chatListAdapter?.filter(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }


    override fun setupObservers() {
    }

    private fun setBack() {
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().navigate(R.id.action_chatFragment_to_homeFragment)
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }


    private fun initSocket() {
        try {
            SocketManager.connect()
            socket = SocketManager.getSocket()
            val data = JSONObject()
            data.put("isRequestedChatList", false)
            socket.emit("chatList", data)
            socket.on("chatList", fun(args: Array<Any?>) {
                if (isAdded) {
                    requireActivity().runOnUiThread {
                        val providerData = args[0] as JSONObject
                        try {
                           // Log.e("TAG", "checkData123:${providerData} ")
                            chatListResponse = Gson().fromJson(
                                JSONArray().put(providerData)[0].toString(),
                                ChatListResponse::class.java
                            )
                            chatList.clear()
                            chatList.addAll(chatListResponse.result.chatlist)
                            initChatAdapter()
                            socket.off("chatList")


                        } catch (ex: JSONException) {
                            ex.printStackTrace()
                        }
                    }
                }
            })

        } catch (ex: Exception) {

        }

    }

    private fun initChatAdapter() {
        binding.chatListAdapter = ChatFragmentAdapter(requireContext(), chatList, true, onItemClick)
    }

    private val onItemClick: (String, String) -> Unit = { identifires, data ->

        when (identifires) {
            getString(R.string.container) -> {
                findNavController().navigate(
                    ChatsFragmentDirections.actionChatFragmentToChattingFragment(
                        data,
                        getString(R.string.chatfragment)
                    )
                )

            }

            getString(R.string.accept) -> {

            }
        }

    }


}