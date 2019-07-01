package com.fialasfiasco.customdiceroller.history

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.fialasfiasco.customdiceroller.R
import com.fialasfiasco.customdiceroller.data.PageViewModel

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

        // Notify about new items and then scroll to the top.
        pageViewModel.singleRollHistory.observe(this, Observer<HistoryStamp> {
            val recyclerView = view as RecyclerView?
            recyclerView?.adapter?.notifyItemInserted(0)
            recyclerView?.layoutManager?.scrollToPosition(0)
        })

        // Notify about new items and then scroll to the top.
        pageViewModel.clearHistory.observe(this, Observer<Boolean> {
            val recyclerView = view as RecyclerView?
            recyclerView?.adapter?.notifyDataSetChanged()
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_roll_history_layout, container, false) as RecyclerView

        // Set the adapter
        view.layoutManager = LinearLayoutManager(context)
        view.adapter = RollHistoryRecyclerViewAdapter(pageViewModel)

        return view
    }

    companion object {

        @JvmStatic
        fun newInstance() = RollHistoryFragment()
    }
}

// Simple class for storing roll history
class HistoryStamp(val result : String,
                   val title : String,
                   val details : String,
                   val timeStamp : String)
