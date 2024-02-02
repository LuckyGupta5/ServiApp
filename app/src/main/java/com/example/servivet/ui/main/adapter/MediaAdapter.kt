package com.example.servivet.ui.main.adapter

import android.content.Context
import android.media.MediaPlayer
import android.util.Log
import android.view.View
import com.bumptech.glide.Glide
import com.example.servivet.R
import com.example.servivet.databinding.CustomMediaLayoutBinding
import com.example.servivet.ui.base.BaseAdapter
import com.example.servivet.ui.main.fragment.ImageVideoViewFragment
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
        binding.playPauseButton.setOnClickListener {
            togglePlayPause(binding)
        }
    }

    override fun getItemCount(): Int {
        return mediaList.size
    }


    private fun bindMedia(
        mediaItem: ImageVideoViewFragment.MediaItem,
        binding: CustomMediaLayoutBinding
    ) {
        when (mediaItem.mediaType) {
            ImageVideoViewFragment.MediaType.IMAGE -> {
                binding.imageView.visibility = View.VISIBLE
                binding.videoView.visibility = View.GONE
                binding.playPauseButton.visibility = View.GONE
                binding.seekBar.visibility = View.GONE
                Glide.with(requireContext).load(mediaItem.path).into(binding.imageView)
            }

            ImageVideoViewFragment.MediaType.VIDEO -> {
                binding.imageView.visibility = View.GONE
                binding.videoView.visibility = View.VISIBLE
                binding.playPauseButton.visibility = View.VISIBLE
                binding.seekBar.visibility = View.VISIBLE
                binding.videoView.setVideoPath(mediaItem.path)
                binding.videoView.setOnPreparedListener(MediaPlayer.OnPreparedListener { mp ->
                    mp.isLooping = false
                    mp.start()
                    Log.e("TAG", "232bindMedia: $mp", )
                    binding.seekBar.max = mp.duration

                    CoroutineScope(Dispatchers.Main).launch {
                        delay(1000)
                        binding.seekBar.progress = mp.currentPosition
                    }

                })
            }
        }

    }

    private fun togglePlayPause(binding: CustomMediaLayoutBinding) {
        if (binding.videoView.isPlaying) {
            binding.videoView.pause()
            binding.playPauseButton.setImageResource(android.R.drawable.ic_media_play)
        } else {
            binding.videoView.start()
            binding.playPauseButton.setImageResource(android.R.drawable.ic_media_pause)
        }
    }


}