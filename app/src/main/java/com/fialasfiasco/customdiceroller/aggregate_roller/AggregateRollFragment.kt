package com.fialasfiasco.customdiceroller.aggregate_roller

import android.graphics.Point
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
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
        setupRecycler()
        setupChildFragments()
        setupRollerDialog()
        setupRecycler()
        setupObservers()
        setupSaveButton()
        setupRollButton()
        super.onStart()
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
        return inflater.inflate(R.layout.fragment_aggregate_roll, container, false)
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
            //val aggregateDieList = getAggregateDies()
        }
    }

    private fun setupRollButton()
    {
        rollButton.setOnClickListener {
            val aggregateRoll = pageViewModel.createAggregateRollFromCustomRollerState("", pageViewModel.getAggregateModifier())

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
        NumberDialog(context, layoutInflater).createDialog(
            "Number of Dice",
            MIN_ALLOWED_ROLLED_DICE_AGGREGATE,
            MAX_ALLOWED_ROLLED_DICE,
            pageViewModel.getAggregateDieCount(position),
            object : NumberDialog.NumberDialogListener {
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