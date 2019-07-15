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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_aggregate_roll, container, false)

        createRollerDialog()
        setupRecycler(view)
        setupBottomBar(view)

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        rollerDialog?.kill()
        rollerDialog = null
    }

    private fun createRollerDialog()
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
//        setupSaveButton(view)
    }

    private fun setupRollButton(view: View)
    {
        val rollButton = view.findViewById<Button>(R.id.rollButton)

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

    private fun setupModifierButtons(newView: View)
    {
        val modifierUpBut = newView.findViewById<ImageButton>(R.id.modifierUpButton)
        modifierUpBut.setOnClickListener {
            pageViewModel.incrementAggregateModifier()
        }

        modifierUpBut.setOnLongClickListener {
            pageViewModel.largeIncrementAggregateModifier()
            true
        }

        val modifierDownBut = newView.findViewById<ImageButton>(R.id.modifierDownButton)
        modifierDownBut.setOnClickListener {
            pageViewModel.decrementAggregateModifier()
        }

        modifierDownBut.setOnLongClickListener {
            pageViewModel.largeDecrementAggregateModifier()
            true
        }

        val modifierTextView = newView.findViewById<TextView>(R.id.modifierText)

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

//    private fun setupSaveButton(view: View)
//    {
//        val saveButton = view.findViewById<Button>(R.id.saveButton)
//        saveButton.visibility = View.INVISIBLE
//
//        saveButton.setOnClickListener {
//            Toast.makeText(context, "Todo", Toast.LENGTH_LONG).show()
//        }
//    }

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