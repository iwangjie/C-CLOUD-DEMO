package com.wangjie.c_cloud.config

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode

/**
 * 配置 C-CLOUD 服务器配置
 */
class CloudConfig() {

    companion object {

        val objectMapper = ObjectMapper();

        private const val CLOUD_SERVER = "http://192.168.0.101:8080/ccloud"

        private const val APP_ID = 1

        const val UPDATE_API = "$CLOUD_SERVER/api/update/version"


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