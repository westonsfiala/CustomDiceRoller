package com.fialasfiasco.customdiceroller.simple_roller

import android.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.widget.*
import androidx.preference.PreferenceManager

import com.fialasfiasco.customdiceroller.R
import kotlinx.android.synthetic.main.fragment_simple_roll.*
import kotlin.math.min
import android.graphics.Point
import android.view.*
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.GridLayoutManager
import com.fialasfiasco.customdiceroller.data.*
import com.fialasfiasco.customdiceroller.dice.*
import com.fialasfiasco.customdiceroller.helper.*
import com.fialasfiasco.customdiceroller.history.HistoryStamp
import kotlinx.android.synthetic.main.layout_up_down_buttons.view.*

class SimpleRollFragment : androidx.fragment.app.Fragment(),
    SimpleRollRecyclerViewAdapter.OnSimpleDieViewInteractionListener,
    UpDownButtonsHelper.UpDownButtonsListener,
    DiceRollerDialog.DiceRollerListener,
    RollPropertyHelper.PropertyChangeListener
{

    private lateinit var pageViewModel: PageViewModel

    private var rollerDialog : DiceRollerDialog? = null
    private val modifierUpDownButtonsID = 0
    private val numDiceUpDownButtonsID = 1

    private lateinit var fabOpen : Animation
    private lateinit var fabClose : Animation
    private lateinit var rotateForward : Animation
    private lateinit var rotateBackward : Animation
    private lateinit var instantHide : Animation
    private lateinit var instantShow : Animation
    private lateinit var instantZero : Animation
    private lateinit var instantFourtyFive : Animation

    private var fabsShown = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pageViewModel = activity?.run {
            ViewModelProviders.of(this).get(PageViewModel::class.java)
        } ?: throw Exception("Invalid Activity")

        fabOpen = AnimationUtils.loadAnimation(context, R.anim.fab_open)
        fabClose = AnimationUtils.loadAnimation(context, R.anim.fab_close)

        rotateForward = AnimationUtils.loadAnimation(context, R.anim.rotate_forward)
        rotateBackward = AnimationUtils.loadAnimation(context, R.anim.rotate_backward)

        instantHide = AnimationUtils.loadAnimation(context, R.anim.instant_hide)
        instantShow = AnimationUtils.loadAnimation(context, R.anim.instant_show)

        instantZero = AnimationUtils.loadAnimation(context, R.anim.instant_0)
        instantFourtyFive = AnimationUtils.loadAnimation(context, R.anim.instant_45)
    }

    override fun onStart() {
        super.onStart()
        setupObservers()
        setupDiceButtons()
        setupUpDownButtons()
        setupBottomBar()
    }

    private fun setupObservers() {
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

    private fun setupRollerDialog() {
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
        inflater: LayoutInflater,
        container: ViewGroup?,
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

    private fun setupUpDownButtons() {
        UpDownButtonsHelper(context!!, layoutInflater,
            numDiceUpDownButtons.upButton, numDiceUpDownButtons.downButton, numDiceUpDownButtons.upDownDisplayText,
            numDiceUpDownButtonsID, this)

        UpDownButtonsHelper(context!!, layoutInflater,
            modifierUpDownButtons.upButton, modifierUpDownButtons.downButton, modifierUpDownButtons.upDownDisplayText,
            modifierUpDownButtonsID, this)
    }

    private fun setupBottomBar() {
        setupDieEditFab()
        RollPropertyHelper(context!!, layoutInflater, addPropertyButton, currentPropertiesButton, 0, this)
        advancedGroup.visibility = if(pageViewModel.getRollPropertiesEnabled()) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }

    private fun setupDieEditFab() {

        if(pageViewModel.getEditEnabled()) {
            editDieFab.startAnimation(instantShow)

            if(fabsShown) {
                openFabs(true)
            } else {
                closeFabs(true)
            }

            editDieFab.isClickable = true

            editDieFab.setOnClickListener {
                if(!fabsShown) {
                    openFabs(false)
                } else {
                    closeFabs(false)
                }
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
        } else {
            closeFabs(true)
            editDieFab.startAnimation(instantHide)
            editDieFab.setOnClickListener {  }

            fabsShown = false
        }
    }

    private fun openFabs(instant : Boolean) {
        if(instant) {
            editDieFab.startAnimation(instantFourtyFive)
            simpleDieFab.startAnimation(instantShow)
            customDieFab.startAnimation(instantShow)

            simpleDieFabText.startAnimation(instantShow)
            customDieFabText.startAnimation(instantShow)
            editDieFabText.startAnimation(instantShow)
        } else {
            editDieFab.startAnimation(rotateForward)
            simpleDieFab.startAnimation(fabOpen)
            customDieFab.startAnimation(fabOpen)

            simpleDieFabText.startAnimation(fabOpen)
            customDieFabText.startAnimation(fabOpen)
            editDieFabText.startAnimation(fabOpen)
        }

        simpleDieFab.isClickable = true
        customDieFab.isClickable = true

        simpleDieFab.setOnClickListener {
            EditDialogs(context, layoutInflater).createNumberDialog(
                "Create Simple Die",
                "",
                MIN_DICE_SIDE_COUNT_SIMPLE,
                MAX_BOUNDING_VALUE,
                1,
                object : EditDialogs.NumberDialogListener {
                    override fun respondToOK(outputValue: Int) {
                        createSimpleDie(outputValue)
                    }
                }) }

        customDieFab.setOnClickListener {
            EditDialogs(context, layoutInflater).createNameMinMaxDialog(
                "Create Custom Die",
                "",
                MIN_BOUNDING_VALUE,
                MAX_BOUNDING_VALUE,
                object : EditDialogs.NameMinMaxDialogListener {
                    override fun respondToOK(name : String, min : Int, max : Int) {
                        createCustomDie(name,min,max)
                    }
                }) }

        fabsShown = true
    }

    private fun closeFabs(instant : Boolean) {
        if(instant) {
            editDieFab.startAnimation(instantZero)
            simpleDieFab.startAnimation(instantHide)
            customDieFab.startAnimation(instantHide)

            simpleDieFabText.startAnimation(instantHide)
            customDieFabText.startAnimation(instantHide)
            editDieFabText.startAnimation(instantHide)
        } else {
            editDieFab.startAnimation(rotateBackward)
            simpleDieFab.startAnimation(fabClose)
            customDieFab.startAnimation(fabClose)

            simpleDieFabText.startAnimation(fabClose)
            customDieFabText.startAnimation(fabClose)
            editDieFabText.startAnimation(fabClose)
        }

        simpleDieFab.isClickable = false
        customDieFab.isClickable = false

        simpleDieFab.setOnClickListener {  }
        customDieFab.setOnClickListener {  }

        fabsShown = false
    }

    override fun advantageDisadvantageChanged(id: Int, mode: Int) {
        pageViewModel.setAdvantageDisadvantage(mode)
    }

    override fun dropHighChanged(id: Int, dropValue: Int) {
        pageViewModel.setDropHigh(dropValue)
    }

    override fun dropLowChanged(id: Int, dropValue: Int) {
        pageViewModel.setDropLow(dropValue)
    }

    override fun reRollSet(id: Int, threshold: Int) {
        pageViewModel.setReRoll(threshold)
    }

    override fun reRollCleared(id: Int) {
        pageViewModel.clearReRoll()
    }

    override fun minimumDieValueSet(id: Int, threshold: Int) {
        pageViewModel.setMinimumDieValue(threshold)
    }

    override fun minimumDieValueCleared(id: Int) {
        pageViewModel.clearMinimumDieValue()
    }

    override fun explodeChanged(id: Int, explode: Boolean) {
        pageViewModel.setExplode(explode)
    }

    override fun getCurrentProperties(id: Int): RollProperties {
        return pageViewModel.getSimpleRollProperties()
    }

    private fun createSimpleDie(dieNumber: Int) {
        if(dieNumber < MIN_DICE_SIDE_COUNT_SIMPLE || dieNumber > MAX_BOUNDING_VALUE)
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

    private fun createCustomDie(name : String, min : Int, max : Int) {
        if(min < MIN_BOUNDING_VALUE || min > MAX_BOUNDING_VALUE)
        {
            Toast.makeText(context, "minimum lies outside of allowed range", Toast.LENGTH_LONG).show()
            return
        }

        if(max < MIN_BOUNDING_VALUE || max > MAX_BOUNDING_VALUE)
        {
            Toast.makeText(context, "maximum lies outside of allowed range", Toast.LENGTH_LONG).show()
            return
        }


        for(disallowedNames in saveSplitStrings)
        {
            if(name.contains(disallowedNames))
            {
                Toast.makeText(context,"Roll names may not contain \"$disallowedNames\"", Toast.LENGTH_SHORT).show()
                return
            }
        }

        try {
            if(!pageViewModel.addDieToPool(MinMaxDie(name, min, max))) {
                Toast.makeText(context, "$name die already exists", Toast.LENGTH_LONG).show()
            }
        }
        catch (error : DieLoadError)
        {
            Toast.makeText(context, "Problem making the $name die", Toast.LENGTH_LONG).show()
        }
    }

    override fun upButtonClick(id: Int) {
        when (id)
        {
            numDiceUpDownButtonsID -> pageViewModel.incrementNumDice()
            modifierUpDownButtonsID -> pageViewModel.incrementModifier()
        }
    }

    override fun upButtonLongClick(id: Int) {
        when (id)
        {
            numDiceUpDownButtonsID -> pageViewModel.largeIncrementNumDice()
            modifierUpDownButtonsID -> pageViewModel.largeIncrementModifier()
        }
    }

    override fun downButtonClick(id: Int) {
        when (id)
        {
            numDiceUpDownButtonsID -> pageViewModel.decrementNumDice()
            modifierUpDownButtonsID -> pageViewModel.decrementModifier()
        }
    }

    override fun downButtonLongClick(id: Int) {
        when (id)
        {
            numDiceUpDownButtonsID -> pageViewModel.largeDecrementNumDice()
            modifierUpDownButtonsID -> pageViewModel.largeDecrementModifier()
        }
    }

    override fun getExactValue(id: Int): Int  {
        return when(id)
        {
            numDiceUpDownButtonsID -> pageViewModel.getNumDice()
            modifierUpDownButtonsID -> pageViewModel.getModifier()
            else -> 0
        }
    }

    override fun setExactValue(id: Int, value: Int) {
        when (id)
        {
            numDiceUpDownButtonsID -> pageViewModel.setNumDiceExact(value)
            modifierUpDownButtonsID -> pageViewModel.setModifierExact(value)
        }
    }

    override fun getDisplayText(id: Int): CharSequence {
        return when (id)
        {
            numDiceUpDownButtonsID -> getNumDiceString(pageViewModel.getNumDice())
            modifierUpDownButtonsID -> getModifierString(pageViewModel.getModifier())
            else -> getString(R.string.temp)
        }
    }

    override fun onDieClicked(die: Die) {
        val aggregateRoll = Roll("")

        aggregateRoll.addDieToRoll(die,
            pageViewModel.getSimpleRollProperties()
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
        if(pageViewModel.getEditEnabled() && pageViewModel.getNumberDiceItems() > 1) {
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
