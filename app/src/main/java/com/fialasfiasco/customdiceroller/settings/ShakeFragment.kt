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
        shakePreference.setDefaultValue(resources.getBoolean(R.bool.shake_enabled_default))
        shakePreference.isIconSpaceReserved = false
        screen.addPreference(shakePreference)

        val sensitivityPreference = SeekBarPreference(context)
        sensitivityPreference.key = getString(R.string.shake_sensitivity_key)
        sensitivityPreference.title = getString(R.string.shake_sensitivity_title)
        sensitivityPreference.setDefaultValue(resources.getInteger(R.integer.shake_sensitivity_default))
        sensitivityPreference.isIconSpaceReserved = false
        screen.addPreference(sensitivityPreference)

        val durationPreference = SeekBarPreference(context)
        durationPreference.key = getString(R.string.shake_duration_key)
        durationPreference.title = getString(R.string.shake_duration_title)
        durationPreference.setDefaultValue(resources.getInteger(R.integer.shake_duration_default))
        durationPreference.isIconSpaceReserved = false
        screen.addPreference(durationPreference)

        val holdPreference = SeekBarPreference(context)
        holdPreference.key = getString(R.string.hold_duration_key)
        holdPreference.title = getString(R.string.hold_duration_title)
        holdPreference.setDefaultValue(resources.getInteger(R.integer.hold_duration_default))
        holdPreference.isIconSpaceReserved = false
        screen.addPreference(holdPreference)

        preferenceScreen = screen

        // Dependencies must be added after the preferenceScreen is set.
        sensitivityPreference.dependency = shakePreference.key
        durationPreference.dependency = shakePreference.key
        holdPreference.dependency = shakePreference.key
    }
}