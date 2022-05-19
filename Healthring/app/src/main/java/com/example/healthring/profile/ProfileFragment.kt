package com.example.healthring.profile


import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.healthring.R
import com.example.healthring.databinding.ProfileFragmentBinding


import android.content.SharedPreferences
import android.util.Log
import com.google.android.material.snackbar.Snackbar
import android.graphics.Color


class ProfileFragment : Fragment(R.layout.profile_fragment) {

    private var binding: ProfileFragmentBinding? = null


    lateinit var sharedPreferences: SharedPreferences


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

        loadData()

        binding?.button?.setOnClickListener {
            saveData()
        }

    }

    fun goToHealthMonitorFragment() {
        findNavController().navigate(R.id.action_profileFragment_to_healthMonitorFragment)
    }

    fun navigateBack() {
        findNavController().navigateUp()
    }

    private fun saveData() {

        val profileNameInsertedText = binding?.insideProfileNameTextInput?.editableText?.toString()
        binding?.insideProfileNameTextInput?.setText(profileNameInsertedText.toString())

        val profileBirthdayInsertedText =
            binding?.insideProfileBirthdayTextInput?.editableText?.toString()
        binding?.insideProfileBirthdayTextInput?.setText(profileBirthdayInsertedText.toString())

        val profileHeightInsertedText =
            binding?.insideProfileHeightTextInput?.editableText?.toString()
        binding?.insideProfileHeightTextInput?.setText(profileHeightInsertedText.toString())

        val profileWeightInsertedText =
            binding?.insideProfileWeightTextInput?.editableText?.toString()
        binding?.insideProfileWeightTextInput?.setText(profileWeightInsertedText.toString())

        val profilePhoneInsertedText =
            binding?.insideProfilePhoneTextInput?.editableText?.toString()
        binding?.insideProfilePhoneTextInput?.setText(profilePhoneInsertedText.toString())

        val sharedPreferences = binding?.profileFragment?.activity?.getSharedPreferences(
            "sharedPref",
            Context.MODE_PRIVATE
        ) ?: return
        with(sharedPreferences.edit()) {

            binding?.profileGenderMaleButton?.isSelected?.apply { (Color.parseColor("#CD019C")) }.let {
                if (it != null) {
                    putBoolean("BOOLEAN_KEY1", it)
                }
            }



            putString("STRING_KEY1", profileNameInsertedText)
            putString("STRING_KEY2", profileBirthdayInsertedText)
            putString("STRING_KEY3", profileHeightInsertedText)
            putString("STRING_KEY4", profileWeightInsertedText)
            putString("STRING_KEY5", profilePhoneInsertedText)
            binding?.button?.isClickable?.let { putBoolean("BOOLEAN_KEY", it) }?.apply()
            Snackbar.make(binding?.root!!, "Data Saved!", Snackbar.LENGTH_SHORT).show()
        }

        val savedString = sharedPreferences.getString("STRING_KEY", null)
        Log.i("save String", "Saved Successful $savedString")

    }

    private fun loadData() {
        val sharedPreferences = binding?.profileFragment?.activity?.getSharedPreferences(
            "sharedPref",
            Context.MODE_PRIVATE
        ) ?: return
        val savedString1 = sharedPreferences.getString("STRING_KEY1", null)
        val savedString2 = sharedPreferences.getString("STRING_KEY2", null)
        val savedString3 = sharedPreferences.getString("STRING_KEY3", null)
        val savedString4 = sharedPreferences.getString("STRING_KEY4", null)
        val savedString5 = sharedPreferences.getString("STRING_KEY5", null)
        Log.i("save Load", "Saved Successful $savedString1")
        val savedBoolean = sharedPreferences.getBoolean("BOOLEAN_KEY", false)

        val savedBoolean1 = sharedPreferences.getBoolean("BOOLEAN_KEY1", false)

        binding?.insideProfileNameTextInput?.setText(savedString1.toString())
        binding?.insideProfileBirthdayTextInput?.setText(savedString2.toString())
        binding?.insideProfileHeightTextInput?.setText(savedString3.toString())
        binding?.insideProfileWeightTextInput?.setText(savedString4.toString())
        binding?.insideProfilePhoneTextInput?.setText(savedString5.toString())
        binding?.button?.isClickable = savedBoolean
    }

<<<<<<< HEAD
    fun gotToHealthMonitorFragment() {
        findNavController().navigate(R.id.action_profileFragment_to_healthMonitorFragment)
    }

=======
//    private fun genderButton() {
//        val sharedPreferences = binding?.profileFragment?.activity?.getSharedPreferences(
//            "sharedPref",
//            Context.MODE_PRIVATE
//        ) ?: return
//
//        val maleChosen = binding?.profileGenderMaleButton.
//    }
>>>>>>> ea00ba0 (update for pull)
}