package com.fialasfiasco.customdiceroller.settings

import android.os.Bundle
import androidx.preference.*
import com.fialasfiasco.customdiceroller.R
import java.lang.ClassCastException

class SoundFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {

        val context = preferenceManager.context
        val screen = preferenceManager.createPreferenceScreen(context)

        val soundPreference = SwitchPreferenceCompat(context)
        soundPreference.key = getString(R.string.sound_enabled_key)
        soundPreference.title = getString(R.string.sound_enabled_title)
        soundPreference.setDefaultValue(resources.getBoolean(R.bool.sound_enabled_default))
        soundPreference.isIconSpaceReserved = false
        screen.addPreference(soundPreference)

        val volumePreference = SeekBarPreference(context)
        volumePreference.key = getString(R.string.sound_volume_key)
        volumePreference.title = getString(R.string.sound_volume_title)
        volumePreference.setDefaultValue(resources.getInteger(R.integer.sound_volume_default))
        volumePreference.isIconSpaceReserved = false
        screen.addPreference(volumePreference)

        preferenceScreen = screen

        // Dependencies must be added after the preferenceScreen is set.
        volumePreference.dependency = soundPreference.key
    }
}