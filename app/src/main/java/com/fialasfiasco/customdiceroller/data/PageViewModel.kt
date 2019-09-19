package com.fialasfiasco.customdiceroller.data

import android.util.Range
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.fialasfiasco.customdiceroller.dice.*

import com.fialasfiasco.customdiceroller.history.HistoryStamp
import java.lang.Error
import java.lang.IndexOutOfBoundsException

// Variables for changing numbers via increment and decrement
const val CHANGE_STEP_SMALL = 1
const val CHANGE_STEP_LARGE = 100

const val MAX_BOUNDING_VALUE = 1000
const val MIN_BOUNDING_VALUE = -1000

// Variables for how many sides on dice
const val MIN_DICE_SIDE_COUNT_SIMPLE = 0

val NORMAL_BOUNDING_RANGE = Range(MIN_BOUNDING_VALUE,MAX_BOUNDING_VALUE)
val SIMPLE_DIE_BOUNDING_RANGE = Range(MIN_DICE_SIDE_COUNT_SIMPLE,MAX_BOUNDING_VALUE)

class PageViewModel : ViewModel() {

    private fun enforceLimitsNoZero(value: Int, change: Int) : Int
    {
        val newValue = enforceLimits(value, change)

        // You cannot have a 0 as the value
        return if(newValue == 0)
        {
            when {
                value < -1 && change > 1 -> -1
                value > 1 && change < -1 -> 1
                change >= 0 -> 1
                else -> -1
            }
        } else {
            newValue
        }
    }

    private fun enforceLimits(value: Int, change: Int) : Int {
        val newValue = value + change
        return when {
            newValue > MAX_BOUNDING_VALUE -> MAX_BOUNDING_VALUE
            newValue < MIN_BOUNDING_VALUE -> MIN_BOUNDING_VALUE
            else -> newValue
        }
    }

    // Will return the increment that is needed to snap to the next evenly divisible stepSize.
    // i.e (101, 100) -> 99, (101,-100) -> -1
    private fun snapToNextIncrement(valueIn: Int, stepSize: Int) : Int {
        if(stepSize == 0 )
        {
            return 0
        }

        val valueRem = valueIn.rem(stepSize)

        // If you are negative jumping up, or positive jumping down, just drop down/up the remainder.
        return if((valueRem > 0 && stepSize < 0) || (valueRem < 0 && stepSize > 0))
        {
            -valueRem
        }
        else
        {
            -valueRem + stepSize
        }
    }

    // Temp one for the placeholder
    private val _index = MutableLiveData<Int>()
    val text: LiveData<String> = Transformations.map(_index) {
        "Hello world from section: $it"
    }
    fun setIndex(index: Int) {
        _index.value = index
    }

    private val _shakeEnabled = MutableLiveData<Boolean>()

    fun setShakeEnabled(enabled : Boolean)
    {
        _shakeEnabled.value = enabled
    }

    fun getShakeEnabled() : Boolean
    {
        if(_shakeEnabled.value == null)
        {
            return false
        }

        return _shakeEnabled.value!!
    }

    private val _shakeSensitivity = MutableLiveData<Float>()

    fun setShakeSensitivity(sensitivity : Float)
    {
        _shakeSensitivity.value = sensitivity
    }

    fun getShakeSensitivity() : Float
    {
        if(_shakeSensitivity.value == null)
        {
            return 0.0f
        }

        return _shakeSensitivity.value!!
    }

    private val _shakeDuration = MutableLiveData<Float>()

    fun setShakeDuration(duration : Float)
    {
        _shakeDuration.value = duration
    }

    fun getShakeDuration() : Float
    {
        if(_shakeDuration.value == null)
        {
            return 0f
        }

        return _shakeDuration.value!!
    }

    private val _holdDuration = MutableLiveData<Float>()

    fun setHoldDuration(duration : Float)
    {
        _holdDuration.value = duration
    }

    fun getHoldDuration() : Float
    {
        if(_holdDuration.value == null)
        {
            return 0f
        }

        return _holdDuration.value!!
    }

