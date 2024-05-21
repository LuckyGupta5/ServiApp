package com.example.servivet.data.model

import com.example.servivet.ui.main.fragment.AddServiceFragment
import com.example.servivet.utils.interfaces.ListAdapterItem

data class CustomeServiceModeData(
    var type: String? = null,
    var isSelected: Boolean? = null,
    /*  var sessionTimeList: ArrayList<ServiceListSlot>? = null,*/
    var daysList:ArrayList<AddServiceFragment.Days>?=null,
    var previousPosition:Int?=null,
    var price:String=""

) : ListAdapterItem