package com.wangjie.c_cloud.config

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import okhttp3.OkHttpClient

/**
 * 配置 C-CLOUD 服务器配置
 */
class CloudConfig() {

    companion object {

        /**
         * 全局 HttpClient
         */
        val okHttpClient = OkHttpClient()

        /**
         * 全局 ObjectMapper
         */
        val objectMapper = ObjectMapper();

        private const val CLOUD_SERVER = "http://192.168.0.101:8080/ccloud"

        private const val APP_ID = 1

        // 登录
        const val LOGIN_API = "$CLOUD_SERVER/api/login"

        // 检查更新
        const val UPDATE_API = "$CLOUD_SERVER/api/update/version"

        // 公告
        const val NOTICE_API = "$CLOUD_SERVER/api/notice/last"

        // 留言
        const val MESSAGE_API = "$CLOUD_SERVER/api/feedback/save"


        /**
         * 向 APP 添加 元参数
         */
        fun buildParamMeta(param: JsonNode): String {
            val objectNode = param as ObjectNode
            objectNode.put("appId", APP_ID)
            return objectNode.toString()
        }

    }


}