    private val _sortType = MutableLiveData<Int>()

    fun setSortType(type : Int)
    {
        _sortType.value = type
    }

    fun getSortType() : Int
    {
        if(_sortType.value == null)
        {
            return 0
        }

        return _sortType.value!!
    }

    private val _showAverageRollResult = MutableLiveData<Boolean>()

    fun setShowAverageRollResult(type : Boolean)
    {
        _showAverageRollResult.value = type
    }

    fun getShowAverageRollResult() : Boolean
    {
        if(_showAverageRollResult.value == null)
        {
            return false
        }

        return _showAverageRollResult.value!!
    }

    private val _enableRollProperties = MutableLiveData<Boolean>()

    fun setRollPropertiesEnabled(type : Boolean)
    {
        _enableRollProperties.value = type
    }

    fun getRollPropertiesEnabled() : Boolean
    {
        if(_enableRollProperties.value == null)
        {
            return false
        }

        return _enableRollProperties.value!!
    }

    private val _editEnabled = MutableLiveData<Boolean>()

    fun setEditEnabled(enabled : Boolean)
    {
        _editEnabled.value = enabled
    }

    fun getEditEnabled() : Boolean
    {
        if(_editEnabled.value == null)
        {
            return false
        }

        return _editEnabled.value!!
    }

    private val _itemsInRowSimple = MutableLiveData<Int>()

    fun setItemsInRow(type : Int)
    {
        _itemsInRowSimple.value = type
    }

    fun getItemsInRowSimple() : Int
    {
        if(_itemsInRowSimple.value == null)
        {
            return 4
        }

        return _itemsInRowSimple.value!!
    }

    private val _soundEnabled = MutableLiveData<Boolean>()

    fun setSoundEnabled(enabled : Boolean)
    {
        _soundEnabled.value = enabled
    }

    fun getSoundEnabled() : Boolean
    {
        if(_soundEnabled.value == null)
        {
            return false
        }

        return _soundEnabled.value!!
    }

    private val _volume = MutableLiveData<Float>()

    fun setVolume(volume : Float)
    {
        _volume.value = volume
    }

    fun getVolume() : Float
    {
        if(_volume.value == null)
        {
            return 0.0f
        }

        return _volume.value!!
    }

    private val _critSoundEnabled = MutableLiveData<Boolean>()

    fun setCritSoundEnabled(enabled : Boolean)
    {
        _critSoundEnabled.value = enabled
    }

    fun getCritSoundEnabled() : Boolean
    {
        if(_critSoundEnabled.value == null)
        {
            return false
        }

        return _critSoundEnabled.value!!
    }

    private val _simpleDieProperties = MutableLiveData<RollProperties>()

    private fun ensureSimpleProperties()
    {
        if(_simpleDieProperties.value == null)
        {
            _simpleDieProperties.value = RollProperties()
        }
    }

    // How many dice will be rolled at a time.
    fun setNumDiceExact(numDice: Int) {
        getSimpleRollProperties().mDieCount = enforceLimitsNoZero(numDice, 0)
    }

    fun incrementNumDice() {
        getSimpleRollProperties().mDieCount = enforceLimitsNoZero(getNumDice(), CHANGE_STEP_SMALL)
    }

    fun decrementNumDice() {
        getSimpleRollProperties().mDieCount = enforceLimitsNoZero(getNumDice(), -CHANGE_STEP_SMALL)
    }

    fun largeIncrementNumDice() {
        getSimpleRollProperties().mDieCount = enforceLimitsNoZero(getNumDice(), snapToNextIncrement(getNumDice(), CHANGE_STEP_LARGE))
    }

    fun largeDecrementNumDice() {
        getSimpleRollProperties().mDieCount = enforceLimitsNoZero(getNumDice(), snapToNextIncrement(getNumDice(), -CHANGE_STEP_LARGE))
    }

    // Need this so that we know what the value is even when it isn't broadcast.
    fun getNumDice() : Int
    {
        return getSimpleRollProperties().mDieCount
    }

