package com.example.servivet.ui.main.fragment

import android.util.Log
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.servivet.R
import com.example.servivet.databinding.FragmentImageVideoViewBinding
import com.example.servivet.ui.base.BaseFragment
import com.example.servivet.ui.main.adapter.MediaAdapter
import com.example.servivet.ui.main.view_model.SharedViewModel
import com.example.servivet.utils.interfaces.ListAdapterItem
import com.google.gson.Gson
import java.io.Serializable

class ImageVideoViewFragment() :
    BaseFragment<FragmentImageVideoViewBinding, SharedViewModel>(R.layout.fragment_image_video_view) {

    override val binding: FragmentImageVideoViewBinding by viewBinding(FragmentImageVideoViewBinding::bind)
    override val mViewModel: SharedViewModel by viewModels()
    private val mediaList: ImageVideoViewFragmentArgs by navArgs()
    private var list = ArrayList<String>()

    private lateinit var mediaAdapter: MediaAdapter

    override fun isNetworkAvailable(boolean: Boolean) {
    }

    override fun setupViewModel() {
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            binding.clickEvents = ::onClick
        }
    }

    override fun setupViews() {
        getMediaList()
        initAdapter()
    }

    private fun initAdapter() {


        val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.idMediaRecycle.layoutManager = layoutManager
        val snapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(binding.idMediaRecycle)
        mediaAdapter = MediaAdapter(createMediaItemList(list), requireContext())
        binding.idMediaRecycle.adapter = mediaAdapter
            binding.idMediaRecycle.scrollToPosition(mediaList.position)
    }

    override fun setupObservers() {
    }


    private fun onClick(type: String) {
        when (type) {
            getString(R.string.back_press) ->
                findNavController().popBackStack()
        }

    }

    private fun getMediaList() {
        val data = Gson().fromJson(mediaList.data, Array<String>::class.java)
        list.addAll(data)

    }

    private fun createMediaItemList(urlList: ArrayList<String>): List<MediaItem> {
        return urlList.map { url ->
            val mediaType = determineMediaType(url)
            MediaItem(mediaType, url)
        }
    }

    private fun determineMediaType(url: String): MediaType {
        return when {
            url.endsWith(".jpg") || url.endsWith(".jpeg") || url.endsWith(".png") -> MediaType.IMAGE
            url.endsWith(".mp4") -> MediaType.VIDEO
            else -> MediaType.IMAGE // Handle other media types as needed
        }
    }


    enum class MediaType { IMAGE, VIDEO }

    data class MediaItem(val mediaType: MediaType, val path: String) : ListAdapterItem, Serializable


}