package com.wangjie.c_cloud.ui.gallery

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

class GalleryFragment : Fragment() {

    private lateinit var galleryViewModel: GalleryViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        galleryViewModel =
            ViewModelProviders.of(this).get(GalleryViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_gallery, container, false)
        val textView: TextView = root.findViewById(R.id.text_gallery)
        galleryViewModel.text.observe(this, Observer {
            textView.text = it
        })

        galleryViewModel.userName.observe(this, Observer {
            if (it != null) {
                val userName = it
                Toast.makeText(context, "欢迎您$userName", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(context, "您已经登出！", Toast.LENGTH_LONG).show()
            }
        })

        val userName: EditText = root.findViewById(R.id.user_name)
        val passWord: EditText = root.findViewById(R.id.password)
        val loginBtn: Button = root.findViewById(R.id.login_btn)

        loginBtn.setOnClickListener {
            login(userName.text.toString(), passWord.text.toString())
        }
        return root
    }

    /**
     * 登录
     */
    private fun login(userName: String, password: String) {
        Thread(Runnable {
            // 登录
            val param = CloudConfig.objectMapper.readTree("{}") as ObjectNode
            param.put("username",userName)
            param.put("password",password)
            val requestBody =
                CloudConfig.buildParamMeta(param)
                    .toRequestBody("application/json; charset=utf-8".toMediaType())
            val request =
                Request.Builder().url(CloudConfig.LOGIN_API).post(requestBody).build()
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
                galleryViewModel.setUserToken(respData.get("data").toString())
                galleryViewModel.setUserName(userName)
            }

        }).start()

    }
}