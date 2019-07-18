package com.fialasfiasco.customdiceroller.aggregate_roller

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.FragmentManager
import com.fialasfiasco.customdiceroller.R
import com.fialasfiasco.customdiceroller.data.PageViewModel
import com.fialasfiasco.customdiceroller.helper.UpDownButtonsFragment
import kotlinx.android.synthetic.main.holder_aggregate_die.view.*
import kotlinx.android.synthetic.main.holder_simple_die.view.*

/**
 * [RecyclerView.Adapter] that can display a [AggregateDieViewHolder]
 */
class AggregateRollRecyclerViewAdapter(private val pageViewModel: PageViewModel,
                                       private val fragmentManager: FragmentManager,
                                       private val listener: AggregateRollInterfaceListener)
    :
    RecyclerView.Adapter<AggregateRollRecyclerViewAdapter.AggregateDieViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AggregateDieViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.holder_aggregate_die, parent, false)

        return AggregateDieViewHolder(view)
    }

    override fun onBindViewHolder(holder: AggregateDieViewHolder, position: Int) {
        val aggregateDie = pageViewModel.getAggregateDie(position)
        holder.mImage.setImageResource(aggregateDie.mInnerDie.getImageID())
        holder.mText.text = aggregateDie.mInnerDie.getName()

        val upDownButtonsFragment = UpDownButtonsFragment()

        fragmentManager
            .beginTransaction()
            .add(aggregateDie.getName().hashCode(), upDownButtonsFragment)
            .commit()

//        holder.mUpButton.setOnClickListener {
//            pageViewModel.incrementAggregateDieCount(pageViewModel.getInnerDie(position))
//            updateDieCount(holder, position)
//        }
//
//        holder.mUpButton.setOnLongClickListener {
//            pageViewModel.largeIncrementAggregateDieCount(pageViewModel.getInnerDie(position))
//            updateDieCount(holder, position)
//            true
//        }
//
//        holder.mDownButton.setOnClickListener {
//            pageViewModel.decrementAggregateDieCount(pageViewModel.getInnerDie(position))
//            updateDieCount(holder, position)
//        }
//
//        holder.mDownButton.setOnLongClickListener {
//            pageViewModel.largeDecrementAggregateDieCount(pageViewModel.getInnerDie(position))
//            updateDieCount(holder, position)
//            true
//        }
//
//        holder.mCount.setOnClickListener {
//            listener.onDisplayTextClicked(holder, position)
//        }
    }

    private fun updateDieCount(holder: AggregateDieViewHolder, position: Int)
    {
        val updatedAggregateDie = pageViewModel.getAggregateDie(position)
        //holder.mCount.text = updatedAggregateDie.mDieCount.toString()
    }

    override fun getItemCount(): Int = pageViewModel.getInnerDiceSize()

    inner class AggregateDieViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val mImage: ImageView = view.simpleDieInclude.dieDisplay
        val mText: TextView = view.simpleDieInclude.dieDisplayText
        val mFrame: FrameLayout = view.aggregateDieUpDownFrame
    }

    interface AggregateRollInterfaceListener
    {
        fun onDisplayTextClicked(holder: AggregateDieViewHolder, position: Int)
    }
}