    // What modifier will be added to the roll
    fun setModifierExact(modifier: Int) {
        getSimpleRollProperties().mModifier = enforceLimits(modifier, 0)
    }

    fun incrementModifier() {
        getSimpleRollProperties().mModifier = enforceLimits(getModifier(), CHANGE_STEP_SMALL)
    }

    fun decrementModifier() {
        getSimpleRollProperties().mModifier = enforceLimits(getModifier(), -CHANGE_STEP_SMALL)
    }

    fun largeIncrementModifier() {
        getSimpleRollProperties().mModifier = enforceLimits(getModifier(), snapToNextIncrement(getModifier(), CHANGE_STEP_LARGE))
    }

    fun largeDecrementModifier() {
        getSimpleRollProperties().mModifier = enforceLimits(getModifier(), snapToNextIncrement(getModifier(), -CHANGE_STEP_LARGE))
    }

    // Need this so that we know what the value is even when it isn't broadcast.
    fun getModifier() : Int
    {
        return getSimpleRollProperties().mModifier
    }

    // What modifier will be added to the roll
    fun setAdvantageDisadvantage(value : Int) {
        getSimpleRollProperties().mAdvantageDisadvantage = value
    }

    fun setDropHigh(drop: Int) {
        getSimpleRollProperties().mDropHigh = enforceLimits(drop, 0)
    }

    fun setDropLow(drop: Int) {
        getSimpleRollProperties().mDropLow = enforceLimits(drop, 0)
    }

    fun setReRoll(threshold: Int) {
        getSimpleRollProperties().mUseReRoll = true
        getSimpleRollProperties().mReRoll = enforceLimits(threshold, 0)
    }

    fun clearReRoll() {
        getSimpleRollProperties().mUseReRoll = false
        getSimpleRollProperties().mReRoll = 0
    }

    fun setMinimumDieValue(threshold: Int) {
        getSimpleRollProperties().mUseMinimumRoll = true
        getSimpleRollProperties().mMinimumRoll = enforceLimits(threshold, 0)
    }

    fun clearMinimumDieValue() {
        getSimpleRollProperties().mUseMinimumRoll = false
        getSimpleRollProperties().mMinimumRoll = 0
    }

    fun setExplode(explode: Boolean) {
        getSimpleRollProperties().mExplode = explode
    }

    fun getSimpleRollProperties() : RollProperties
    {
        ensureSimpleProperties()
        return _simpleDieProperties.value!!
    }

    // All of the rolls that have been stored in the current session
    private val _singleRollHistory = MutableLiveData<HistoryStamp>()
    val singleRollHistory: LiveData<HistoryStamp> = Transformations.map(_singleRollHistory) {
        _singleRollHistory.value
    }

    private val _rollHistory = MutableLiveData<MutableList<HistoryStamp>>()
    private val _savedRollHistory = MutableLiveData<List<HistoryStamp>>()

    fun addRollHistory(rollData: HistoryStamp)
    {
        _savedRollHistory.value = null
        if(_rollHistory.value == null)
        {
            _rollHistory.value = mutableListOf()
        }
        _rollHistory.value!!.add(0,rollData)
        _singleRollHistory.value = rollData
    }

    fun numHistoryStamps() : Int
    {
        if(_rollHistory.value == null)
        {
            return 0
        }

        return _rollHistory.value!!.size
    }

    fun getRollHistory(position: Int) : HistoryStamp
    {
        if(_rollHistory.value == null || _rollHistory.value!!.size <= position || position < 0)
        {
            return HistoryStamp("temp", "temp", "temp", "temp")
        }

        return _rollHistory.value!![position]
    }

    fun hasSavedHistory() : Boolean
    {
        return _savedRollHistory.value != null
    }

    fun restoreClearedHistory() {
        if(_savedRollHistory.value != null) {
            _rollHistory.value = _savedRollHistory.value!!.toMutableList()
            _savedRollHistory.value = null
        }
    }

