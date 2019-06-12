package com.fialasfiasco.customdiceroller.ui.main

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.fialasfiasco.customdiceroller.R

class HeaderFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.header_preferences, rootKey)
    }
}