package com.example.servivet.ui.main.fragment

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.servivet.R
import com.example.servivet.data.model.call_module.video_call.VideoCallResponse
import com.example.servivet.data.model.chat_models.initiate_chat.InitiateChatResponse
import com.example.servivet.data.model.chat_models.manual_chating_objest.ManualUserDataClass
import com.example.servivet.data.model.user_profile.response.UserProfile
import com.example.servivet.databinding.FragmentProviderProfileBinding
import com.example.servivet.ui.base.BaseFragment
import com.example.servivet.ui.main.view_model.ConnectionRequestViewModel
import com.example.servivet.ui.main.view_model.ProfileViewModel
import com.example.servivet.utils.CommonUtils
import com.example.servivet.utils.CommonUtils.showSnackBar
import com.example.servivet.utils.ProcessDialog
import com.example.servivet.utils.SocketManager
import com.example.servivet.utils.Status
import com.example.servivet.utils.StatusCode
import com.google.gson.Gson
import io.socket.client.Socket
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject


class ProviderProfileFragment :
    BaseFragment<FragmentProviderProfileBinding, ProfileViewModel>(R.layout.fragment_provider_profile) {
    override val binding: FragmentProviderProfileBinding by viewBinding(
        FragmentProviderProfileBinding::bind
    )
    override val mViewModel: ProfileViewModel by viewModels()
    private val requestViewModel: ConnectionRequestViewModel by viewModels()
    private val argumentData: ProviderProfileFragmentArgs by navArgs()
    private lateinit var profileData: UserProfile
    private var bussinessType: Int? = null
    private var callType = 7
    private lateinit var socket: Socket
    private lateinit var initateChatResponse: InitiateChatResponse
    private lateinit var videoCallResponse: VideoCallResponse
    private var mediaList: ArrayList<String> = ArrayList()
    private lateinit var roomId: String
    private var manualUserDataClass = ManualUserDataClass()


    override fun isNetworkAvailable(boolean: Boolean) {
    }

    override fun setupViewModel() {
        getArgumentData()
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = mViewModel
            click = mViewModel.ClickAction(requireActivity(), requireContext())
            clickEvents = ::onClick
        }
        initRequestViewModel()
    }


    private fun getArgumentData() {

    }

    override fun setupViews() {
    }


    private fun onClick(type: String) {
        when (type) {
            getString(R.string.back_press) -> {
                findNavController().popBackStack()
            }

            getString(R.string.services) -> {
                //  findNavController().navigate(R.id.action_providerProfileFragment_to_myServiceFragment)
                findNavController().navigate(
                    ProviderProfileFragmentDirections.actionProviderProfileFragmentToMyServiceFragment(
                        profileData._id, bussinessType = bussinessType.toString(),
                        from = getString(
                            R.string.provider_profile
                        )
                    )
                )
            }

            getString(R.string.call) -> {
                val intent = Intent(Intent.ACTION_DIAL)
                intent.data = Uri.parse("tel:${7830203108}")
                startActivity(intent)


            }

            getString(R.string.whatsApp) -> {
                manualUserDataClass.userName = profileData.name
                manualUserDataClass.image = profileData.image

                if (roomId.isNotEmpty()) {
                    generateAgoraToken()
                } else {
                    initInitiateChatSocket()
                }

//                val url = "https://api.whatsapp.com/send?phone=$7830203108"
//                val i = Intent(Intent.ACTION_VIEW)
//                i.data = Uri.parse(url)
//
//                try {
//                    startActivity(i)
//                } catch (e: ActivityNotFoundException) {
//                    Toast.makeText(
//                        requireContext(),
//                        "Whatsapp app not installed in your phone",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                    e.printStackTrace()
//                }

            }

            getString(R.string.connect) -> {
                requestViewModel.getConnectionRequest(profileData, argumentData.data)
            }

            getString(R.string.message) -> {
                findNavController().navigate(
                    ProviderProfileFragmentDirections.actionProviderProfileFragmentToChattingFragment(
                        Gson().toJson(profileData),
                        getString(
                            R.string.provider_profile
                        ),
                        false
                    )
                )
            }


            getString(R.string.share) -> {
                val shareIntent = Intent()
                shareIntent.action = Intent.ACTION_SEND
                shareIntent.type = "text/plain"
                shareIntent.putExtra(Intent.EXTRA_TEXT, "Share Via this that");
                startActivity(Intent.createChooser(shareIntent, "Share via"))

            }
        }
    }

    private fun generateAgoraToken() {
        socket = SocketManager.getSocket()
        val data = JSONObject()
        try {
            data.put("receiverId", profileData._id)
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


                            findNavController().navigate(
                                ProviderProfileFragmentDirections.actionProviderProfileFragmentToOutgoingVideoCallActivity(
                                    Gson().toJson(videoCallResponse), getString(
                                        R.string.outgoing_video
                                    ), Gson().toJson(manualUserDataClass)
                                )
                            )


                            socket.off("agoraToken")


                        } catch (ex: JSONException) {
                            ex.printStackTrace()
                        }
                    }
                }
            })
        } catch (ex: JSONException) {
        }
    }


    private fun initInitiateChatSocket() {
        try {
            socket = SocketManager.getSocket()
            val data = JSONObject()
            data.put("receiverId", profileData._id)
            data.put("messageType", 7)
            data.put("message", "")
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
                            generateAgoraToken()

                        } catch (ex: JSONException) {
                            ex.printStackTrace()
                        }
                    }
                }
            })

        } catch (ex: Exception) {

        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun setupObservers() {
        userProfileApi()

        mViewModel.userProfileResponse.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    ProcessDialog.dismissDialog()
                    when (it.data?.code) {
                        StatusCode.STATUS_CODE_SUCCESS -> {
                            profileData = it.data.result.profile
                            binding.detailData = profileData
                            roomId = profileData.roomId
                            bussinessType = it.data.result.profile.businessType
                            Log.e("TAG", "setupObservers: ${Gson().toJson(profileData)}")
                        }

                        StatusCode.STATUS_CODE_FAIL -> {
                            showSnackBar(it.data.message)
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

    private fun userProfileApi() {
        mViewModel.hitUserProfileApi(
            argumentData.data,
            0,
            requireContext(),
            requireActivity(),
            false
        )
    }


    private fun initRequestViewModel() {

        requestViewModel.getConnectionRequestData().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    ProcessDialog.dismissDialog()
                    when (it.data?.code) {
                        StatusCode.STATUS_CODE_SUCCESS -> {

                            showSnackBar(it.data.message)
                            userProfileApi()
                        }

                        StatusCode.STATUS_CODE_FAIL -> {
                            showSnackBar(it.data.message)
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


}