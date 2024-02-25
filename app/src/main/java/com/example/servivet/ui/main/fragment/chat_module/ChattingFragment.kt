package com.example.servivet.ui.main.fragment.chat_module

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.util.Log
import android.widget.PopupMenu
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.servivet.R
import com.example.servivet.data.model.call_module.video_call.VideoCallResponse
import com.example.servivet.data.model.chat_models.chat_list.ChatMessage
import com.example.servivet.data.model.chat_models.chat_list.ChattingListResponse
import com.example.servivet.data.model.chat_models.initiate_chat.InitiateChatResponse
import com.example.servivet.data.model.chat_models.request_list.response.Chatlist
import com.example.servivet.data.model.user_profile.response.UserProfile
import com.example.servivet.databinding.FragmentChattingBinding
import com.example.servivet.ui.base.BaseFragment
import com.example.servivet.ui.main.adapter.ChattingAdapter
import com.example.servivet.ui.main.view_model.ChattingViewModel
import com.example.servivet.utils.CommonUtils
import com.example.servivet.utils.CommonUtils.showSnackBar
import com.example.servivet.utils.FileMaker
import com.example.servivet.utils.ProcessDialog
import com.example.servivet.utils.Session
import com.example.servivet.utils.SocketManager
import com.example.servivet.utils.Status
import com.example.servivet.utils.StatusCode
import com.example.servivet.utils.checkMediaPermission
import com.example.servivet.utils.checkVideoFileSize
import com.example.servivet.utils.downloadFile
import com.example.servivet.utils.getVideoPathFromUri
import com.google.gson.Gson
import io.socket.client.Socket
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class ChattingFragment :
    BaseFragment<FragmentChattingBinding, ChattingViewModel>(R.layout.fragment_chatting) {
    override val binding: FragmentChattingBinding by viewBinding(FragmentChattingBinding::bind)
    override val mViewModel: ChattingViewModel by viewModels()
    private lateinit var socket: Socket
    private val argumentData: ChattingFragmentArgs by navArgs()
    private lateinit var chatListResponse: ChattingListResponse
    private lateinit var initateChatResponse: InitiateChatResponse
    private lateinit var videoCallResponse: VideoCallResponse
    private lateinit var profileData: UserProfile
    private lateinit var chatListData: Chatlist
    private var isBlocked = false
    private lateinit var roomId: String
    private lateinit var recieverId: String
    private lateinit var type: String
    private var imagePath: String = ""
    private var chattingList = ArrayList<ChatMessage>()
    private var mediaList: ArrayList<String> = ArrayList()
    private var checkMediaList: ArrayList<String> = ArrayList()
    private var typeOfMessage = 1
    private var callType = 0
    private var list = ArrayList<String>()
    private var isMediaCome = false

    override fun isNetworkAvailable(boolean: Boolean) {

    }

    override fun setupViewModel() {
        binding.apply {
            // lifecycleOwner = viewLifecycleOwner
            viewModel = mViewModel
            binding.clickEvents = ::onClick

        }
    }

    override fun setupViews() {
        checkRoomIdIsEmpty()
        bottomSheetCallBack()
    }


    private fun checkRoomIdIsEmpty() {
        when (argumentData.from) {
            getString(R.string.provider_profile) -> {
                profileData = Gson().fromJson(argumentData.data, UserProfile::class.java)
                roomId = profileData.roomId
                recieverId = profileData._id
                binding.idUserName.text = profileData.name

                if (profileData.roomId.isNotEmpty()) {
                    initChatListSocket()
                }
            }

            getString(R.string.chatfragment) -> {
                chatListData = Gson().fromJson(argumentData.data, Chatlist::class.java)
                roomId = chatListData._id
                if (chatListData.senderId._id == Session.userDetails._id) {
                    binding.idUserName.text = chatListData.receiverId.name
                    recieverId = chatListData.receiverId._id

                } else {
                    binding.idUserName.text = chatListData.senderId.name
                    recieverId = chatListData.senderId._id
                }
                if (roomId.isNotEmpty()) {
                    initChatListSocket()
                }

            }

        }
    }

    private fun onClick(type: String) {
        when (type) {

            getString(R.string.back_press) -> {
                findNavController().popBackStack()
            }

            getString(R.string.message) -> {
                binding.idUploadImageCard.isVisible = false

                if (roomId.isEmpty()) {
                    initInitiateChatSocket()
                } else {
                    initSendMessageEvent()
                }
            }

            getString(R.string.pop_up) -> {
                showPopupMenu()
            }

            getString(R.string.open_gallery) -> {
                //   findNavController().navigate(R.id.action_chattingFragment_to_selectMediaBottomSheet)
                findNavController().navigate(
                    ChattingFragmentDirections.actionChattingFragmentToSelectMediaBottomSheet(
                        "",
                        getString(R.string.gallery)
                    )
                )
            }

            getString(R.string.cross) -> {
                binding.idUploadImageCard.isVisible = false
                mediaList.clear()
            }


        }
    }


    private fun showPopupMenu() {
        val popupMenu = PopupMenu(requireContext(), binding.idMenuItems)
        popupMenu.inflate(R.menu.chat_menu)
        val menuItem = popupMenu.menu.findItem(R.id.idBlockUser)
        menuItem.title = if (isBlocked) getString(R.string.unblock) else getString(R.string.block)

        // Set item click listener
        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.idBlockUser -> {
                    isBlocked = !isBlocked
                    if (isBlocked) {
                        menuItem.title = getString(R.string.unblock)
                        initBlockUnblockUser()

                    } else {
                        menuItem.title = getString(R.string.block)
                        initUnblockUser()

                    }
                    true

                }

                R.id.idDeleteChat -> {
                    initDeleteChatEvent()
                    true
                }

                R.id.idCall -> {
//                    findNavController().navigate(R.id.action_chattingFragment_to_selectMediaBottomSheet)
                    findNavController().navigate(
                        ChattingFragmentDirections.actionChattingFragmentToSelectMediaBottomSheet(
                            "",
                            getString(R.string.call)
                        )
                    )
                    true
                }

                else -> false
            }
        }

        // Show the popup menu
        popupMenu.show()
    }

    private fun initDeleteChatEvent() {
        try {
            socket = SocketManager.getSocket()
            val data = JSONObject()
            data.put("roomId", roomId)
            socket.emit("clearChat", data)
            mediaList.clear()
            initChatListSocket()

        } catch (ex: Exception) {

        }
    }

    private fun initInitiateChatSocket() {
        try {
            socket = SocketManager.getSocket()
            val data = JSONObject()
            data.put("receiverId", profileData._id)
            data.put("messageType", typeOfMessage)
            data.put("message", binding.idMessage.text.toString())
            data.put("file", Gson().toJson(mediaList))
            socket.emit("initiateChat", data)
            socket.on("getRoomId", fun(args: Array<Any?>) {
                if (isAdded) {
                    requireActivity().runOnUiThread {
                        val initChatData = args[0] as JSONObject
                        try {
                            Log.e("TAG", "initChatDatafirst:${initChatData} ")
                            initateChatResponse = Gson().fromJson(
                                JSONArray().put(initChatData)[0].toString(),
                                InitiateChatResponse::class.java
                            )
                            roomId = initateChatResponse.result.roomDetail._id
                            binding.idMessage.setText("")
                            initChatListSocket()


                        } catch (ex: JSONException) {
                            ex.printStackTrace()
                        }
                    }
                }
            })

        } catch (ex: Exception) {

        }
    }


    private fun initBlockUnblockUser() {
        try {
            socket = SocketManager.getSocket()
            val data = JSONObject()
            data.put("roomId", roomId)
            data.put("blockUserId", "65c5f68112f8237e89e4770e")
            socket.emit("blockedUser", data)
            socket.on("blockedUser", fun(args: Array<Any?>) {
                if (isAdded) {
                    requireActivity().runOnUiThread {
                        val blockChat = args[0] as JSONObject
                        try {
                            Log.e("TAG", "blockChat:${blockChat}")
                        } catch (ex: JSONException) {
                            ex.printStackTrace()
                        }
                    }
                }
            })
        } catch (ex: Exception) {

        }
    }

    private fun initUnblockUser() {
        try {
            socket = SocketManager.getSocket()
            val data = JSONObject()
            data.put("roomId", roomId)
            data.put("blockUserId", "65c5f68112f8237e89e4770e")
            socket.emit("unBlockeUser", data)
            socket.on("blockedUser", fun(args: Array<Any?>) {
                if (isAdded) {
                    requireActivity().runOnUiThread {
                        val unBlockChat = args[0] as JSONObject
                        try {
                            Log.e("TAG", "unBlockChat:${unBlockChat}")
                        } catch (ex: JSONException) {
                            ex.printStackTrace()
                        }
                    }
                }
            })
        } catch (ex: Exception) {

        }
    }

    private fun initSendMessageEvent() {
        if (binding.idMessage.text.toString().trim() != "" || isMediaCome) {
            val data = JSONObject()
            try {
                data.put("receiverId", recieverId)
                data.put("roomId", roomId)
                data.put("messageType", typeOfMessage)
                data.put("message", binding.idMessage.text.toString())
                data.put("file", Gson().toJson(mediaList))
                socket.emit("sendMessage", data)
                binding.idMessage.setText("")
                typeOfMessage = 1
                isMediaCome =false
                mediaList.clear()
                Log.d("TAG", "sentMessage: ${Gson().toJson(data)}")

            } catch (ex: JSONException) {
            }
        }

    }

    private fun initChatListSocket() {
        try {
            socket = SocketManager.getSocket()
            val data = JSONObject()
            data.put("roomId", roomId)
            socket.emit("chatMessageList", data)
            socket.on("receiveMessages", fun(args: Array<Any?>) {
                if (isAdded) {
                    requireActivity().runOnUiThread {
                        val unBlockChat = args[0] as JSONObject
                        try {
                            Log.e("TAG", "chatListDataMessages:${unBlockChat}")
                            chatListResponse = Gson().fromJson(JSONArray().put(unBlockChat)[0].toString(), ChattingListResponse::class.java)
                            chattingList = chatListResponse.result.message
                            initChattingAdapter()
                            binding.idChatRecycle.scrollToPosition(chattingList.size - 1)
                            socket.off("receiveMessages")

                        } catch (ex: JSONException) {
                            ex.printStackTrace()
                        }
                    }
                }
            })

            // Single message

            socket.off("receiveMessage")
            socket.on("receiveMessage", fun(args: Array<Any?>) {
                if (isAdded) {
                    requireActivity().runOnUiThread {
                        val sendMessage = args[0] as JSONObject
                        try {
                            Log.e("TAG", "sendMessage:${sendMessage}")
                            chatListResponse = Gson().fromJson(
                                JSONArray().put(sendMessage)[0].toString(),
                                ChattingListResponse::class.java
                            )
                            chattingList.addAll(chatListResponse.result.message)
                            //chattingList =chatListResponse.result.message

                            binding.chattingAdapter?.updateList(chattingList)
                            binding.idChatRecycle.scrollToPosition(chattingList.size - 1)

                        } catch (ex: JSONException) {
                            ex.printStackTrace()
                        }
                    }
                }
            })
        } catch (ex: Exception) {

        }
    }

    private fun initChattingAdapter() {
        binding.chattingAdapter = ChattingAdapter(requireContext(), chattingList, onItemClick)
    }

    override fun setupObservers() {
        mViewModel.getDocumentData().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    ProcessDialog.dismissDialog()
                    when (it.data!!.code) {
                        StatusCode.STATUS_CODE_SUCCESS -> {
                            Log.e("TAG",
                                "xsetupObserveradjads: ${Gson().toJson(it.data.result.uploadImage)}",
                            )
                            mediaList.clear()
                            mediaList.addAll(it.data.result.uploadImage)
                            binding.uploadFileData = it.data.result
                            binding.idMessage.setText("..")
                            isMediaCome = true
                            binding.idUploadImageCard.isVisible = true


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

    private fun bottomSheetCallBack() {
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<String>(getString(R.string.videos))
            ?.observe(viewLifecycleOwner) {
                type = it
                when (type) {
                    getString(R.string.audio_call) -> {
                        callType = 6
                        generateAgoraToken()

                    }

                    getString(R.string.video_call) -> {
                        callType = 7
                        generateAgoraToken()

                    }

                    getString(R.string.image) -> {
                        if (checkMediaPermission(requireActivity())) {
                            openGallery()
                        } else {
                            showSnackBar("Permission not Granted")
                        }

                    }

                    getString(R.string.videos) -> {
                        if (checkMediaPermission(requireActivity())) {
                            openGallery()
                        } else {
                            showSnackBar("Permission not Granted")
                        }
                    }

                    getString(R.string.files) -> {
                        if (checkMediaPermission(requireActivity())) {
                            openGallery()
                        } else {
                            showSnackBar("Permission not Granted")
                        }
                    }
                }


            }
    }

    private fun generateAgoraToken() {
        socket = SocketManager.getSocket()
        val data = JSONObject()
        try {
            data.put("receiverId", recieverId)
            data.put("roomId", roomId)
            data.put("messageType", callType)
            data.put("message", "")
            data.put("uid", "0")
            socket.emit("agoraToken", data)
            socket.on("agoraToken", fun(args: Array<Any?>) {
                if (isAdded) {
                    requireActivity().runOnUiThread {
                        val agoraToken = args[0] as JSONObject
                        try {
                            Log.e("TAG", "chatListDataMessages121221:${agoraToken}")
                            videoCallResponse = Gson().fromJson(
                                JSONArray().put(agoraToken)[0].toString(),
                                VideoCallResponse::class.java
                            )

                            when (callType) {
                                6 -> {
                                    CoroutineScope(Dispatchers.Main).launch {
                                        delay(1000)
                                        findNavController().navigate(
                                            ChattingFragmentDirections.actionChattingFragmentToOutgoingAudioCallActivity(
                                                Gson().toJson(videoCallResponse),
                                                getString(
                                                    R.string.outgoing_Audio
                                                )
                                            )
                                        )
                                    }
                                }

                                7 -> {
                                    CoroutineScope(Dispatchers.Main).launch {
                                        delay(1000)
                                        findNavController().navigate(
                                            ChattingFragmentDirections.actionChattingFragmentToOutgoingVideoCallActivity(
                                                Gson().toJson(videoCallResponse),
                                                getString(
                                                    R.string.outgoing_video
                                                )
                                            )
                                        )
                                    }
                                }
                            }


                        } catch (ex: JSONException) {
                            ex.printStackTrace()
                        }
                    }
                }
            })
        } catch (ex: JSONException) {
        }
    }

    private fun openGallery() {
        val intent = Intent()
        when (type) {
            getString(R.string.videos) -> {
                intent.type = "video/*"
                typeOfMessage = 2
            }

            getString(R.string.image) -> {
                intent.type = "image/*"
                typeOfMessage = 5
            }

            getString(R.string.files) -> {
                intent.type = "application/pdf"
                // intent.type = "*/*"
                typeOfMessage = 4
            }
        }
        intent.action = Intent.ACTION_GET_CONTENT
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startForImageGallery.launch(intent)
    }

    @SuppressLint("SuspiciousIndentation")
    private val startForImageGallery =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                checkMediaList.clear()
                binding.idUploadImageCard.isVisible = false
                if (data!!.clipData != null) {
                    for (i in 0 until data.clipData!!.itemCount) {
                        val imageUri = data.clipData!!.getItemAt(i).uri
                        imagePath = getVideoPathFromUri(requireActivity(), imageUri).toString()
                        checkMediaList.add(imagePath)
                    }

                } else {
                    val file = FileMaker.from(requireContext(), data?.data)



                    if (file!!.path!!.isNotEmpty()) {
                        imagePath = file.path
                        checkMediaList.add(imagePath)

                        Log.e("TAG", "pathpath:${imagePath} ")

                    }
                }

                if (checkMediaList.size == 1) {
                    val fileSize = checkVideoFileSize(checkMediaList[0])
                    if (fileSize < 100) {
                        mViewModel.getRequest(imagePath)
                    } else {
                        showSnackBar(getString(R.string.media_size_check))
                    }
                } else {
                    showSnackBar("You can select only one item at time")

                }

            }
        }

    private val onItemClick: (Int, String) -> Unit = { position, data ->
        when (position) {
            0 -> {

                val listData = Gson().fromJson(data, Array<String>::class.java)
                list.addAll(listData)

                if (list[0].endsWith(".pdf")) {
                    downloadFile(
                        requireContext(),
                        list[0],
                        "Notes",
                        "pdf"
                    )
                } else {
                    findNavController().navigate(
                        ChattingFragmentDirections.actionChattingFragmentToImageVideoViewFragment(
                            data,
                            getString(R.string.chatfragment),
                            position
                        )
                    )
                }

            }
        }

    }


}