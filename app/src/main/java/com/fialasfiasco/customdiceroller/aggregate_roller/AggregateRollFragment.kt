package com.fialasfiasco.customdiceroller.aggregate_roller

import android.graphics.Point
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fialasfiasco.customdiceroller.R
import com.fialasfiasco.customdiceroller.data.*
import com.fialasfiasco.customdiceroller.helper.DiceRollerDialog
import com.fialasfiasco.customdiceroller.helper.NumberDialog
import com.fialasfiasco.customdiceroller.helper.UpDownButtonsFragment
import com.fialasfiasco.customdiceroller.history.HistoryStamp
import kotlinx.android.synthetic.main.fragment_aggregate_roll.*
import java.lang.NumberFormatException
import kotlin.math.min

/**
 * A fragment representing a list of Items.
 */
class AggregateRollFragment : Fragment(),
    AggregateRollRecyclerViewAdapter.AggregateRollInterfaceListener,
    DiceRollerDialog.DiceRollerListener
{
    private lateinit var pageViewModel: PageViewModel

    private var aggregateModifierUpDownButtonsFragment : UpDownButtonsFragment? = null

    private var rollerDialog : DiceRollerDialog? = null

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        pageViewModel = activity?.run {
            ViewModelProviders.of(this).get(PageViewModel::class.java)
        } ?: throw Exception("Invalid Activity")
    }

    override fun onStart() {
        setupRecycler()
        setupRollerDialog()
        setupRecycler()
        setupBottomBar()
        super.onStart()
    }

    private fun updateRecycler()
    {
        aggregateRecycler.layoutManager = GridLayoutManager(context, pageViewModel.getItemsInRowAggregate())
    }

    override fun onResume() {
        super.onResume()
        rollerDialog?.resume()
    }

    override fun onPause() {
        super.onPause()
        rollerDialog?.pause()
    }

    override fun onStop() {
        super.onStop()
        rollerDialog?.kill()
        rollerDialog = null
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_aggregate_roll, container)
    }

    private fun setupRollerDialog()
    {
        val size = Point()
        activity?.windowManager?.defaultDisplay?.getSize(size)

        rollerDialog = DiceRollerDialog(
            context!!,
            activity,
            min(size.x, size.y),
            pageViewModel,
            this)
    }

    private fun setupRecycler()
    {
        // Set the adapter
        aggregateRecycler.layoutManager = GridLayoutManager(context, pageViewModel.getItemsInRowAggregate())
        aggregateRecycler.adapter = AggregateRollRecyclerViewAdapter(pageViewModel, this)
    }

    private fun setupBottomBar()
    {
        setupRollButton()
        setupModifierButtons()
    }

    private fun setupRollButton()
    {
        rollButton.setOnClickListener {
            val aggregateDieList = mutableListOf<AggregateDie>()

            for(index in 0 until pageViewModel.getInnerDiceSize())
            {
                val aggregateDie = pageViewModel.getAggregateDie(index)
                if(aggregateDie.mDieCount > 0)
                {
                    aggregateDieList.add(aggregateDie)
                }
            }

            if (pageViewModel.getShakeEnabled()) {
                rollerDialog?.runShakeRoller(
                    aggregateDieList.toTypedArray(),
                    pageViewModel.getAggregateModifier()
                )
            } else {
                rollerDialog?.runRollDisplay(
                    aggregateDieList.toTypedArray(),
                    pageViewModel.getAggregateModifier()
                )
            }
        }
    }

    private fun setupModifierButtons()
    {
        modifierUpBut.setOnClickListener {
            pageViewModel.incrementAggregateModifier()
        }

        modifierUpBut.setOnLongClickListener {
            pageViewModel.largeIncrementAggregateModifier()
            true
        }

        modifierDownBut.setOnClickListener {
            pageViewModel.decrementAggregateModifier()
        }

        modifierDownBut.setOnLongClickListener {
            pageViewModel.largeDecrementAggregateModifier()
            true
        }

        modifierTextView.setOnClickListener {
            NumberDialog(context, layoutInflater).createDialog(
                "Modifier",
                MIN_MODIFIER,
                MAX_MODIFIER,
                pageViewModel.getAggregateModifier(),
                object : NumberDialog.NumberDialogListener {
                    override fun respondToOK(outputValue: Int) {
                        pageViewModel.setAggregateModifierExact(outputValue)
                    }
                })
        }

        pageViewModel.aggregateModifier.observe(this, Observer<Int> {
            updateModifierText()
        })

        updateModifierText()
    }

    private fun updateModifierText()
    {
        when {
            pageViewModel.getAggregateModifier() >= 0 -> aggregateModifierUpDownButtonsFragment?.setDisplayText(String.format("+%d", pageViewModel.getAggregateModifier()))
            pageViewModel.getAggregateModifier() < 0 -> aggregateModifierUpDownButtonsFragment?.setDisplayText(String.format("%d", pageViewModel.getAggregateModifier()))
        }
    }

    override fun onDisplayTextClicked(holder : AggregateRollRecyclerViewAdapter.AggregateDieViewHolder, position: Int) {
        NumberDialog(context, layoutInflater).createDialog(
            "Number of Dice",
            0,
            100,
            pageViewModel.getAggregateDie(position).mDieCount,
            object : NumberDialog.NumberDialogListener {
                override fun respondToOK(outputValue: Int) {
                    try {
                        pageViewModel.setAggregateDieCountExact(pageViewModel.getInnerDie(position), outputValue)
                        holder.mCount.text = pageViewModel.getAggregateDie(position).mDieCount.toString()
                    } catch (error: NumberFormatException) {
                    }
                }
            })
    }

    override fun onRollResult(stamp: HistoryStamp) {
        pageViewModel.addRollHistory(stamp)
    }

    companion object {

        @JvmStatic
        fun newInstance() = AggregateRollFragment()
    }
}