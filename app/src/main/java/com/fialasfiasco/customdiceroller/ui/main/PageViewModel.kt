package com.fialasfiasco.customdiceroller.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel

import com.fialasfiasco.customdiceroller.ui.main.HistoryStamp

class PageViewModel : ViewModel() {

    // Temp one for the placeholder
    private val _index = MutableLiveData<Int>()
    val text: LiveData<String> = Transformations.map(_index) {
        "Hello world from section: $it"
    }
    fun setIndex(index: Int) {
        _index.value = index
    }

    // Save data for when the screen rotates.
    private val _numDice = MutableLiveData<Int>()
    val numDice: LiveData<Int> = Transformations.map(_numDice) {
        _numDice.value
    }
    fun setNumDice(numDice: Int) {
        _numDice.value = numDice
    }

    fun getNumDice() : Int
    {
        if(_numDice.value != null)
        {
            return _numDice.value!!
        }
        return 1
    }

    private val _modifier = MutableLiveData<Int>()
    val modifier: LiveData<Int> = Transformations.map(_modifier) {
        _modifier.value
    }
    fun setModifier(modifier: Int) {
        _modifier.value = modifier
    }

    fun getModifier() : Int
    {
        if(_modifier.value != null)
        {
            return _modifier.value!!
        }
        return 0
    }

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
        if(_rollHistory.value == null || _rollHistory.value!!.size <= position)
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

}