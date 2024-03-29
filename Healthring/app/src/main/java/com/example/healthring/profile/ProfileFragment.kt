package com.example.healthring.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.healthring.R
import com.example.healthring.databinding.ProfileFragmentBinding

class ProfileFragment : Fragment(R.layout.profile_fragment){

    private var binding : ProfileFragmentBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragmentBinding = ProfileFragmentBinding.inflate(inflater, container, false)
        binding = fragmentBinding
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.apply {
            lifecycleOwner = viewLifecycleOwner
            // pass instance of the viewmodel and ProfileFragment
            profileFragment = this@ProfileFragment
        }
    }

    fun goToHealthMonitorFragment() {
        findNavController().navigate(R.id.action_profileFragment_to_healthMonitorFragment)
    }

    fun navigateBack() {
        findNavController().navigateUp()
    }
}