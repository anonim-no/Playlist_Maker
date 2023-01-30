package com.example.playlistmaker

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView

class SettingsActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        // открываем главное активити по нажатию на стрелку назад
        // правильно делать назад мы еще не умеем
        findViewById<ImageView>(R.id.back_to_main_activity).setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }
}