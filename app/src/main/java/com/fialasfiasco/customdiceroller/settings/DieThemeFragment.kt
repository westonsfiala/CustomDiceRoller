package com.fialasfiasco.customdiceroller.settings

import android.os.Bundle
import androidx.preference.*
import com.fialasfiasco.customdiceroller.R

class DieThemeFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {

        val context = preferenceManager.context
        val screen = preferenceManager.createPreferenceScreen(context)

        // Since all of the preferences share the same key, you need to verify that you
        // actually clicked the preference before you save any values.
        var lastClickedPreference : Preference ?= null
        val dieThemeKey = getString(R.string.die_theme_key)
        val noThemeString = getString(R.string.no_theme)

        val changeText = PreferenceCategory(context)
        changeText.title = "Item must change to update"
        changeText.isIconSpaceReserved = false
        screen.addPreference(changeText)

        val displayCategory = PreferenceCategory(context)
        val manager = PreferenceManager.getDefaultSharedPreferences(context)
        val currentTheme = manager.getString(getString(R.string.die_theme_key), getString(R.string.white_theme))
        displayCategory.title = currentTheme
        displayCategory.isIconSpaceReserved = false
        screen.addPreference(displayCategory)

        val basicDieTheme = DropDownPreference(context)
        basicDieTheme.key = getString(R.string.basic_die_theme_key)
        basicDieTheme.title = getString(R.string.basic_die_theme_title)
        basicDieTheme.setEntries(R.array.basic_die_themes)
        basicDieTheme.setEntryValues(R.array.basic_die_themes)
        basicDieTheme.setDefaultValue(noThemeString)
        basicDieTheme.summaryProvider = ListPreference.SimpleSummaryProvider.getInstance()
        basicDieTheme.setIcon(R.drawable.ic_palette)
        basicDieTheme.setOnPreferenceClickListener {
            lastClickedPreference = it
            true
        }
        basicDieTheme.setOnPreferenceChangeListener { preference: Preference, any: Any ->
            if(lastClickedPreference == preference) {
                displayCategory.title = any.toString()
                val editor = manager.edit()
                editor.putString(dieThemeKey,any.toString())
                editor.apply()
            }
            true
        }
        displayCategory.addPreference(basicDieTheme)

        val metallicDieTheme = DropDownPreference(context)
        metallicDieTheme.key = getString(R.string.metallic_die_theme_key)
        metallicDieTheme.title = getString(R.string.metallic_die_theme_title)
        metallicDieTheme.setEntries(R.array.metallic_die_themes)
        metallicDieTheme.setEntryValues(R.array.metallic_die_themes)
        metallicDieTheme.setDefaultValue(noThemeString)
        metallicDieTheme.summaryProvider = ListPreference.SimpleSummaryProvider.getInstance()
        metallicDieTheme.setIcon(R.drawable.ic_gold_stack)
        metallicDieTheme.setOnPreferenceClickListener {
            lastClickedPreference = it
            true
        }
        metallicDieTheme.setOnPreferenceChangeListener { preference: Preference, any: Any ->
            if(lastClickedPreference == preference) {
                displayCategory.title = any.toString()
                val editor = manager.edit()
                editor.putString(dieThemeKey,any.toString())
                editor.apply()
            }
            true
        }
        displayCategory.addPreference(metallicDieTheme)

        val iceCreamDieTheme = DropDownPreference(context)
        iceCreamDieTheme.key = getString(R.string.ice_cream_die_theme_key)
        iceCreamDieTheme.title = getString(R.string.ice_cream_die_theme_title)
        iceCreamDieTheme.setEntries(R.array.ice_cream_die_themes)
        iceCreamDieTheme.setEntryValues(R.array.ice_cream_die_themes)
        iceCreamDieTheme.setDefaultValue(noThemeString)
        iceCreamDieTheme.summaryProvider = ListPreference.SimpleSummaryProvider.getInstance()
        iceCreamDieTheme.setIcon(R.drawable.ic_ice_cream_cone)
        iceCreamDieTheme.setOnPreferenceClickListener {
            lastClickedPreference = it
            true
        }
        iceCreamDieTheme.setOnPreferenceChangeListener { preference: Preference, any: Any ->
            if(lastClickedPreference == preference) {
                displayCategory.title = any.toString()
                val editor = manager.edit()
                editor.putString(dieThemeKey,any.toString())
                editor.apply()
            }
            true
        }
        displayCategory.addPreference(iceCreamDieTheme)

        preferenceScreen = screen
    }
}