package com.example.servivet.ui.base

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel

open class BaseViewModel: ViewModel() {
    var isInternetAvailable: Boolean = false
    val job = Job()
    val coroutineScope = CoroutineScope(Dispatchers.IO + job)
    override fun onCleared() {
        super.onCleared()
        coroutineScope.cancel()
    }
}