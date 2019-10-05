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

        val sortType = DropDownPreference(context)
        sortType.key = getString(R.string.sort_type_key)
        sortType.title = getString(R.string.sort_type_title)
        sortType.summaryProvider = ListPreference.SimpleSummaryProvider.getInstance()
        sortType.setEntries(R.array.sort_entries)
        sortType.setEntryValues(R.array.sort_values)
        sortType.setDefaultValue(getString(R.string.sort_type_default))
        sortType.setIcon(R.drawable.ic_sort)
        generalCategory.addPreference(sortType)

        val itemsPerRowPreference = DropDownPreference(context)
        itemsPerRowPreference.key = getString(R.string.items_per_row_preference_key)
        itemsPerRowPreference.title = getString(R.string.items_per_row_preference_title)
        itemsPerRowPreference.summaryProvider = ListPreference.SimpleSummaryProvider.getInstance()
        itemsPerRowPreference.setEntries(R.array.items_per_row_entries)
        itemsPerRowPreference.setEntryValues(R.array.items_per_row_values)
        itemsPerRowPreference.setDefaultValue(resources.getInteger(R.integer.items_per_row_default).toString())
        itemsPerRowPreference.setIcon(R.drawable.ic_resize)
        generalCategory.addPreference(itemsPerRowPreference)

        val diceEditPreference = SwitchPreferenceCompat(context)
        diceEditPreference.key = getString(R.string.dice_edit_enabled_key)
        diceEditPreference.title = getString(R.string.dice_edit_enabled_title)
        diceEditPreference.setIcon(R.drawable.ic_pencil)
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

        val enableRollProperties = SwitchPreferenceCompat(context)
        enableRollProperties.key = getString(R.string.roll_properties_enable_key)
        enableRollProperties.title = getString(R.string.roll_properties_enable_title)
        enableRollProperties.summaryProvider = Preference.SummaryProvider<SwitchPreferenceCompat> {
            if(it.isChecked) {
                "Enabled"
            }
            else {
                "Disabled"
            }
        }
        enableRollProperties.setDefaultValue(resources.getBoolean(R.bool.roll_properties_enable_default))
        enableRollProperties.setIcon(R.drawable.ic_gear_wrench)
        generalCategory.addPreference(enableRollProperties)

        val dieTheme = Preference(context)
        dieTheme.title = getString(R.string.die_theme_title)
        dieTheme.fragment = getString(R.string.die_theme_fragment)
        dieTheme.summaryProvider = Preference.SummaryProvider<Preference> {
            val manager = PreferenceManager.getDefaultSharedPreferences(context)
            manager.getString(getString(R.string.die_theme_key), getString(R.string.white_theme))
        }
        dieTheme.setIcon(R.drawable.ic_palette)
        generalCategory.addPreference(dieTheme)

        val shakeCategory = PreferenceCategory(context)
        shakeCategory.key = getString(R.string.shake_category_key)
        shakeCategory.title = getString(R.string.shake_category_title)
        shakeCategory.isIconSpaceReserved = false
        screen.addPreference(shakeCategory)

        val shakePreference = Preference(context)
        shakePreference.key = getString(R.string.shake_preference_key)
        shakePreference.title = getString(R.string.shake_preference_title)
        shakePreference.fragment = getString(R.string.shake_preference_fragment)
        shakePreference.setIcon(R.drawable.ic_rolling_dice_cup)
        shakeCategory.addPreference(shakePreference)

        val soundPreference = Preference(context)
        soundPreference.key = getString(R.string.sound_preference_key)
        soundPreference.title = getString(R.string.sound_preference_title)
        soundPreference.fragment = getString(R.string.sound_preference_fragment)
        soundPreference.setIcon(R.drawable.ic_speaker_on)
        shakeCategory.addPreference(soundPreference)

        val advancedCategory = PreferenceCategory(context)
        advancedCategory.key = getString(R.string.advanced_category_key)
        advancedCategory.title = getString(R.string.advanced_category_title)
        advancedCategory.isIconSpaceReserved = false
        screen.addPreference(advancedCategory)

        val showAverage = SwitchPreferenceCompat(context)
        showAverage.key = getString(R.string.show_dice_roll_average_enable_key)
        showAverage.title = getString(R.string.show_dice_roll_average_enable_title)
        showAverage.summaryProvider = Preference.SummaryProvider<SwitchPreferenceCompat> {
            if(it.isChecked) {
                "Enabled"
            }
            else {
                "Disabled"
            }
        }
        showAverage.setDefaultValue(resources.getBoolean(R.bool.show_dice_roll_average_enable_default))
        showAverage.setIcon(R.drawable.ic_info)
        advancedCategory.addPreference(showAverage)

        val keepScreenOn = SwitchPreferenceCompat(context)
        keepScreenOn.key = getString(R.string.keep_screen_on_key)
        keepScreenOn.title = getString(R.string.keep_screen_on_title)
        keepScreenOn.summaryProvider = Preference.SummaryProvider<SwitchPreferenceCompat> {
            if(it.isChecked) {
                "Enabled"
            }
            else {
                "Disabled"
            }
        }
        keepScreenOn.setDefaultValue(resources.getBoolean(R.bool.keep_screen_on_default))
        keepScreenOn.setIcon(R.drawable.ic_light_bulb)
        advancedCategory.addPreference(keepScreenOn)

        preferenceScreen = screen
    }
}