    private val _clearHistory = MutableLiveData<Boolean>()
    val clearHistory: LiveData<Boolean> = Transformations.map(_clearHistory) {
        _clearHistory.value
    }

    fun clearHistory()
    {
        if(_rollHistory.value != null) {
            _savedRollHistory.value = _rollHistory.value!!.toList()
        }
        _rollHistory.value?.clear()
        _clearHistory.value = clearHistory.value?.not()
    }

    val fateDie = MinMaxDie("Fate", -1, 1)

    // Access for all of the dice that can be rolled
    private val diePoolArray = arrayOf(
        fateDie,
        SimpleDie("d2",2),
        SimpleDie("d3",3),
        SimpleDie("d4",4),
        SimpleDie("d6",6),
        SimpleDie("d8",8),
        SimpleDie("d10",10),
        SimpleDie("d12",12),
        SimpleDie("d20",20),
        SimpleDie("d100",100)
    )

    fun getDefaultDiePoolString() : String
    {
        var dieString = ""

        for(die in diePoolArray)
        {
            dieString += die.getDisplayName() + ", "
        }

        return dieString.removeRange(dieString.length-2, dieString.length)
    }

    private val _diePool = MutableLiveData<Array<Die>>()
    val diePool: LiveData<Set<String>> = Transformations.map(_diePool) {dieArray ->
        dieArray.sortBy {it.average()}
        val dieSet = mutableSetOf<String>()
        for (die in dieArray) {
            dieSet.add(die.saveToString())
        }
        dieSet
    }

    private fun ensureDiePoolExists() {
        if(_diePool.value == null)
        {
            resetDiePool()
        }
    }

    fun initDiePoolFromStrings(pool : Set<String>?) : Boolean
    {
        var somethingFailed = false
        if(pool != null) {
            val dice = mutableListOf<Die>()

            for (dieString in pool) {
                try {
                    dice.add(DieFactory().createUnknownDie(dieString))
                } catch (error: DieLoadError) {
                    // Throw away that die, but report that something went wrong.
                    somethingFailed = true
                }
            }

            val dieArray = dice.toTypedArray()
            dieArray.sortBy{it.average()}

            _diePool.value = dieArray
        }
        else
        {
            _diePool.value = diePoolArray
        }

        return somethingFailed
    }

    fun attemptRecoveryOfDiePoolFromStrings(recoveryPool : Set<String>?) : Boolean {
        ensureDiePoolExists()
        var somethingFailed = false
        if(recoveryPool != null) {
            val dice = _diePool.value!!.toMutableSet()

            for (dieString in recoveryPool) {
                try {
                    val recoveryDie = DieFactory().createUnknownDie(dieString)
                    if(getDieByName(recoveryDie.getDisplayName()) == null) {
                        dice.add(recoveryDie)
                    }
                } catch (error: DieLoadError) {
                    // Throw away that die, but report that something went wrong.
                    somethingFailed = true
                }
            }

            val dieArray = dice.toTypedArray()
            dieArray.sortBy{it.average()}

            _diePool.value = dieArray
        }

        return somethingFailed
    }

    fun addDieToPool(die: Die) : Boolean
    {
        ensureDiePoolExists()

        val newPool = _diePool.value!!.toMutableSet()

        val added = if(hasDieByName(die))
        {
            false
        }
        else
        {
            newPool.add(die)
        }

        _diePool.value = newPool.toTypedArray()

        return added
    }

    private fun getDieByName(dieName : String) : Die? {

        ensureDiePoolExists()

        for(die in _diePool.value!!) {
            if(die.getDisplayName() == dieName) {
                return die
            }
        }

        return null
    }

    private fun hasDieByName(die: Die) : Boolean
    {
        ensureDiePoolExists()

        return getDieByName(die.getDisplayName()) != null
    }

    fun removeDieFromPool(die: Die) : Boolean
    {
        ensureDiePoolExists()

        val newPool = _diePool.value!!.toMutableSet()
        val removed = newPool.remove(die)

        _diePool.value = newPool.toTypedArray()

        removeCustomDieFromPool(die)

        return removed
    }

