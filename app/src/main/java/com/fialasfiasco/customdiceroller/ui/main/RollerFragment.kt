package com.fialasfiasco.customdiceroller.ui.main

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
import android.view.LayoutInflater
import android.view.Surface
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.preference.PreferenceManager

import com.fialasfiasco.customdiceroller.R
import kotlinx.android.synthetic.main.fragment_roller.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.min
import kotlin.math.sqrt
import kotlin.random.Random

private const val MAX_DICE = 100
private const val MIN_DICE = 1
private const val MAX_MODIFIER = 100
private const val START_MODIFIER = 0
private const val MIN_MODIFIER = -100

/**
 * A simple [Fragment] subclass.
 * Use the [RollerFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class RollerFragment : androidx.fragment.app.Fragment(), DieView.OnDieViewInteractionListener, SensorEventListener {

    private lateinit var pageViewModel: PageViewModel

    // Saved settings variables
    private var shakeEnabled = false
    private var shakeSensitivity = 0.0f
    private var shakeDuration = 0
    private var holdDuration = 0

    private var sortType = 0

    // Accelerometer variables
    private var mSensorManager : SensorManager?= null
    private var mAccelerometer : Sensor?= null

    private var xAcceleration = 0.0f
    private var yAcceleration = 0.0f
    private var zAcceleration = 0.0f

    private var changeVector = mutableListOf(0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f)
    private var accelerationStable = false

    // Thread variables
    private var shakerDice = mutableListOf<ShakeDie>()
    private var runThread = false
    private var threadDead = true

    private val dice = mapOf(
        4 to R.drawable.ic_d4,
        6 to R.drawable.ic_d6,
        8 to R.drawable.ic_d8,
        10 to R.drawable.ic_d10,
        12 to R.drawable.ic_d12,
        20 to R.drawable.ic_d20,
        100 to R.drawable.ic_d100
        )

    private var numDice = 1
    private var modifier = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pageViewModel = activity?.run {
            ViewModelProviders.of(this).get(PageViewModel::class.java)
        } ?: throw Exception("Invalid Activity")

        setupSavedSettings()
    }

    private fun setupSavedSettings()
    {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)

        shakeEnabled = preferences.getBoolean(getString(R.string.shake_enabled_key),
            getString(R.string.shake_enabled_default).toBoolean())

        val savedShakeSensitivity = preferences.getInt(getString(R.string.shake_sensitivity_key),
            getString(R.string.shake_sensitivity_default).toInt()).toFloat()
        shakeSensitivity = 12f - savedShakeSensitivity / 10f

        val savedShakeDuration = preferences.getInt(getString(R.string.shake_duration_key),
            getString(R.string.shake_duration_default).toInt())
        shakeDuration = 500 + savedShakeDuration*5

        val savedHoldDuration = preferences.getInt(getString(R.string.hold_duration_key),
            getString(R.string.hold_duration_default).toInt())
        holdDuration = 500 + savedHoldDuration*5

        sortType = preferences.getString(getString(R.string.sort_type_key),
            getString(R.string.sort_type_default))!!.toInt()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val createdView = inflater.inflate(R.layout.fragment_roller, container, false)

        setupObservers()
        setupAccelerometer()
        return setupCreatedView(createdView)
    }

    private fun setupObservers()
    {
        pageViewModel.numDice.observe(this, Observer<Int> {
            numDice = it!!
            updateNumDiceText(view!!)
        })

        pageViewModel.modifier.observe(this, Observer<Int> {
            modifier = it!!
            updateModifierText(view!!)
        })
    }

    private fun setupAccelerometer() {
        // get reference of the service
        mSensorManager = activity?.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        // focus in accelerometer
        mAccelerometer = mSensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    }

    private fun setupCreatedView(view: View) : View
    {
        setupDiceButtons(view)
        setupUpAndDownButtons(view)
        return view
    }

    private fun setupDiceButtons(view: View)
    {
        val tableLayout = view.findViewById<TableLayout>(R.id.tableLayout)

        val rotation = activity?.windowManager?.defaultDisplay?.rotation

        var itemsInRow = 4

        if(rotation == Surface.ROTATION_90 || rotation == Surface.ROTATION_270)
        {
            itemsInRow = 7
        }

        val tableLayoutParams = TableLayout.LayoutParams(
            TableLayout.LayoutParams.MATCH_PARENT,
            TableLayout.LayoutParams.MATCH_PARENT, 1.0f
        )

        val viewLayoutParams = TableRow.LayoutParams(
            TableRow.LayoutParams.WRAP_CONTENT,
            TableRow.LayoutParams.WRAP_CONTENT, 1.0f
        )

        var rowInTable = 0
        var columnInRow = 0
        var line = TableRow(context)

        line.layoutParams = tableLayoutParams
        tableLayout.addView(line, rowInTable)
        line.id = "$rowInTable Line".hashCode()
        for(die in dice)
        {
            if(columnInRow >= itemsInRow)
            {
                line = TableRow(context)
                line.layoutParams = tableLayoutParams
                ++rowInTable
                line.id = "$rowInTable Line".hashCode()
                columnInRow = 0
                tableLayout.addView(line, rowInTable)
            }
            val dieNumber = die.key
            val dieID = die.value

            val dieView = DieView(context)
            dieView.layoutParams = viewLayoutParams
            dieView.attachAndInitialize("d$dieNumber", dieID, dieNumber, this)

            line.addView(dieView)
            ++columnInRow
        }
    }

    private fun setupUpAndDownButtons(view: View)
    {
        val diceUpBut = view.findViewById<ImageButton>(R.id.diceUpButton)
        diceUpBut.setOnClickListener {
            setNumDice(numDice + 1)
        }

        diceUpBut.setOnLongClickListener {
            setNumDice(MAX_DICE)
            true
        }

        val diceDownBut = view.findViewById<ImageButton>(R.id.diceDownButton)
        diceDownBut.setOnClickListener {
            setNumDice(numDice - 1)
        }

        diceDownBut.setOnLongClickListener {
            setNumDice(MIN_DICE)
            true
        }

        val modifierUpBut = view.findViewById<ImageButton>(R.id.modifierUpButton)
        modifierUpBut.setOnClickListener {
            setModifier(modifier + 1)
        }

        modifierUpBut.setOnLongClickListener {
            if(modifier >= 0)
            {
                setModifier(MAX_MODIFIER)
            }
            else
            {
                setModifier(START_MODIFIER)
            }
            true
        }

        val modifierDownBut = view.findViewById<ImageButton>(R.id.modifierDownButton)
        modifierDownBut.setOnClickListener {
            setModifier(modifier - 1)
        }

        modifierDownBut.setOnLongClickListener {
            if(modifier <= 0)
            {
                setModifier(MIN_MODIFIER)
            }
            else
            {
                setModifier(START_MODIFIER)
            }
            true
        }

        updateNumDiceText(view)
        updateModifierText(view)
    }

    private fun setNumDice(newNumDice: Int)
    {
        pageViewModel.setNumDice(newNumDice)
        if(numDice < 1)
        {
            numDice = 1
        }
        else if(numDice > 100)
        {
            numDice = 100
        }
        updateNumDiceText(view!!)
    }

    private fun updateNumDiceText(view: View)
    {
        val diceText = view.findViewById<TextView>(R.id.numDiceText)
        diceText.text = String.format("%dd",numDice)
    }

    private fun setModifier(newModifier: Int)
    {
        pageViewModel.setModifier(newModifier)
        if(modifier > 100)
        {
            modifier = 100
        }
        else if(modifier < -100)
        {
            modifier = -100
        }
        updateModifierText(view!!)
    }

    private fun updateModifierText(view: View)
    {
        val modifierText = view.findViewById<TextView>(R.id.modifierText)
        when
        {
            modifier >= 0 -> modifierText.text = String.format("+%d",modifier)
            modifier < 0 -> modifierText.text = String.format("%d",modifier)
        }
    }

    override fun onDieClicked(dieView: DieView)
    {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        val shakeIt = preferences.getBoolean(getString(R.string.shake_enabled_key), true)

        if(shakeIt)
        {
            runShakeRoller(dieView.getDiceLookupId(), dieView.getDiceImageID())
        }
        else
        {
            displayRollResult(dieView.getDiceLookupId())
        }
    }

    private fun runShakeRoller(dieNumber: Int, dieID : Int)
    {
        val dialog = Dialog(context!!)
        dialog.setContentView(R.layout.diceroll_layout)
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
            for(dice in 0 until numDice) {
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
        dialog.setContentView(R.layout.dialog_layout)
        val layout = dialog.findViewById<LinearLayout>(R.id.dieViewLayout)
        layout.setOnClickListener {
            dialog.dismiss()
        }

        layout.minimumWidth = (view!!.width / 2.5f).toInt()

        val rollName = dialog.findViewById<TextView>(R.id.rollName)

        var rollDisplay = String.format("%dd%d", numDice, dieNumber)
        if (modifier != 0) {
            if (modifier >= 0) {
                rollDisplay += "+"
            }
            rollDisplay += "$modifier"
        }
        rollName.text = rollDisplay


        val rollValues = mutableListOf<Int>()

        for (rollIndex in 1..(numDice)) {
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

        val sum = modifier + rollValues.sum()

        // Take off the last space and comma.
        val correctedString = detailString.removeRange(detailString.length - 2, detailString.length)

        val rollTotal = dialog.findViewById<TextView>(R.id.rollTotal)
        rollTotal.text = "$sum"

        val rollDetails = dialog.findViewById<TextView>(R.id.rollDetails)
        rollDetails.text = correctedString

        val time = Calendar.getInstance().time
        val formatter = SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault())
        val formattedDate = formatter.format(time)

        pageViewModel.addRollHistory(HistoryStamp.newInstance(sum, rollDisplay, correctedString, formattedDate))

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
                            shakeDie.xVelocity *= -.85f
                            // Throw a bit of randomness in.
                            shakeDie.xVelocity += Random.nextFloat() - 0.5f
                        }

                        val tooHigh = newY < 0
                        val tooLow = newBottom > rollContainer.height

                        if (tooHigh) {
                            newY = 0f
                        } else if (tooLow) {
                            newY = rollContainer.height.toFloat() - shakeDie.getImage().height
                        }

                        if (tooHigh || tooLow) {
                            shakeDie.yVelocity *= -.85f
                            // Throw a bit of randomness in.
                            shakeDie.yVelocity += Random.nextFloat() - 0.5f
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

                        shakeDie.xVelocity += -xAcceleration * 0.05f
                        shakeDie.yVelocity += yAcceleration * 0.05f
                        shakeDie.rotationSpeed = newRotation
                    }

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

                sleep(500)

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

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         * @return A new instance of fragment RollerFragment.
         */
        @JvmStatic
        fun newInstance() =
            RollerFragment().apply {
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
