package com.fialasfiasco.customdiceroller.data

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel

import com.fialasfiasco.customdiceroller.history.HistoryStamp

// Variables for changing numbers via increment and decrement
const val CHANGE_STEP_SMALL = 1
const val CHANGE_STEP_LARGE = 100


// Variables for how many dice are rolled
const val MAX_ALLOWED_ROLLED_DICE = 1000
const val MIN_ALLOWED_ROLLED_DICE_SIMPLE = 1
const val MIN_ALLOWED_ROLLED_DICE_AGGREGATE = 0

// Variables for how many sides on dice
const val MAX_DICE_SIDE_COUNT = 1000
const val MIN_DICE_SIDE_COUNT_SIMPLE = 1
const val MIN_DICE_SIDE_COUNT_CUSTOM = -1000

// Variables for how much to add to any given roll
const val MAX_MODIFIER = 1000
const val MIN_MODIFIER = -1000

class PageViewModel : ViewModel() {

    private fun enforceDieCount(numDice: Int) : Int
    {
        return when {
            numDice < MIN_ALLOWED_ROLLED_DICE_SIMPLE -> MIN_ALLOWED_ROLLED_DICE_SIMPLE
            numDice > MAX_ALLOWED_ROLLED_DICE -> MAX_ALLOWED_ROLLED_DICE
            else -> numDice
        }
    }

    private fun enforceDieCountMinZero(numDice: Int) : Int
    {
        return when {
            numDice < MIN_ALLOWED_ROLLED_DICE_AGGREGATE -> MIN_ALLOWED_ROLLED_DICE_AGGREGATE
            numDice > MAX_ALLOWED_ROLLED_DICE -> MAX_ALLOWED_ROLLED_DICE
            else -> numDice
        }
    }

    private fun enforceModifier(modifier: Int) : Int {
        return when {
            modifier > MAX_MODIFIER -> MAX_MODIFIER
            modifier < MIN_MODIFIER -> MIN_MODIFIER
            else -> modifier
        }
    }

    // Will return the input + stepSize, snapped to the next evenly divisible stepSize.
    // i.e (101, 100) -> 200, (101,-100) -> 100
    private fun snapToNextIncrement(valueIn: Int, stepSize: Int) : Int {
        if(stepSize == 0 )
        {
            return 0
        }

        val valueRem = valueIn.rem(stepSize)

        // If you are negative jumping up, or positive jumping down, just drop down/up the remainder.
        return if((valueRem > 0 && stepSize < 0) || (valueRem < 0 && stepSize > 0))
        {
            valueIn - valueRem
        }
        else
        {
            valueIn - valueRem + stepSize
        }
    }



