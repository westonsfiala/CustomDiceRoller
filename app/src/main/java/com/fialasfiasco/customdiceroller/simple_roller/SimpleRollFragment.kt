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
import com.fialasfiasco.customdiceroller.helper.CustomDieEditDialog
import com.fialasfiasco.customdiceroller.helper.DiceRollerDialog
import com.fialasfiasco.customdiceroller.helper.NumberDialog
import com.fialasfiasco.customdiceroller.helper.UpDownButtonsFragment
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pageViewModel = activity?.run {
            ViewModelProviders.of(this).get(PageViewModel::class.java)
        } ?: throw Exception("Invalid Activity")
    }

    override fun onStart() {
        modifierUpDownButtonsFragment = fragmentManager?.findFragmentById(R.id.modifierUpDownFragment) as UpDownButtonsFragment?
        modifierUpDownButtonsFragment?.setListener(this)

        setupObservers()
        createRollerDialog()
        setupCreatedView()
        alignViewsWithSavedSettings()
        super.onStart()
    }

    private fun alignViewsWithSavedSettings() {

        if(pageViewModel.getEditEnabled())
        {
            editDieFab.show()
        }
        else
        {
            editDieFab.hide()
        }

        dieViewRecycler.layoutManager = GridLayoutManager(context, pageViewModel.getItemsInRowSimple())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        rollerDialog?.kill()
        rollerDialog = null
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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_simple_roll, container, false)
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

    private fun setupCreatedView() {
        setupDiceButtons()
        setupDieEditFab()
        setupUpAndDownButtons()
        setupModifierDialogs()
    }

    private fun setupDiceButtons() {

        // Set the adapter
        dieViewRecycler.layoutManager = GridLayoutManager(context, pageViewModel.getItemsInRowSimple())
        dieViewRecycler.adapter =
            SimpleRollRecyclerViewAdapter(pageViewModel, this)


        // Notify about new items and then scroll to the top.
        pageViewModel.diePool.observe(this, Observer<Set<String>> {
            dieViewRecycler.adapter?.notifyDataSetChanged()
        })
    }

    private fun setupDieEditFab()
    {
        editDieFab.setOnClickListener {

            val builder = AlertDialog.Builder(context)

            builder.setTitle("Which type of die would you like to create?")

            builder.setNeutralButton("Cancel") { _, _ -> }
            builder.setPositiveButton("Simple") { _, _ ->
                NumberDialog(context, layoutInflater).createDialog(
                    "Create Die",
                    MIN_ALLOWED_ROLLED_DICE_SIMPLE,
                    MAX_ALLOWED_ROLLED_DICE,
                    MIN_ALLOWED_ROLLED_DICE_SIMPLE,
                    object : NumberDialog.NumberDialogListener {
                        override fun respondToOK(outputValue: Int) {
                            createSimpleDie(outputValue)
                        }
                    })
            }
            builder.setNegativeButton("Custom") { _, _ ->
                CustomDieEditDialog(context, layoutInflater).createDialog(
                    "Create Custom Die",
                    MIN_DICE_SIDE_COUNT_CUSTOM,
                    MAX_DICE_SIDE_COUNT,
                    object : CustomDieEditDialog.CustomDieEditListener {
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
            if(pageViewModel.addDieToPool(SimpleDie(dieNumber)).not()) {
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

        try {
            if(pageViewModel.addDieToPool(CustomDie(name, min, max)).not()) {
                Toast.makeText(context, "$name die already exists", Toast.LENGTH_LONG).show()
            }
        }
        catch (error : DieLoadError)
        {
            Toast.makeText(context, "Problem making the $name die", Toast.LENGTH_LONG).show()
        }
    }

    override fun upButtonClick(upDownButtonsFragment: UpDownButtonsFragment) {
        pageViewModel.incrementModifier()
    }

    override fun upButtonLongClick(upDownButtonsFragment: UpDownButtonsFragment) {
        pageViewModel.largeIncrementModifier()
    }

    override fun downButtonClick(upDownButtonsFragment: UpDownButtonsFragment) {
        pageViewModel.decrementModifier()
    }

    override fun downButtonLongClick(upDownButtonsFragment: UpDownButtonsFragment) {
        pageViewModel.largeDecrementModifier()
    }

    override fun displayTextClick(upDownButtonsFragment: UpDownButtonsFragment) {
        NumberDialog(context, layoutInflater).createDialog(
                "Modifier",
                MIN_MODIFIER,
                MAX_MODIFIER,
                pageViewModel.getModifier(),
                object : NumberDialog.NumberDialogListener {
                    override fun respondToOK(outputValue: Int) {
                        pageViewModel.setModifierExact(outputValue)
                    }
                })
    }

    private fun setupUpAndDownButtons() {
        diceUpButton.setOnClickListener {
            pageViewModel.incrementNumDice()
        }

        diceUpButton.setOnLongClickListener {
            pageViewModel.largeIncrementNumDice()
            true
        }

        diceDownButton.setOnClickListener {
            pageViewModel.decrementNumDice()
        }

        diceDownButton.setOnLongClickListener {
            pageViewModel.largeDecrementNumDice()
            true
        }

//        val modifierUpBut = view.findViewById<ImageButton>(R.id.modifierUpButton)
//        modifierUpBut.setOnClickListener {
//            pageViewModel.incrementModifier()
//        }
//
//        modifierUpBut.setOnLongClickListener {
//            pageViewModel.largeIncrementModifier()
//            true
//        }
//
//        val modifierDownBut = view.findViewById<ImageButton>(R.id.modifierDownButton)
//        modifierDownBut.setOnClickListener {
//            pageViewModel.decrementModifier()
//        }
//
//        modifierDownBut.setOnLongClickListener {
//            pageViewModel.largeDecrementModifier()
//            true
//        }

        updateNumDiceText()
        updateModifierText()
    }

    private fun setupModifierDialogs() {
        numDiceText.setOnClickListener {
            NumberDialog(context, layoutInflater).createDialog(
                "Number of Dice",
                MIN_ALLOWED_ROLLED_DICE_SIMPLE,
                MAX_ALLOWED_ROLLED_DICE,
                pageViewModel.getNumDice(),
                object : NumberDialog.NumberDialogListener {
                    override fun respondToOK(outputValue: Int) {
                        pageViewModel.setNumDiceExact(outputValue)
                    }
                })
        }

//        val modifierTextView = view.findViewById<TextView>(R.id.modifierText)
//
//        modifierTextView.setOnClickListener {
//            NumberDialog(context, layoutInflater).createDialog(
//                "Modifier",
//                MIN_MODIFIER,
//                MAX_MODIFIER,
//                pageViewModel.getModifier(),
//                object : NumberDialog.NumberDialogListener {
//                    override fun respondToOK(outputValue: Int) {
//                        pageViewModel.setModifierExact(outputValue)
//                    }
//                })
//        }
    }

    private fun updateNumDiceText() {
        numDiceText.text = String.format("%dd", pageViewModel.getNumDice())
    }

    private fun updateModifierText() {
        when {
            pageViewModel.getModifier() >= 0 -> modifierUpDownButtonsFragment?.setDisplayText(String.format("+%d", pageViewModel.getModifier()))
            pageViewModel.getModifier() < 0 -> modifierUpDownButtonsFragment?.setDisplayText(String.format("%d", pageViewModel.getModifier()))
        }
    }

    override fun onDieClicked(die: InnerDie) {
        if (pageViewModel.getShakeEnabled()) {
            rollerDialog?.runShakeRoller(arrayOf(AggregateDie(die, pageViewModel.getNumDice())), pageViewModel.getModifier())
        } else {
            rollerDialog?.runRollDisplay(arrayOf(AggregateDie(die, pageViewModel.getNumDice())), pageViewModel.getModifier())
        }
    }

    override fun onDieLongClick(die: InnerDie) {
        val builder = AlertDialog.Builder(context)

        builder.setTitle("Die Info - " + die.getName())
        builder.setMessage(die.getInfo())

        builder.setPositiveButton("OK") { _, _ -> }

        // Don't let the user remove all of the dice.
        if(pageViewModel.getEditEnabled() && pageViewModel.getInnerDiceSize() > 1) {
            builder.setNegativeButton("Remove Die") { dialog, _ ->
                dialog.dismiss()
                // Confirm the removal of die
                val confirmRemoveBuilder = AlertDialog.Builder(context)

                confirmRemoveBuilder.setTitle("Remove - " + die.getName())
                confirmRemoveBuilder.setMessage("Are you sure you wish to remove the " + die.getName())

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
        fun newInstance() =
            SimpleRollFragment().apply {
                arguments = Bundle().apply {
                    //putString(ARG_PARAM1, param1)
                }
            }
    }
}
