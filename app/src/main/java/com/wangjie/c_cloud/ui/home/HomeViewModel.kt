package com.wangjie.c_cloud.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.fasterxml.jackson.databind.JsonNode

class HomeViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is home Fragment"
    }
    val text: LiveData<String> = _text


    /**
     * 公告相关
     */
    private val _noticeText = MutableLiveData<JsonNode>().apply {

    }
    val noticeText: MutableLiveData<JsonNode> = _noticeText

    fun setNoticeText(noticeText: JsonNode?) {
        _noticeText.postValue(noticeText)
    }
}