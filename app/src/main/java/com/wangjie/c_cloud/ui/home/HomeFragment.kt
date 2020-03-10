package com.wangjie.c_cloud.ui.home

import android.os.Bundle
import android.os.Looper
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.fasterxml.jackson.databind.node.ObjectNode
import com.wangjie.c_cloud.R
import com.wangjie.c_cloud.config.CloudConfig
import com.wangjie.c_cloud.config.CloudConfig.Companion.objectMapper
import com.wangjie.c_cloud.config.CloudConfig.Companion.okHttpClient
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody


class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        homeViewModel =
            ViewModelProviders.of(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        val textView: TextView = root.findViewById(R.id.text_home)

        val noticeTitleText: TextView = root.findViewById(R.id.notice_title_text)
        val noticeInfoText: TextView = root.findViewById(R.id.notice_info_text)

        homeViewModel.text.observe(this, Observer {
            textView.text = it
        })


        // 公告监听器
        homeViewModel.noticeText.observe(this, Observer {
            if (it != null) {
                val noticeTitle = it.get("noticeTitle").asText()
                val noticeInfo = it.get("noticeInfo").asText()
                noticeTitleText.text = Html.fromHtml(noticeTitle, Html.FROM_HTML_MODE_COMPACT)
                noticeInfoText.text = Html.fromHtml(noticeInfo, Html.FROM_HTML_MODE_COMPACT)
            } else {
                noticeTitleText.text = Html.fromHtml("暂无公告", Html.FROM_HTML_MODE_COMPACT)
                noticeInfoText.text = Html.fromHtml("", Html.FROM_HTML_MODE_COMPACT)
            }

        })

        textView.setOnClickListener {
            val textView1 = it as TextView
            textView1.text = "正在重新获取公告"
            getNoticeInfo()
            textView1.text = "获取成功"
        }
        getNoticeInfo()

        return root
    }

    /**
     * 查询公告
     */
    private fun getNoticeInfo() {
        Thread(Runnable {
            // 查询公告
            val param = CloudConfig.objectMapper.readTree("{}") as ObjectNode
            val requestBody =
                CloudConfig.buildParamMeta(param)
                    .toRequestBody("application/json; charset=utf-8".toMediaType())
            val request =
                Request.Builder().url(CloudConfig.NOTICE_API).post(requestBody).build()
            val call = okHttpClient.newCall(request)
            val execute = call.execute()
            val body = execute.body
            val objectNode = ObjectNode(null)
            val respData = objectMapper.readTree(body?.string())
            val code = respData.get("code").asInt()
            if (code == -1) {
                Looper.prepare()
                Toast.makeText(context, "暂无公告", Toast.LENGTH_SHORT).show()
                homeViewModel.setNoticeText(null)
                Looper.loop()
            } else {
                homeViewModel.setNoticeText(respData.get("data"))
            }
        }).start()

    }

}

