package com.example.servivet.ui.main.fragment.chat_module

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.servivet.R
import com.example.servivet.data.model.chat_models.request_list.response.ChatListResponse
import com.example.servivet.data.model.user_profile.response.UserProfile
import com.example.servivet.databinding.FragmentChattingBinding
import com.example.servivet.ui.base.BaseFragment
import com.example.servivet.ui.main.adapter.ChattingAdapter
import com.example.servivet.ui.main.view_model.ChattingViewModel
import com.example.servivet.ui.main.view_model.ProfileViewModel
import com.example.servivet.utils.SocketManager
import com.google.gson.Gson
import io.socket.client.Socket
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject


class ChattingFragment :
    BaseFragment<FragmentChattingBinding, ChattingViewModel>(R.layout.fragment_chatting) {
    override val binding: FragmentChattingBinding by viewBinding(FragmentChattingBinding::bind)
    override val mViewModel: ChattingViewModel by viewModels()
    private lateinit var socket: Socket
    private val argumentData: ChattingFragmentArgs by navArgs()
    private lateinit var profileData: UserProfile
    override fun isNetworkAvailable(boolean: Boolean) {

    }

    override fun setupViewModel() {
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = mViewModel
            binding.clickEvents = ::onClick

            binding.chattingAdapter = ChattingAdapter(ArrayList())
        }
    }

    override fun setupViews() {
        checkRoomIdIsEmpty()

    }


    private fun checkRoomIdIsEmpty() {
        when (argumentData.from) {
            getString(R.string.provider_profile) -> {
                profileData = Gson().fromJson(argumentData.data, UserProfile::class.java)
            }
        }
    }

    private fun onClick(type: String) {
        when (type) {
            getString(R.string.message) -> {
                if (profileData.roomId.isEmpty()) {
                    initInitiateChatSocket()

                } else {

                }
            }

            getString(R.string.pop_up) -> {
                showPopupMenu()
            }
        }
    }

    private fun showPopupMenu() {
        val popupMenu = PopupMenu(requireContext(), binding.idMenuItems)
        popupMenu.inflate(R.menu.chat_menu)

        // Set item click listener
        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_item1 -> {
                    // Handle menu item 1 click
                    true
                }

                R.id.menu_item2 -> {
                    // Handle menu item 2 click
                    true
                }

                else -> false
            }
        }

        // Show the popup menu
        popupMenu.show()
    }


    private fun initInitiateChatSocket() {
        try {
            socket = SocketManager.getSocket()
            val data = JSONObject()
            data.put("receiverId", profileData._id)
            data.put("messageType", 1)
            data.put("message", binding.idMessage.text.toString())
            // data.put("file", true)
            socket.emit("initiateChat", data)
            socket.on("getRoomId", fun(args: Array<Any?>) {
                if (isAdded) {
                    requireActivity().runOnUiThread {
                        val initChatData = args[0] as JSONObject
                        try {
                            Log.e("TAG", "initChatData:${initChatData} ")

                        } catch (ex: JSONException) {
                            ex.printStackTrace()
                        }
                    }
                }
            })

        } catch (ex: Exception) {

        }
    }

    override fun setupObservers() {

    }


}