    fun resetDiePool()
    {
        _diePool.value = diePoolArray
        _customDiePool.value = Roll("temp", "temp")
    }

    fun getNumberDiceItems() : Int
    {
        ensureDiePoolExists()
        return _diePool.value!!.size
    }

    fun getDie(position: Int) : Die
    {
        ensureDiePoolExists()
        if(_diePool.value!!.size <= position || position < 0 ) {
            return SimpleDie("",1)
        }

        return _diePool.value!![position]
    }

    private val _customDiePool = MutableLiveData<Roll>()
    val customDiePool: LiveData<Roll> = Transformations.map(_customDiePool) {roll ->
        roll.clone(roll.getDisplayName(), roll.getCategoryName())
    }

    private fun ensureCustomDiePoolExists() {
        if(_customDiePool.value == null)
        {
            _customDiePool.value = Roll("temp", "temp")
        }
    }

    fun getNumberCustomRollItems() : Int {
        ensureCustomDiePoolExists()
        return _customDiePool.value!!.getDice().size
    }

    fun addDieToCustomRoll(die: Die) : Boolean
    {
        ensureCustomDiePoolExists()
        return if(!_customDiePool.value!!.containsDie(die)) {
            _customDiePool.value!!.addDieToRoll(die, RollProperties())
            true
        } else {
            false
        }
    }

    fun removeCustomDieFromPool(die: Die) : Boolean
    {
        ensureCustomDiePoolExists()

        return _customDiePool.value!!.removeDieFromRoll(die)
    }

    fun getCustomDieAt(customDiePosition: Int) : Die
    {
        ensureCustomDiePoolExists()

        return _customDiePool.value!!.getDieAt(customDiePosition)
    }

    fun moveCustomDieUp(customDiePosition: Int) : Boolean
    {
        ensureCustomDiePoolExists()

        return _customDiePool.value!!.moveDieUp(customDiePosition)
    }

    fun moveCustomDieDown(customDiePosition: Int) : Boolean
    {
        ensureCustomDiePoolExists()
        return _customDiePool.value!!.moveDieDown(customDiePosition)
    }

    fun createRollFromCustomRollerState(rollName : String, rollCategory: String) : Roll
    {
        ensureCustomDiePoolExists()
        return _customDiePool.value!!.clone(rollName, rollCategory)
    }

    fun getCustomDieRollProperties(customDiePosition: Int) : RollProperties
    {
        ensureCustomDiePoolExists()
        val props = _customDiePool.value!!.getRollPropertiesAt(customDiePosition)

        if(!getRollPropertiesEnabled()) {
            props.mAdvantageDisadvantage = rollNaturalValue
            props.mDropHigh = 0
            props.mDropLow = 0
            props.mUseReRoll = false
            props.mReRoll = 0
            props.mUseMinimumRoll = false
            props.mMinimumRoll= 0
            props.mExplode = false
        }

        return props
    }

    fun getCustomDieDieCount(customDiePosition: Int) : Int
    {
        return getCustomDieRollProperties(customDiePosition).mDieCount
    }

    fun setCustomDieCountExact(customDiePosition: Int, count: Int)
    {
        getCustomDieRollProperties(customDiePosition).mDieCount = enforceLimitsNoZero(count,0)
    }

    fun incrementCustomDieCount(customDiePosition: Int) {
        val props = getCustomDieRollProperties(customDiePosition)
        props.mDieCount = enforceLimitsNoZero(props.mDieCount, CHANGE_STEP_SMALL)
    }

    fun decrementCustomDieCount(customDiePosition: Int) {
        val props = getCustomDieRollProperties(customDiePosition)
        props.mDieCount = enforceLimitsNoZero(props.mDieCount, -CHANGE_STEP_SMALL)
    }

    fun largeIncrementCustomDieCount(customDiePosition: Int) {
        val props = getCustomDieRollProperties(customDiePosition)
        props.mDieCount = enforceLimitsNoZero(props.mDieCount, snapToNextIncrement(props.mDieCount, CHANGE_STEP_LARGE))
    }

