package com.example.servivet.ui.main.bottom_sheet

import android.content.Intent
import android.util.Log
import androidx.fragment.app.viewModels
import com.example.servivet.R
import com.example.servivet.data.model.language_model.LanguageModel
import com.example.servivet.databinding.FragmentChangeLanguageBottomSheetBinding
import com.example.servivet.ui.base.BaseBottomSheetDailogFragment
import com.example.servivet.ui.main.activity.HomeActivity
import com.example.servivet.ui.main.adapter.LanguageAdapter
import com.example.servivet.ui.main.view_model.wallet.MyWalletBottomsheetViewModel
import com.example.servivet.utils.Constants.COME_FROM
import com.example.servivet.utils.Session
import com.example.servivet.utils.setLocal

class ChangeLanguageBottomSheet :
    BaseBottomSheetDailogFragment<FragmentChangeLanguageBottomSheetBinding, MyWalletBottomsheetViewModel>(
        R.layout.fragment_change_language_bottom_sheet
    ) {
    override val mViewModel: MyWalletBottomsheetViewModel by viewModels()
    override fun getLayout() = R.layout.fragment_change_language_bottom_sheet
    private var language = ArrayList<LanguageModel>()
    private var saveLanguage = LanguageModel()
    private var selectedLanguage: String = Session.language
    private var selectedPosition: Int = Session.position
    override fun isNetworkAvailable(boolean: Boolean) {

    }

    override fun setupViewModel() {
    }

    override fun setupViews() {
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = mViewModel
            click = mViewModel.ClickAction(requireContext())
            binding.clickEvent = ::onClick
        }
        addData()
        setupRecyclerView()
    }

    private fun onClick(value: Int) {
        when (value) {
            0 -> {}

            1 -> {
                Log.d("TAG", "onClick:  ${saveLanguage.tag}")
                Session.saveIsLanguage(selectedLanguage)
                Session.savePosition(selectedPosition)
                mViewModel.setLanguageChanged(true)
                context.let { requireActivity().setLocal(selectedLanguage, 2) }
                val i = Intent(requireContext(), HomeActivity::class.java)
                i.putExtra(COME_FROM,"language")
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(i)
                dialog?.dismiss()
            }
        }
    }

    override fun setupObservers() {

    }

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

    }

    fun setupRecyclerView() {
        binding.languagerecycler.adapter =
            LanguageAdapter(requireContext(), language, onItemClick, 0)
    }

    private val onItemClick: (String, Int) -> Unit = { key, position ->
        selectedLanguage = key
        saveLanguage = language[position]
        selectedPosition = position
        binding.languagerecycler.adapter?.notifyDataSetChanged()
    }
}