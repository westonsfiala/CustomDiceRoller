package com.fialasfiasco.customdiceroller.aggregate_roller

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.fialasfiasco.customdiceroller.R
import com.fialasfiasco.customdiceroller.data.MAX_DICE
import com.fialasfiasco.customdiceroller.data.PageViewModel
import kotlinx.android.synthetic.main.holder_aggregate_die.view.*

/**
 * [RecyclerView.Adapter] that can display a [AggregateDieViewHolder]
 */
class AggregateRollRecyclerViewAdapter(private val pageViewModel: PageViewModel, private val listener: AggregateRollInterfaceListener) : RecyclerView.Adapter<AggregateRollRecyclerViewAdapter.AggregateDieViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AggregateDieViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.holder_aggregate_die, parent, false)
        return AggregateDieViewHolder(view)
    }

    override fun onBindViewHolder(holder: AggregateDieViewHolder, position: Int) {
        val aggregateDie = pageViewModel.getAggregateDie(position)
        holder.mImage.setImageResource(aggregateDie.mInnerDie.getImageID())
        holder.mCount.text = aggregateDie.mDieCount.toString()
        holder.mText.text = aggregateDie.mInnerDie.getName()

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
        val simpleDieKey = pageViewModel.getSimpleDie(position)
        pageViewModel.setAggregateDieCount(simpleDieKey, newValue)
        val updatedAggregateDie = pageViewModel.getAggregateDie(position)
        holder.mCount.text = updatedAggregateDie.mDieCount.toString()
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
