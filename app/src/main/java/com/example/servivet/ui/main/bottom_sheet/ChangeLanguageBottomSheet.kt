package com.example.servivet.ui.main.bottom_sheet

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.example.servivet.R
import com.example.servivet.data.model.language_model.LanguageModel
import com.example.servivet.databinding.FragmentChangeLanguageBottomSheetBinding
import com.example.servivet.databinding.FragmentRatingUsBottomSheetBinding
import com.example.servivet.ui.base.BaseBottomSheetDailogFragment
import com.example.servivet.ui.main.adapter.LanguageAdapter
import com.example.servivet.ui.main.view_model.RateUseBottomSheetViewModel
import com.example.servivet.ui.main.view_model.wallet.MyWalletBottomsheetViewModel

class ChangeLanguageBottomSheet :
    BaseBottomSheetDailogFragment<FragmentChangeLanguageBottomSheetBinding, MyWalletBottomsheetViewModel>(
        R.layout.fragment_change_language_bottom_sheet
    ) {
    override val mViewModel: MyWalletBottomsheetViewModel by viewModels()
    override fun getLayout() = R.layout.fragment_change_language_bottom_sheet

    private var language = ArrayList<LanguageModel>()


    override fun isNetworkAvailable(boolean: Boolean) {
    }

    override fun setupViewModel() {
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = mViewModel
            click = mViewModel.ClickAction(requireContext())
            binding.clickEvent = ::onClick

        }
        addData()


    }

    override fun setupViews() {

    }

    private fun onClick(value: Int) {
        when (value) {
            0 -> {

            }

            1 -> {
                dialog?.dismiss()
            }
        }
    }


    override fun setupObservers() {
    }


    private fun addData() {

        language.add(LanguageModel("English", "Hi, I am John Doe.", true, ""))
        language.add(LanguageModel("Mandarin Chinese", "嗨，我是約翰·多伊。", false, ""))
        language.add(LanguageModel("Spanish", "Spanish", false, ""))
        language.add(LanguageModel("Hindi", "नमस्ते, मैं जॉन डो हूं।", false, ""))
        language.add(LanguageModel("Arabic", "مرحبًا، أنا جون دو.", false, ""))
        language.add(LanguageModel("French", "Bonjour, je m'appelle John Doe.", false, ""))
        language.add(LanguageModel("Bengali", "হাই, আমি জন ডো", false, ""))
        language.add(LanguageModel("Russian", "Привет, я Джон Доу.", false, ""))
        language.add(LanguageModel("Portuguese", "Olá, eu sou John Doe.", false, ""))
        language.add(LanguageModel("Urdu", "ہیلو، میں جان ڈو ہوں۔", false, ""))
        language.add(LanguageModel("Indonesian", "Hai, saya John Doe.", false, ""))
        language.add(LanguageModel("German", "Hallo, ich bin John Doe.", false, ""))
        language.add(LanguageModel("Japanese", "こんにちは、ジョン・ドゥです。", false, ""))
        language.add(LanguageModel("Swahili", "Habari, mimi ni John Doe.", false, ""))
        language.add(LanguageModel("Korean", "안녕하세요, 저는 John Doe입니다.", false, ""))
        language.add(LanguageModel("Zulu", "Sawubona, ngingu-John Doe.", false, ""))
        language.add(LanguageModel("Afrikaans", "Hallo, ek is John Doe.", false, ""))
        language.add(LanguageModel("Modern Standard Arabic", "مرحبًا، أنا جون دو.", false, ""))
        language.add(LanguageModel("Vietnamese", "Xin chào, tôi là John Doe.", false, ""))
        language.add(LanguageModel("Telugu", "హాయ్, నేను జాన్ డో.", false, ""))
        language.add(LanguageModel("Turkish", "Merhaba, ben John Doe.", false, ""))
        language.add(LanguageModel("Marathi", "हाय, मी जॉन डो आहे.", false, ""))
        language.add(LanguageModel("Sesotho", "Lumela, ke John Doe.", false, ""))
        language.add(LanguageModel("Tamil", "வணக்கம், நான் ஜான் டோ.", false, ""))
        language.add(LanguageModel("Romanian", "Bună, sunt John Doe.", false, ""))
        language.add(LanguageModel("Polish", "Cześć, jestem John Doe.", false, ""))
        language.add(LanguageModel("Ukrainian", "Привіт, я Джон Доу.", false, ""))
        language.add(LanguageModel("Italian", "Ciao, sono John Doe.", false, ""))
        language.add(LanguageModel("Thai", "สวัสดี ฉันชื่อจอห์น โด", false, ""))
        language.add(LanguageModel("Filipino", "Hi, ako si John Doe.", false, ""))
        language.add(LanguageModel("Dutch", "Hallo, ik ben John Doe.", false, ""))

        binding.languagerecycler.adapter = LanguageAdapter(requireContext(), language, onItemClick)

    }

    private val onItemClick: (String) -> Unit = { key ->

    }


}