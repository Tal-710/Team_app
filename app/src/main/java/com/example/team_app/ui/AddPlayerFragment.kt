package com.example.team_app.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.team_app.databinding.AddEditTeamLayoutBinding
import com.example.team_app.databinding.AddPlayerLayoutBinding
import com.example.team_app.databinding.AllTeamLayoutBinding

class AddPlayerFragment:Fragment() {

    private var _binding: AddPlayerLayoutBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = AddPlayerLayoutBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onDestroyView() {

        super.onDestroyView()
        _binding= null
    }
}
