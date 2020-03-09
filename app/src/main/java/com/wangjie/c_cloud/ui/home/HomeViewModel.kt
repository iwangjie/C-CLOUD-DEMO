package com.wangjie.c_cloud.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel

class HomeViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is home Fragment"
    }

    val addressInput = MutableLiveData<String>()

    fun youfun(str: String) = "测试一下。。。。。${str}"

    val postalCodeInput: LiveData<String> = Transformations.map(addressInput, ::youfun)

    val text: LiveData<String> = _text


    fun setVal(value: String) {
        _text.postValue(value)
    }


}