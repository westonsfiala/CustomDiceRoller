package com.fialasfiasco.customdiceroller.data

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel

import com.fialasfiasco.customdiceroller.history.HistoryStamp

const val MAX_DICE = 100
const val MIN_DICE = 1
const val MAX_DICE_NUM = 100
const val MIN_DICE_NUM_POSITIVE = 1
const val MIN_DICE_NUM_NEGATIVE = -100
const val MAX_MODIFIER = 100
const val START_MODIFIER = 0
const val MIN_MODIFIER = -100

class PageViewModel : ViewModel() {

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

    private fun enforceDieCount(numDice: Int) : Int
    {
        return when {
            numDice < MIN_DICE -> MIN_DICE
            numDice > MAX_DICE -> MAX_DICE
            else -> numDice
        }
    }

    fun setNumDice(numDice: Int) {
        val newNumDice = enforceDieCount(numDice)
        _numDice.value = newNumDice
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
    fun setModifier(modifier: Int) {
        val newModifier = when {
            modifier > MAX_MODIFIER -> MAX_MODIFIER
            modifier < MIN_MODIFIER -> MIN_MODIFIER
            else -> modifier
        }
        _modifier.value = newModifier
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

    fun setAggregateModifier(modifier: Int) {
        val newModifier = when {
            modifier > MAX_MODIFIER -> MAX_MODIFIER
            modifier < MIN_MODIFIER -> MIN_MODIFIER
            else -> modifier
        }
        _aggregateModifier.value = newModifier
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

    fun removeDieFromPool(die: Die) : Boolean
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

    fun getAggregateDie(position: Int) : AggregateDie
    {
        if(_aggregateDiePool.value == null)
        {
            _aggregateDiePool.value = mutableMapOf()
        }

        // Might not need this one.
        if(_diePool.value == null || _diePool.value!!.size <= position || position < 0) {
            return AggregateDie(SimpleDie(1), 1)
        }

        val baseDie = getInnerDie(position)

        var baseDieCount = 0

        if(_aggregateDiePool.value!!.containsKey(baseDie.saveToString()))
        {
            baseDieCount = _aggregateDiePool.value!!.getValue(baseDie.saveToString())
        }

        return AggregateDie(baseDie, baseDieCount)
    }

    private fun enforceDieCountMinZero(numDice: Int) : Int
    {
        return when {
            numDice < 0 -> 0
            numDice > MAX_DICE -> MAX_DICE
            else -> numDice
        }
    }

    fun setAggregateDieCount(die: Die, count: Int)
    {
        if(_aggregateDiePool.value == null)
        {
            _aggregateDiePool.value = mutableMapOf()
        }

        _aggregateDiePool.value!![die.saveToString()] = enforceDieCountMinZero(count)
    }

}