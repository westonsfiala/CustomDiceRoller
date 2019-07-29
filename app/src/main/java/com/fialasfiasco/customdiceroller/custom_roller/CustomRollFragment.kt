package com.fialasfiasco.customdiceroller.custom_roller

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
import com.fialasfiasco.customdiceroller.dice.DieLoadError
import com.fialasfiasco.customdiceroller.dice.saveSplitStrings
import com.fialasfiasco.customdiceroller.helper.DiceRollerDialog
import com.fialasfiasco.customdiceroller.helper.EditDialogs
import com.fialasfiasco.customdiceroller.helper.UpDownButtonsFragment
import com.fialasfiasco.customdiceroller.history.HistoryStamp
import kotlinx.android.synthetic.main.fragment_custom_roll.*
import java.lang.NumberFormatException
import kotlin.math.min

/**
 * A fragment representing a list of Items.
 */
class CustomRollFragment : Fragment(),
    CustomRollRecyclerViewAdapter.CustomRollInterfaceListener,
    UpDownButtonsFragment.UpDownButtonsListener,
    DiceRollerDialog.DiceRollerListener
{
    private lateinit var pageViewModel: PageViewModel

    private var customModifierUpDownButtonsFragment : UpDownButtonsFragment? = null

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
        return inflater.inflate(R.layout.fragment_custom_roll, container, false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        rollerDialog?.kill()
        rollerDialog = null
    }

    private fun setupChildFragments()
    {
        customModifierUpDownButtonsFragment = childFragmentManager.findFragmentById(R.id.customModifierUpDownFragment) as UpDownButtonsFragment?
        customModifierUpDownButtonsFragment?.setListener(this)
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
        customRecycler.layoutManager = GridLayoutManager(context, pageViewModel.getItemsInRowCustom())
        customRecycler.adapter = CustomRollRecyclerViewAdapter(pageViewModel, this)
    }

    private fun setupObservers()
    {
        pageViewModel.customModifier.observe(this, Observer<Int> {
            updateModifierText()
        })

        updateModifierText()

        // Notify about new items and then scroll to the top.
        pageViewModel.diePool.observe(this, Observer<Set<String>> {
            customRecycler.adapter?.notifyDataSetChanged()
        })

    }

    private fun setupSaveButton()
    {
        saveButton.setOnClickListener {

            val tempRoll = pageViewModel.createCustomRollFromCustomRollerState("")
            if(tempRoll.getTotalDiceInRoll() == 0)
            {
                Toast.makeText(context, "A roll must have at least one die", Toast.LENGTH_SHORT).show()
            } else {
                EditDialogs(context, layoutInflater).createNameDialog(
                    "Name of roll",
                    object : EditDialogs.NameDialogListener {
                        override fun respondToOK(name: String) {
                            createSavedRoll(name)
                        }
                    })
            }
        }
    }

    private fun createSavedRoll(name: String) {

        for(disallowedNames in saveSplitStrings)
        {
            if(name.contains(disallowedNames))
            {
                Toast.makeText(context,"Roll names may not contain \"$disallowedNames\"", Toast.LENGTH_SHORT).show()
                return
            }
        }

        try {
            val newRoll = pageViewModel.createCustomRollFromCustomRollerState(name)
            if(!pageViewModel.addSavedRollToPool(newRoll)) {
                Toast.makeText(context, "$name roll already exists", Toast.LENGTH_LONG).show()
            }
        }
        catch (error : DieLoadError)
        {
            Toast.makeText(context, "Problem making the $name roll", Toast.LENGTH_LONG).show()
        }
    }

    private fun setupRollButton()
    {
        rollButton.setOnClickListener {
            val customRoll = pageViewModel.createCustomRollFromCustomRollerState("")

            if (pageViewModel.getShakeEnabled()) {
                rollerDialog?.runShakeRoller(
                    customRoll
                )
            } else {
                rollerDialog?.runRollDisplay(
                    customRoll
                )
            }
        }
    }

    override fun upButtonClick(upDownButtonsFragment: UpDownButtonsFragment) {
        when (upDownButtonsFragment)
        {
            customModifierUpDownButtonsFragment -> pageViewModel.incrementCustomModifier()
        }
    }

    override fun upButtonLongClick(upDownButtonsFragment: UpDownButtonsFragment) {
        when (upDownButtonsFragment)
        {
            customModifierUpDownButtonsFragment -> pageViewModel.largeIncrementCustomModifier()
        }
    }

    override fun downButtonClick(upDownButtonsFragment: UpDownButtonsFragment) {
        when (upDownButtonsFragment)
        {
            customModifierUpDownButtonsFragment -> pageViewModel.decrementCustomModifier()
        }
    }

    override fun downButtonLongClick(upDownButtonsFragment: UpDownButtonsFragment) {
        when (upDownButtonsFragment)
        {
            customModifierUpDownButtonsFragment -> pageViewModel.largeDecrementCustomModifier()
        }
    }

    override fun displayTextClick(upDownButtonsFragment: UpDownButtonsFragment) {
        when (upDownButtonsFragment)
        {
            customModifierUpDownButtonsFragment -> {
                EditDialogs(context, layoutInflater).createNumberDialog(
                    "Modifier",
                    MIN_MODIFIER,
                    MAX_MODIFIER,
                    pageViewModel.getCustomModifier(),
                    object : EditDialogs.NumberDialogListener {
                        override fun respondToOK(outputValue: Int) {
                            pageViewModel.setCustomModifierExact(outputValue)
                        }
                    })
            }
        }
    }

    private fun updateModifierText()
    {
        when {
            pageViewModel.getCustomModifier() >= 0 -> customModifierUpDownButtonsFragment?.setDisplayText(String.format("+%d", pageViewModel.getCustomModifier()))
            pageViewModel.getCustomModifier() < 0 -> customModifierUpDownButtonsFragment?.setDisplayText(String.format("%d", pageViewModel.getCustomModifier()))
        }
    }

    override fun onDisplayTextClicked(holder : CustomRollRecyclerViewAdapter.CustomDieViewHolder, position: Int) {
        EditDialogs(context, layoutInflater).createNumberDialog(
            "Number of Dice",
            MIN_ALLOWED_ROLLED_DICE_AGGREGATE,
            MAX_ALLOWED_ROLLED_DICE,
            pageViewModel.getCustomDieCount(position),
            object : EditDialogs.NumberDialogListener {
                override fun respondToOK(outputValue: Int) {
                    try {
                        pageViewModel.setCustomDieCountExact(position, outputValue)
                        holder.mModText.text = pageViewModel.getCustomDieCount(position).toString()
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
        fun newInstance() = CustomRollFragment()
    }
}