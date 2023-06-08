package com.example.playlistmaker.settings.ui

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.App
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private lateinit var viewModel: SettingsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.apply {
            setNavigationOnClickListener {
                finish()
            }
        }

        viewModel = ViewModelProvider(
            this,
            SettingsViewModel.getViewModelFactory(application as App)
        )[SettingsViewModel::class.java]

        binding.themeSwitcher
            .apply {
                isChecked = viewModel.isDarkThemeOn()
                setOnCheckedChangeListener { _, isChecked ->
                    viewModel.switchTheme(isChecked)
                }
            }

        binding.buttonSharing.apply {
            setOnClickListener {
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
        }

        binding.buttonSupport.apply {
            setOnClickListener {
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
        }

        binding.buttonTerms.apply {
            setOnClickListener {
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
}