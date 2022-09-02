package com.owllabs.talktomephone.activities

import android.graphics.Color
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.owllabs.talktomephone.R

class ZoomInActivity:AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.zoom_in)
        val backgroundScreen = findViewById<ConstraintLayout>(R.id.backgroundScreen)
        backgroundScreen.setBackgroundColor(Color.parseColor("#FFFF00"))
        val zoomInDetailTextView = findViewById<TextView>(R.id.zoomInDetailTextView)
        var intent = intent;
        zoomInDetailTextView.text =intent.getStringExtra("detailText")

    }
}