package com.example.servivet.ui.main.fragment

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.servivet.R
import com.example.servivet.data.model.language_model.LanguageModel
import com.example.servivet.data.model.language_model.Savelanguage
import com.example.servivet.databinding.FragmentChoosePreferredLanguageBinding
import com.example.servivet.ui.base.BaseFragment
import com.example.servivet.ui.main.adapter.LanguageAdapter
import com.example.servivet.ui.main.view_model.ChooseLanguageViewModel
import com.example.servivet.ui.main.view_model.SplashViewModel
import com.example.servivet.utils.CommonUtils
import com.example.servivet.utils.PreferenceEntity
import com.example.servivet.utils.Session
import com.example.servivet.utils.setLocal
import com.hbb20.CountryCodePicker.Language
import kotlin.math.log


class ChoosePreferredLanguageFragment : BaseFragment<FragmentChoosePreferredLanguageBinding,ChooseLanguageViewModel>(R.layout.fragment_choose_preferred_language) {

    override val binding: FragmentChoosePreferredLanguageBinding by viewBinding(FragmentChoosePreferredLanguageBinding::bind)
    override val mViewModel: ChooseLanguageViewModel by viewModels()
    private var language=ArrayList<LanguageModel>()
    private var savelanguage=LanguageModel()
    private var savePosition=Session.position


    private lateinit var sharedPreferences: SharedPreferences


    override fun isNetworkAvailable(boolean: Boolean) {

    }

    override fun setupViewModel() {
    }

    private fun setViewPagerAdapter() {

    }

    override fun setupViews() {

        binding.apply {
            lifecycleOwner=viewLifecycleOwner
            viewModel=mViewModel
            click=mViewModel.ClickAction(requireContext(),requireActivity())
        }
        Log.i("TAG", "setupViews890: "+Session.language)
        if(Session.language.isNullOrEmpty()||Session.language=="language"){
            activity?.setLocal("en",2)
            Session.saveIsLanguage("en")
        }else{

            activity?.setLocal(Session.language,2)
        }
        mViewModel.saveLanguage.tag=Session.language
        setBack()
//        checkLanguage()
        setViewPagerAdapter()
        addData()
        sharedPreferences=requireActivity().getPreferences(Context.MODE_PRIVATE)

    }

    override fun setupObservers() {

    }

    private fun  addData() {
        language.clear()
        language.add(LanguageModel( "English", "Hi, I am John Doe.",false,"en"))
        language.add(LanguageModel( "Mandarin Chinese", "嗨，我是約翰·多伊。",false,"zh"))
        language.add(LanguageModel( "Spanish", "Spanish",false,"es"))
        language.add(LanguageModel( "Hindi", "नमस्ते, मैं जॉन डो हूं।", false,"hi"))
        language.add(LanguageModel( "Arabic", "مرحبًا، أنا جون دو.",false,"ar"))
        language.add(LanguageModel( "French", "Bonjour, je m'appelle John Doe.",false,"fr"))
        language.add(LanguageModel( "Bengali", "হাই, আমি জন ডো",false,"bn"))
        language.add(LanguageModel( "Russian", "Привет, я Джон Доу.",false,"ru"))
        language.add(LanguageModel( "Portuguese", "Olá, eu sou John Doe.",false,"pt"))
        language.add(LanguageModel( "Urdu", "ہیلو، میں جان ڈو ہوں۔",false,"ur"))
        language.add(LanguageModel( "Indonesian", "Hai, saya John Doe.",false,"in"))
        language.add(LanguageModel( "German", "Hallo, ich bin John Doe.",false,"de"))
        language.add(LanguageModel( "Japanese", "こんにちは、ジョン・ドゥです。",false,"ja"))
        language.add(LanguageModel( "Swahili", "Habari, mimi ni John Doe.",false,"sw"))
        language.add(LanguageModel( "Korean", "안녕하세요, 저는 John Doe입니다.",false,"ko"))
        language.add(LanguageModel( "Zulu", "Sawubona, ngingu-John Doe.",false,"zu"))
        language.add(LanguageModel( "Afrikaans", "Hallo, ek is John Doe.",false,"af"))
        language.add(LanguageModel( "Modern Standard Arabic", "مرحبًا، أنا جون دو.",false,"ar"))
        language.add(LanguageModel( "Vietnamese", "Xin chào, tôi là John Doe.",false,"vi"))
        language.add(LanguageModel( "Telugu", "హాయ్, నేను జాన్ డో.",false,"te"))
        language.add(LanguageModel( "Turkish", "Merhaba, ben John Doe.",false,"tr"))
        language.add(LanguageModel( "Marathi", "हाय, मी जॉन डो आहे.",false,"mr"))
        language.add(LanguageModel( "Sesotho", "Lumela, ke John Doe.",false,"st"))
        language.add(LanguageModel( "Tamil", "வணக்கம், நான் ஜான் டோ.",false,"ta"))
        language.add(LanguageModel( "Romanian", "Bună, sunt John Doe.",false,"ro"))
        language.add(LanguageModel( "Polish", "Cześć, jestem John Doe.",false,"pl"))
        language.add(LanguageModel( "Ukrainian", "Привіт, я Джон Доу.",false,"uk"))
        language.add(LanguageModel( "Italian", "Ciao, sono John Doe.",false,"it"))
        language.add(LanguageModel( "Thai", "สวัสดี ฉันชื่อจอห์น โด",false,"th"))
        language.add(LanguageModel( "Filipino", "Hi, ako si John Doe.",false,"fil"))
        language.add(LanguageModel( "Dutch", "Hallo, ik ben John Doe.",false,"nl"))
//        Log.i("TAG", "addData: 123456"+language.getOrNull(Session.position)?.tag)
        requireActivity().setLocal(language.getOrNull(Session.position)?.tag ?: "", 2)
        binding.languagerecycler.adapter= LanguageAdapter(requireContext(),language,onItemClick,2)

    }

    private val onItemClick:(String,Int)-> Unit=
        {
            key,position->
            Log.i("TAG", "onitemclick567: "+language[position])
            mViewModel.saveLanguage=language[position]
            mViewModel.savePosition=position

        }



    fun setBack()
    {
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true)
            {
                override fun handleOnBackPressed()
                {
                    val dialog = AlertDialog.Builder(activity)
                    dialog.setMessage(requireContext().getString(R.string.are_you_logout))
                    dialog.setTitle(requireContext().resources.getString(R.string.app_name))
                    dialog.setPositiveButton("Ok") { dialog, which ->
                        dialog.dismiss()
                        activity?.finishAffinity()
                    }
                    dialog.setNegativeButton("Cancel") { dialog, which ->
                        dialog.dismiss()
                    }

                    dialog.show()
                }
            }
        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, callback)
    }
}