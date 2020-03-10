package com.wangjie.c_cloud.util

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Looper
import android.text.Html
import android.widget.Toast
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import com.wangjie.c_cloud.BuildConfig
import com.wangjie.c_cloud.config.CloudConfig
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

class UpdateUtil {

    companion object {
        /**
         * 检查更新
         */
        fun checkUpdate(context: Context) {
            Thread(Runnable {
                //获取当前版本信息
                val versionCode = BuildConfig.VERSION_CODE.toString()

                // 请求更新接口
                val okHttpClient = OkHttpClient()
                val param = CloudConfig.objectMapper.readTree("{}") as ObjectNode
                param.put("versionId", versionCode)
                val requestBody =
                    CloudConfig.buildParamMeta(param)
                        .toRequestBody("application/json; charset=utf-8".toMediaType())
                val request =
                    Request.Builder().url(CloudConfig.UPDATE_API).post(requestBody).build()
                val call = okHttpClient.newCall(request)
                val execute = call.execute()
                val body = execute.body
                val objectMapper = ObjectMapper()

                // 解析更新结果
                if (body == null) {
                    return@Runnable
                }
                val updateRespBody = objectMapper.readTree(body.string())
                val code = updateRespBody.get("code").asInt()
                if (code == -1) {
                    // 最新版本
                    Looper.prepare()
                    Toast.makeText(context, "当前版本为最新版本", Toast.LENGTH_SHORT).show()
                    Looper.loop()
                }
                if (code != 0) {
                    // 检查更新出错了
                    Looper.prepare()
                    Toast.makeText(context, "检查更新出错，错误码：$code", Toast.LENGTH_SHORT).show()
                    Looper.loop()
                }
                // 获取更新标题和更新内容
                val it = updateRespBody.get("data")

                if (it.has("id")) {
                    val updateTitle = it.get("updateTitle").asText()
                    val updateInfo = it.get("updateInfo").asText()

                    val updateAlert = AlertDialog.Builder(context)
                    updateAlert
                        .setTitle(updateTitle)
                        .setMessage(Html.fromHtml(updateInfo, Html.FROM_HTML_MODE_COMPACT))
                        .setPositiveButton("更新", object : DialogInterface.OnClickListener {
                            override fun onClick(p0: DialogInterface?, p1: Int) {
                                // 跳转浏览器
                                val url = it.get("updateUrl").asText()
                                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                context.startActivity(browserIntent)
                            }
                        })
                    if (it.get("updateForced").asInt() == 0) {
                        updateAlert.setNegativeButton(
                            "下次再说"
                        ) { _, _ -> Toast.makeText(context, "点击了下次再说", Toast.LENGTH_LONG).show() }
                    }
                    Looper.prepare()
                    updateAlert.show()
                    Looper.loop()
                }
            }).start()

        }
    }

}