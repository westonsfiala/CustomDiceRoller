package com.fialasfiasco.customdiceroller.settings

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SeekBarPreference
import androidx.preference.SwitchPreferenceCompat
import com.fialasfiasco.customdiceroller.R

class ShakeFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {

        val context = preferenceManager.context
        val screen = preferenceManager.createPreferenceScreen(context)

        val shakePreference = SwitchPreferenceCompat(context)
        shakePreference.key = getString(R.string.shake_enabled_key)
        shakePreference.title = getString(R.string.shake_enabled_title)
        shakePreference.setDefaultValue(getString(R.string.shake_enabled_default))
        screen.addPreference(shakePreference)

        val sensitivityPreference = SeekBarPreference(context)
        sensitivityPreference.key = getString(R.string.shake_sensitivity_key)
        sensitivityPreference.title = getString(R.string.shake_sensitivity_title)
        sensitivityPreference.setDefaultValue(getString(R.string.shake_sensitivity_default))
        screen.addPreference(sensitivityPreference)

        val durationPreference = SeekBarPreference(context)
        durationPreference.key = getString(R.string.shake_sensitivity_key)
        durationPreference.title = getString(R.string.shake_sensitivity_title)
        durationPreference.setDefaultValue(getString(R.string.shake_sensitivity_default))
        screen.addPreference(durationPreference)

        val holdPreference = SeekBarPreference(context)
        holdPreference.key = getString(R.string.hold_duration_key)
        holdPreference.title = getString(R.string.hold_duration_title)
        holdPreference.setDefaultValue(getString(R.string.hold_duration_default))
        screen.addPreference(holdPreference)

        preferenceScreen = screen

        // Dependencies must be added after the preferenceScreen is set.
        sensitivityPreference.dependency = shakePreference.key
        durationPreference.dependency = shakePreference.key
        holdPreference.dependency = shakePreference.key
    }
}