package com.example.playlistmaker

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        // закрываем SettingsActivity и возвращаемся на предыдущее
        findViewById<ImageView>(R.id.back_to_main_activity).setOnClickListener {
            finish()
        }

        // Поделиться - текст-ссылка
        findViewById<Button>(R.id.button_sharing).setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND)
            intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.support_share_link))
            intent.type = "text/plain"
            try {
                startActivity(intent)
            } catch (ex: ActivityNotFoundException) {
                Toast.makeText(
                    this@SettingsActivity,
                    getString(R.string.settings_not_found_app),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        // Обратиться в техподдержку - отправляем в почту
        findViewById<Button>(R.id.button_support).setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO)
            intent.data = Uri.parse("mailto:")
            intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.support_email)))
            intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.support_email_text))
            intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.support_email_subject))
            try {
                startActivity(intent)
            } catch (ex: ActivityNotFoundException) {
                Toast.makeText(
                    this@SettingsActivity,
                    getString(R.string.settings_not_found_app),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        // Пользовательское соглашение - отправляем в браузер
        findViewById<Button>(R.id.button_terms).setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(getString(R.string.support_terms_link))
            try {
                startActivity(intent)
            } catch (ex: ActivityNotFoundException) {
                Toast.makeText(
                    this@SettingsActivity,
                    getString(R.string.settings_not_found_app),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }


    }
}