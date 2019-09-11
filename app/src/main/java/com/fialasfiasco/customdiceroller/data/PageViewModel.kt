package com.fialasfiasco.customdiceroller.data

import android.util.Range
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.fialasfiasco.customdiceroller.dice.*

import com.fialasfiasco.customdiceroller.history.HistoryStamp

// Variables for changing numbers via increment and decrement
const val CHANGE_STEP_SMALL = 1
const val CHANGE_STEP_LARGE = 100

const val MAX_BOUNDING_VALUE = 1000
const val MIN_BOUNDING_VALUE = -1000

// Variables for how many sides on dice
const val MIN_DICE_SIDE_COUNT_SIMPLE = 1

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

    private val _shakeDuration = MutableLiveData<Int>()

    fun setShakeDuration(duration : Int)
    {
        _shakeDuration.value = duration
    }

    fun getShakeDuration() : Int
    {
        if(_shakeDuration.value == null)
        {
            return 0
        }

        return _shakeDuration.value!!
    }

    private val _holdDuration = MutableLiveData<Int>()

    fun setHoldDuration(duration : Int)
    {
        _holdDuration.value = duration
    }

    fun getHoldDuration() : Int
    {
        if(_holdDuration.value == null)
        {
            return 0
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

    fun initDiePoolFromStrings(pool : Set<String>?)
    {
        if(pool != null) {
            val dice = mutableListOf<Die>()

            for (dieString in pool) {
                try {
                    dice.add(DieFactory().createUnknownDie(dieString))
                } catch (error: DieLoadError) {
                    // Throw away that die.
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
    }

    fun addDieToPool(die: Die) : Boolean
    {
        if(_diePool.value == null)
        {
            resetDiePool()
        }

        val newPool = _diePool.value!!.toMutableSet()

        val added = if(hasDie(die))
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

    private fun hasDie(die: Die) : Boolean
    {
        if(_diePool.value == null)
        {
            resetDiePool()
        }

        for(savedDie in _diePool.value!!)
        {
            if(savedDie.getDisplayName() == die.getDisplayName())
            {
                return true
            }
        }

        return false
    }

    fun removeDieFromPool(die: Die) : Boolean
    {
        if(_diePool.value == null)
        {
            resetDiePool()
        }

        val newPool = _diePool.value!!.toMutableList()
        val removed = newPool.remove(die)

        _diePool.value = newPool.toTypedArray()

        removeCustomDieFromPool(die)

        return removed
    }

    fun resetDiePool()
    {
        _diePool.value = diePoolArray
        _customDiePool.value = Roll("POOL")
    }

    fun getNumberDiceItems() : Int
    {
        if(_diePool.value != null) {
            return _diePool.value!!.size
        }
        return 0
    }

    fun getDie(position: Int) : Die
    {
        if(_diePool.value == null || _diePool.value!!.size <= position || position < 0 ) {
            return SimpleDie("",1)
        }

        return _diePool.value!![position]
    }

    private val _customDiePool = MutableLiveData<Roll>()
    val customDiePool: LiveData<Int> = Transformations.map(_customDiePool) {roll ->
        roll.getDice().size
    }

    private fun ensureCustomDiePoolExists() {
        if(_customDiePool.value == null)
        {
            _customDiePool.value = Roll("POOL")
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

    fun createRollFromCustomRollerState(rollName : String) : Roll
    {
        ensureCustomDiePoolExists()
        return _customDiePool.value!!.clone(rollName)
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

    private val _savedRollPool = MutableLiveData<Array<Roll>>()
    val savedRollPool: LiveData<Set<String>> = Transformations.map(_savedRollPool) {rollArray ->
        rollArray.sortBy {it.getDisplayName()}
        val rollSet = mutableSetOf<String>()
        for (roll in rollArray) {
            rollSet.add(roll.saveToString())
        }
        rollSet
    }

    fun initSavedRollPoolFromStrings(pool : Set<String>?)
    {
        if(pool != null) {
            val rolls = mutableListOf<Roll>()

            for (rollString in pool) {
                try {
                    rolls.add(DieFactory().createRoll(rollString))
                } catch (error: DieLoadError) {
                    // Throw away that roll.
                }
            }

            val rollArray = rolls.toTypedArray()
            rollArray.sortBy{it.getDisplayName()}

            _savedRollPool.value = rollArray
        }
        else
        {
            _savedRollPool.value = arrayOf()
        }
    }

    fun addSavedRollToPool(roll: Roll, override: Boolean) : Boolean
    {
        if(_savedRollPool.value == null)
        {
            resetSavedRollPool()
        }

        val rollName = roll.getDisplayName()
        if(override)
        {
            removeSavedRollByName(rollName)
        }

        val newPool = _savedRollPool.value!!.toMutableSet()

        val added = if(hasSavedRollByName(rollName))
        {
            false
        }
        else
        {
            newPool.add(roll)
        }

        _savedRollPool.value = newPool.toTypedArray()

        return added
    }

    fun hasSavedRollByName(rollName: String) : Boolean
    {
        return getSavedRollByName(rollName) != null
    }

    private fun getSavedRollByName(rollName: String) : Roll?
    {
        if(_savedRollPool.value == null)
        {
            resetSavedRollPool()
        }

        for(savedRoll in _savedRollPool.value!!)
        {
            if(savedRoll.getDisplayName() == rollName)
            {
                return savedRoll
            }
        }

        return null
    }

    private fun removeSavedRollByName(rollName: String)
    {
        val roll = getSavedRollByName(rollName)
        if(roll != null)
        {
            removeSavedRollFromPool(roll)
        }
    }

    fun removeSavedRollFromPool(roll: Roll) : Boolean
    {
        if(_savedRollPool.value == null)
        {
            resetSavedRollPool()
        }

        val newPool = _savedRollPool.value!!.toMutableSet()
        val removed = newPool.remove(roll)

        _savedRollPool.value = newPool.toTypedArray()

        return removed
    }

    fun resetSavedRollPool()
    {
        _savedRollPool.value = arrayOf()
    }

    fun getSavedRollSize() : Int
    {
        if(_savedRollPool.value != null) {
            return _savedRollPool.value!!.size
        }
        return 0
    }

    fun getSavedRoll(position: Int) : Roll
    {
        if(_savedRollPool.value == null || _savedRollPool.value!!.size <= position || position < 0 ) {
            return Roll("INVALID")
        }

        return _savedRollPool.value!![position]
    }

}