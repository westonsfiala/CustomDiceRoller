package com.fialasfiasco.customdiceroller.settings

import android.os.Bundle
import androidx.preference.*
import com.fialasfiasco.customdiceroller.R

class HeaderFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {

        val context = preferenceManager.context
        val screen = preferenceManager.createPreferenceScreen(context)

        val generalCategory = PreferenceCategory(context)
        generalCategory.key = getString(R.string.general_category_key)
        generalCategory.title = getString(R.string.general_category_title)
        generalCategory.isIconSpaceReserved = false
        screen.addPreference(generalCategory)

        val sortType = ListPreference(context)
        sortType.key = getString(R.string.sort_type_key)
        sortType.title = getString(R.string.sort_type_title)
        sortType.summaryProvider = ListPreference.SimpleSummaryProvider.getInstance()
        sortType.setEntries(R.array.sort_entries)
        sortType.setEntryValues(R.array.sort_values)
        sortType.setDefaultValue(getString(R.string.sort_type_default))
        sortType.setIcon(android.R.drawable.ic_menu_sort_by_size)
        generalCategory.addPreference(sortType)

        val shakeCategory = PreferenceCategory(context)
        shakeCategory.key = getString(R.string.shake_category_key)
        shakeCategory.title = getString(R.string.shake_category_title)
        shakeCategory.isIconSpaceReserved = false
        screen.addPreference(shakeCategory)

        val shakePreference = Preference(context)
        shakePreference.key = getString(R.string.shake_preference_key)
        shakePreference.title = getString(R.string.shake_preference_title)
        shakePreference.fragment = getString(R.string.shake_preference_fragment)
        shakePreference.setIcon(android.R.drawable.ic_menu_always_landscape_portrait)
        shakePreference.summaryProvider = Preference.SummaryProvider<Preference> {
            val manager = PreferenceManager.getDefaultSharedPreferences(context)
            val shakeEnabled = manager.getBoolean(getString(R.string.shake_enabled_key),
                                                           resources.getBoolean(R.bool.shake_enabled_default))

            if (shakeEnabled) {
                "Enabled"
            } else {
                "Disabled"
            }
        }
        shakeCategory.addPreference(shakePreference)

        val soundPreference = Preference(context)
        soundPreference.key = getString(R.string.sound_preference_key)
        soundPreference.title = getString(R.string.sound_preference_title)
        soundPreference.fragment = getString(R.string.sound_preference_fragment)
        soundPreference.setIcon(android.R.drawable.ic_lock_silent_mode_off)
        soundPreference.summaryProvider = Preference.SummaryProvider<Preference> {
            val manager = PreferenceManager.getDefaultSharedPreferences(context)
            val soundEnabled = manager.getBoolean(getString(R.string.sound_enabled_key),
                resources.getBoolean(R.bool.sound_enabled_default))

            if (soundEnabled) {
                "Enabled"
            } else {
                "Disabled"
            }
        }
        shakeCategory.addPreference(soundPreference)

        preferenceScreen = screen
    }
}