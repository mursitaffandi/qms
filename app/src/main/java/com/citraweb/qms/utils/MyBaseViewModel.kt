package com.citraweb.qms.utils

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

open class MyBaseViewModel : ViewModel() {
    protected val _echo = MutableLiveData<String?>()
    val toast: LiveData<String?>
        get() = _echo

    private val _loading = MutableLiveData(false)
    val spinner: LiveData<Boolean>
        get() = _loading

    fun launchDataLoad(block: suspend () -> Unit): Job {
        return viewModelScope.launch {
            try {
                _loading.value = true
                block()
            } catch (error: Throwable) {
                _echo.value = error.message
            } finally {
                _loading.value = false
            }
        }
    }

    fun onToastShown() {
        _echo.value = null
    }
}