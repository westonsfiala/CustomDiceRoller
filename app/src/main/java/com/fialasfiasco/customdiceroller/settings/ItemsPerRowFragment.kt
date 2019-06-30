package com.fialasfiasco.customdiceroller.settings

import android.os.Bundle
import androidx.preference.*
import com.fialasfiasco.customdiceroller.R

class ItemsPerRowFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {

        val context = preferenceManager.context
        val screen = preferenceManager.createPreferenceScreen(context)

        val itemsPerRowSimple = DropDownPreference(context)
        itemsPerRowSimple.key = getString(R.string.items_per_row_simple_key)
        itemsPerRowSimple.title = getString(R.string.items_per_row_simple_title)
        itemsPerRowSimple.setEntries(R.array.items_per_row_simple_entries)
        itemsPerRowSimple.setEntryValues(R.array.items_per_row_simple_values)
        itemsPerRowSimple.setDefaultValue(resources.getInteger(R.integer.items_per_row_simple_default).toString())
        itemsPerRowSimple.summaryProvider = Preference.SummaryProvider<DropDownPreference> {
            val manager = PreferenceManager.getDefaultSharedPreferences(context)
            manager.getString(getString(R.string.items_per_row_simple_key),
                resources.getInteger(R.integer.items_per_row_simple_default).toString())
        }
        itemsPerRowSimple.setIcon(R.drawable.ic_unknown)
        screen.addPreference(itemsPerRowSimple)

        val itemsPerRowCustom = DropDownPreference(context)
        itemsPerRowCustom.key = getString(R.string.items_per_row_custom_key)
        itemsPerRowCustom.title = getString(R.string.items_per_row_custom_title)
        itemsPerRowCustom.setEntries(R.array.items_per_row_custom_entries)
        itemsPerRowCustom.setEntryValues(R.array.items_per_row_custom_values)
        itemsPerRowCustom.setDefaultValue(resources.getInteger(R.integer.items_per_row_custom_default).toString())
        itemsPerRowCustom.summaryProvider = Preference.SummaryProvider<DropDownPreference> {
            val manager = PreferenceManager.getDefaultSharedPreferences(context)
            manager.getString(getString(R.string.items_per_row_custom_key),
                resources.getInteger(R.integer.items_per_row_custom_default).toString())
        }
        itemsPerRowCustom.setIcon(R.drawable.ic_cubes)
        screen.addPreference(itemsPerRowCustom)

        preferenceScreen = screen
    }
}