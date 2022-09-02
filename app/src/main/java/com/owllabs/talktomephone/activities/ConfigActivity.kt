package com.owllabs.talktomephone.activities

import android.content.ContentValues
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GetTokenResult
import com.google.firebase.messaging.FirebaseMessaging
import com.owllabs.talktomephone.R
import com.owllabs.talktomephone.service.VisitorService


class ConfigActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.config)
        val sharedPreference:SharedPreferences = getSharedPreferences("config",Context.MODE_PRIVATE);
        val storedServerAddress = sharedPreference.getString("server_address","");

        val pushTokenValueTextView = findViewById<TextView>(R.id.pushTokenValue);

        val serverAddress = findViewById<TextInputLayout>(R.id.serverLocationInput);

        serverAddress.hint  ="초인종의 IP주소를 입력하세요."
        serverAddress.placeholderText = storedServerAddress
        val serverAddressSubmitBtn = findViewById<Button>(R.id.serverSubmitBtn);
        serverAddressSubmitBtn.setOnClickListener(clickSubmitEventListener())

        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(ContentValues.TAG, "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result

            // Log and toast
            val msg = token
            Log.d(ContentValues.TAG, msg)
            pushTokenValueTextView.text = msg;
        })
    }
    inner class clickSubmitEventListener() : View.OnClickListener {

        override fun onClick(view: View) {
            var text:String = findViewById<TextInputLayout>(R.id.serverLocationInput).editText!!.text.toString()

            Log.d("onClick", "server address submit click${text}")
            val sharedPreference:SharedPreferences = getSharedPreferences("config",Context.MODE_PRIVATE);
            sharedPreference.edit().putString("server_address", text)
            .apply()
            val visitorService = VisitorService(text);
            try{
                val response = visitorService.postPushNotificationToken(findViewById<TextView>(R.id.pushTokenValue).text.toString())

            }catch (e:Exception){
                Toast.makeText(baseContext,"토큰 등록에 실패하였습니다. 초인종의 ip주소를 다시한번 확인해주시고, 같은 무선랜으로 연결되어있는지 확인해 주세요.",Toast.LENGTH_LONG).show()
            }
            Log.d("onClick", "서버 데이터 호출")

            Log.d("value Test", sharedPreference.getString("server_address", "")!!)
        }

    }

}