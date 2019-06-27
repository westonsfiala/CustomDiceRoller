package com.fialasfiasco.customdiceroller.aggregate_roller

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.fialasfiasco.customdiceroller.R
import com.fialasfiasco.customdiceroller.data.PageViewModel
import kotlinx.android.synthetic.main.aggregate_die_view_layout.view.*

/**
 * [RecyclerView.Adapter] that can display a [AggregateDieViewHolder]
 */
class AggregateRollerRecyclerViewAdapter(private val pageViewModel: PageViewModel) : RecyclerView.Adapter<AggregateRollerRecyclerViewAdapter.AggregateDieViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AggregateDieViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.aggregate_die_view_layout, parent, false)
        return AggregateDieViewHolder(view)
    }

    override fun onBindViewHolder(holder: AggregateDieViewHolder, position: Int) {
        val aggregateDie = pageViewModel.getAggregateDie(position)
        holder.mDieImage.setImageResource(aggregateDie.mSimpleDie.mImageID)
        holder.mDieCount.text = aggregateDie.mDieCount.toString()
        holder.mDieText.text = "d" + aggregateDie.mSimpleDie.mDie.toString()
    }

    override fun getItemCount(): Int = pageViewModel.getSimpleDiceSize()

    inner class AggregateDieViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val mDieImage: ImageView = view.dieImage
        val mDieCount: TextView = view.displayText
        val mDieText: TextView = view.dieText
    }
}
