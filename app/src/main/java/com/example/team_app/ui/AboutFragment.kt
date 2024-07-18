package com.example.team_app.ui

import android.content.Intent
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
        val text = getString(R.string.about_text)
        binding.textViewAboutApp.text = Html.fromHtml(text, Html.FROM_HTML_MODE_LEGACY)
        binding.textViewAboutApp.movementMethod = LinkMovementMethod.getInstance()
    }

    private fun shareAppInfo() {
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "text/plain"
            putExtra(
                Intent.EXTRA_TEXT,
                getString(R.string.Share_text).trimIndent()
            )
        }
        startActivity(Intent.createChooser(shareIntent, getString(R.string.share_via)))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
