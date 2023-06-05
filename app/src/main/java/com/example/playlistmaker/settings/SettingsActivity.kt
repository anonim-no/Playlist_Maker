package com.example.playlistmaker.settings

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.App
import com.example.playlistmaker.R
import com.google.android.material.switchmaterial.SwitchMaterial

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        // свитч смены темы
        val themeSwitcher = findViewById<SwitchMaterial>(R.id.themeSwitcher)
        // отмечаем свитч включеным, если тема темная
        if ((applicationContext as App).darkTheme) themeSwitcher.isChecked = true
        // слушатель смены темы
        themeSwitcher.setOnCheckedChangeListener { _, checked ->
            (applicationContext as App).switchTheme(checked)
        }

        // закрываем SettingsActivity и возвращаемся на предыдущее
        findViewById<Toolbar>(R.id.toolbar).setNavigationOnClickListener {
            finish()
        }

        // Поделиться - текст-ссылка
        findViewById<Button>(R.id.buttonSharing).setOnClickListener {
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
        findViewById<Button>(R.id.buttonSupport).setOnClickListener {
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
        findViewById<Button>(R.id.buttonTerms).setOnClickListener {
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