package com.example.team_app.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.team_app.R
import com.example.team_app.databinding.AboutLayoutBinding

// Fragment to display the "About" information of the app
class AboutFragment : Fragment() {

    // Backing property for view binding
    private var _binding: AboutLayoutBinding? = null

    // Public property for accessing the binding, ensuring it's not null
    private val binding get() = _binding!!

    // Inflate the layout for this fragment
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Initialize the view binding
        _binding = AboutLayoutBinding.inflate(inflater, container, false)
        // Return the root view of the binding
        return binding.root
    }

    // Set up the view after it has been created
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set click listener for the share button
        binding.buttonShare.setOnClickListener {
            shareAppInfo()
        }

        // Handle the back press to navigate to the All Teams fragment
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().navigate(R.id.action_aboutFragment_to_allTeamsFragment)
        }
    }

    // Method to share app information using an intent
    private fun shareAppInfo() {
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "text/plain"
            putExtra(
                Intent.EXTRA_TEXT,
                getString(R.string.Share_text).trimIndent()
            )
        }
        // Start the share intent
        startActivity(Intent.createChooser(shareIntent, getString(R.string.share_via)))
    }

    // Clean up the view binding when the view is destroyed
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
