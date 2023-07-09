package com.example.playlistmaker.settings.ui

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentSettingsBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : Fragment() {

    private lateinit var binding: FragmentSettingsBinding

    private val viewModel by viewModel<SettingsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.themeSwitcher
            .apply {
                isChecked = viewModel.isDarkThemeOn()
                setOnCheckedChangeListener { _, isChecked ->
                    viewModel.switchTheme(isChecked)
                }
            }

        binding.buttonSharing.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND)
            intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.support_share_link))
            intent.type = "text/plain"
            try {
                startActivity(intent)
            } catch (ex: ActivityNotFoundException) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.settings_not_found_app),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        binding.buttonSupport.setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO)
            intent.data = Uri.parse("mailto:")
            intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.support_email)))
            intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.support_email_text))
            intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.support_email_subject))
            try {
                startActivity(intent)
            } catch (ex: ActivityNotFoundException) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.settings_not_found_app),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        binding.buttonTerms.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(getString(R.string.support_terms_link))
            try {
                startActivity(intent)
            } catch (ex: ActivityNotFoundException) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.settings_not_found_app),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

}