    fun largeDecrementCustomDieCount(customDiePosition: Int) {
        val props = getCustomDieRollProperties(customDiePosition)
        props.mDieCount = enforceLimitsNoZero(props.mDieCount, snapToNextIncrement(props.mDieCount, -CHANGE_STEP_LARGE))
    }

    fun getCustomDieModifier(customDiePosition: Int) : Int
    {
        return getCustomDieRollProperties(customDiePosition).mModifier
    }

    fun setCustomDieModifierExact(customDiePosition: Int, modifier: Int)
    {
        getCustomDieRollProperties(customDiePosition).mModifier = enforceLimits(modifier, 0)
    }

    fun incrementCustomDieModifier(customDiePosition: Int) {
        val props = getCustomDieRollProperties(customDiePosition)
        props.mModifier = enforceLimits(props.mModifier, CHANGE_STEP_SMALL)
    }

    fun decrementCustomDieModifier(customDiePosition: Int) {
        val props = getCustomDieRollProperties(customDiePosition)
        props.mModifier = enforceLimits(props.mModifier, -CHANGE_STEP_SMALL)
    }

    fun largeIncrementCustomDieModifier(customDiePosition: Int) {
        val props = getCustomDieRollProperties(customDiePosition)
        props.mModifier = enforceLimits(props.mModifier, snapToNextIncrement(props.mModifier, CHANGE_STEP_LARGE))
    }

    fun largeDecrementCustomDieModifier(customDiePosition: Int) {
        val props = getCustomDieRollProperties(customDiePosition)
        props.mModifier = enforceLimits(props.mModifier, snapToNextIncrement(props.mModifier, -CHANGE_STEP_LARGE))
    }

    fun setAdvantageDisadvantageCustomDie(customDiePosition: Int, value : Int) {
        getCustomDieRollProperties(customDiePosition).mAdvantageDisadvantage = value
    }

    fun setCustomDieDropHigh(customDiePosition: Int, value : Int) {
        getCustomDieRollProperties(customDiePosition).mDropHigh = value
    }

    fun setCustomDieDropLow(customDiePosition: Int, value : Int) {
        getCustomDieRollProperties(customDiePosition).mDropLow = value
    }


    fun setCustomDieReRoll(customDiePosition: Int, threshold: Int) {
        getCustomDieRollProperties(customDiePosition).mUseReRoll = true
        getCustomDieRollProperties(customDiePosition).mReRoll = enforceLimits(threshold, 0)
    }

    fun clearCustomDieReRoll(customDiePosition: Int) {
        getCustomDieRollProperties(customDiePosition).mUseReRoll = false
        getCustomDieRollProperties(customDiePosition).mReRoll = 0
    }

    fun setCustomDieMinimumDieValue(customDiePosition: Int, threshold: Int) {
        getCustomDieRollProperties(customDiePosition).mUseMinimumRoll = true
        getCustomDieRollProperties(customDiePosition).mMinimumRoll = enforceLimits(threshold, 0)
    }

    fun clearCustomDieMinimumDieValue(customDiePosition: Int) {
        getCustomDieRollProperties(customDiePosition).mUseMinimumRoll = false
        getCustomDieRollProperties(customDiePosition).mMinimumRoll = 0
    }

    fun setCustomDieExplode(customDiePosition: Int, explode: Boolean) {
        getCustomDieRollProperties(customDiePosition).mExplode = explode
    }

    private val _savedRollPool = MutableLiveData<Map<String,MutableList<Roll>>>()
    val savedRollPool: LiveData<Set<String>> = Transformations.map(_savedRollPool) {rollMap ->
        val rollSet = mutableSetOf<String>()

        for(rollArray in rollMap) {
            rollArray.value.sortBy {it.getDisplayName().toLowerCase()}
            for (roll in rollArray.value) {
                rollSet.add(roll.saveToString())
            }
        }
        rollSet
    }

