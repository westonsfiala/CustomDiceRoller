package com.fialasfiasco.customdiceroller.aggregate_roller

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.fialasfiasco.customdiceroller.R
import com.fialasfiasco.customdiceroller.data.PageViewModel
import kotlinx.android.synthetic.main.fragment_up_down_buttons.view.*
import kotlinx.android.synthetic.main.holder_aggregate_die.view.*
import kotlinx.android.synthetic.main.holder_simple_die.view.*

/**
 * [RecyclerView.Adapter] that can display a [AggregateDieViewHolder]
 */
class AggregateRollRecyclerViewAdapter(private val pageViewModel: PageViewModel,
                                       private val listener: AggregateRollInterfaceListener)
    :
    RecyclerView.Adapter<AggregateRollRecyclerViewAdapter.AggregateDieViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AggregateDieViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.holder_aggregate_die, parent, false)

        return AggregateDieViewHolder(view)
    }

    override fun onBindViewHolder(holder: AggregateDieViewHolder, position: Int) {
        val aggregateDie = pageViewModel.getInnerDie(position)
        holder.mImage.setImageResource(aggregateDie.getImageID())
        holder.mText.text = aggregateDie.getDisplayName()

        holder.mUpButton.setOnClickListener {
            pageViewModel.incrementAggregateDieCount(position)
            updateDieCount(holder, position)
        }

        holder.mUpButton.setOnLongClickListener {
            pageViewModel.largeIncrementAggregateDieCount(position)
            updateDieCount(holder, position)
            true
        }

        holder.mDownButton.setOnClickListener {
            pageViewModel.decrementAggregateDieCount(position)
            updateDieCount(holder, position)
        }

        holder.mDownButton.setOnLongClickListener {
            pageViewModel.largeDecrementAggregateDieCount(position)
            updateDieCount(holder, position)
            true
        }

        updateDieCount(holder,position)

        holder.mModText.setOnClickListener {
            listener.onDisplayTextClicked(holder, position)
        }
    }

    private fun updateDieCount(holder: AggregateDieViewHolder, position: Int)
    {
        holder.mModText.text = pageViewModel.getAggregateDieCount(position).toString()
    }

    override fun getItemCount(): Int = pageViewModel.getInnerDiceSize()

    inner class AggregateDieViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val mImage: ImageView = view.simpleDieInclude.dieDisplay
        val mText: TextView = view.simpleDieInclude.dieDisplayText
        val mUpButton: ImageButton = view.upDownButtonsInclude.upButton
        val mDownButton: ImageButton = view.upDownButtonsInclude.downButton
        val mModText: TextView = view.upDownButtonsInclude.upDownDisplayText

        init {
            view.simpleDieInclude.isClickable = false
        }
    }

    interface AggregateRollInterfaceListener
    {
        fun onDisplayTextClicked(holder: AggregateDieViewHolder, position: Int)
    }
}
