package com.example.playlistmaker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView

class SearchActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        // закрываем SearchActivity и возвращаемся на предыдущее
        findViewById<ImageView>(R.id.back_to_main_activity).setOnClickListener {
            finish()
        }

    }
}