    private fun ensureSavedRollPoolExists() {
        if(_savedRollPool.value == null)
        {
            resetSavedRollPool()
        }
    }

    fun initSavedRollPoolFromStrings(pool : Set<String>?) : Boolean
    {
        var somethingFailed = false
        if(pool != null) {
            val rolls = mutableMapOf<String, MutableList<Roll>>()

            for (rollString in pool) {
                try {
                    val newRoll = DieFactory().createRoll(rollString)
                    val rollCategory = newRoll.getCategoryName()
                    if(rolls.containsKey(rollCategory)) {
                        rolls.getValue(rollCategory).add(newRoll)
                    } else {
                        rolls[rollCategory] = mutableListOf(newRoll)
                    }
                } catch (error: DieLoadError) {
                    // Throw away that roll & report error.
                    somethingFailed = true
                } catch (error: Error) {
                    somethingFailed = true
                }
            }

            for(rollMapping in rolls) {
                rollMapping.value.sortBy {it.getDisplayName()}
            }

            _savedRollPool.value = rolls.toSortedMap()
        }
        else
        {
            _savedRollPool.value = mutableMapOf()
        }

        return somethingFailed
    }

    fun attemptRecoveryOfSavedRollPoolFromStrings(recoveryPool : Set<String>?) : Boolean
    {
        ensureCustomDiePoolExists()
        var somethingFailed = false
        if(recoveryPool != null) {
            val rolls = _savedRollPool.value!!.toMutableMap()

            for (rollString in recoveryPool) {
                try {
                    val recoveryRoll = DieFactory().createRoll(rollString)
                    val recoveryRollCategory = recoveryRoll.getCategoryName()
                    if(getSavedRollByName(recoveryRoll.getDisplayName(), recoveryRoll.getCategoryName()) == null) {
                        if (rolls.containsKey(recoveryRollCategory)) {
                            rolls.getValue(recoveryRollCategory).add(recoveryRoll)
                        } else {
                            rolls[recoveryRollCategory] = mutableListOf(recoveryRoll)
                        }
                    }
                } catch (error: DieLoadError) {
                    // Throw away that roll & report error.
                    somethingFailed = true
                } catch (error: Error) {
                    somethingFailed = true
                }
            }

            for(rollMapping in rolls) {
                rollMapping.value.sortBy {it.getDisplayName()}
            }

            _savedRollPool.value = rolls.toSortedMap()
        }

        return somethingFailed
    }

    fun addSavedRollToPool(roll: Roll, override: Boolean) : Boolean
    {
        ensureSavedRollPoolExists()

        val rollName = roll.getDisplayName()
        val rollCategory = roll.getCategoryName()
        if(override)
        {
            removeSavedRollByName(rollName, rollCategory)
        }

        val newPool = _savedRollPool.value!!.toMutableMap()

        val added = if(hasSavedRollByName(rollName, rollCategory))
        {
            false
        }
        else
        {
            if (newPool.containsKey(rollCategory)) {
                newPool.getValue(rollCategory).add(roll)
            } else {
                newPool[rollCategory] = mutableListOf(roll)
                true
            }
        }

        _savedRollPool.value = newPool.toSortedMap()

        return added
    }

    fun changeSavedRollCategory(roll: Roll, newCategory: String) : Boolean {
        val clonedRoll = roll.clone(roll.getDisplayName(), newCategory)

        var movedRoll = false
        if(removeSavedRollFromPool(roll)) {
            if(addSavedRollToPool(clonedRoll, false)) {
                movedRoll = true
            } else {
                addSavedRollToPool(roll, false)
            }
        }

        return movedRoll
    }

    fun hasSavedRollByName(rollName: String, rollCategory: String) : Boolean
    {
        return getSavedRollByName(rollName, rollCategory) != null
    }

