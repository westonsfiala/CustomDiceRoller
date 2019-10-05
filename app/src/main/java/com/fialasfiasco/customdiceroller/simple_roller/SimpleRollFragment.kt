package com.fialasfiasco.customdiceroller.simple_roller

import android.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.widget.*

import com.fialasfiasco.customdiceroller.R
import kotlinx.android.synthetic.main.fragment_simple_roll.*
import kotlin.math.min
import android.graphics.Point
import android.view.*
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.constraintlayout.widget.ConstraintLayout
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
    private lateinit var instantFortyFive : Animation

    private var fabsShown = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pageViewModel = activity?.run {
            ViewModelProviders.of(this).get(PageViewModel::class.java)
        } ?: throw Exception("Invalid Activity")

        val openListener = object : Animation.AnimationListener {
            override fun onAnimationStart(arg0: Animation) {
                subFabHolder.visibility = ConstraintLayout.VISIBLE

                simpleDieFab.show()
                minMaxDieFab.show()
                imbalancedDieFab.show()

                simpleDieFabText.visibility = TextView.VISIBLE
                minMaxDieFabText.visibility = TextView.VISIBLE
                imbalancedDieFabText.visibility = TextView.VISIBLE
            }
            override fun onAnimationRepeat(arg0: Animation) {}
            override fun onAnimationEnd(arg0: Animation) {}
        }

        val closeListener = object : Animation.AnimationListener {
            override fun onAnimationStart(arg0: Animation) {}
            override fun onAnimationRepeat(arg0: Animation) {}
            override fun onAnimationEnd(arg0: Animation) {
                subFabHolder.visibility = ConstraintLayout.GONE

                simpleDieFab.hide()
                minMaxDieFab.hide()
                imbalancedDieFab.hide()

                simpleDieFabText.visibility = TextView.GONE
                minMaxDieFabText.visibility = TextView.GONE
                imbalancedDieFabText.visibility = TextView.GONE
            }
        }

        fabOpen = AnimationUtils.loadAnimation(context, R.anim.fab_open)
        fabOpen.setAnimationListener(openListener)
        fabClose = AnimationUtils.loadAnimation(context, R.anim.fab_close)
        fabClose.setAnimationListener(closeListener)

        rotateForward = AnimationUtils.loadAnimation(context, R.anim.rotate_forward)
        rotateBackward = AnimationUtils.loadAnimation(context, R.anim.rotate_backward)

        instantShow = AnimationUtils.loadAnimation(context, R.anim.instant_show)
        instantShow.setAnimationListener(openListener)
        instantHide = AnimationUtils.loadAnimation(context, R.anim.instant_hide)
        instantHide.setAnimationListener(closeListener)

        instantZero = AnimationUtils.loadAnimation(context, R.anim.instant_0)
        instantFortyFive = AnimationUtils.loadAnimation(context, R.anim.instant_45)
    }

    override fun onStart() {
        super.onStart()
        setupObservers()
        setupDiceButtons()
        setupUpDownButtons()
        setupBottomBar()
    }

    private fun setupObservers() {
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
        dieViewRecycler.adapter = SimpleRollRecyclerViewAdapter(context!!, pageViewModel, this)
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

        val unknownDrawable = ThemedDieImageGetter(context!!, pageViewModel).getDieDrawable(DIE_UNKNOWN)
        simpleDieFab.setImageDrawable(unknownDrawable)
        minMaxDieFab.setImageDrawable(unknownDrawable)
        imbalancedDieFab.setImageDrawable(unknownDrawable)

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
            editDieFab.startAnimation(instantFortyFive)
            editDieFabText.startAnimation(instantShow)
            subFabHolder.startAnimation(instantShow)
        } else {
            editDieFab.startAnimation(rotateForward)
            editDieFabText.startAnimation(fabOpen)
            subFabHolder.startAnimation(fabOpen)
        }

        simpleDieFab.isClickable = true
        minMaxDieFab.isClickable = true
        imbalancedDieFab.isClickable = true

        simpleDieFab.setOnClickListener {
            EditDialogs(context, layoutInflater).createNameDieDialog(
                "Create Simple Die",
                "Will roll between 1 and given number.",
                MIN_DICE_SIDE_COUNT_SIMPLE,
                MAX_BOUNDING_VALUE,
                getString(R.string.temp),
                1,
                object : EditDialogs.NameDieDialogListener {
                    override fun respondToOK(name : String, number: Int) {
                        createSimpleDie(name, number)
                    }
                })
            closeFabs(false)
        }

        minMaxDieFab.setOnClickListener {
            EditDialogs(context, layoutInflater).createNameMinMaxDialog(
                "Create Min/Max Die",
                "Will roll between min and max.",
                MIN_BOUNDING_VALUE,
                MAX_BOUNDING_VALUE,
                getString(R.string.temp),
                0,
                0,
                object : EditDialogs.NameMinMaxDialogListener {
                    override fun respondToOK(name : String, min : Int, max : Int) {
                        createMinMaxDie(name,min,max)
                    }
                })
            closeFabs(false)
        }

        imbalancedDieFab.setOnClickListener {
            EditDialogs(context, layoutInflater).createNameFacesDialog(
                "Create Imbalanced Die",
                "Will roll one of the faces given here.",
                MIN_BOUNDING_VALUE,
                MAX_BOUNDING_VALUE,
                getString(R.string.temp),
                listOf(0),
                object : EditDialogs.NameFacesDialogListener {
                    override fun respondToOK(name : String, numbers : List<Int>) {
                        createImbalancedDie(name, numbers)
                    }
                })
            closeFabs(false)
        }

        fabsShown = true
    }

    private fun closeFabs(instant : Boolean) {
        if(instant) {
            editDieFab.startAnimation(instantZero)
            editDieFabText.startAnimation(instantHide)
            subFabHolder.startAnimation(instantHide)
        } else {
            editDieFab.startAnimation(rotateBackward)
            editDieFabText.startAnimation(fabClose)
            subFabHolder.startAnimation(fabClose)
        }

        simpleDieFab.isClickable = false
        minMaxDieFab.isClickable = false
        imbalancedDieFab.isClickable = false

        simpleDieFab.setOnClickListener {  }
        minMaxDieFab.setOnClickListener {  }
        imbalancedDieFab.setOnClickListener {  }

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

    override fun keepHighChanged(id: Int, keepValue: Int) {
        pageViewModel.setKeepHigh(keepValue)
    }

    override fun keepLowChanged(id: Int, keepValue: Int) {
        pageViewModel.setKeepLow(keepValue)
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

    private fun createSimpleDie(name: String, dieNumber: Int) : Boolean {
        if(!SIMPLE_DIE_BOUNDING_RANGE.contains(dieNumber))
        {
            Toast.makeText(context, "die lies outside of allowed range", Toast.LENGTH_LONG).show()
            return false
        }

        val nameIssue = checkName(name)
        if(nameIssue.isNotEmpty())
        {
            Toast.makeText(context, nameIssue, Toast.LENGTH_SHORT).show()
            return false
        }

        try {
            if(!pageViewModel.addDieToPool(SimpleDie(name, dieNumber))) {
                Toast.makeText(context, "die already exists", Toast.LENGTH_LONG).show()
                return false
            }
        }
        catch (error : DieLoadError)
        {
            Toast.makeText(context, "Problem making the die", Toast.LENGTH_LONG).show()
            return false
        }
        return true
    }

    private fun createMinMaxDie(name : String, min : Int, max : Int) : Boolean {
        if(!NORMAL_BOUNDING_RANGE.contains(min)) {
            Toast.makeText(context, "minimum lies outside of allowed range", Toast.LENGTH_LONG).show()
            return false
        }

        if(!NORMAL_BOUNDING_RANGE.contains(max)) {
            Toast.makeText(context, "maximum lies outside of allowed range", Toast.LENGTH_LONG).show()
            return false
        }

        val nameIssue = checkName(name)
        if(nameIssue.isNotEmpty()) {
            Toast.makeText(context, nameIssue, Toast.LENGTH_SHORT).show()
            return false
        }

        try {
            if(!pageViewModel.addDieToPool(MinMaxDie(name, min, max))) {
                Toast.makeText(context, "$name die already exists", Toast.LENGTH_LONG).show()
                return false
            }
        }
        catch (error : DieLoadError)
        {
            Toast.makeText(context, "Problem making the $name die", Toast.LENGTH_LONG).show()
            return false
        }
        return true
    }

    private fun createImbalancedDie(name : String, faces : List<Int>) : Boolean {

        if(faces.isEmpty()) {
            Toast.makeText(context, "An imbalanced die must have at least one face", Toast.LENGTH_LONG).show()
            return false
        }

        if(!NORMAL_BOUNDING_RANGE.contains(faces.min())) {
            Toast.makeText(context, "minimum face value lies outside of allowed range", Toast.LENGTH_LONG).show()
            return false
        }

        if(!NORMAL_BOUNDING_RANGE.contains(faces.max()))
        {
            Toast.makeText(context, "maximum face value lies outside of allowed range", Toast.LENGTH_LONG).show()
            return false
        }

        val nameIssue = checkName(name)
        if(nameIssue.isNotEmpty()) {
            Toast.makeText(context, nameIssue, Toast.LENGTH_SHORT).show()
            return false
        }

        try {
            if(!pageViewModel.addDieToPool(ImbalancedDie(name, faces))) {
                Toast.makeText(context, "$name die already exists", Toast.LENGTH_LONG).show()
                return false
            }
        }
        catch (error : DieLoadError)
        {
            Toast.makeText(context, "Problem making the $name die", Toast.LENGTH_LONG).show()
            return false
        }
        return true
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
        val aggregateRoll = Roll("", "")

        aggregateRoll.addDieToRoll(die,
            pageViewModel.getSimpleRollProperties()
        )

        rollerDialog?.runRoll(aggregateRoll, pageViewModel.getShakeEnabled())
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

        if(pageViewModel.getEditEnabled()) {
            builder.setNeutralButton("Edit") { _, _ ->
                when (die) {
                    is SimpleDie -> editSimpleDie(die)
                    is MinMaxDie -> editMinMaxDie(die)
                    is ImbalancedDie -> editImbalancedDie(die)
                    else -> Toast.makeText(context, "Unable to determine die type", Toast.LENGTH_SHORT).show()
                }
            }
        }

        builder.show()
    }

    private fun editSimpleDie(die: SimpleDie) {
        EditDialogs(context, layoutInflater).createNameDieDialog(
            "Edit Simple Die",
            "Existing die will be overwritten.",
            MIN_DICE_SIDE_COUNT_SIMPLE,
            MAX_BOUNDING_VALUE,
            die.getDisplayName(),
            die.max(),
            object : EditDialogs.NameDieDialogListener {
                override fun respondToOK(name : String, number: Int) {
                    // remove the old die and try to add the new one. If it fails, add back in the old one.
                    pageViewModel.removeDieFromPool(die)
                    if(!createSimpleDie(name, number)) {
                        pageViewModel.addDieToPool(die)
                    }
                }
            })
    }

    private fun editMinMaxDie(die: MinMaxDie) {
        EditDialogs(context, layoutInflater).createNameMinMaxDialog(
            "Edit Min/Max Die",
            "Existing die will be overwritten.",
            MIN_BOUNDING_VALUE,
            MAX_BOUNDING_VALUE,
            die.getDisplayName(),
            die.min(),
            die.max(),
            object : EditDialogs.NameMinMaxDialogListener {
                override fun respondToOK(name : String, min : Int, max : Int) {
                    // remove the old die and try to add the new one. If it fails, add back in the old one.
                    pageViewModel.removeDieFromPool(die)
                    if(!createMinMaxDie(name, min, max)) {
                        pageViewModel.addDieToPool(die)
                    }
                }
            })
    }

    private fun editImbalancedDie(die: ImbalancedDie) {
        EditDialogs(context, layoutInflater).createNameFacesDialog(
            "Edit Imbalanced Die",
            "Existing die will be overwritten.",
            MIN_BOUNDING_VALUE,
            MAX_BOUNDING_VALUE,
            die.getDisplayName(),
            die.getFaces(),
            object : EditDialogs.NameFacesDialogListener {
                override fun respondToOK(name : String, numbers : List<Int>) {
                    // remove the old die and try to add the new one. If it fails, add back in the old one.
                    pageViewModel.removeDieFromPool(die)
                    if(!createImbalancedDie(name, numbers)) {
                        pageViewModel.addDieToPool(die)
                    }
                }
            })
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
