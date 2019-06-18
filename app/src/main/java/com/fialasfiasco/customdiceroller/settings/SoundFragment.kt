package com.fialasfiasco.customdiceroller.settings

import android.os.Bundle
import androidx.preference.*
import com.fialasfiasco.customdiceroller.R

class SoundFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {

        val context = preferenceManager.context
        val screen = preferenceManager.createPreferenceScreen(context)

        val soundPreference = SwitchPreferenceCompat(context)
        soundPreference.key = getString(R.string.sound_enabled_key)
        soundPreference.title = getString(R.string.sound_enabled_title)
        soundPreference.setDefaultValue(getString(R.string.sound_enabled_default))
        screen.addPreference(soundPreference)

        val volumePreference = SeekBarPreference(context)
        volumePreference.key = getString(R.string.sound_volume_key)
        volumePreference.title = getString(R.string.sound_volume_title)
        volumePreference.setDefaultValue(getString(R.string.sound_volume_default))
        screen.addPreference(volumePreference)

        preferenceScreen = screen

        // Dependencies must be added after the preferenceScreen is set.
        volumePreference.dependency = soundPreference.key
    }
}