    private fun getSavedRollByName(rollName: String, rollCategory: String) : Roll?
    {
        ensureSavedRollPoolExists()

        if(_savedRollPool.value!!.containsKey(rollCategory)) {
            for (savedRoll in _savedRollPool.value!!.getValue(rollCategory)) {
                if (savedRoll.getDisplayName() == rollName) {
                    return savedRoll
                }
            }
        }

        return null
    }

    private fun removeSavedRollByName(rollName: String, rollCategory: String)
    {
        val roll = getSavedRollByName(rollName, rollCategory)
        if(roll != null)
        {
            removeSavedRollFromPool(roll)
        }
    }

    fun removeSavedRollFromPool(roll: Roll) : Boolean
    {
        ensureSavedRollPoolExists()

        val newPool = _savedRollPool.value!!.toMutableMap()

        val removed = if (newPool.containsKey(roll.getCategoryName())) {
            val list = newPool.getValue(roll.getCategoryName())
            val innerRemoved = list.remove(roll)

            if(list.isEmpty()) {
                newPool.remove(roll.getCategoryName())
            }

            innerRemoved
        } else {
            false
        }

        _savedRollPool.value = newPool.toSortedMap()

        return removed
    }

    fun resetSavedRollPool()
    {
        _savedRollPool.value = mapOf()
    }

    fun getNumSavedRolls(rollCategory: String) : Int
    {
        ensureSavedRollPoolExists()
        return if(_savedRollPool.value!!.containsKey(rollCategory)) {
            _savedRollPool.value!!.getValue(rollCategory).size
        } else {
            0
        }
    }

    fun getNumSavedRollCategories() : Int {
        ensureSavedRollPoolExists()
        return _savedRollPool.value!!.size
    }

    fun getSavedRoll(rollCategory: String, position: Int) : Roll
    {
        ensureSavedRollPoolExists()

        if(!_savedRollPool.value!!.containsKey(rollCategory) ||
            _savedRollPool.value!!.getValue(rollCategory).size <= position ||
            position < 0 ) {
            return Roll("INVALID", "INVALID")
        }

        return _savedRollPool.value!!.getValue(rollCategory)[position]
    }

    private val _rollToEdit = MutableLiveData<Roll>()
    val rollToEdit: LiveData<Roll> = Transformations.map(_rollToEdit) { roll ->
        roll
    }

    fun beginRollEdit(roll: Roll) {
        try {
            val clonedRoll = DieFactory().createRoll(roll.saveToString())
            _customDiePool.value = clonedRoll
            _rollToEdit.value = clonedRoll
            _rollToEdit.value = null
        } catch (error: DieLoadError)
        {
            // Just let it pass
        }
    }

    fun getSavedRollCategory(position: Int) : String {
        ensureSavedRollPoolExists()
        return try {
            if (_savedRollPool.value!!.isNotEmpty()) {
                _savedRollPool.value!!.toList()[position].first
            } else {
                ""
            }
        } catch (error : IndexOutOfBoundsException) {
            ""
        }
    }


    private val mDefaultCategories = listOf(
    "Attack",
    "Damage",
    "Critical Hit!",
    "Spell",
    "Saving Throw",
    "Ability Check"
    )

    fun getExistingCategories() : List<String> {
        ensureSavedRollPoolExists()
        val allCategories = mutableSetOf<String>()

        allCategories.addAll(_savedRollPool.value!!.keys)
        allCategories.addAll(mDefaultCategories)

        return allCategories.sortedBy { it }
    }


    private val _expandedCategoryState = MutableLiveData<MutableMap<String,Boolean>>()

    private fun ensureExpandedCategoryStateExists() {
        if(_expandedCategoryState.value == null) {
            _expandedCategoryState.value = mutableMapOf()
        }
    }

    fun setCategoryExpanded(category: String, expanded: Boolean) {
        ensureExpandedCategoryStateExists()

        _expandedCategoryState.value!![category] = expanded
    }

    fun getCategoryExpanded(category: String) : Boolean {
        ensureExpandedCategoryStateExists()

        if(_expandedCategoryState.value!!.containsKey(category)) {
            return _expandedCategoryState.value!!.getValue(category)
        }

        return false
    }

}