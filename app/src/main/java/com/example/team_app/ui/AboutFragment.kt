package com.example.team_app.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Html
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.team_app.R
import com.example.team_app.databinding.AboutLayoutBinding

class AboutFragment : Fragment() {

    private var _binding: AboutLayoutBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = AboutLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonShare.setOnClickListener {
            shareAppInfo()
        }

        // Enable back button to navigate back to AllTeamsFragment
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().navigate(R.id.action_aboutFragment_to_allTeamsFragment)
        }

        setupHyperlink()
    }

    private fun setupHyperlink() {
        val text = "This team management app is designed with MVVM architecture and utilizes Room DB for local storage and Firebase for sending emails. It allows users to manage teams, add players, and connect emails. Coaches and team managers can easily keep track of their teams and players with this app."
        binding.textViewAboutApp.text = Html.fromHtml(text, Html.FROM_HTML_MODE_LEGACY)
        binding.textViewAboutApp.movementMethod = LinkMovementMethod.getInstance()
    }

    private fun shareAppInfo() {
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "text/plain"
            putExtra(
                Intent.EXTRA_TEXT,
                """
                Check out this amazing team management app! 
                It uses MVVM architecture, Room DB for local storage, and Firebase for sending emails. 
                Manage your teams, add players, and connect emails easily. 
                Ideal for coaches and team managers.
                For more details, visit our GitHub repository: https://github.com/Tal-710/Team_app
                """.trimIndent()
            )
        }
        startActivity(Intent.createChooser(shareIntent, "Share via"))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
