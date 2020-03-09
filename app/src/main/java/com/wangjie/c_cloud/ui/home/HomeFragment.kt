package com.wangjie.c_cloud.ui.home

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.fasterxml.jackson.databind.ObjectMapper
import com.wangjie.c_cloud.R
import com.wangjie.c_cloud.config.CloudConfig
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
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
        val textEdit: EditText = root.findViewById(R.id.edit_text1)

        homeViewModel.text.observe(this, Observer {
            textView.text = it
        })
        homeViewModel.postalCodeInput.observe(this, Observer {
            Toast.makeText(context, homeViewModel.postalCodeInput.value, Toast.LENGTH_SHORT).show()
        })


        textEdit.setOnClickListener { p0 ->
            var editView: EditText = p0 as EditText
            homeViewModel.addressInput.value = editView.text.toString()
            //                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }




        doSomeThing()
        return root


    }

    /**
     * 检查更新
     */
    private fun checkUpdate() {
        Thread(Runnable {
            //获取当前版本信息
            val packageManager = context?.packageManager
            val packageInfo = packageManager.getPackageInfo(context?.packageName, 0)
            val versionName = android.os.Build.VERSION

            // 请求更新接口
            val okHttpClient = OkHttpClient()
            val requestBody =
                CloudConfig.buildParamMeta(mutableMapOf("versionId" to versionName))
                    .toRequestBody("application/json; charset=utf-8".toMediaType())
            val request = Request.Builder().url(CloudConfig.UPDATE_API).post(requestBody).build()
            val call = okHttpClient.newCall(request)
            val execute = call.execute()
            val body = execute.body

            val objectMapper = ObjectMapper()
            if (body != null) {
                val readTree = objectMapper.readTree(body.string())
                val code = readTree.get("code").asInt()
                val updateTitle = readTree.get("data").get("updateTitle").asText()
                val updateInfo = readTree.get("data").get("updateInfo").asText()
                if (code == 0) {
                    Looper.prepare()
                    AlertDialog.Builder(context)
                        .setTitle(updateTitle)
                        .setMessage(updateInfo)
                        .setPositiveButton("更新", object : DialogInterface.OnClickListener {
                            override fun onClick(p0: DialogInterface?, p1: Int) {
                                Toast.makeText(context, "点击了更新", Toast.LENGTH_LONG).show()
                            }
                        })
                        .setNegativeButton("下次再说", object : DialogInterface.OnClickListener {
                            override fun onClick(p0: DialogInterface?, p1: Int) {
                                Toast.makeText(context, "点击了下次再说", Toast.LENGTH_LONG).show()
                            }
                        })
                        .show()
                    Looper.loop()
                }
            }

        }).start()


    }


}

