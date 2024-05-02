package com.example.servivet.ui.main.bottom_sheet

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.servivet.R
import com.example.servivet.data.model.language_model.LanguageModel
import com.example.servivet.databinding.FragmentChangeLanguageBottomSheetBinding
import com.example.servivet.databinding.FragmentRatingUsBottomSheetBinding
import com.example.servivet.ui.base.BaseBottomSheetDailogFragment
import com.example.servivet.ui.main.adapter.LanguageAdapter
import com.example.servivet.ui.main.view_model.RateUseBottomSheetViewModel
import com.example.servivet.ui.main.view_model.SettingsViewModel
import com.example.servivet.ui.main.view_model.wallet.MyWalletBottomsheetViewModel
import com.example.servivet.utils.Session
import com.example.servivet.utils.setLocal

class ChangeLanguageBottomSheet : BaseBottomSheetDailogFragment<FragmentChangeLanguageBottomSheetBinding, MyWalletBottomsheetViewModel>(R.layout.fragment_change_language_bottom_sheet) {


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
                mViewModel.setLanguageChanged(true)
                findNavController().previousBackStackEntry?.savedStateHandle?.set("comeFrom","language")
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
        language.add(LanguageModel("Zulu", "Sawubona, ngingu-John Doe.", false, "zu"))
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


    private val onItemClick: (String,Int) -> Unit = { key,position ->

        when(language[position].language){
            "English"->{
                context.let { requireActivity().setLocal("en",2) }
                Session.saveIsLanguage("en")
                //   Session.saveIsLanguage(PreferenceEntity.LANGUAGE)
            }
            "Mandarin Chinese"->{
                context.let { requireActivity().setLocal("en",2)
                    //Session.saveIsLanguage("ch")
                }
                //  Session.saveIsLanguage(PreferenceEntity.LANGUAGE)

            }
            "Spanish"->{
                context.let { requireActivity().setLocal("en",2) }
                //   Session.saveIsLanguage(PreferenceEntity.LANGUAGE)

            }
            "Hindi"-> {
                context.let { requireActivity().setLocal("en",2) }
                Session.saveIsLanguage("hi")

            }
            "Arabic"-> {
                context.let { requireActivity().setLocal("en",2) }
                //  Session.saveIsLanguage(PreferenceEntity.LANGUAGE)
            }
            "French"-> {
                context.let { requireActivity().setLocal("en",2) }
                //   Session.saveIsLanguage(PreferenceEntity.LANGUAGE)
            }
            "Bengali"->
            {
                context.let { requireActivity().setLocal("en",2) }
                //  Session.saveIsLanguage(PreferenceEntity.LANGUAGE)

            }
            "Russian"->{
                context.let { requireActivity().setLocal("en",2) }
                // Session.saveIsLanguage(PreferenceEntity.LANGUAGE)

            }
            "Portuguese"-> {
                context.let { requireActivity().setLocal("en",2) }
                // Session.saveIsLanguage(PreferenceEntity.LANGUAGE)

            }
            "Urdu"->{
                context.let { requireActivity().setLocal("en",2) }
                //  Session.saveIsLanguage(PreferenceEntity.LANGUAGE)

            }
            "Indonesian"->{
                context.let { requireActivity().setLocal("en",2) }
                // Session.saveIsLanguage(PreferenceEntity.LANGUAGE)

            }
            "German"->{
                context.let { requireActivity().setLocal("en",2) }
                // Session.saveIsLanguage(PreferenceEntity.LANGUAGE)

            }
            "Japanese"->{
                context.let { requireActivity().setLocal("en",2) }
                //  Session.saveIsLanguage(PreferenceEntity.LANGUAGE)

            }
            "Swahili"->{
                context.let { requireActivity().setLocal("en",2) }
                //   Session.saveIsLanguage(PreferenceEntity.LANGUAGE)

            }
            "Korean"->{
                context.let { requireActivity().setLocal("en",2) }
                // Session.saveIsLanguage(PreferenceEntity.LANGUAGE)

            }
            "Zulu"->{
                context.let { requireActivity().setLocal("zu",2) }
                // Session.saveIsLanguage(PreferenceEntity.LANGUAGE)
                Session.saveIsLanguage("zu")
            }
            "Afrikaans"->{
                context.let { requireActivity().setLocal("en",2) }
                //  Session.saveIsLanguage(PreferenceEntity.LANGUAGE)

            }
            "Modern Standard Arabic"->{
                context.let { requireActivity().setLocal("en",2) }
                //  Session.saveIsLanguage(PreferenceEntity.LANGUAGE)

            }
            "Vietnamese"->{
                context.let { requireActivity().setLocal("en",2) }
                // Session.saveIsLanguage(PreferenceEntity.LANGUAGE)
            }
            "Telugu"->{
                context.let { requireActivity().setLocal("en",2) }
                // Session.saveIsLanguage(PreferenceEntity.LANGUAGE)
            }
            "Turkish"->{
                context.let { requireActivity().setLocal("en",2) }
                //  Session.saveIsLanguage(PreferenceEntity.LANGUAGE)
            }
            "Marathi"->{
                context.let { requireActivity().setLocal("en",2) }
                //  Session.saveIsLanguage(PreferenceEntity.LANGUAGE)
            }
            "Sesotho"->{
                context.let { requireActivity().setLocal("en",2) }
                // Session.saveIsLanguage(PreferenceEntity.LANGUAGE)
            }
            "Tamil"->{
                context.let { requireActivity().setLocal("en",2) }
                // Session.saveIsLanguage(PreferenceEntity.LANGUAGE)
            }
            "Romanian"->{
                context.let { requireActivity().setLocal("en",2) }
                // Session.saveIsLanguage(PreferenceEntity.LANGUAGE)
            }
            "Polish"->{
                context.let { requireActivity().setLocal("en",2) }
                //  Session.saveIsLanguage(PreferenceEntity.LANGUAGE)
            }
            "Ukrainian"->{
                context.let { requireActivity().setLocal("en",2) }
                //  Session.saveIsLanguage(PreferenceEntity.LANGUAGE)
            }
            "Italian"->{
                context.let { requireActivity().setLocal("en",2) }
                //  Session.saveIsLanguage(PreferenceEntity.LANGUAGE)
            }
            "Thai"->{
                context.let { requireActivity().setLocal("en",2) }
                // Session.saveIsLanguage(PreferenceEntity.LANGUAGE)
            }
            "Filipino"->{
                context.let { requireActivity().setLocal("en",2) }
                // Session.saveIsLanguage(PreferenceEntity.LANGUAGE)
            }
            "Dutch"->{
                context.let { requireActivity().setLocal("en",2) }
                // Session.saveIsLanguage(PreferenceEntity.LANGUAGE)
            }

            else->{
                Toast.makeText(requireContext(),"new one is  ${language[position].language.toString()}",
                    Toast.LENGTH_LONG).show()
            }
        }
        if(language[position].language=="English"){
        }
        requireActivity().setLocal(key,2)

    }



}