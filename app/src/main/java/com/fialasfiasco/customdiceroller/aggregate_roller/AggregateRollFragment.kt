package com.fialasfiasco.customdiceroller.aggregate_roller

import android.graphics.Point
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import com.fialasfiasco.customdiceroller.R
import com.fialasfiasco.customdiceroller.data.*
import com.fialasfiasco.customdiceroller.helper.DiceRollerDialog
import com.fialasfiasco.customdiceroller.helper.EditDialogs
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
    UpDownButtonsFragment.UpDownButtonsListener,
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
        super.onStart()
        setupRecycler()
        setupChildFragments()
        setupObservers()
        setupSaveButton()
        setupRollButton()
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
        setupRollerDialog()
        return inflater.inflate(R.layout.fragment_aggregate_roll, container, false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        rollerDialog?.kill()
        rollerDialog = null
    }

    private fun setupChildFragments()
    {
        aggregateModifierUpDownButtonsFragment = childFragmentManager.findFragmentById(R.id.aggregateModifierUpDownFragment) as UpDownButtonsFragment?
        aggregateModifierUpDownButtonsFragment?.setListener(this)
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

    private fun setupObservers()
    {
        pageViewModel.aggregateModifier.observe(this, Observer<Int> {
            updateModifierText()
        })

        updateModifierText()
    }

    private fun setupSaveButton()
    {
        saveButton.setOnClickListener {
            EditDialogs(context, layoutInflater).createNameDialog(
                "Name of roll",
                object : EditDialogs.NameDialogListener {
                    override fun respondToOK(name: String) {
                        try {
                            val newRoll = pageViewModel.createAggregateRollFromCustomRollerState(name)
                            if(!pageViewModel.addSavedRollToPool(newRoll)) {
                                Toast.makeText(context, "$name roll already exists", Toast.LENGTH_LONG).show()
                            }
                        }
                        catch (error : DieLoadError)
                        {
                            Toast.makeText(context, "Problem making the $name roll", Toast.LENGTH_LONG).show()
                        }
                    }
                })
        }
    }

    private fun setupRollButton()
    {
        rollButton.setOnClickListener {
            val aggregateRoll = pageViewModel.createAggregateRollFromCustomRollerState("")

            if (pageViewModel.getShakeEnabled()) {
                rollerDialog?.runShakeRoller(
                    aggregateRoll
                )
            } else {
                rollerDialog?.runRollDisplay(
                    aggregateRoll
                )
            }
        }
    }

    override fun upButtonClick(upDownButtonsFragment: UpDownButtonsFragment) {
        when (upDownButtonsFragment)
        {
            aggregateModifierUpDownButtonsFragment -> pageViewModel.incrementAggregateModifier()
        }
    }

    override fun upButtonLongClick(upDownButtonsFragment: UpDownButtonsFragment) {
        when (upDownButtonsFragment)
        {
            aggregateModifierUpDownButtonsFragment -> pageViewModel.largeIncrementAggregateModifier()
        }
    }

    override fun downButtonClick(upDownButtonsFragment: UpDownButtonsFragment) {
        when (upDownButtonsFragment)
        {
            aggregateModifierUpDownButtonsFragment -> pageViewModel.decrementAggregateModifier()
        }
    }

    override fun downButtonLongClick(upDownButtonsFragment: UpDownButtonsFragment) {
        when (upDownButtonsFragment)
        {
            aggregateModifierUpDownButtonsFragment -> pageViewModel.largeDecrementAggregateModifier()
        }
    }

    override fun displayTextClick(upDownButtonsFragment: UpDownButtonsFragment) {
        when (upDownButtonsFragment)
        {
            aggregateModifierUpDownButtonsFragment -> {
                EditDialogs(context, layoutInflater).createNumberDialog(
                    "Modifier",
                    MIN_MODIFIER,
                    MAX_MODIFIER,
                    pageViewModel.getAggregateModifier(),
                    object : EditDialogs.NumberDialogListener {
                        override fun respondToOK(outputValue: Int) {
                            pageViewModel.setAggregateModifierExact(outputValue)
                        }
                    })
            }
        }
    }

    private fun updateModifierText()
    {
        when {
            pageViewModel.getAggregateModifier() >= 0 -> aggregateModifierUpDownButtonsFragment?.setDisplayText(String.format("+%d", pageViewModel.getAggregateModifier()))
            pageViewModel.getAggregateModifier() < 0 -> aggregateModifierUpDownButtonsFragment?.setDisplayText(String.format("%d", pageViewModel.getAggregateModifier()))
        }
    }

    override fun onDisplayTextClicked(holder : AggregateRollRecyclerViewAdapter.AggregateDieViewHolder, position: Int) {
        EditDialogs(context, layoutInflater).createNumberDialog(
            "Number of Dice",
            MIN_ALLOWED_ROLLED_DICE_AGGREGATE,
            MAX_ALLOWED_ROLLED_DICE,
            pageViewModel.getAggregateDieCount(position),
            object : EditDialogs.NumberDialogListener {
                override fun respondToOK(outputValue: Int) {
                    try {
                        pageViewModel.setAggregateDieCountExact(position, outputValue)
                        holder.mModText.text = pageViewModel.getAggregateDieCount(position).toString()
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