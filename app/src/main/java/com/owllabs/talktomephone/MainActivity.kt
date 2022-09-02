package com.owllabs.talktomephone

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.owllabs.talktomephone.activities.ConfigActivity
import com.owllabs.talktomephone.activities.ZoomInActivity
import com.owllabs.talktomephone.service.MyFirebaseMessagingService
import com.owllabs.talktomephone.service.VisitorService
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject
import okhttp3.*

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.doorbell_detail)

        askNotificationPermission();
        val sharedPreference: SharedPreferences = getSharedPreferences("config",Context.MODE_PRIVATE);
        //컴포넌트 호
        val backgroundScreen = findViewById<ConstraintLayout>(R.id.backgroundScreen)
        val menuNameTV = findViewById<TextView>(R.id.menuTextView);
        val detailTextView = findViewById<TextView>(R.id.detailTextView);
        val menuPictogramIV = findViewById<ImageView>(R.id.menuPictogramView)
        val zoomInTextView = findViewById<TextView>(R.id.zoomInTextView);
        val zoomInImageView = findViewById<ImageView>(R.id.zoomInImageView)
        val zoomInImageUrl  = "https://pbs.twimg.com/media/FbgBl0OUIAA1svF?format=png&name=small";
        Glide.with(this).load(zoomInImageUrl).into(zoomInImageView)

        val okHttpClient = OkHttpClient()

        val serverAddr:String? = sharedPreference.getString("server_address",null)
        if (serverAddr!=null){
            Log.d("server addr",serverAddr)
            var menuNameText:String? = ""
            var detailText:String? = ""
            try {
                Thread{
                    val categoryRequest = Request.Builder().url("${serverAddr}/doorbell/category/").get().build();
                    try {
                        okHttpClient.newCall(categoryRequest).execute().use{
                            response ->
                            var menuList = JSONArray(response.body()!!.string());
                            val fbService = MyFirebaseMessagingService();
                            for (i in 0 until menuList.length()-1) {
                                val menuItem = menuList.getJSONObject(i);
                                fbService.createNotificationChannel(
                                    "${menuItem.getString("id")}_category_notification",
                                    menuItem.getString("type"),
                                    "${menuItem.getString("type")}의 알림입니다.",
                                    menuItem.getString("vibration_pattern")
                                )
                            }

                        }

                    }catch (e:Exception){
                        throw Exception(e.message);
                    }

                }.start()


                Thread{
                    val doorbellRequest = Request.Builder().url("${serverAddr}/doorbell/visit/latest/").get().build();
                    try {
                        okHttpClient.newCall(doorbellRequest).execute().use{
                            response ->
                            val visitorResponse = response.body()!!.string()
                            Log.d("visitorResponse:",visitorResponse);
                            if (visitorResponse.length==0){
                                return@use;
                            }
                            val visitorObj = JSONObject(visitorResponse)

                            if (visitorObj.has("category")) {
                                Log.d("visitorObj:",visitorObj.getString("category"))

                                runOnUiThread {
                                    menuNameText = visitorObj.getString("category")
                                    detailText = visitorObj.getString("visit_reason")
                                    menuNameTV.text = menuNameText
                                    detailTextView.text = detailText;
                                    if (detailText != null) {
                                        zoomInTextView.setOnClickListener(
                                            zoomInClickListener(
                                                detailText!!
                                            )
                                        )
                                    }
                                }

                            }

                        }

                    }catch (e:Exception){
                        throw Exception(e.message);
                    }

                }.start()






            }catch (e:Exception){
                Toast.makeText(baseContext,"방문 카테고리 조회에 실패하였습니다. 설정 - 초인종의 ip주소를 다시한번 확인해주시고, 같은 무선랜으로 연결되어있는지 확인해 주세요.",Toast.LENGTH_LONG).show()

                Log.d("exception",e.message!!)
            }
        }





        val pictogramURL:String = "https://pbs.twimg.com/media/FbgAXppVUAEn9ae?format=png&name=900x900";
        Glide.with(this).load(pictogramURL).into(menuPictogramIV)


        val menuColorCode:String = "#FFFF00";
        backgroundScreen.setBackgroundColor(Color.parseColor(menuColorCode))



        val configBtn = findViewById<Button>(R.id.configBtn)
        configBtn.setOnClickListener(configClickListener())
        requestPermissionLauncher

    }

    inner class configClickListener:View.OnClickListener{
        override fun onClick(p0: View?) {
            Log.d("eventChecker","onClick!");
            val intent = Intent(this@MainActivity,ConfigActivity::class.java)
            startActivity(intent);
        }
    }

    inner class zoomInClickListener(val detailText:String):View.OnClickListener{
        override fun onClick(view: View) {
            Log.d("eventChecker","onClick!")
            val intent:Intent = Intent(this@MainActivity,ZoomInActivity::class.java)
            intent.putExtra("detailText",detailText);
            startActivity(intent);
        }
    }


    // Declare the launcher at the top of your Activity/Fragment:
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // FCM SDK (and your app) can post notifications.
        } else {
            // TODO: Inform user that that your app will not show notifications.
        }
    }
    private fun askNotificationPermission() {
//
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
//            PackageManager.PERMISSION_GRANTED
//        ) {
//            // FCM SDK (and your app) can post notifications.
//        } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
//            // TODO: display an educational UI explaining to the user the features that will be enabled
//            //       by them granting the POST_NOTIFICATION permission. This UI should provide the user
//            //       "OK" and "No thanks" buttons. If the user selects "OK," directly request the permission.
//            //       If the user selects "No thanks," allow the user to continue without notifications.
//        } else {
//            // Directly ask for the permission
//            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
//        }
    }


}