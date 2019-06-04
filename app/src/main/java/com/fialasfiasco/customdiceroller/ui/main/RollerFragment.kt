package com.fialasfiasco.customdiceroller.ui.main

import android.app.Dialog
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.SystemClock.sleep
import android.support.constraint.ConstraintLayout
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.Surface
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.fialasfiasco.customdiceroller.MainActivity

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
class RollerFragment : Fragment(), RollFragment.OnFragmentInteractionListener, SensorEventListener {

    private lateinit var pageViewModel: PageViewModel

    private var xAccel = 0.0f
    private var yAccel = 0.0f
    private var zAccel = 0.0f

    private var changeVector = mutableListOf(0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f)
    private var accelStable = false

    // Accelerometer variables
    private var mSensorManager : SensorManager?= null
    private var mAccelerometer : Sensor?= null

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

        var rowInTable = 0
        var columnInRow = 0
        var line = TableRow(context)
        tableLayout.addView(line, rowInTable)
        line.id = "$rowInTable Line".hashCode()
        for(die in dice)
        {
            if(columnInRow >= itemsInRow)
            {
                line = TableRow(context)
                ++rowInTable
                line.id = "$rowInTable Line".hashCode()
                columnInRow = 0
                tableLayout.addView(line, rowInTable)
            }
            val dieNumber = die.key
            val dieID = die.value
            val fragmentTag = "$dieNumber Tag"
            val oldFragment = fragmentManager?.findFragmentByTag(fragmentTag)

            if(oldFragment != null) {
                fragmentManager?.beginTransaction()?.remove(oldFragment)?.commit()
            }

            val rollFragment = RollFragment.newInstance(dieNumber, dieID, this)
            fragmentManager?.beginTransaction()?.add(line.id, rollFragment, fragmentTag)?.commit()
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

    override fun onRollClicked(rollFragment: RollFragment)
    {
        val mainActivity = activity!! as MainActivity

        if(mainActivity.isShakeToRoll())
        {
            runShakeRoller(rollFragment)
        }
        else
        {
            displayRollResult(rollFragment)
        }
    }

    private fun runShakeRoller(rollFragment: RollFragment)
    {
        val dialog = Dialog(context!!)
        dialog.setContentView(R.layout.diceroll_layout)
        val rollArea = dialog.findViewById<ConstraintLayout>(R.id.rollArea)

        val minDim = min(rollerLayout.width, rollerLayout.height).times(3).div(4)

        rollArea.minWidth = minDim
        rollArea.minHeight = minDim

        rollArea.setOnClickListener {
            dialog.dismiss()
        }

        dialog.setOnDismissListener {
            runThread = false
            while(!threadDead)
            {
                sleep(1)
            }
            displayRollResult(rollFragment)
        }

        dialog.setOnShowListener {
            runThread = true
            for(dice in 0 until numDice) {
                val die = ShakeDie(rollFragment.getDiceImageID())
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

    private fun displayRollResult(rollFragment: RollFragment)
    {
        val dialog = Dialog(context!!)
        dialog.setContentView(R.layout.dialog_layout)
        val layout = dialog.findViewById<LinearLayout>(R.id.rollerLayout)
        layout.setOnClickListener {
            dialog.dismiss()
        }

        layout.minimumWidth = (view!!.width / 2.5f).toInt()

        val fragmentDice = rollFragment.getDiceNumber()

        val rollName = dialog.findViewById<TextView>(R.id.rollName)

        var rollDisplay = String.format("%dd%d", numDice, fragmentDice)
        if (modifier != 0) {
            if (modifier >= 0) {
                rollDisplay += "+"
            }
            rollDisplay += "$modifier"
        }
        rollName.text = rollDisplay

        var sum = modifier
        var detailString = ""

        for (rollIndex in 1..(numDice)) {
            val roll = Random.Default.nextInt(1, fragmentDice + 1)
            sum += roll
            detailString += "$roll, "
        }

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
            val dX = xAccel - event.values[0]
            val dY = yAccel - event.values[1]
            val dZ = zAccel - event.values[2]

            val combinedChange = sqrt(dX*dX + dY*dY + dZ*dZ)

            changeVector.add(0, combinedChange)
            changeVector.removeAt(10)

            val totalChange = changeVector.sum()
            val mainActivity = activity!! as MainActivity
            accelStable = totalChange < mainActivity.shakeSensitivity()

            xAccel = event.values[0]
            yAccel = event.values[1]
            zAccel = event.values[2]


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
        val mainActivity = activity!! as MainActivity

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
                    val speedKillMod = 1.0f - (killFrames.toFloat() / mainActivity.holdDuration())
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

                        shakeDie.xVelocity += -xAccel * 0.05f
                        shakeDie.yVelocity += yAccel * 0.05f
                        shakeDie.rotationSpeed = newRotation
                    }

                    sleep(1)


                    if(accelStable)
                    {
                        stableFrames++
                        activeFrames = 0
                    }
                    else
                    {
                        stableFrames = 0
                        activeFrames++
                    }

                    if(activeFrames > mainActivity.shakeDuration() && !shakeHappened)
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

                    if(stableFrames > mainActivity.holdDuration() && shakeHappened)
                    {
                        killMovement = true
                    }

                    if(killFrames > mainActivity.holdDuration())
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
