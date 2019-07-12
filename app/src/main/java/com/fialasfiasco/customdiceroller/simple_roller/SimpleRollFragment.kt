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
import androidx.recyclerview.widget.RecyclerView
import com.fialasfiasco.customdiceroller.data.*
import com.fialasfiasco.customdiceroller.helper.DiceRollerDialog
import com.fialasfiasco.customdiceroller.helper.NumberDialog
import com.fialasfiasco.customdiceroller.history.HistoryStamp
import com.google.android.material.floatingactionbutton.FloatingActionButton

/**
 * A simple [Fragment] subclass.
 * Use the [SimpleRollFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class SimpleRollFragment : androidx.fragment.app.Fragment(),
    SimpleRollRecyclerViewAdapter.OnSimpleDieViewInteractionListener,
    DiceRollerDialog.DiceRollerListener{

    private lateinit var pageViewModel: PageViewModel

    private var rollerDialog : DiceRollerDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pageViewModel = activity?.run {
            ViewModelProviders.of(this).get(PageViewModel::class.java)
        } ?: throw Exception("Invalid Activity")
    }

    override fun onStart() {
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
        val createdView = inflater.inflate(R.layout.fragment_simple_roll, container, false)

        setupObservers(createdView)
        createRollerDialog()
        return setupCreatedView(createdView)
    }

    private fun setupObservers(newView: View) {
        pageViewModel.numDice.observe(this, Observer<Int> {
            updateNumDiceText(view!!)
        })

        updateNumDiceText(newView)

        pageViewModel.modifier.observe(this, Observer<Int> {
            updateModifierText(view!!)
        })

        updateModifierText(newView)

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

    private fun setupCreatedView(view: View): View {
        setupDiceButtons(view)
        setupDieEditFab(view)
        setupUpAndDownButtons(view)
        setupModifierDialogs(view)
        return view
    }

    private fun setupDiceButtons(view: View) {
        val recycler = view.findViewById<RecyclerView>(R.id.dieViewRecycler)

        // Set the adapter
        recycler.layoutManager = GridLayoutManager(context, pageViewModel.getItemsInRowSimple())
        recycler.adapter =
            SimpleRollRecyclerViewAdapter(pageViewModel, this)


        // Notify about new items and then scroll to the top.
        pageViewModel.diePool.observe(this, Observer<Set<String>> {
            recycler.adapter?.notifyDataSetChanged()
        })
    }

    private fun setupDieEditFab(view: View)
    {
        val fab = view.findViewById<FloatingActionButton>(R.id.editDieFab)

        fab.setOnClickListener {
            NumberDialog(context, layoutInflater).createDialog(
                "Create Die",
                MIN_DICE,
                MAX_DICE,
                MIN_DICE,
                object : NumberDialog.NumberDialogListener {
                    override fun respondToOK(outputValue: Int) {
                        createSimpleDie(outputValue)
                    }
                })
        }

        fab.setOnLongClickListener {
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
        if(dieNumber < 1 || dieNumber > 100)
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

    private fun setupUpAndDownButtons(view: View) {
        val diceUpBut = view.findViewById<ImageButton>(R.id.diceUpButton)
        diceUpBut.setOnClickListener {
            pageViewModel.setNumDice(pageViewModel.getNumDice() + 1)
        }

        diceUpBut.setOnLongClickListener {
            pageViewModel.setNumDice(pageViewModel.getNumDice() + MAX_DICE)
            true
        }

        val diceDownBut = view.findViewById<ImageButton>(R.id.diceDownButton)
        diceDownBut.setOnClickListener {
            pageViewModel.setNumDice(pageViewModel.getNumDice() - 1)
        }

        diceDownBut.setOnLongClickListener {
            pageViewModel.setNumDice(pageViewModel.getNumDice() - MAX_DICE)
            true
        }

        val modifierUpBut = view.findViewById<ImageButton>(R.id.modifierUpButton)
        modifierUpBut.setOnClickListener {
            pageViewModel.setModifier(pageViewModel.getModifier() + 1)
        }

        modifierUpBut.setOnLongClickListener {
            if (pageViewModel.getModifier() >= 0) {
                pageViewModel.setModifier(pageViewModel.getModifier() + MAX_MODIFIER)
            } else {
                pageViewModel.setModifier(START_MODIFIER)
            }
            true
        }

        val modifierDownBut = view.findViewById<ImageButton>(R.id.modifierDownButton)
        modifierDownBut.setOnClickListener {
            pageViewModel.setModifier(pageViewModel.getModifier() - 1)
        }

        modifierDownBut.setOnLongClickListener {
            if (pageViewModel.getModifier() <= 0) {
                pageViewModel.setModifier(pageViewModel.getModifier() - MAX_MODIFIER)
            } else {
                pageViewModel.setModifier(START_MODIFIER)
            }
            true
        }

        updateNumDiceText(view)
        updateModifierText(view)
    }

    private fun setupModifierDialogs(view: View) {
        val numDiceTextView = view.findViewById<TextView>(R.id.numDiceText)

        numDiceTextView.setOnClickListener {
            NumberDialog(context, layoutInflater).createDialog(
                "Number of Dice",
                MIN_DICE,
                MAX_DICE,
                pageViewModel.getNumDice(),
                object : NumberDialog.NumberDialogListener {
                    override fun respondToOK(outputValue: Int) {
                        pageViewModel.setNumDice(outputValue)
                    }
                })
        }

        val modifierTextView = view.findViewById<TextView>(R.id.modifierText)

        modifierTextView.setOnClickListener {
            NumberDialog(context, layoutInflater).createDialog(
                "Modifier",
                MIN_MODIFIER,
                MAX_MODIFIER,
                pageViewModel.getModifier(),
                object : NumberDialog.NumberDialogListener {
                    override fun respondToOK(outputValue: Int) {
                        pageViewModel.setModifier(outputValue)
                    }
                })
        }
    }

    private fun updateNumDiceText(view: View) {
        val diceText = view.findViewById<TextView>(R.id.numDiceText)
        diceText.text = String.format("%dd", pageViewModel.getNumDice())
    }

    private fun updateModifierText(view: View) {
        val modifierText = view.findViewById<TextView>(R.id.modifierText)
        when {
            pageViewModel.getModifier() >= 0 -> modifierText.text = String.format("+%d", pageViewModel.getModifier())
            pageViewModel.getModifier() < 0 -> modifierText.text = String.format("%d", pageViewModel.getModifier())
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
