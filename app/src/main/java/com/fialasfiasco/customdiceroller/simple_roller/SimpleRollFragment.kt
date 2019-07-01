package com.fialasfiasco.customdiceroller.simple_roller

import android.app.AlertDialog
import android.app.Dialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.SystemClock.sleep
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import android.widget.*
import androidx.preference.PreferenceManager

import com.fialasfiasco.customdiceroller.R
import kotlinx.android.synthetic.main.fragment_simple_roll_layout.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.min
import kotlin.math.sqrt
import kotlin.random.Random
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.media.MediaPlayer
import android.view.*
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fialasfiasco.customdiceroller.data.PageViewModel
import com.fialasfiasco.customdiceroller.data.SimpleDie
import com.fialasfiasco.customdiceroller.helper.NumberDialog
import com.fialasfiasco.customdiceroller.history.HistoryStamp
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlin.math.abs
import kotlin.math.max

const val MAX_DICE = 100
const val MIN_DICE = 1
const val MAX_MODIFIER = 100
const val START_MODIFIER = 0
const val MIN_MODIFIER = -100

/**
 * A simple [Fragment] subclass.
 * Use the [RollerFragmentRecycler.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class RollerFragmentRecycler : androidx.fragment.app.Fragment(),
    SimpleRollRecyclerViewAdapter.OnSimpleDieViewInteractionListener, SensorEventListener {

    private lateinit var pageViewModel: PageViewModel

    // Saved settings variables
    private var shakeEnabled = false
    private var shakeSensitivity = 0.0f
    private var shakeDuration = 0
    private var holdDuration = 0

    private var sortType = 0

    private var editEnabled = false

    private var itemsInRow = 4

    // Accelerometer variables
    private var mSensorManager: SensorManager? = null
    private var mAccelerometer: Sensor? = null

    private var xAcceleration = 0.0f
    private var yAcceleration = 0.0f
    private var zAcceleration = 0.0f

    private var lockedRotation: Int? = null

    private var changeVector = mutableListOf(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)
    private var accelerationStable = false

    // Thread variables
    private var shakerDice = mutableListOf<ShakeDie>()
    private var runThread = false
    private var threadDead = true

    // Sound variables
    private var mediaPlayers = mutableListOf<MediaPlayer>()
    private var soundEnabled = false
    private var volume = 1.0f


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pageViewModel = activity?.run {
            ViewModelProviders.of(this).get(PageViewModel::class.java)
        } ?: throw Exception("Invalid Activity")
    }

    override fun onStart() {
        super.onStart()
        setupSavedSettings()
        initMediaPlayers()
    }

    private fun setupSavedSettings() {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)

        val diePool = preferences.getStringSet(
            getString(R.string.dice_pool_key),
            resources.getStringArray(R.array.dice_pool_entries).toSet()
        )

        pageViewModel.initDiePoolFromStrings(diePool)

        shakeEnabled = preferences.getBoolean(
            getString(R.string.shake_enabled_key),
            resources.getBoolean(R.bool.shake_enabled_default)
        )

        val savedShakeSensitivity = preferences.getInt(
            getString(R.string.shake_sensitivity_key),
            resources.getInteger(R.integer.shake_sensitivity_default)
        ).toFloat()
        shakeSensitivity = 12f - savedShakeSensitivity / 10f

        val savedShakeDuration = preferences.getInt(
            getString(R.string.shake_duration_key),
            resources.getInteger(R.integer.shake_duration_default)
        )
        shakeDuration = 500 + savedShakeDuration * 5

        val savedHoldDuration = preferences.getInt(
            getString(R.string.hold_duration_key),
            resources.getInteger(R.integer.hold_duration_default)
        )
        holdDuration = 500 + savedHoldDuration * 5

        sortType = preferences.getString(
            getString(R.string.sort_type_key),
            getString(R.string.sort_type_default)
        )!!.toInt()

        soundEnabled = preferences.getBoolean(
            getString(R.string.sound_enabled_key),
            resources.getBoolean(R.bool.sound_enabled_default)
        )

        val intVolume = preferences.getInt(
            getString(R.string.sound_volume_key),
            resources.getInteger(R.integer.sound_volume_default)
        )
        volume = intVolume.toFloat().div(100.0f)

        editEnabled = preferences.getBoolean(
            getString(R.string.dice_edit_enabled_key),
            resources.getBoolean(R.bool.dice_edit_enabled_default)
        )

        if(editEnabled)
        {
            editDieFab.show()
        }
        else
        {
            editDieFab.hide()
        }

        itemsInRow = preferences.getString(
            getString(R.string.items_per_row_simple_key),
            resources.getInteger(R.integer.items_per_row_simple_default).toString()
        )!!.toInt()

        dieViewRecycler.layoutManager = GridLayoutManager(context, itemsInRow)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val createdView = inflater.inflate(R.layout.fragment_simple_roll_layout, container, false)

        setupObservers(createdView)
        setupAccelerometer()
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

    private fun setupAccelerometer() {
        // get reference of the service
        mSensorManager = activity?.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        // focus in accelerometer
        mAccelerometer = mSensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
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
        recycler.layoutManager = GridLayoutManager(context, itemsInRow)
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
                        createDie(outputValue)
                    }
                })
        }

        fab.setOnLongClickListener {
            val builder = AlertDialog.Builder(context)

            builder.setTitle("Reset Dice Pool?")
            builder.setMessage("This will restore dice pool to 2, 4, 6, 8, 10, 12, 20, and 100")

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

    private fun createDie(dieNumber: Int)
    {
        if(dieNumber < 1 || dieNumber > 100)
        {
            Toast.makeText(context, "d$dieNumber, lies outside of allowed range", Toast.LENGTH_LONG).show()
            return
        }

        if(pageViewModel.addDieToPool(dieNumber).not()) {
            Toast.makeText(context, "d$dieNumber already exists", Toast.LENGTH_LONG).show()
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

    override fun onDieClicked(simpleDie: SimpleDie) {
        lockRotation()
        if (shakeEnabled) {
            runShakeRoller(simpleDie.mDie, simpleDie.mImageID)
        } else {
            displayRollResult(simpleDie.mDie)
        }
    }

    override fun onDieLongClick(simpleDie: SimpleDie) {
        val builder = AlertDialog.Builder(context)

        builder.setTitle("Die Info - d" + simpleDie.mDie)
        builder.setMessage("When rolled, produces a value from 1 to " + simpleDie.mDie)

        builder.setPositiveButton("OK") { _, _ -> }

        // Don't let the user remove all of the dice.
        if(editEnabled && pageViewModel.getSimpleDiceSize() > 1) {
            builder.setNegativeButton("Remove Die") { dialog, _ ->
                dialog.dismiss()
                // Confirm the removal of die
                val confirmRemoveBuilder = AlertDialog.Builder(context)

                confirmRemoveBuilder.setTitle("Remove - d" + simpleDie.mDie)
                confirmRemoveBuilder.setMessage("Are you sure you wish to remove the d" + simpleDie.mDie)

                confirmRemoveBuilder.setPositiveButton("Yes") { _, _ ->
                    pageViewModel.removeDieFromPool(simpleDie.mDie)
                }
                confirmRemoveBuilder.setNegativeButton("No") { _, _ -> }

                confirmRemoveBuilder.show()
            }
        }

        builder.show()
    }

    private fun runShakeRoller(dieNumber: Int, dieID : Int)
    {
        val dialog = Dialog(context!!)
        dialog.setContentView(R.layout.shake_dialog_layout)
        val rollArea = dialog.findViewById<ConstraintLayout>(R.id.rollArea)

        val minDim = min(dieViewLayout.width, dieViewLayout.height).times(3).div(4)

        rollArea.minWidth = minDim
        rollArea.minHeight = minDim

        rollArea.setOnClickListener {
            runThread = false
            dialog.dismiss()
        }

        dialog.setOnDismissListener {
            runThread = false
            while(!threadDead)
            {
                sleep(1)
            }
            displayRollResult(dieNumber)
        }

        dialog.setOnShowListener {
            runThread = true
            for(dice in 0 until pageViewModel.getNumDice()) {
                val die = ShakeDie(dieID)
                die.getImage().maxWidth = rollArea.width.div(12)
                die.getImage().maxHeight = rollArea.width.div(12)
                die.getImage().adjustViewBounds = true
                rollArea.addView(die.getImage())
                die.getImage().x = Random.nextFloat() * rollArea.width.toFloat()
                die.getImage().y = Random.nextFloat() * rollArea.height.toFloat()
                val scale = 1.0f + Random.nextFloat()
                die.getImage().scaleX = scale
                die.getImage().scaleY = scale
                shakerDice.add(die)
            }
            diceRollThreadRunner(dialog)
        }

        dialog.show()
    }

    private fun displayRollResult(dieNumber: Int)
    {
        val dialog = Dialog(context!!)
        dialog.setContentView(R.layout.roll_result_dialog_layout)
        val layout = dialog.findViewById<LinearLayout>(R.id.dieViewLayout)
        layout.setOnClickListener {
            dialog.dismiss()
        }

        layout.minimumWidth = (view!!.width / 2.5f).toInt()

        val rollName = dialog.findViewById<TextView>(R.id.rollName)

        var rollDisplay = String.format("%dd%d", pageViewModel.getNumDice(), dieNumber)
        if (pageViewModel.getModifier() != 0) {
            if (pageViewModel.getModifier() >= 0) {
                rollDisplay += "+"
            }
            rollDisplay += pageViewModel.getModifier().toString()
        }
        rollName.text = rollDisplay


        val rollValues = mutableListOf<Int>()

        for (rollIndex in 1..(pageViewModel.getNumDice())) {
            val roll = Random.Default.nextInt(1, dieNumber + 1)
            rollValues.add(roll)
        }

        when (sortType)
        {
            1 -> {
                rollValues.sort()
            }
            2 -> {
                rollValues.sort()
                rollValues.reverse()
            }
        }

        var detailString = ""

        for (roll in rollValues) {
            detailString += "$roll, "
        }

        val sum = pageViewModel.getModifier() + rollValues.sum()

        // Take off the last space and comma.
        val correctedString = detailString.removeRange(detailString.length - 2, detailString.length)

        val rollTotal = dialog.findViewById<TextView>(R.id.rollTotal)
        rollTotal.text = "$sum"

        val rollDetails = dialog.findViewById<TextView>(R.id.rollDetails)
        rollDetails.text = correctedString

        val time = Calendar.getInstance().time
        val formatter = SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault())
        val formattedDate = formatter.format(time)

        pageViewModel.addRollHistory(
            HistoryStamp(
                sum.toString(),
                rollDisplay,
                correctedString,
                formattedDate
            )
        )

        dialog.setOnDismissListener {
            unlockRotation()
        }

        dialog.show()
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event != null) {
            val dX = xAcceleration - event.values[0]
            val dY = yAcceleration - event.values[1]
            val dZ = zAcceleration - event.values[2]

            val combinedChange = sqrt(dX*dX + dY*dY + dZ*dZ)

            changeVector.add(0, combinedChange)
            changeVector.removeAt(10)

            val totalChange = changeVector.sum()

            accelerationStable = totalChange < shakeSensitivity

            xAcceleration = event.values[0]
            yAcceleration = event.values[1]
            zAcceleration = event.values[2]
        }
    }

    override fun onResume() {
        super.onResume()
        mSensorManager!!.registerListener(this,mAccelerometer,
            SensorManager.SENSOR_DELAY_GAME)
    }

    override fun onPause() {
        super.onPause()
        mSensorManager!!.unregisterListener(this)
    }

    private fun diceRollThreadRunner(rollDialog: Dialog)
    {
        val diceShakerThread = Thread {
            threadDead = false
            val rollContainer = rollDialog.findViewById<ConstraintLayout>(R.id.rollArea)
            if(rollContainer != null) {

                // Used to track how many frames they shook it for.
                var stableFrames = 0
                var activeFrames = 0
                var killFrames = 0
                var shakeHappened = false
                var killMovement = false

                while (runThread) {
                    val speedKillMod = 1.0f - (killFrames.toFloat() / holdDuration)
                    var maxBounceVelocity = 0.0f

                    for (shakeDie in shakerDice) {
                        var newX = shakeDie.getImage().x + shakeDie.xVelocity * speedKillMod
                        val newRight = newX + shakeDie.getImage().width
                        var newY = shakeDie.getImage().y + shakeDie.yVelocity * speedKillMod
                        val newBottom = newY + shakeDie.getImage().height
                        var newRotation = shakeDie.rotationSpeed * speedKillMod

                        val tooLeft = newX < 0
                        val tooRight = newRight > rollContainer.width

                        if (tooLeft) {
                            newX = 0f
                        } else if (tooRight) {
                            newX = rollContainer.width.toFloat() - shakeDie.getImage().width
                        }

                        if (tooLeft || tooRight) {
                            shakeDie.xVelocity *= -.95f
                            // Throw a bit of randomness in.
                            shakeDie.xVelocity += Random.nextFloat() - 0.5f

                            maxBounceVelocity = max(abs(shakeDie.xVelocity), maxBounceVelocity)
                        }

                        val tooHigh = newY < 0
                        val tooLow = newBottom > rollContainer.height

                        if (tooHigh) {
                            newY = 0f
                        } else if (tooLow) {
                            newY = rollContainer.height.toFloat() - shakeDie.getImage().height
                        }

                        if (tooHigh || tooLow) {
                            shakeDie.yVelocity *= -.95f
                            // Throw a bit of randomness in.
                            shakeDie.yVelocity += Random.nextFloat() - 0.5f

                            maxBounceVelocity = max(abs(shakeDie.yVelocity), maxBounceVelocity)
                        }

                        activity?.runOnUiThread {
                            shakeDie.getImage().x = newX
                            shakeDie.getImage().y = newY
                            shakeDie.getImage().rotation += newRotation
                        }

                        val movement = sqrt(
                            (shakeDie.getImage().x - newX) * (shakeDie.getImage().x - newX)
                                    + (shakeDie.getImage().y - newY) * (shakeDie.getImage().y - newY)
                        )

                        if (movement < 1) {
                            newRotation *= .99f
                        } else if (movement > 25) {
                            newRotation *= 1.01f
                        }

                        if (newRotation > 1f) {
                            newRotation = 1f
                        } else if (newRotation < -1f) {
                            newRotation = -1f
                        }

                        if(lockedRotation == Configuration.ORIENTATION_PORTRAIT) {
                            shakeDie.xVelocity += -xAcceleration * 0.05f
                            shakeDie.yVelocity += yAcceleration * 0.05f
                        }
                        else
                        {
                            shakeDie.xVelocity += -yAcceleration * 0.05f
                            shakeDie.yVelocity += -xAcceleration * 0.05f
                        }
                        shakeDie.rotationSpeed = newRotation
                    }

                    playSound(maxBounceVelocity)

                    sleep(1)

                    if(accelerationStable)
                    {
                        stableFrames++
                        activeFrames = 0
                    }
                    else
                    {
                        stableFrames = 0
                        activeFrames++
                    }

                    if(activeFrames > shakeDuration && !shakeHappened)
                    {
                        shakeHappened = true
                        activity?.runOnUiThread {
                            val shakeText = rollDialog.findViewById<TextView>(R.id.shakeText)
                            shakeText.text = getString(R.string.hold_still)
                        }
                    }

                    if(killMovement)
                    {
                        killFrames++
                    }

                    if(stableFrames > holdDuration && shakeHappened)
                    {
                        killMovement = true
                    }

                    if(killFrames > holdDuration)
                    {
                        runThread = false
                    }
                }

                if(killMovement) {
                    sleep(500)
                }

                activity?.runOnUiThread {
                    for(shakeDie in shakerDice) {
                        rollContainer.removeView(shakeDie.getImage())
                    }
                    shakerDice.clear()
                }
            }

            rollDialog.dismiss()
            threadDead = true
        }

        diceShakerThread.start()
    }

    private fun initMediaPlayers()
    {
        mediaPlayers.clear()
        if(soundEnabled) {
            for (num in 0..9) {
                val player = when (num % 2) {
                    0 -> MediaPlayer.create(context, R.raw.diceroll_no_silence)
                    else -> MediaPlayer.create(context, R.raw.diceroll_quiet)
                }
                mediaPlayers.add(player)
            }
        }
    }

    private fun playSound(maxVelocity : Float)
    {
        if(soundEnabled && maxVelocity != 0.0f) {
            for (player in mediaPlayers) {
                if (!player.isPlaying) {
                    val bounceVolume = min(abs(maxVelocity) / 50.0f, 1.0f)
                    player.setVolume(volume * bounceVolume, volume * bounceVolume)
                    player.start()
                    break
                }
            }
        }
    }

    private fun lockRotation()
    {
        val currentOrientation = resources.configuration.orientation
        if (currentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE //locks landscape
        } else {
            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT //locks port
        }
        lockedRotation = currentOrientation
    }

    private fun unlockRotation()
    {
        // keep this !! because otherwise it will complain about not being a special ActivityInfo type
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         * @return A new instance of fragment RollerFragmentRecycler.
         */
        @JvmStatic
        fun newInstance() =
            RollerFragmentRecycler().apply {
                arguments = Bundle().apply {
                    //putString(ARG_PARAM1, param1)
                }
            }
    }

    inner class ShakeDie(private var dieImageID : Int)
    {
        var xVelocity = 0f
        var yVelocity = 0f
        var rotationSpeed = 0f
        private var dieView : ImageView ?= null

        init {
            xVelocity = Random.nextInt(-50, 50).toFloat()
            yVelocity = Random.nextInt(-50,50).toFloat()
            rotationSpeed = Random.nextFloat() - 0.5f
        }

        fun getImage() : ImageView
        {
            if(dieView == null)
            {
                dieView = ImageView(context)
                dieView?.setImageResource(dieImageID)
            }

            return dieView!!
        }
    }
}
