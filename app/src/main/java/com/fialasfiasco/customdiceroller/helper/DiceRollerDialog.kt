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
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.StrikethroughSpan
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import com.fialasfiasco.customdiceroller.R
import com.fialasfiasco.customdiceroller.data.PageViewModel
import com.fialasfiasco.customdiceroller.dice.Roll
import com.fialasfiasco.customdiceroller.history.HistoryStamp
import java.text.SimpleDateFormat
import java.util.*
import kotlin.NoSuchElementException
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt
import kotlin.random.Random

const val MAX_DICE_IN_SHAKE_ROLLER = 50

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
    private var wilhelmScreamPlayer = MediaPlayer()
    private var tripleHornPlayer = MediaPlayer()

    // Thread variables
    private var shakerThread: Thread ?= null
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
            wilhelmScreamPlayer = MediaPlayer.create(context, R.raw.wilhelm_scream)
            tripleHornPlayer = MediaPlayer.create(context, R.raw.triple_airhorn)
        } catch (error : Resources.NotFoundException) {
            Toast.makeText(context, "Error with loading sound", Toast.LENGTH_SHORT).show()
            mediaPlayers.clear()
            wilhelmScreamPlayer = MediaPlayer()
            tripleHornPlayer = MediaPlayer()
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
        if(wilhelmScreamPlayer.isPlaying) {
            wilhelmScreamPlayer.pause()
        }
        if(tripleHornPlayer.isPlaying) {
            tripleHornPlayer.pause()
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
        wilhelmScreamPlayer = MediaPlayer()
        tripleHornPlayer = MediaPlayer()
    }

    fun runShakeRoller(roll: Roll)
    {
        val totalDice = roll.getTotalDiceInRoll()

        if(totalDice <= 0)
        {
            Toast.makeText(context, "No dice to roll", Toast.LENGTH_SHORT).show()
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

        // If we have too many dice, only take a portion of each set.
        val needsDieRepresentation = totalDice > MAX_DICE_IN_SHAKE_ROLLER

        dialog.setOnShowListener {
            lockRotation()
            runThread = true
            val shakerDice = mutableListOf<ShakeDie>()
            for(dieCountPair in roll.getDice())
            {
                val maxDice = abs(dieCountPair.value.mDieCount)

                // how many of these dice will we add
                val numDiceToAdd = if(needsDieRepresentation) {
                    ((maxDice.toFloat() / totalDice) * MAX_DICE_IN_SHAKE_ROLLER).toInt()
                } else {
                    maxDice
                }

                for(index in 0 until max(1,numDiceToAdd))
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

            diceRollThreadRunner(dialog, shakerDice)
        }

        dialog.show()
    }

    fun runRollDisplay(roll : Roll)
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
        val rollValues = roll.roll()

        when (pageViewModel.getSortType())
        {
            1 -> {
                rollValues.sortAscending()
            }
            2 -> {
                rollValues.sortDescending()
            }
        }

        val detailString = SpannableStringBuilder()

        for (rollMapping in rollValues.mRollResults) {
            try {
                val dieName = rollMapping.key

                val rollResults = rollValues.mRollResults.getValue(dieName)
                val rollResultsDropped = rollValues.mDroppedRolls.getValue(dieName)
                val rollResultsReRolled = rollValues.mReRolledRolls.getValue(dieName)
                val rollResultsStruck = rollValues.mStruckRollResults.getValue(dieName)
                val rollResultsStruckDropped = rollValues.mStruckDroppedRolls.getValue(dieName)
                val rollResultsStruckReRolled = rollValues.mStruckReRolledRolls.getValue(dieName)
                val rollModifier = rollValues.mRollModifiers.getValue(dieName)

                val rollResultsString = rollResults.joinToString()
                val rollResultsDroppedString = rollResultsDropped.joinToString()
                val rollResultsReRolledString = rollResultsReRolled.joinToString()
                val rollResultsStruckString = rollResultsStruck.joinToString()
                val rollResultsStruckDroppedString = rollResultsStruckDropped.joinToString()
                val rollResultsStruckReRolledString = rollResultsStruckReRolled.joinToString()
                val rollModifierString = getModifierString(rollModifier)

                if(rollResults.isNotEmpty() || rollResultsStruck.isNotEmpty() || rollModifier != 0) {
                    detailString.append(String.format("$dieName [$numberFormatString]: ", rollResults.sum() + rollModifier))
                    detailString.append(rollResultsString)
                    if(rollModifier != 0)
                    {
                        if(rollResultsString.isNotEmpty())
                        {
                            detailString.append(",")
                        }
                        detailString.append("($rollModifierString)")
                    }
                    if(rollResultsStruckString.isNotEmpty()) {
                        detailString.append(" ")
                        detailString.append(
                            rollResultsStruckString,
                            StrikethroughSpan(),
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                        )
                    }
                    detailString.append("\n")
                }

                if(rollResultsDropped.isNotEmpty() || rollResultsStruckDropped.isNotEmpty()) {
                    detailString.append(String.format("$dieName (dropped) [$numberFormatString]: ", rollResultsDropped.sum()))
                    detailString.append(rollResultsDroppedString)
                    if(rollResultsStruckDroppedString.isNotEmpty()) {
                        detailString.append(" ")
                        detailString.append(
                            rollResultsStruckDroppedString,
                            StrikethroughSpan(),
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                        )
                    }
                    detailString.append("\n")
                }

                if(rollResultsReRolled.isNotEmpty() || rollResultsStruckReRolled.isNotEmpty()) {
                    detailString.append(String.format("$dieName (re-rolled) [$numberFormatString]: ", rollResultsReRolled.sum()))
                    detailString.append(rollResultsReRolledString)
                    if(rollResultsStruckReRolledString.isNotEmpty()) {
                        detailString.append(" ")
                        detailString.append(
                            rollResultsStruckReRolledString,
                            StrikethroughSpan(),
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                        )
                    }
                    detailString.append("\n")
                }

            } catch (error : NoSuchElementException) {
                // Should never happen, but going to wrap it just in case.
            }
        }

        if(pageViewModel.getShowAverageRollResult()) {
            val averageText = String.format(numberFormatString, roll.average().toInt())

            detailString.append("Average Roll - [$averageText]")
        }

        var sumResult = 0
        var sumStruck = sumResult
        var displayStruck = false
        var displayDropped = false

        for(list in rollValues.mRollResults)
        {
            sumResult += list.value.sum()
        }

        for(list in rollValues.mRollModifiers) {
            sumResult += list.value
            sumStruck += list.value
        }

        // Only show the struck through text
        if(rollValues.mStruckRollResults.isNotEmpty()) {
            for (list in rollValues.mStruckRollResults) {
                if(list.value.isNotEmpty()) {
                    sumStruck += list.value.sum()
                    displayStruck = true
                }
            }
        }

        var sumDropped = sumResult
        if(rollValues.mDroppedRolls.isNotEmpty()) {
            for (list in rollValues.mDroppedRolls) {
                if(list.value.isNotEmpty()) {
                    sumDropped += list.value.sum()
                    displayDropped = true
                }
            }
        }

        val correctedString = detailString.trim()

        val rollTotal = dialog.findViewById<TextView>(R.id.rollTotal)

        val highText = String.format(numberFormatString, sumResult)
        val lowText = String.format(numberFormatString, sumStruck)
        val droppedText = String.format(numberFormatString, sumDropped)

        val struckText = when {
            displayStruck -> lowText
            displayDropped -> droppedText
            else -> ""
        }

        rollTotal.text = if(displayDropped || displayStruck) {
            val text = SpannableString("$highText $struckText")
            text.setSpan(StrikethroughSpan(), highText.length + 1, highText.length + 1 + struckText.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            text
        }
        else
        {
            SpannableString(highText)
        }

        val rollDetails = dialog.findViewById<TextView>(R.id.rollDetails)
        rollDetails.text = correctedString

        val time = Calendar.getInstance().time
        val formatter = SimpleDateFormat("yyyy/MM/dd\nHH:mm:ss", Locale.getDefault())
        val formattedDate = formatter.format(time)

        listener.onRollResult(HistoryStamp(
            rollTotal.text,
            rollName.text,
            rollDetails.text,
            formattedDate
        ))

        dialog.setOnDismissListener {
            unlockRotation()
        }

        if (rollValues.mRollMaximumValue) {
            playTripleHorn()
        } else if (rollValues.mRollMinimumValue) {
            playWilhelmScream()
        }

        dialog.show()
    }

    private fun diceRollThreadRunner(rollDialog: Dialog, shakerDice : List<ShakeDie>)
    {
        // Someone experienced a crash by having two of the treads alive and modifying the same stuff.
        // Kill the existing thread if this is called
        if(shakerThread != null) {
            while (shakerThread?.isAlive!!) {
                runThread = false
                SystemClock.sleep(1)
            }
            runThread = true
        }

        shakerThread = Thread {
            threadDead = false
            val rollContainer = rollDialog.findViewById<ConstraintLayout>(R.id.rollArea)
            if(rollContainer != null) {

                // Used to track how many frames they shook it for.
                var stableTime = 0f
                var activeTime = 0f
                var totalTime = 0f
                var killTime = 0f
                var shakeHappened = false
                var killMovement = false

                while (runThread) {

                    while(pauseThread)
                    {
                        SystemClock.sleep(100)
                    }

                    val startTime = System.currentTimeMillis()

                    val speedKillMod = max(1.0f - (killTime / pageViewModel.getHoldDuration()), 0.0f)
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

                    val endTime = System.currentTimeMillis()
                    val elapsedSeconds = (endTime - startTime) / 1000.0f

                    if(accelerationStable)
                    {
                        stableTime += elapsedSeconds
                        activeTime = 0f
                    }
                    else
                    {
                        stableTime = 0f
                        activeTime += elapsedSeconds
                    }

                    if(activeTime > pageViewModel.getShakeDuration() && !shakeHappened)
                    {
                        shakeHappened = true
                        activity?.runOnUiThread {
                            val shakeText = rollDialog.findViewById<TextView>(R.id.shakeText)
                            shakeText.text = context.getString(R.string.hold_still)
                        }
                    }

                    if(killMovement)
                    {
                        killTime += elapsedSeconds
                    }

                    if(stableTime > pageViewModel.getHoldDuration() && shakeHappened)
                    {
                        killMovement = true
                    }

                    if(killTime > pageViewModel.getHoldDuration())
                    {
                        runThread = false
                    }

                    totalTime += elapsedSeconds

                    if(totalTime > 10f)
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
                }
            }

            activity?.runOnUiThread {
                rollDialog.dismiss()
            }
            threadDead = true
        }

        shakerThread?.start()
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

    private fun playWilhelmScream()
    {
        if(pageViewModel.getSoundEnabled() && pageViewModel.getCritSoundEnabled())
        {
            if(!wilhelmScreamPlayer.isPlaying) {
                wilhelmScreamPlayer.setVolume(pageViewModel.getVolume(), pageViewModel.getVolume())
                wilhelmScreamPlayer.start()
            }
        }
    }

    private fun playTripleHorn()
    {
        if(pageViewModel.getSoundEnabled() && pageViewModel.getCritSoundEnabled())
        {
            if(!tripleHornPlayer.isPlaying) {
                tripleHornPlayer.setVolume(pageViewModel.getVolume(), pageViewModel.getVolume())
                tripleHornPlayer.start()
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