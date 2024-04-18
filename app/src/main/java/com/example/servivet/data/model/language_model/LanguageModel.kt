package com.example.servivet.data.model.language_model

import com.example.servivet.utils.interfaces.ListAdapterItem

data class LanguageModel(var language:String="",var message:String="",var isSelected:Boolean=false,var tag:String=""): ListAdapterItem

