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
import androidx.recyclerview.widget.LinearLayoutManager
import com.fialasfiasco.customdiceroller.R
import com.fialasfiasco.customdiceroller.data.*
import com.fialasfiasco.customdiceroller.dice.DieLoadError
import com.fialasfiasco.customdiceroller.dice.SimpleDie
import com.fialasfiasco.customdiceroller.dice.saveSplitStrings
import com.fialasfiasco.customdiceroller.helper.DiceRollerDialog
import com.fialasfiasco.customdiceroller.helper.EditDialogs
import com.fialasfiasco.customdiceroller.history.HistoryStamp
import kotlinx.android.synthetic.main.fragment_custom_roll.*
import java.lang.NumberFormatException
import kotlin.math.min

/**
 * A fragment representing a list of Items.
 */
class CustomRollFragment : Fragment(),
    CustomRollRecyclerViewAdapter.CustomRollInterfaceListener,
    DiceRollerDialog.DiceRollerListener
{
    private lateinit var pageViewModel: PageViewModel

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
        setupObservers()
        setupAddDieButton()
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
        customRecycler.layoutManager = LinearLayoutManager(context)
        customRecycler.adapter = CustomRollRecyclerViewAdapter(pageViewModel, this)
    }

    private fun setupObservers()
    {
        // Notify about new items and then scroll to the top.
        pageViewModel.customDiePool.observe(this, Observer<Int> {
            //customRecycler.adapter?.notifyDataSetChanged()
        })
    }

    private fun setupAddDieButton() {
        addDieButton.setOnClickListener {
            if(!pageViewModel.addDieToCustomRoll(SimpleDie(20))) {
                Toast.makeText(context, "Die is already in Roll", Toast.LENGTH_SHORT).show()
            } else {
                customRecycler.adapter?.notifyDataSetChanged()
            }
        }
    }

    private fun setupSaveButton()
    {
        saveButton.setOnClickListener {

            val tempRoll = pageViewModel.createRollFromCustomRollerState("")
            if(tempRoll.getTotalDiceInRoll() == 0)
            {
                Toast.makeText(context, "A roll must have at least one die", Toast.LENGTH_SHORT).show()
            } else {
                EditDialogs(context, layoutInflater).createNameDialog(
                    "Name of roll",
                    "",
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
            val newRoll = pageViewModel.createRollFromCustomRollerState(name)
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
            val customRoll = pageViewModel.createRollFromCustomRollerState("")

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

    override fun onNumDiceDisplayTextClicked(holder : CustomRollRecyclerViewAdapter.CustomDieViewHolder, position: Int) {
        EditDialogs(context, layoutInflater).createNumberDialog(
            "Number of Dice",
            "",
            MIN_ALLOWED_ROLLED_DICE_AGGREGATE,
            MAX_ALLOWED_ROLLED_DICE,
            pageViewModel.getCustomDieDieCount(position),
            object : EditDialogs.NumberDialogListener {
                override fun respondToOK(outputValue: Int) {
                    try {
                        pageViewModel.setCustomDieCountExact(position, outputValue)
                        holder.mNumDiceDisplayText.text = pageViewModel.getCustomDieDieCount(position).toString()
                    } catch (error: NumberFormatException) {
                    }
                }
            })
    }

    override fun onModifierDisplayTextClicked(holder : CustomRollRecyclerViewAdapter.CustomDieViewHolder, position: Int) {
        EditDialogs(context, layoutInflater).createNumberDialog(
            "Modifier",
            "",
            MIN_MODIFIER,
            MAX_MODIFIER,
            pageViewModel.getCustomDieDieCount(position),
            object : EditDialogs.NumberDialogListener {
                override fun respondToOK(outputValue: Int) {
                    try {
                        pageViewModel.setCustomDieModifierExact(position, outputValue)
                        holder.mModifierDisplayText.text = pageViewModel.getCustomDieModifier(position).toString()
                    } catch (error: NumberFormatException) {
                    }
                }
            })
    }

    override fun onDropDiceButtonClicked(holder: CustomRollRecyclerViewAdapter.CustomDieViewHolder, position: Int) {
        Toast.makeText(context,"Implement Me!", Toast.LENGTH_SHORT).show()
    }

    override fun onRollResult(stamp: HistoryStamp) {
        pageViewModel.addRollHistory(stamp)
    }

    companion object {

        @JvmStatic
        fun newInstance() = CustomRollFragment()
    }
}