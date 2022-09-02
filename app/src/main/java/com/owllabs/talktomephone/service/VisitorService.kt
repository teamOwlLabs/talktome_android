package com.owllabs.talktomephone.service

import android.util.Log
import okhttp3.*

import org.json.JSONArray
import org.json.JSONObject


class VisitorService(val address:String) {

    val okHttpClient = OkHttpClient()

    fun getLastVisitor():String{
        Log.d("getLastVisitor","start")
        val request = Request.Builder().url("${address}/doorbell/visit/latest/").get().build();
        try {
            okHttpClient.newCall(request).execute().use{
                response ->  Log.d("getLastVisitor", response.body()!!.string())
                return response.body()!!.string();
            }

        }catch (e:Exception){
            throw Exception(e.message);
        }
    }
    fun getMenuList():String{
        Log.d("getMenuList","start")

        val request = Request.Builder().url("${address}/doorbell/category/").get().build();
        try {
            okHttpClient.newCall(request).execute().use{
                    response ->  Log.d("getLastVisitor", response.body()!!.string())
                return response.body()!!.string();
            }
        }catch (e:Exception){
            throw Exception(e.message);
        }
    }
    fun postPushNotificationToken(token:String):JSONObject{
//        Log.d("pushNotification token register request","토큰 등록 시도")
//        val params = JSONObject()
//        params.put("token",token)
//        var result = JSONObject()
//        val request = JsonObjectRequest(
//            Request.Method.POST,"${address}/doorbell/token/",params,
//            Response.Listener { response ->
//                Log.d("pushNotification token register request","토큰 등록 성공")
//
//                result = response
//            },
//            Response.ErrorListener { response ->
//                Log.d("pushNotification token register request","토큰 등록 실패")
//
//                throw Error(response.message);
//
//            }
//        )
//        val queue = Volley.newRequestQueue(this).add(request)
//
//        return result
        return JSONObject()
    }
}