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
        val dieThemeKey = getString(R.string.die_theme_key)
        val noThemeString = getString(R.string.no_theme)

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
        basicDieTheme.setIcon(R.drawable.palette)
        basicDieTheme.setOnPreferenceChangeListener { _, any: Any ->
            val newName = any.toString()
            if(newName != noThemeString) {
                displayCategory.title = any.toString()
                val editor = manager.edit()
                editor.putString(dieThemeKey, any.toString())
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
        metallicDieTheme.setIcon(R.drawable.gold_stack)
        metallicDieTheme.setOnPreferenceChangeListener { _, any: Any ->
            val newName = any.toString()
            if(newName != noThemeString) {
                displayCategory.title = any.toString()
                val editor = manager.edit()
                editor.putString(dieThemeKey, any.toString())
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
        iceCreamDieTheme.setIcon(R.drawable.ice_cream_cone)
        iceCreamDieTheme.setOnPreferenceChangeListener { _, any: Any ->
            val newName = any.toString()
            if(newName != noThemeString) {
                displayCategory.title = any.toString()
                val editor = manager.edit()
                editor.putString(dieThemeKey, any.toString())
                editor.apply()
            }
            true
        }
        displayCategory.addPreference(iceCreamDieTheme)

        preferenceScreen = screen

        basicDieTheme.setValueIndex(0)
        metallicDieTheme.setValueIndex(0)
        iceCreamDieTheme.setValueIndex(0)
    }
}