package com.jansir.kglide

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        KGlide.with(this).load("xxxx").into(findViewById(R.id.imageView1))
        KGlide.with(this).load("xxxx").into(findViewById(R.id.imageView2))
    }
}