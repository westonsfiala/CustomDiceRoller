package com.fialasfiasco.customdiceroller.aggregate_roller

import android.graphics.Point
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fialasfiasco.customdiceroller.R
import com.fialasfiasco.customdiceroller.data.AggregateDie
import com.fialasfiasco.customdiceroller.data.MAX_MODIFIER
import com.fialasfiasco.customdiceroller.data.PageViewModel
import com.fialasfiasco.customdiceroller.data.START_MODIFIER
import com.fialasfiasco.customdiceroller.helper.DiceRollerDialog
import com.fialasfiasco.customdiceroller.helper.NumberDialog
import com.fialasfiasco.customdiceroller.history.HistoryStamp
import kotlinx.android.synthetic.main.fragment_aggregate_roll_layout.*
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

    private var itemsInRow = 2

    private var rollerDialog : DiceRollerDialog? = null

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        pageViewModel = activity?.run {
            ViewModelProviders.of(this).get(PageViewModel::class.java)
        } ?: throw Exception("Invalid Activity")
    }

    override fun onStart() {
        updateRecycler()
        super.onStart()
    }

    private fun updateRecycler()
    {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)

        itemsInRow = preferences.getString(
            getString(R.string.items_per_row_custom_key),
            resources.getInteger(R.integer.items_per_row_custom_default).toString()
        )!!.toInt()

        aggregateRecycler.layoutManager = GridLayoutManager(context, itemsInRow)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_aggregate_roll_layout, container, false)

        setupRecycler(view)
        setupBottomBar(view)

        return view
    }

    private fun setupRecycler(view: View)
    {
        val recycler = view.findViewById<RecyclerView>(R.id.aggregateRecycler)

        // Set the adapter
        recycler.layoutManager = GridLayoutManager(context, itemsInRow)
        recycler.adapter = AggregateRollRecyclerViewAdapter(pageViewModel, this)
    }

    private fun setupBottomBar(view: View)
    {
        setupRollButton(view)
        setupModifierButtons(view)
        setupSaveButton(view)
    }

    private fun setupRollButton(view: View)
    {
        val rollButton = view.findViewById<Button>(R.id.rollButton)

        rollButton.setOnClickListener {
            val layoutSize = min(aggregateRollLayout.width, aggregateRollLayout.height)

            val size = Point(layoutSize, layoutSize)
            activity?.windowManager?.defaultDisplay?.getSize(size)

            rollerDialog = DiceRollerDialog(
                context!!,
                activity,
                min(size.x, size.y),
                pageViewModel,
                this
            )

            if (pageViewModel.getShakeEnabled()) {
                rollerDialog?.runShakeRoller(
                    arrayOf(AggregateDie(2, 1)),
                    0
                )
            } else {
                rollerDialog?.runRollDisplay(
                    arrayOf(AggregateDie(2, 1)),
                    0
                )
            }
        }
    }

    private fun setupModifierButtons(newView: View)
    {
        val modifierUpBut = newView.findViewById<ImageButton>(R.id.modifierUpButton)
        modifierUpBut.setOnClickListener {
            pageViewModel.setAggregateModifier(pageViewModel.getAggregateModifier() + 1)
        }

        modifierUpBut.setOnLongClickListener {
            if (pageViewModel.getAggregateModifier() >= 0) {
                pageViewModel.setAggregateModifier(pageViewModel.getAggregateModifier() + MAX_MODIFIER)
            } else {
                pageViewModel.setAggregateModifier(START_MODIFIER)
            }
            true
        }

        val modifierDownBut = newView.findViewById<ImageButton>(R.id.modifierDownButton)
        modifierDownBut.setOnClickListener {
            pageViewModel.setAggregateModifier(pageViewModel.getAggregateModifier() - 1)
        }

        modifierDownBut.setOnLongClickListener {
            if (pageViewModel.getAggregateModifier() <= 0) {
                pageViewModel.setAggregateModifier(pageViewModel.getAggregateModifier() - MAX_MODIFIER)
            } else {
                pageViewModel.setAggregateModifier(START_MODIFIER)
            }
            true
        }

        pageViewModel.aggregateModifier.observe(this, Observer<Int> {
            updateModifierText(view!!)
        })

        updateModifierText(newView)
    }

    private fun updateModifierText(view: View)
    {
        val modifierText = view.findViewById<TextView>(R.id.modifierText)
        when {
            pageViewModel.getAggregateModifier() >= 0 -> modifierText.text = String.format("+%d", pageViewModel.getAggregateModifier())
            pageViewModel.getAggregateModifier() < 0 -> modifierText.text = String.format("%d", pageViewModel.getAggregateModifier())
        }
    }

    private fun setupSaveButton(view: View)
    {
        val saveButton = view.findViewById<Button>(R.id.saveButton)

        saveButton.setOnClickListener {
            Toast.makeText(context, "Todo", Toast.LENGTH_LONG).show()
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
                        pageViewModel.setAggregateDieCount(pageViewModel.getAggregateDie(position).mSimpleDie.mDie, outputValue)
                        holder.mCount.text = pageViewModel.getAggregateDie(position).mDieCount.toString()
                    } catch (error: NumberFormatException) {
                    }
                }
            })
    }

    override fun onDieBounce(maxVelocity: Float) {
        //playSound(maxVelocity)
    }

    override fun onRollResult(stamp: HistoryStamp) {
        pageViewModel.addRollHistory(stamp)
    }

    companion object {

        @JvmStatic
        fun newInstance() = AggregateRollFragment()
    }
}