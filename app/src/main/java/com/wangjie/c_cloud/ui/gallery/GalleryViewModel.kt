package com.wangjie.c_cloud.ui.gallery

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.fasterxml.jackson.databind.JsonNode

class GalleryViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is gallery Fragment"
    }
    val text: LiveData<String> = _text


    private val _userToken = MutableLiveData<String>().apply {
    }
    val userInfo: LiveData<String> = _userToken


    fun setUserToken(userInfo: String) {
        _userToken.postValue(userInfo)
    }


    private val _userName = MutableLiveData<String>().apply {
    }
    val userName: LiveData<String> = _userName

    fun setUserName(userName: String) {
        _userName.postValue(userName)
    }

}