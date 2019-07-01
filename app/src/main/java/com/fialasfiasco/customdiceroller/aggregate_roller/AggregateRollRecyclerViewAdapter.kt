package com.fialasfiasco.customdiceroller.aggregate_roller

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.fialasfiasco.customdiceroller.R
import com.fialasfiasco.customdiceroller.data.PageViewModel
import com.fialasfiasco.customdiceroller.simple_roller.MAX_DICE
import kotlinx.android.synthetic.main.aggregate_die_view_layout.view.*

/**
 * [RecyclerView.Adapter] that can display a [AggregateDieViewHolder]
 */
class AggregateRollRecyclerViewAdapter(private val pageViewModel: PageViewModel, private val listener: AggregateRollInterfaceListener) : RecyclerView.Adapter<AggregateRollRecyclerViewAdapter.AggregateDieViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AggregateDieViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.aggregate_die_view_layout, parent, false)
        return AggregateDieViewHolder(view)
    }

    override fun onBindViewHolder(holder: AggregateDieViewHolder, position: Int) {
        val aggregateDie = pageViewModel.getAggregateDie(position)
        holder.mImage.setImageResource(aggregateDie.mSimpleDie.mImageID)
        holder.mCount.text = aggregateDie.mDieCount.toString()
        holder.mText.text = "d" + aggregateDie.mSimpleDie.mDie.toString()

        holder.mUpButton.setOnClickListener {
            updateDieCount(holder, position, pageViewModel.getAggregateDie(position).mDieCount + 1)
        }

        holder.mUpButton.setOnLongClickListener {
            updateDieCount(holder, position, pageViewModel.getAggregateDie(position).mDieCount + MAX_DICE)
            true
        }

        holder.mDownButton.setOnClickListener {
            updateDieCount(holder, position, pageViewModel.getAggregateDie(position).mDieCount - 1)
        }

        holder.mDownButton.setOnLongClickListener {
            updateDieCount(holder, position, pageViewModel.getAggregateDie(position).mDieCount - MAX_DICE)
            true
        }

        holder.mCount.setOnClickListener {
            listener.onDisplayTextClicked(holder, position)
        }
    }

    private fun updateDieCount(holder: AggregateDieViewHolder, position: Int, newValue: Int)
    {
        val updatedAggregateDie = pageViewModel.getAggregateDie(position)
        pageViewModel.setAggregateDieCount(updatedAggregateDie.mSimpleDie.mDie, newValue)
        holder.mCount.text = pageViewModel.getAggregateDie(position).mDieCount.toString()
    }

    override fun getItemCount(): Int = pageViewModel.getSimpleDiceSize()

    inner class AggregateDieViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val mImage: ImageView = view.dieImage
        val mCount: TextView = view.displayText
        val mText: TextView = view.dieText
        val mUpButton: ImageButton = view.upButton
        val mDownButton: ImageButton = view.downButton
    }

    interface AggregateRollInterfaceListener
    {
        fun onDisplayTextClicked(holder: AggregateDieViewHolder, position: Int)
    }
}
