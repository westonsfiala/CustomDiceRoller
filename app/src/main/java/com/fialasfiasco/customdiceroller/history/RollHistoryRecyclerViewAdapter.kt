package com.fialasfiasco.customdiceroller.history

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.fialasfiasco.customdiceroller.R
import com.fialasfiasco.customdiceroller.data.PageViewModel

import kotlinx.android.synthetic.main.holder_roll_history.view.*

/**
 * [RecyclerView.Adapter] that can display a [HistoryStampHolder]
 */
class RollHistoryRecyclerViewAdapter(private val pageViewModel: PageViewModel) : RecyclerView.Adapter<RollHistoryRecyclerViewAdapter.HistoryStampHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryStampHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.holder_roll_history, parent, false)
        return HistoryStampHolder(view)
    }

    override fun onBindViewHolder(holder: HistoryStampHolder, position: Int) {
        val stamp = pageViewModel.getRollHistory(position)
        holder.mResultView.text = stamp.result
        holder.mTitleView.text = stamp.title
        holder.mDetailsView.text = stamp.details
        holder.mTimeStampView.text = stamp.timeStamp
    }

    override fun getItemCount(): Int = pageViewModel.numHistoryStamps()

    inner class HistoryStampHolder(view: View) : RecyclerView.ViewHolder(view) {
        val mResultView: TextView = view.rollResultText
        val mTitleView: TextView = view.rollTitleText
        val mDetailsView: TextView = view.rollDetailsText
        val mTimeStampView: TextView = view.rollTimeStampText
    }
}
