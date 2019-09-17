package com.fialasfiasco.customdiceroller.data

import android.content.Context
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.fialasfiasco.customdiceroller.R
import com.fialasfiasco.customdiceroller.PlaceholderFragment
import com.fialasfiasco.customdiceroller.custom_roller.CustomRollFragment
import com.fialasfiasco.customdiceroller.history.RollHistoryFragment
import com.fialasfiasco.customdiceroller.saved_roller.SavedRollerFragment
import com.fialasfiasco.customdiceroller.simple_roller.SimpleRollFragment

const val HISTORY_TAB_INDEX = 0
const val SIMPLE_ROLL_TAB_INDEX = 1
const val CUSTOM_ROLL_TAB_INDEX = 2
const val SAVED_ROLLS_TAB_INDEX = 3

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
class SectionsPagerAdapter(private val context: Context, fm: FragmentManager, private val listener: tabUpdateListener)
    : FragmentPagerAdapter(fm,BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment {
        return when (position) {
            HISTORY_TAB_INDEX -> RollHistoryFragment.newInstance()
            SIMPLE_ROLL_TAB_INDEX -> SimpleRollFragment.newInstance()
            CUSTOM_ROLL_TAB_INDEX -> CustomRollFragment.newInstance()
            SAVED_ROLLS_TAB_INDEX -> SavedRollerFragment.newInstance()
            else -> PlaceholderFragment.newInstance(-1)
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return context.resources.getString(TAB_TITLES[position])
    }

    override fun getCount(): Int {
        return 4
    }

    override fun finishUpdate(container: ViewGroup) {
        super.finishUpdate(container)
        listener.tabUpdated()
    }

    interface tabUpdateListener {
        fun tabUpdated()
    }
}