    private var mContext : Context ?= null
    fun setContext(context: Context)
    {
        mContext = context
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

    fun setItemsInRowSimple(type : Int)
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

    private val _itemsInRowAggregate = MutableLiveData<Int>()

    fun setItemsInRowAggregate(type : Int)
    {
        _itemsInRowAggregate.value = type
    }

    fun getItemsInRowAggregate() : Int
    {
        if(_itemsInRowAggregate.value == null)
        {
            return 2
        }

        return _itemsInRowAggregate.value!!
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


    // How many dice will be rolled at a time.
    private val _numDice = MutableLiveData<Int>()
    val numDice: LiveData<Int> = Transformations.map(_numDice) {
        _numDice.value
    }

    fun setNumDiceExact(numDice: Int) {
        _numDice.value = enforceDieCount(numDice)
    }

    fun incrementNumDice() {
        _numDice.value = enforceDieCount(getNumDice() + CHANGE_STEP_SMALL)
    }

    fun decrementNumDice() {
        _numDice.value = enforceDieCount(getNumDice() - CHANGE_STEP_SMALL)
    }

    fun largeIncrementNumDice() {
        _numDice.value = enforceDieCount(snapToNextIncrement(getNumDice(), CHANGE_STEP_LARGE))
    }

    fun largeDecrementNumDice() {
        _numDice.value = enforceDieCount(snapToNextIncrement(getNumDice(), -CHANGE_STEP_LARGE))
    }

    // Need this so that we know what the value is even when it isn't broadcast.
    fun getNumDice() : Int
    {
        if(_numDice.value != null)
        {
            return _numDice.value!!
        }
        return 1
    }

    // What modifier will be added to the roll
    private val _modifier = MutableLiveData<Int>()
    val modifier: LiveData<Int> = Transformations.map(_modifier) {
        _modifier.value
    }

    fun setModifierExact(modifier: Int) {
        _modifier.value = enforceModifier(modifier)
    }

    fun incrementModifier() {
        _modifier.value = enforceModifier(getModifier() + CHANGE_STEP_SMALL)
    }

    fun decrementModifier() {
        _modifier.value = enforceModifier(getModifier() - CHANGE_STEP_SMALL)
    }

    fun largeIncrementModifier() {
        _modifier.value = enforceModifier(snapToNextIncrement(getModifier(), CHANGE_STEP_LARGE))
    }

    fun largeDecrementModifier() {
        _modifier.value = enforceModifier(snapToNextIncrement(getModifier(), -CHANGE_STEP_LARGE))
    }

    // Need this so that we know what the value is even when it isn't broadcast.
    fun getModifier() : Int
    {
        if(_modifier.value != null)
        {
            return _modifier.value!!
        }
        return 0
    }

    // What modifier will be added to the aggregate roll
    private val _aggregateModifier = MutableLiveData<Int>()
    val aggregateModifier: LiveData<Int> = Transformations.map(_aggregateModifier) {
        _aggregateModifier.value
    }

    fun setAggregateModifierExact(modifier: Int) {
        _aggregateModifier.value = enforceModifier(modifier)
    }

    fun incrementAggregateModifier() {
        _aggregateModifier.value = enforceModifier(getAggregateModifier() + CHANGE_STEP_SMALL)
    }

    fun decrementAggregateModifier() {
        _aggregateModifier.value = enforceModifier(getAggregateModifier() - CHANGE_STEP_SMALL)
    }

    fun largeIncrementAggregateModifier() {
        _aggregateModifier.value = enforceModifier(snapToNextIncrement(getAggregateModifier(), CHANGE_STEP_LARGE))
    }

    fun largeDecrementAggregateModifier() {
        _aggregateModifier.value = enforceModifier(snapToNextIncrement(getAggregateModifier(), -CHANGE_STEP_LARGE))
    }

    // Need this so that we know what the value is even when it isn't broadcast.
    fun getAggregateModifier() : Int
    {
        if(_aggregateModifier.value != null)
        {
            return _aggregateModifier.value!!
        }
        return 0
    }

    // All of the rolls that have been stored in the current session
    private val _singleRollHistory = MutableLiveData<HistoryStamp>()
    val singleRollHistory: LiveData<HistoryStamp> = Transformations.map(_singleRollHistory) {
        _singleRollHistory.value
    }

    private val _rollHistory = MutableLiveData<MutableList<HistoryStamp>>()

    fun addRollHistory(rollData: HistoryStamp)
    {
        _singleRollHistory.value = rollData
        if(_rollHistory.value == null)
        {
            _rollHistory.value = mutableListOf()
        }
        _rollHistory.value!!.add(0,rollData)
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

    private val _clearHistory = MutableLiveData<Boolean>()
    val clearHistory: LiveData<Boolean> = Transformations.map(_clearHistory) {
        _clearHistory.value
    }

    fun clearHistory()
    {
        _clearHistory.value = clearHistory.value?.not()
        _rollHistory.value?.clear()
    }

    val fateDie = CustomDie("Fate",-1,1)

    // Access for all of the dice that can be rolled
    private val diePoolArray = arrayOf(
        fateDie,
        SimpleDie(2),
        SimpleDie(3),
        SimpleDie(4),
        SimpleDie(6),
        SimpleDie(8),
        SimpleDie(10),
        SimpleDie(12),
        SimpleDie(20),
        SimpleDie(100)
    )

    fun getDefaultDiePoolString() : String
    {
        var dieString = ""

        for(die in diePoolArray)
        {
            dieString += die.getName() + ", "
        }

        return dieString.removeRange(dieString.length-2, dieString.length)
    }

    private val _diePool = MutableLiveData<Array<InnerDie>>()
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
            val dice = mutableListOf<InnerDie>()

            for (dieString in pool) {
                try {
                    dice.add(DieFactory().createUnknownInnerDie(dieString))
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

    fun addDieToPool(die: InnerDie) : Boolean
    {
        if(_diePool.value == null)
        {
            resetDiePool()
        }

        val newPool = _diePool.value!!.toMutableSet()

        val added = if(hasInnerDie(die))
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

    private fun hasInnerDie(die: InnerDie) : Boolean
    {
        if(_diePool.value == null)
        {
            resetDiePool()
        }

        for(savedDie in _diePool.value!!)
        {
            if(savedDie.getName() == die.getName())
            {
                return true
            }
        }

        return false
    }

    fun removeDieFromPool(die: InnerDie) : Boolean
    {
        if(_diePool.value == null)
        {
            resetDiePool()
        }

        val newPool = _diePool.value!!.toMutableSet()
        val removed = newPool.remove(die)

        _diePool.value = newPool.toTypedArray()

        if(_aggregateDiePool.value != null)
        {
            _aggregateDiePool.value!!.remove(die.saveToString())
        }

        return removed
    }

    fun resetDiePool()
    {
        _diePool.value = diePoolArray
        _aggregateDiePool.value = mutableMapOf()
    }

    fun getInnerDiceSize() : Int
    {
        if(_diePool.value != null) {
            return _diePool.value!!.size
        }
        return 0
    }

    fun getInnerDie(position: Int) : InnerDie
    {
        if(_diePool.value == null || _diePool.value!!.size <= position || position < 0 ) {
            return SimpleDie(1)
        }

        return _diePool.value!![position]
    }

    private val _aggregateDiePool = MutableLiveData<MutableMap<String,Int>>()

    private fun ensureAggregateDiePoolExists() {
        if(_aggregateDiePool.value == null)
        {
            _aggregateDiePool.value = mutableMapOf()
        }
    }

    fun createAggregateRollFromCustomRollerState(rollName : String) : AggregateRoll
    {
        ensureAggregateDiePoolExists()

        val newAggregateRoll = AggregateRoll(rollName, getAggregateModifier())

        for(innerDiePos in 0 until getInnerDiceSize())
        {
            val baseDie = getInnerDie(innerDiePos)

            var baseDieCount = 0

            if(_aggregateDiePool.value!!.containsKey(baseDie.saveToString()))
            {
                baseDieCount = _aggregateDiePool.value!!.getValue(baseDie.saveToString())
            }

            if(baseDieCount > 0)
            {
                newAggregateRoll.addDieToRoll(baseDie, baseDieCount)
            }
        }

        return newAggregateRoll
    }

    fun getAggregateDieCount(simpleDiePosition: Int) : Int
    {
        ensureAggregateDiePoolExists()
        return if(_aggregateDiePool.value!!.containsKey(getInnerDie(simpleDiePosition).saveToString()))
        {
            _aggregateDiePool.value!!.getValue(getInnerDie(simpleDiePosition).saveToString())
        }
        else
        {
            0
        }
    }

    fun setAggregateDieCountExact(simpleDiePosition: Int, count: Int)
    {
        ensureAggregateDiePoolExists()
        _aggregateDiePool.value!![getInnerDie(simpleDiePosition).saveToString()] =
            enforceDieCountMinZero(count)
    }

    fun incrementAggregateDieCount(simpleDiePosition: Int) {
        ensureAggregateDiePoolExists()
        _aggregateDiePool.value!![getInnerDie(simpleDiePosition).saveToString()] =
            enforceDieCountMinZero(getAggregateDieCount(simpleDiePosition) + CHANGE_STEP_SMALL)
    }

    fun decrementAggregateDieCount(simpleDiePosition: Int) {
        ensureAggregateDiePoolExists()
        _aggregateDiePool.value!![getInnerDie(simpleDiePosition).saveToString()] =
            enforceDieCountMinZero(getAggregateDieCount(simpleDiePosition) - CHANGE_STEP_SMALL)
    }

    fun largeIncrementAggregateDieCount(simpleDiePosition: Int) {
        ensureAggregateDiePoolExists()
        _aggregateDiePool.value!![getInnerDie(simpleDiePosition).saveToString()] =
            enforceDieCountMinZero(snapToNextIncrement(getAggregateDieCount(simpleDiePosition), CHANGE_STEP_LARGE))
    }

    fun largeDecrementAggregateDieCount(simpleDiePosition: Int) {
        ensureAggregateDiePoolExists()
        _aggregateDiePool.value!![getInnerDie(simpleDiePosition).saveToString()] =
            enforceDieCountMinZero(snapToNextIncrement(getAggregateDieCount(simpleDiePosition), -CHANGE_STEP_LARGE))
    }

    private val _savedRollPool = MutableLiveData<Array<AggregateRoll>>()
    val savedRollPool: LiveData<Set<String>> = Transformations.map(_savedRollPool) {rollArray ->
        rollArray.sortBy {it.getName()}
        val rollSet = mutableSetOf<String>()
        for (roll in rollArray) {
            rollSet.add(roll.saveToString())
        }
        rollSet
    }

    fun initSavedRollPoolFromStrings(pool : Set<String>?)
    {
        if(pool != null) {
            val rolls = mutableListOf<AggregateRoll>()

            for (rollString in pool) {
                try {
                    rolls.add(DieFactory().createAggregateRoll(rollString))
                } catch (error: DieLoadError) {
                    // Throw away that roll.
                }
            }

            val rollArray = rolls.toTypedArray()
            rollArray.sortBy{it.getName()}

            _savedRollPool.value = rollArray
        }
        else
        {
            _savedRollPool.value = arrayOf()
        }
    }

    fun addSavedRollToPool(roll: AggregateRoll) : Boolean
    {
        if(_savedRollPool.value == null)
        {
            resetSavedRollPool()
        }

        val newPool = _savedRollPool.value!!.toMutableSet()

        val added = if(hasSavedRoll(roll))
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

    private fun hasSavedRoll(roll: AggregateRoll) : Boolean
    {
        if(_savedRollPool.value == null)
        {
            resetSavedRollPool()
        }

        for(savedRoll in _savedRollPool.value!!)
        {
            if(savedRoll.getName() == roll.getName())
            {
                return true
            }
        }

        return false
    }

    fun removeSavedRollFromPool(roll: AggregateRoll) : Boolean
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

    fun getSavedRoll(position: Int) : AggregateRoll
    {
        if(_savedRollPool.value == null || _savedRollPool.value!!.size <= position || position < 0 ) {
            return AggregateRoll("INVALID", 0)
        }

        return _savedRollPool.value!![position]
    }

}