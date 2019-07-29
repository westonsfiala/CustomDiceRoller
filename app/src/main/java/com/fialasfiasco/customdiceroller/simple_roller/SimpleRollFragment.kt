package com.fialasfiasco.customdiceroller.simple_roller

import android.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.widget.*
import androidx.preference.PreferenceManager

import com.fialasfiasco.customdiceroller.R
import kotlinx.android.synthetic.main.fragment_simple_roll.*
import kotlin.math.min
import android.graphics.Point
import android.view.*
import androidx.recyclerview.widget.GridLayoutManager
import com.fialasfiasco.customdiceroller.data.*
import com.fialasfiasco.customdiceroller.dice.*
import com.fialasfiasco.customdiceroller.helper.*
import com.fialasfiasco.customdiceroller.history.HistoryStamp

/**
 * A simple [Fragment] subclass.
 * Use the [SimpleRollFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class SimpleRollFragment : androidx.fragment.app.Fragment(),
    SimpleRollRecyclerViewAdapter.OnSimpleDieViewInteractionListener,
    UpDownButtonsFragment.UpDownButtonsListener,
    DiceRollerDialog.DiceRollerListener{

    private lateinit var pageViewModel: PageViewModel

    private var rollerDialog : DiceRollerDialog? = null
    private var modifierUpDownButtonsFragment : UpDownButtonsFragment? = null
    private var numDiceUpDownButtonsFragment : UpDownButtonsFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pageViewModel = activity?.run {
            ViewModelProviders.of(this).get(PageViewModel::class.java)
        } ?: throw Exception("Invalid Activity")
    }

    override fun onStart() {
        super.onStart()
        setupChildFragments()
        setupObservers()
        setupDiceButtons()
        setupDieEditFab()
    }

    private fun setupChildFragments()
    {
        modifierUpDownButtonsFragment = childFragmentManager.findFragmentById(R.id.modifierUpDownFragment) as UpDownButtonsFragment?
        modifierUpDownButtonsFragment?.setListener(this)

        numDiceUpDownButtonsFragment = childFragmentManager.findFragmentById(R.id.numDiceUpDownFragment) as UpDownButtonsFragment?
        numDiceUpDownButtonsFragment?.setListener(this)
    }

    private fun setupObservers() {
        pageViewModel.numDice.observe(this, Observer<Int> {
            updateNumDiceText()
        })

        updateNumDiceText()

        pageViewModel.modifier.observe(this, Observer<Int> {
            updateModifierText()
        })

        updateModifierText()

        pageViewModel.diePool.observe(this, Observer<Set<String>> {dieStrings ->
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)

            val prefEditor = preferences.edit()
            prefEditor.putStringSet(getString(R.string.dice_pool_key), dieStrings)
            prefEditor.apply()
        })

        // Notify about new items and then scroll to the top.
        pageViewModel.diePool.observe(this, Observer<Set<String>> {
            dieViewRecycler.adapter?.notifyDataSetChanged()
        })
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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_simple_roll, container, false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        rollerDialog?.kill()
        rollerDialog = null
    }

    private fun setupDiceButtons() {
        // Set the adapter
        dieViewRecycler.layoutManager = GridLayoutManager(context, pageViewModel.getItemsInRowSimple())
        dieViewRecycler.adapter = SimpleRollRecyclerViewAdapter(pageViewModel, this)
    }

    private fun setupDieEditFab()
    {
        if(pageViewModel.getEditEnabled())
        {
            editDieFab.show()
        }
        else
        {
            editDieFab.hide()
        }

        editDieFab.setOnClickListener {

            val builder = AlertDialog.Builder(context)

            builder.setTitle("Which type of die would you like to create?")

            builder.setNeutralButton("Cancel") { _, _ -> }
            builder.setPositiveButton("Simple") { _, _ ->
                EditDialogs(context, layoutInflater).createNumberDialog(
                    "Create Die",
                    MIN_ALLOWED_ROLLED_DICE_SIMPLE,
                    MAX_ALLOWED_ROLLED_DICE,
                    MIN_ALLOWED_ROLLED_DICE_SIMPLE,
                    object : EditDialogs.NumberDialogListener {
                        override fun respondToOK(outputValue: Int) {
                            createSimpleDie(outputValue)
                        }
                    })
            }
            builder.setNegativeButton("Custom") { _, _ ->
                EditDialogs(context, layoutInflater).createNameMinMaxDialog(
                    "Create Custom Die",
                    MIN_DICE_SIDE_COUNT_CUSTOM,
                    MAX_DICE_SIDE_COUNT,
                    object : EditDialogs.NameMinMaxDialogListener {
                        override fun respondToOK(name : String, min : Int, max : Int) {
                            createCustomDie(name,min,max)
                        }
                    })
            }

            builder.show()
        }

        editDieFab.setOnLongClickListener {
            val builder = AlertDialog.Builder(context)

            builder.setTitle("Reset Dice Pool?")
            builder.setMessage("This will restore dice pool to " + pageViewModel.getDefaultDiePoolString())

            builder.setPositiveButton("Cancel") { _, _ -> }

            builder.setNegativeButton("Reset") { dialog, _ ->
                dialog.dismiss()
                // Confirm the removal of die
                val confirmRemoveBuilder = AlertDialog.Builder(context)

                confirmRemoveBuilder.setTitle("Reset all die?")
                confirmRemoveBuilder.setMessage("Are you sure you wish to reset the dice?")

                confirmRemoveBuilder.setPositiveButton("Yes") { _, _ ->
                    pageViewModel.resetDiePool()
                }
                confirmRemoveBuilder.setNegativeButton("No") { _, _ -> }

                confirmRemoveBuilder.show()
            }

            val dialog = builder.create()
            dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
            dialog.show()
            true
        }
    }

    private fun createSimpleDie(dieNumber: Int)
    {
        if(dieNumber < MIN_DICE_SIDE_COUNT_SIMPLE || dieNumber > MAX_DICE_SIDE_COUNT)
        {
            Toast.makeText(context, "d$dieNumber, lies outside of allowed range", Toast.LENGTH_LONG).show()
            return
        }

        try {
            if(!pageViewModel.addDieToPool(SimpleDie(dieNumber))) {
                Toast.makeText(context, "d$dieNumber already exists", Toast.LENGTH_LONG).show()
            }
        }
        catch (error : DieLoadError)
        {
            Toast.makeText(context, "Problem making the d$dieNumber die", Toast.LENGTH_LONG).show()
        }
    }

    private fun createCustomDie(name : String, min : Int, max : Int)
    {
        if(min < MIN_DICE_SIDE_COUNT_CUSTOM || min > MAX_DICE_SIDE_COUNT)
        {
            Toast.makeText(context, "minimum lies outside of allowed range", Toast.LENGTH_LONG).show()
            return
        }

        if(max < MIN_DICE_SIDE_COUNT_CUSTOM || max > MAX_DICE_SIDE_COUNT)
        {
            Toast.makeText(context, "maximum lies outside of allowed range", Toast.LENGTH_LONG).show()
            return
        }

        when
        {
            name.contains(aggregateRollSplitString) -> {
                Toast.makeText(context,"Die name may not contain \"$aggregateRollSplitString\"", Toast.LENGTH_SHORT).show()
                return
            }
            name.contains(customDieSplitString) -> {
                Toast.makeText(context,"Die name may not contain \"$customDieSplitString\"", Toast.LENGTH_SHORT).show()
                return
            }
            name.contains(simpleDieSplitString) -> {
                Toast.makeText(context,"Die name may not contain \"$simpleDieSplitString\"", Toast.LENGTH_SHORT).show()
                return
            }
        }

        try {
            if(!pageViewModel.addDieToPool(CustomDie(name, min, max))) {
                Toast.makeText(context, "$name die already exists", Toast.LENGTH_LONG).show()
            }
        }
        catch (error : DieLoadError)
        {
            Toast.makeText(context, "Problem making the $name die", Toast.LENGTH_LONG).show()
        }
    }

    override fun upButtonClick(upDownButtonsFragment: UpDownButtonsFragment) {
        when (upDownButtonsFragment)
        {
            numDiceUpDownButtonsFragment -> pageViewModel.incrementNumDice()
            modifierUpDownButtonsFragment -> pageViewModel.incrementModifier()
        }
    }

    override fun upButtonLongClick(upDownButtonsFragment: UpDownButtonsFragment) {
        when (upDownButtonsFragment)
        {
            numDiceUpDownButtonsFragment -> pageViewModel.largeIncrementNumDice()
            modifierUpDownButtonsFragment -> pageViewModel.largeIncrementModifier()
        }
    }

    override fun downButtonClick(upDownButtonsFragment: UpDownButtonsFragment) {
        when (upDownButtonsFragment)
        {
            numDiceUpDownButtonsFragment -> pageViewModel.decrementNumDice()
            modifierUpDownButtonsFragment -> pageViewModel.decrementModifier()
        }
    }

    override fun downButtonLongClick(upDownButtonsFragment: UpDownButtonsFragment) {
        when (upDownButtonsFragment)
        {
            numDiceUpDownButtonsFragment -> pageViewModel.largeDecrementNumDice()
            modifierUpDownButtonsFragment -> pageViewModel.largeDecrementModifier()
        }
    }

    override fun displayTextClick(upDownButtonsFragment: UpDownButtonsFragment) {
        when (upDownButtonsFragment)
        {
            numDiceUpDownButtonsFragment -> {
                EditDialogs(context, layoutInflater).createNumberDialog(
                "Number of Dice",
                MIN_ALLOWED_ROLLED_DICE_SIMPLE,
                MAX_ALLOWED_ROLLED_DICE,
                pageViewModel.getNumDice(),
                object : EditDialogs.NumberDialogListener {
                    override fun respondToOK(outputValue: Int) {
                        pageViewModel.setNumDiceExact(outputValue)
                    }
                })
            }
            modifierUpDownButtonsFragment ->  {
                EditDialogs(context, layoutInflater).createNumberDialog(
                "Modifier",
                MIN_MODIFIER,
                MAX_MODIFIER,
                pageViewModel.getModifier(),
                object : EditDialogs.NumberDialogListener {
                    override fun respondToOK(outputValue: Int) {
                        pageViewModel.setModifierExact(outputValue)
                    }
                })
            }
        }
    }

    private fun updateNumDiceText() {
        numDiceUpDownButtonsFragment?.setDisplayText(String.format("%dd", pageViewModel.getNumDice()))
    }

    private fun updateModifierText() {
        modifierUpDownButtonsFragment?.setDisplayText(getModifierString(pageViewModel.getModifier()))
    }

    override fun onDieClicked(die: Die) {
        val aggregateRoll = Roll("", pageViewModel.getModifier())
        aggregateRoll.addDieToRoll(die,
            RollProperties(pageViewModel.getNumDice(), 0, 0, 0)
        )

        if (pageViewModel.getShakeEnabled()) {
            rollerDialog?.runShakeRoller(aggregateRoll)
        } else {
            rollerDialog?.runRollDisplay(aggregateRoll)
        }
    }

    override fun onDieLongClick(die: Die) {
        val builder = AlertDialog.Builder(context)

        builder.setTitle("Die Info - " + die.getDisplayName())
        builder.setMessage(die.getInfo())

        builder.setPositiveButton("OK") { _, _ -> }

        // Don't let the user remove all of the dice.
        if(pageViewModel.getEditEnabled() && pageViewModel.getInnerDiceSize() > 1) {
            builder.setNegativeButton("Remove Die") { dialog, _ ->
                dialog.dismiss()
                // Confirm the removal of die
                val confirmRemoveBuilder = AlertDialog.Builder(context)

                confirmRemoveBuilder.setTitle("Remove - " + die.getDisplayName())
                confirmRemoveBuilder.setMessage("Are you sure you wish to remove the " + die.getDisplayName())

                confirmRemoveBuilder.setPositiveButton("Yes") { _, _ ->
                    pageViewModel.removeDieFromPool(die)
                }
                confirmRemoveBuilder.setNegativeButton("No") { _, _ -> }

                confirmRemoveBuilder.show()
            }
        }

        builder.show()
    }

    override fun onRollResult(stamp: HistoryStamp) {
        pageViewModel.addRollHistory(stamp)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         * @return A new instance of fragment SimpleRollFragment.
         */
        @JvmStatic
        fun newInstance() = SimpleRollFragment()
    }
}
