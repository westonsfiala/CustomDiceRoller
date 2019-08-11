package com.fialasfiasco.customdiceroller.history

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import com.fialasfiasco.customdiceroller.R
import com.fialasfiasco.customdiceroller.data.PageViewModel
import kotlinx.android.synthetic.main.fragment_roll_history.*

/**
 * A fragment representing a list of Items.
 */
class RollHistoryFragment : Fragment() {

    private lateinit var pageViewModel: PageViewModel

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        pageViewModel = activity?.run {
            ViewModelProviders.of(this).get(PageViewModel::class.java)
        } ?: throw Exception("Invalid Activity")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_roll_history, container, false)
    }

    override fun onStart() {
        super.onStart()
        // Set the adapter
        list.layoutManager = LinearLayoutManager(context)
        list.adapter = RollHistoryRecyclerViewAdapter(pageViewModel)
        list.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))

        // Notify about new items and then scroll to the top.
        pageViewModel.singleRollHistory.observe(this, Observer<HistoryStamp> {
            list.adapter?.notifyDataSetChanged()
            list.layoutManager?.scrollToPosition(0)
            showHideTextAndRestoreButton()
        })

        pageViewModel.clearHistory.observe(this, Observer<Boolean> {
            list.adapter?.notifyDataSetChanged()
            showHideTextAndRestoreButton()
        })

        showHideTextAndRestoreButton()
    }

    private fun showHideTextAndRestoreButton() {
        if(pageViewModel.numHistoryStamps() == 0) {
            noHistoryText.visibility = TextView.VISIBLE
            if(pageViewModel.hasSavedHistory()) {
                restoreHistoryButton.visibility = TextView.VISIBLE
                restoreHistoryButton.setOnClickListener {
                    pageViewModel.restoreClearedHistory()
                    list.adapter?.notifyDataSetChanged()
                    showHideTextAndRestoreButton()
                }
            } else {
                restoreHistoryButton.visibility = TextView.GONE
            }
        } else {
            noHistoryText.visibility = TextView.INVISIBLE
            restoreHistoryButton.visibility = TextView.GONE
        }
    }

    companion object {

        @JvmStatic
        fun newInstance() = RollHistoryFragment()
    }
}

// Simple class for storing roll history
class HistoryStamp(val result : CharSequence,
                   val title : CharSequence,
                   val details : CharSequence,
                   val timeStamp : CharSequence)
