package com.example.servivet.ui.main.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.media.MediaPlayer
import android.util.Log
import android.view.View
import android.webkit.WebSettings
import android.webkit.WebViewClient
import android.widget.MediaController
import com.bumptech.glide.Glide
import com.example.servivet.R
import com.example.servivet.databinding.CustomMediaLayoutBinding
import com.example.servivet.ui.base.BaseAdapter
import com.example.servivet.ui.main.fragment.ImageVideoViewFragment
import com.example.servivet.utils.downloadFile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MediaAdapter(
    val mediaList: List<ImageVideoViewFragment.MediaItem>,
    val requireContext: Context
) :
    BaseAdapter<CustomMediaLayoutBinding, ImageVideoViewFragment.MediaItem>(ArrayList()) {
    override val layoutId: Int = R.layout.custom_media_layout
    override fun bind(
        binding: CustomMediaLayoutBinding,
        item: ImageVideoViewFragment.MediaItem?,
        position: Int
    ) {
        bindMedia(mediaList[position], binding)
    }

    override fun getItemCount(): Int {
        return mediaList.size
    }


    @SuppressLint("SetJavaScriptEnabled")
    private fun bindMedia(
        mediaItem: ImageVideoViewFragment.MediaItem,
        binding: CustomMediaLayoutBinding
    ) {
        when (mediaItem.mediaType) {
            ImageVideoViewFragment.MediaType.IMAGE -> {
                binding.imageView.visibility = View.VISIBLE
                binding.videoView.visibility = View.GONE
                binding.idWebView.visibility = View.GONE
                Glide.with(requireContext).load(mediaItem.path).into(binding.imageView)
            }

            ImageVideoViewFragment.MediaType.VIDEO -> {
                binding.imageView.visibility = View.GONE
                binding.videoView.visibility = View.VISIBLE
                binding.idWebView.visibility = View.GONE
                val mediaController = MediaController(requireContext)
                mediaController.setAnchorView(binding.videoView)

                binding.videoView.setOnPreparedListener { mediaPlayer ->
                    // Autoplay the video when it's prepared
                    mediaPlayer.start()
                }
                binding.videoView.setMediaController(mediaController)
                binding.videoView.setVideoPath(mediaItem.path)
            }

            ImageVideoViewFragment.MediaType.FILE -> {


//                downloadFile(
//                    requireContext,
//                    mediaItem.path,
//                    "Notes",
//                    "pdf"
//                )

//                binding.imageView.visibility = View.GONE
//                binding.videoView.visibility = View.GONE
//                binding.idWebView.visibility = View.VISIBLE
//                val webSettings: WebSettings = binding.idWebView.settings
//                webSettings.javaScriptEnabled = true
//
//                // Load PDF URL
//                binding.idWebView.loadUrl("https://ride-chef-dev.s3.ap-south-1.amazonaws.com/Servivet/1708856573575-pdf-15.pdf")
//
//                // Force links and redirects to open in the WebView instead of in a browser
//                binding.idWebView.webViewClient = WebViewClient()
            }
        }

    }


}