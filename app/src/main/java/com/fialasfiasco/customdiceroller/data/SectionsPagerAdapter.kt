package com.fialasfiasco.customdiceroller.data

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.fialasfiasco.customdiceroller.R
import com.fialasfiasco.customdiceroller.PlaceholderFragment
import com.fialasfiasco.customdiceroller.aggregate_roller.AggregateRollFragment
import com.fialasfiasco.customdiceroller.history.RollHistoryFragment
import com.fialasfiasco.customdiceroller.saved_roller.SavedRollerFragment
import com.fialasfiasco.customdiceroller.simple_roller.SimpleRollFragment

private val TAB_TITLES = arrayOf(
    R.string.tab_text_1,
    R.string.tab_text_2,
    R.string.tab_text_3,
    R.string.tab_text_4
)

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
class SectionsPagerAdapter(private val context: Context, fm: FragmentManager) : FragmentPagerAdapter(fm,BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> SimpleRollFragment.newInstance()
            1 -> RollHistoryFragment.newInstance()
            2 -> AggregateRollFragment.newInstance()
            3 -> SavedRollerFragment.newInstance()
            else -> PlaceholderFragment.newInstance(-1)
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return context.resources.getString(TAB_TITLES[position])
    }

    override fun getCount(): Int {
        return 4
    }
}