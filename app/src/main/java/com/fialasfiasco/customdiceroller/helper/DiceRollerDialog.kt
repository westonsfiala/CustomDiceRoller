package com.fialasfiasco.customdiceroller.helper

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.content.res.Resources
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.MediaPlayer
import android.os.SystemClock
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import com.fialasfiasco.customdiceroller.R
import com.fialasfiasco.customdiceroller.data.AggregateRoll
import com.fialasfiasco.customdiceroller.data.PageViewModel
import com.fialasfiasco.customdiceroller.history.HistoryStamp
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt
import kotlin.random.Random

class DiceRollerDialog(
    private val context: Context,
    private val activity: Activity?,
    private val minDimension: Int,
    private val pageViewModel: PageViewModel,
    private val listener: DiceRollerListener) :
    SensorEventListener
{
    // Accelerometer variables
    private var mSensorManager: SensorManager? = null
    private var mAccelerometer: Sensor? = null

    private var xAcceleration = 0.0f
    private var yAcceleration = 0.0f
    private var zAcceleration = 0.0f

    private var changeVector = mutableListOf(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)
    private var accelerationStable = false

    // Sound variables
    private var mediaPlayers = mutableListOf<MediaPlayer>()

    // Thread variables
    private var shakerDice = mutableListOf<ShakeDie>()
    private var runThread = false
    private var pauseThread = false
    private var threadDead = true

    private var lockedRotation: Int? = null

    init {
        setupAccelerometer()
        initMediaPlayers()
    }

    private fun setupAccelerometer() {
        // get reference of the service
        mSensorManager = activity?.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        // focus in accelerometer
        mAccelerometer = mSensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    }

    private fun initMediaPlayers()
    {
        mediaPlayers.clear()
        try {
            for (num in 0..9) {
                    val player = when (num % 2) {
                        0 -> MediaPlayer.create(context, R.raw.diceroll_no_silence)
                        else -> MediaPlayer.create(context, R.raw.diceroll_quiet)
                    }
                    mediaPlayers.add(player)
            }
        } catch (error : Resources.NotFoundException) {
            Toast.makeText(context, "Error with loading sound", Toast.LENGTH_SHORT).show()
            mediaPlayers.clear()
        }
    }

    fun pause()
    {
        pauseThread = true
        mSensorManager!!.unregisterListener(this)
        for(player in mediaPlayers)
        {
            if(player.isPlaying) {
                player.pause()
            }
        }
    }

    fun resume()
    {
        pauseThread = false
        mSensorManager!!.registerListener(this,mAccelerometer,
            SensorManager.SENSOR_DELAY_GAME)
    }

    fun kill()
    {
        runThread = false
        for(player in mediaPlayers)
        {
            player.release()
        }
        mediaPlayers.clear()
    }

    fun runShakeRoller(roll: AggregateRoll)
    {
        val totalDice = roll.getTotalDiceInRoll()

        if(totalDice <= 0)
        {
            Toast.makeText(context, "No dice to roll", Toast.LENGTH_SHORT).show()
            return
        }
        else if(totalDice > 100)
        {
            Toast.makeText(context, "Cannot run shake roller with more than 100 dice", Toast.LENGTH_SHORT).show()
            runRollDisplay(roll)
            return
        }

        val dialog = Dialog(context)
        dialog.setContentView(R.layout.dialog_shake_background)
        val rollArea = dialog.findViewById<ConstraintLayout>(R.id.rollArea)

        rollArea.minWidth = minDimension.times(3).div(4)
        rollArea.minHeight = minDimension.times(3).div(4)

        rollArea.setOnClickListener {
            runThread = false
            dialog.dismiss()
        }

        dialog.setOnDismissListener {
            runThread = false
            while(!threadDead)
            {
                SystemClock.sleep(1)
            }
            runRollDisplay(roll)
        }

        dialog.setOnShowListener {
            lockRotation()
            runThread = true
            for(dieCountPair in roll.getInnerDice())
            {
                for(index in 0 until dieCountPair.value)
                {
                    val die = ShakeDie(dieCountPair.key.getImageID())
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
            }

            diceRollThreadRunner(dialog)
        }

        dialog.show()
    }

    fun runRollDisplay(roll : AggregateRoll)
    {
        val dialog = Dialog(context)
        dialog.setContentView(R.layout.dialog_roll_result)
        val layout = dialog.findViewById<LinearLayout>(R.id.rollDetailsLayout)
        layout.setOnClickListener {
            dialog.dismiss()
        }

        layout.minimumWidth = minDimension.div(2)

        val rollName = dialog.findViewById<TextView>(R.id.rollName)

        var rollDisplay = roll.getDetailedRollName()

        if(rollDisplay.isEmpty())
        {
            rollDisplay = "0"
        }

        rollName.text = rollDisplay

        val numberFormatString = if(roll.displayInHex()) {"0x%x"} else {"%d"}

        // Start getting all the rolls
        val rollValues = roll.splitRoll()

        when (pageViewModel.getSortType())
        {
            1 -> {
                for(list in rollValues)
                {
                    list.value.sort()
                }
            }
            2 -> {
                for(list in rollValues)
                {
                    list.value.sort()
                    list.value.reverse()
                }
            }
        }

        var detailString = ""

        for (list in rollValues) {
            val dieName = list.key
            if(roll.getTotalDiceInRoll() > 1) {
                detailString += String.format("$dieName [$numberFormatString]: ",list.value.sum())
            }

            for(individualRoll in list.value)
            {
                detailString += String.format("$numberFormatString, ",individualRoll)
            }

            // Take off the last space and comma.
            detailString = detailString.removeRange(detailString.length - 2, detailString.length)

            detailString += "\n"
        }

        var sum = roll.mModifier

        for(list in rollValues)
        {
            sum += list.value.sum()
        }

        val correctedString = detailString.trim()

        val rollTotal = dialog.findViewById<TextView>(R.id.rollTotal)

        if(pageViewModel.getShowAverageRollResult())
        {
            val intAverage = roll.average().toInt()
            rollTotal.text = String.format("$numberFormatString [$numberFormatString]", sum, intAverage)
        }
        else
        {
            rollTotal.text =  String.format(numberFormatString, sum)
        }

        val rollDetails = dialog.findViewById<TextView>(R.id.rollDetails)
        rollDetails.text = correctedString

        val time = Calendar.getInstance().time
        val formatter = SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault())
        val formattedDate = formatter.format(time)

        listener.onRollResult(HistoryStamp(
            sum.toString(),
            rollDisplay,
            correctedString,
            formattedDate
        ))

        dialog.setOnDismissListener {
            unlockRotation()
        }

        dialog.show()
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

                    while(pauseThread)
                    {
                        SystemClock.sleep(100)
                    }

                    val speedKillMod = 1.0f - (killFrames.toFloat() / pageViewModel.getHoldDuration())
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

                    SystemClock.sleep(1)

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

                    if(activeFrames > pageViewModel.getShakeDuration() && !shakeHappened)
                    {
                        shakeHappened = true
                        activity?.runOnUiThread {
                            val shakeText = rollDialog.findViewById<TextView>(R.id.shakeText)
                            shakeText.text = context.getString(R.string.hold_still)
                        }
                    }

                    if(killMovement)
                    {
                        killFrames++
                    }

                    if(stableFrames > pageViewModel.getHoldDuration() && shakeHappened)
                    {
                        killMovement = true
                    }

                    if(killFrames > pageViewModel.getHoldDuration())
                    {
                        runThread = false
                    }
                }

                if(killMovement) {
                    SystemClock.sleep(500)
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

            accelerationStable = totalChange < pageViewModel.getShakeSensitivity()

            xAcceleration = event.values[0]
            yAcceleration = event.values[1]
            zAcceleration = event.values[2]
        }
    }

    private fun lockRotation()
    {
        val currentOrientation = activity?.resources?.configuration?.orientation
        if (currentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE //locks landscape
        } else {
            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT //locks port
        }
        lockedRotation = currentOrientation
    }

    private fun unlockRotation()
    {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
    }

    private fun playSound(maxVelocity : Float)
    {
        if(pageViewModel.getSoundEnabled() && maxVelocity != 0.0f) {
            for (player in mediaPlayers) {
                if (!player.isPlaying) {
                    val bounceVolume = min(abs(maxVelocity) / 50.0f, 1.0f)
                    player.setVolume(pageViewModel.getVolume() * bounceVolume, pageViewModel.getVolume() * bounceVolume)
                    player.start()
                    break
                }
            }
        }
    }

    interface DiceRollerListener
    {
        fun onRollResult(stamp : HistoryStamp)
    }

    inner class ShakeDie(private var dieImageID : Int)
    {
        var xVelocity = 0f
        var yVelocity = 0f
        var rotationSpeed = 0f
        private var dieView : ImageView?= null

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