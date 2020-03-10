package com.wangjie.c_cloud.ui.tools

import android.os.Bundle
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.fasterxml.jackson.databind.node.ObjectNode
import com.wangjie.c_cloud.R
import com.wangjie.c_cloud.config.CloudConfig
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

class ToolsFragment : Fragment() {

    private lateinit var toolsViewModel: ToolsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        toolsViewModel =
            ViewModelProviders.of(this).get(ToolsViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_tools, container, false)
        val textView: TextView = root.findViewById(R.id.text_tools)
        toolsViewModel.text.observe(this, Observer {
            textView.text = it
        })


        val sendBtn: Button = root.findViewById(R.id.send_btn)
        val emailEdit: EditText = root.findViewById(R.id.email_edit)
        val contentEdit: EditText = root.findViewById(R.id.content_edit)
        sendBtn.setOnClickListener {
            sendMessage(emailEdit.text.toString(), contentEdit.text.toString())
        }


        return root
    }


    private fun sendMessage(email: String, content: String) {
        Thread(Runnable {

            // 登录
            val param = CloudConfig.objectMapper.readTree("{}") as ObjectNode
            param.put("email",email)
            param.put("messageContent",content)
            val requestBody =
                CloudConfig.buildParamMeta(param)
                    .toRequestBody("application/json; charset=utf-8".toMediaType())
            val request =
                Request.Builder().url(CloudConfig.MESSAGE_API).post(requestBody).build()
            val call = CloudConfig.okHttpClient.newCall(request)
            val execute = call.execute()
            val body = execute.body
            val objectNode = ObjectNode(null)
            val respData = CloudConfig.objectMapper.readTree(body?.string())
            val code = respData.get("code").asInt()
            val msg = respData.get("msg").asText()
            if (code != 0) {
                if (code == -1) {
                    Looper.prepare()
                    Toast.makeText(context, "暂无数据", Toast.LENGTH_SHORT).show()
                    Looper.loop()
                } else {
                    Looper.prepare()
                    Toast.makeText(context, "出错了~ $msg，错误代码：$code", Toast.LENGTH_SHORT).show()
                    Looper.loop()
                }
            } else {
                Looper.prepare()
            Toast.makeText(context, "感谢反馈，您已经反馈成功啦~", Toast.LENGTH_SHORT).show()
                Looper.loop()
            }


        }).start()
    }


}