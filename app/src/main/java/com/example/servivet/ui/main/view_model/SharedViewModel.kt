package com.example.servivet.ui.main.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel():ViewModel() {

    private val data: MutableLiveData<String> = MutableLiveData()
    fun setData(newData: String) {
        data.value = newData
    }
    fun getData(): LiveData<String> {
        return data
    }
}