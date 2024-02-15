package com.example.servivet.ui.main.fragment

import android.util.Log
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.servivet.R
import com.example.servivet.data.model.chat_models.request_list.response.ChatListResponse
import com.example.servivet.data.model.chat_models.request_list.response.Chatlist
import com.example.servivet.databinding.FragmentChatRequestBinding
import com.example.servivet.ui.base.BaseFragment
import com.example.servivet.ui.main.adapter.ChatFragmentAdapter
import com.example.servivet.ui.main.view_model.ChatRequestViewModel
import com.example.servivet.utils.SocketManager
import com.google.gson.Gson
import io.socket.client.Socket
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class ChatRequestFragment :
    BaseFragment<FragmentChatRequestBinding, ChatRequestViewModel>(R.layout.fragment_chat_request) {
    override val binding: FragmentChatRequestBinding by viewBinding(FragmentChatRequestBinding::bind)
    override val mViewModel: ChatRequestViewModel by viewModels()
    private lateinit var socket: Socket
    private var chatList = ArrayList<Chatlist>()
    private var isAccept = true
    private lateinit var chatListResponse: ChatListResponse


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
        initSocket()

    }


    override fun setupObservers() {
    }

    private fun initSocket() {
        try {
            socket = SocketManager.getSocket()
            val data = JSONObject()
            data.put("isRequestedChatList", true)
            socket.emit("chatList", data)
            socket.on("chatList", fun(args: Array<Any?>) {
                if (isAdded) {
                    requireActivity().runOnUiThread {
                        val providerData = args[0] as JSONObject
                        try {
                            Log.e("TAG", "checkData123:${providerData} ")
                            chatListResponse = Gson().fromJson(
                                JSONArray().put(providerData)[0].toString(),
                                ChatListResponse::class.java
                            )
                            chatList.clear()
                            chatList.addAll(chatListResponse.result.chatlist)
                            initChatAdapter()


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
        binding.chatListAdapter =
            ChatFragmentAdapter(requireContext(), chatList, false, onItemClick)
    }

    private val onItemClick: (String, String) -> Unit = { identifires, data ->

        when (identifires) {
            getString(R.string.decline) -> {
                initAcceptRejectChat(data, false)
            }

            getString(R.string.accept) -> {
                initAcceptRejectChat(data, true)

            }
        }

    }

    private fun initAcceptRejectChat(id: String, isAccept: Boolean) {
        try {
            socket = SocketManager.getSocket()
            val data = JSONObject()
            data.put("roomId", id)
            data.put("isAccept", isAccept)
            socket.emit("acceptRejectChat", data)
            socket.on("acceptRejectChat", fun(args: Array<Any?>) {
                if (isAdded) {
                    requireActivity().runOnUiThread {
                        val acceptRejectChat = args[0] as JSONObject
                        try {
                            Log.e("TAG", "acceptRejectChat:${acceptRejectChat} ")
//                            chatListResponse = Gson().fromJson(
//                                JSONArray().put(acceptRejectChat)[0].toString(),
//                                ChatListResponse::class.java
//                            )
//                            chatList.clear()
//                            chatList.addAll(chatListResponse.result.chatlist)
//                            initChatAdapter()


                        } catch (ex: JSONException) {
                            ex.printStackTrace()
                        }
                    }
                }
            })

        } catch (ex: Exception) {

        }
    }
}