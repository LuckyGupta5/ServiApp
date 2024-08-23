package com.example.servivet.ui.main.fragment

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.servivet.R
import com.example.servivet.data.model.language_model.LanguageModel
import com.example.servivet.databinding.FragmentChoosePreferredLanguageBinding
import com.example.servivet.ui.base.BaseFragment
import com.example.servivet.ui.main.adapter.LanguageAdapter
import com.example.servivet.ui.main.view_model.ChooseLanguageViewModel
import com.example.servivet.utils.Session
import com.example.servivet.utils.setLocal


class ChoosePreferredLanguageFragment :
    BaseFragment<FragmentChoosePreferredLanguageBinding, ChooseLanguageViewModel>(R.layout.fragment_choose_preferred_language) {

    override val binding: FragmentChoosePreferredLanguageBinding by viewBinding(
        FragmentChoosePreferredLanguageBinding::bind
    )
    override val mViewModel: ChooseLanguageViewModel by viewModels()
    private var language = ArrayList<LanguageModel>()
    private lateinit var sharedPreferences: SharedPreferences


    override fun isNetworkAvailable(boolean: Boolean) {}

    override fun setupViewModel() {}

    private fun setViewPagerAdapter() {}

    override fun setupViews() {

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = mViewModel
            click = mViewModel.ClickAction(requireContext(), requireActivity())
        }
        Log.i("TAG", "setupViews890: " + Session.language)
        if (Session.language.isNullOrEmpty() || Session.language == "language") {
            activity?.setLocal("en", 2)
            Session.saveIsLanguage("en")
        } else {
            activity?.setLocal(Session.language, 2)
        }
        mViewModel.saveLanguage.tag = Session.language
        setBack()
        setViewPagerAdapter()
        addData()
        sharedPreferences = requireActivity().getPreferences(Context.MODE_PRIVATE)
    }

    override fun setupObservers() {}

    private fun addData() {
        val selectedLan = Session.language
        language.clear()
        language.add(
            LanguageModel(
                language = "English",
                message = "Hi, I am John Doe.",
                isSelected = selectedLan == "en",
                tag = "en"
            )
        )
        language.add(
            LanguageModel(
                language = "Mandarin Chinese",
                message = "嗨，我是約翰·多伊。",
                isSelected = selectedLan == "zh",
                tag = "zh"
            )
        )
        language.add(
            LanguageModel(
                language = "Spanish",
                message = "Spanish",
                isSelected = selectedLan == "es",
                tag = "es"
            )
        )
        language.add(
            LanguageModel(
                language = "Hindi",
                message = "नमस्ते, मैं जॉन डो हूं।",
                isSelected = selectedLan == "hi",
                "hi"
            )
        )
        language.add(
            LanguageModel(
                "Arabic",
                "مرحبًا، أنا جون دو.",
                isSelected = selectedLan == "ar",
                "ar"
            )
        )
        language.add(
            LanguageModel(
                "French",
                "Bonjour, je m'appelle John Doe.",
                isSelected = selectedLan == "fr",
                "fr"
            )
        )
        language.add(
            LanguageModel(
                "Bengali",
                "হাই, আমি জন ডো",
                isSelected = selectedLan == "bn",
                "bn"
            )
        )
        language.add(
            LanguageModel(
                "Russian",
                "Привет, я Джон Доу.",
                isSelected = selectedLan == "ru",
                "ru"
            )
        )
        language.add(
            LanguageModel(
                "Portuguese",
                "Olá, eu sou John Doe.",
                isSelected = selectedLan == "pt",
                "pt"
            )
        )
        language.add(
            LanguageModel(
                "Urdu",
                "ہیلو، میں جان ڈو ہوں۔",
                isSelected = selectedLan == "ur",
                "ur"
            )
        )
        language.add(
            LanguageModel(
                "Indonesian",
                "Hai, saya John Doe.",
                isSelected = selectedLan == "in",
                "in"
            )
        )
        language.add(
            LanguageModel(
                "German",
                "Hallo, ich bin John Doe.",
                isSelected = selectedLan == "de",
                "de"
            )
        )
        language.add(
            LanguageModel(
                "Japanese",
                "こんにちは、ジョン・ドゥです。",
                isSelected = selectedLan == "ja",
                "ja"
            )
        )
        language.add(
            LanguageModel(
                "Swahili",
                "Habari, mimi ni John Doe.",
                isSelected = selectedLan == "sw",
                "sw"
            )
        )
        language.add(LanguageModel("Korean", "안녕하세요, 저는 John Doe입니다.", false, "ko"))
        language.add(
            LanguageModel(
                "Zulu",
                "Sawubona, ngingu-John Doe.",
                isSelected = selectedLan == "zu",
                "zu"
            )
        )
        language.add(
            LanguageModel(
                "Afrikaans",
                "Hallo, ek is John Doe.",
                isSelected = selectedLan == "af",
                "af"
            )
        )
        language.add(
            LanguageModel(
                "Modern Standard Arabic",
                "مرحبًا، أنا جون دو.",
                isSelected = selectedLan == "ar",
                "ar"
            )
        )
        language.add(
            LanguageModel(
                "Vietnamese",
                "Xin chào, tôi là John Doe.",
                isSelected = selectedLan == "vi",
                "vi"
            )
        )
        language.add(
            LanguageModel(
                "Telugu",
                "హాయ్, నేను జాన్ డో.",
                isSelected = selectedLan == "te",
                "te"
            )
        )
        language.add(
            LanguageModel(
                "Turkish",
                "Merhaba, ben John Doe.",
                isSelected = selectedLan == "tr",
                "tr"
            )
        )
        language.add(
            LanguageModel(
                "Marathi",
                "हाय, मी जॉन डो आहे.",
                isSelected = selectedLan == "mr",
                "mr"
            )
        )
        language.add(
            LanguageModel(
                "Sesotho",
                "Lumela, ke John Doe.",
                isSelected = selectedLan == "st",
                "st"
            )
        )
        language.add(
            LanguageModel(
                "Tamil",
                "வணக்கம், நான் ஜான் டோ.",
                isSelected = selectedLan == "ta",
                "ta"
            )
        )
        language.add(
            LanguageModel(
                "Romanian",
                "Bună, sunt John Doe.",
                isSelected = selectedLan == "ro",
                "ro"
            )
        )
        language.add(
            LanguageModel(
                "Polish",
                "Cześć, jestem John Doe.",
                isSelected = selectedLan == "pl",
                "pl"
            )
        )
        language.add(
            LanguageModel(
                "Ukrainian",
                "Привіт, я Джон Доу.",
                isSelected = selectedLan == "uk",
                "uk"
            )
        )
        language.add(
            LanguageModel(
                "Italian",
                "Ciao, sono John Doe.",
                isSelected = selectedLan == "it",
                "it"
            )
        )
        language.add(
            LanguageModel(
                "Thai",
                "สวัสดี ฉันชื่อจอห์น โด",
                isSelected = selectedLan == "th",
                "th"
            )
        )
        language.add(
            LanguageModel(
                language = "Filipino",
                message = "Hi, ako si John Doe.",
                isSelected = selectedLan == "fil",
                "fil"
            )
        )
        language.add(
            LanguageModel(
                "Dutch",
                "Hallo, ik ben John Doe.",
                isSelected = selectedLan == "nl",
                "nl"
            )
        )
        requireActivity().setLocal(language.getOrNull(Session.position)?.tag ?: "", 2)
        binding.languagerecycler.adapter =
            LanguageAdapter(requireContext(), language, onItemClick, 2)

    }

    private val onItemClick: (String, Int) -> Unit = { key, position ->
        Log.i("TAG", "onitemclick567: " + language[position])
        mViewModel.saveLanguage = language[position]
        mViewModel.savePosition = position
    }

    private fun setBack() {
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    val dialog = AlertDialog.Builder(activity)
                    dialog.setMessage(requireContext().getString(R.string.are_you_logout))
                    dialog.setTitle(requireContext().resources.getString(R.string.app_name))
                    dialog.setPositiveButton("Ok") { dialog, _ ->
                        dialog.dismiss()
                        activity?.finishAffinity()
                    }
                    dialog.setNegativeButton("Cancel") { dialog, _ ->
                        dialog.dismiss()
                    }

                    dialog.show()
                }
            }
        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, callback)
    }
}