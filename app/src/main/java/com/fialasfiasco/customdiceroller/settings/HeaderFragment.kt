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

        val diceEditPreference = SwitchPreferenceCompat(context)
        diceEditPreference.key = getString(R.string.dice_edit_enabled_key)
        diceEditPreference.title = getString(R.string.dice_edit_enabled_title)
        diceEditPreference.setIcon(android.R.drawable.ic_menu_edit)
        diceEditPreference.setDefaultValue(resources.getBoolean(R.bool.dice_edit_enabled_default))
        diceEditPreference.summaryProvider = Preference.SummaryProvider<SwitchPreferenceCompat> {
            if(it.isChecked) {
                "Enabled - Long click on dice to remove"
            }
            else {
                "Disabled"
            }
        }
        generalCategory.addPreference(diceEditPreference)

        val itemsPerRowPreference = Preference(context)
        itemsPerRowPreference.key = getString(R.string.items_per_row_preference_key)
        itemsPerRowPreference.title = getString(R.string.items_per_row_preference_title)
        itemsPerRowPreference.fragment = getString(R.string.items_per_row_preference_fragment)
        itemsPerRowPreference.setIcon(android.R.drawable.ic_menu_crop)
        itemsPerRowPreference.summaryProvider = Preference.SummaryProvider<Preference> {
            val manager = PreferenceManager.getDefaultSharedPreferences(context)
            val simple = manager.getString(getString(R.string.items_per_row_simple_key),
                resources.getInteger(R.integer.items_per_row_simple_default).toString())

            val custom = manager.getString(getString(R.string.items_per_row_custom_key),
                resources.getInteger(R.integer.items_per_row_custom_default).toString())

            "Simple Roll - $simple\nCustom Roll - $custom"
        }
        generalCategory.addPreference(itemsPerRowPreference)

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