package com.myth.diaryapp.fragments

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.myth.diaryapp.R

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }
}