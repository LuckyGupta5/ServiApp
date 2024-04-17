package com.example.servivet.ui.main.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.servivet.ui.base.BaseViewModel
import com.example.servivet.utils.SingleLiveEvent

class SharedViewModel():BaseViewModel() {

    private val data: MutableLiveData<String> = SingleLiveEvent<String>()
    fun setData(newData: String) {
        data.value = newData
    }
    fun getData(): LiveData<String> {
        return data
    }
}