package com.example.tingzapp.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.tingzapp.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_splash_screen.*

@AndroidEntryPoint
class SplashScreen : BaseActivity() {
    override fun displayProgressBar(boolean: Boolean) {
        if (boolean){
            splash_progress_bar.visibility = View.VISIBLE
        }else{
            splash_progress_bar.visibility = View.GONE
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
    }

    //download the data
    //move to main activity
}