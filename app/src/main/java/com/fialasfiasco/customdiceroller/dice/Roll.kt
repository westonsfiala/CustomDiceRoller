package com.fialasfiasco.customdiceroller.dice

import com.fialasfiasco.customdiceroller.R
import com.fialasfiasco.customdiceroller.helper.getDropDiceString
import com.fialasfiasco.customdiceroller.helper.getModifierString
import kotlin.math.abs

const val aggregateRollStringStart = "Aggregate"
const val rollNaturalValue = 0
const val rollAdvantageValue = 1
const val rollDisadvantageValue = -1

/**
 * Class used to hold all the properties that a die that is added to a roll could use
 * mDieCount - How many die should be rolled, can be any natural number.
 * mModifier - What value to add to each roll, can be any natural number
 * mAdvantageDisadvantage - If the roll should have advantage or disadvantage,
 * negative = disadvantage; 0 = natural; positive = advantage
 * mDropHighLow - How many die should be dropped from the roll.
 * negative = drop X high; 0 = natural; positive = drop X low
 */
data class RollProperties(var mDieCount: Int,
                          var mModifier: Int,
                          var mAdvantageDisadvantage: Int,
                          var mDropHighLow: Int)
{
    constructor() : this(0,0,0,0)
}

// Contains up to 4 groups of rolls, high valid, high dropped, low valid, low dropped
// High valid will always be filled out and is the actual values in the results
// High dropped are values that were dropped because of dropping the highest/lowest values
// The corresponding low groups are filled out when advantage or disadvantage are in play.
class RollResults {
    val mRollResults = mutableMapOf<String, MutableList<Int>>()
    val mDroppedRolls = mutableMapOf<String, MutableList<Int>>()

    val mStruckRollResults = mutableMapOf<String, MutableList<Int>>()
    val mDroppedStruckRolls = mutableMapOf<String, MutableList<Int>>()

    fun sortDescending() {
        sortMapList(mRollResults)
        sortMapList(mDroppedRolls)
        sortMapList(mStruckRollResults)
        sortMapList(mDroppedStruckRolls)

        reverseMapList(mRollResults)
        reverseMapList(mDroppedRolls)
        reverseMapList(mStruckRollResults)
        reverseMapList(mDroppedStruckRolls)
    }

    fun sortAscending() {
        sortMapList(mRollResults)
        sortMapList(mDroppedRolls)
        sortMapList(mStruckRollResults)
        sortMapList(mDroppedStruckRolls)
    }

    private fun sortMapList(rollMap : MutableMap<String, MutableList<Int>>) {
        for(rollList in rollMap)
        {
            rollList.value.sort()
        }
    }

    private fun reverseMapList(rollMap : MutableMap<String, MutableList<Int>>) {
        for(rollList in rollMap)
        {
            rollList.value.reverse()
        }
    }
}

class Roll(private val mRollName: String, val mModifier: Int)
{
    private val mDieMap = mutableMapOf<Die, RollProperties>()

    fun addDieToRoll(die: Die, properties: RollProperties)
    {
        mDieMap[die] = properties
    }

    fun getTotalDiceInRoll() : Int
    {
        var numDice = 0

        for(diePair in mDieMap)
        {
            numDice += diePair.value.mDieCount
        }

        return numDice
    }

    fun saveToString(): String
    {
        // Aggregate
        var saveString = String.format("%s", aggregateRollStringStart)
        // (Splitter)Name
        saveString += String.format("%s%s", saveSplitStrings[rollSplitStringIndex], mRollName)
        // (Splitter)Modifier
        saveString += String.format("%s%d", saveSplitStrings[rollSplitStringIndex], mModifier)

        // (Repeat)
        for(roll in mDieMap)
        {
            // (Splitter)DieString
            saveString += String.format("%s%s", saveSplitStrings[rollSplitStringIndex], roll.key.saveToString())
            // (Splitter)Count
            saveString += String.format("%s%d", saveSplitStrings[rollSplitStringIndex], roll.value.mDieCount)
            // (Splitter)Modifier
            saveString += String.format("%s%d", saveSplitStrings[rollPropertiesSplitStringIndex], roll.value.mModifier)
            // (Splitter)AdvantageDisadvantage
            saveString += String.format("%s%d", saveSplitStrings[rollPropertiesSplitStringIndex], roll.value.mAdvantageDisadvantage)
            // (Splitter)DropHighLow
            saveString += String.format("%s%d", saveSplitStrings[rollPropertiesSplitStringIndex], roll.value.mDropHighLow)
        }

        return saveString
    }

    fun getDice() : Map<Die, RollProperties>
    {
        return mDieMap
    }

    fun roll() : RollResults
    {
        val returnResults = RollResults()

        for(diePair in mDieMap) {

            val die = diePair.key
            val dieName = die.getDisplayName()
            val properties = diePair.value

            val rollPair = produceRollLists(die, properties)

            when
            {
                properties.mAdvantageDisadvantage == rollDisadvantageValue -> {
                    val secondRollPair = produceRollLists(die, properties)
                    if(rollPair.first.sum() < secondRollPair.first.sum()) {
                        returnResults.mRollResults[dieName] = rollPair.first
                        returnResults.mDroppedRolls[dieName] = rollPair.second
                        returnResults.mStruckRollResults[dieName] = secondRollPair.first
                        returnResults.mDroppedStruckRolls[dieName] = secondRollPair.second
                    } else {
                        returnResults.mRollResults[dieName] = secondRollPair.first
                        returnResults.mDroppedRolls[dieName] = secondRollPair.second
                        returnResults.mStruckRollResults[dieName] = rollPair.first
                        returnResults.mDroppedStruckRolls[dieName] = rollPair.second
                    }
                }
                properties.mAdvantageDisadvantage == rollNaturalValue -> {
                    returnResults.mRollResults[dieName] = rollPair.first
                    returnResults.mDroppedRolls[dieName] = rollPair.second
                    returnResults.mStruckRollResults[dieName] = mutableListOf()
                    returnResults.mDroppedStruckRolls[dieName] = mutableListOf()
                }
                properties.mAdvantageDisadvantage == rollAdvantageValue -> {
                    val secondRollPair = produceRollLists(die, properties)
                    if(rollPair.first.sum() > secondRollPair.first.sum()) {
                        returnResults.mRollResults[dieName] = rollPair.first
                        returnResults.mDroppedRolls[dieName] = rollPair.second
                        returnResults.mStruckRollResults[dieName] = secondRollPair.first
                        returnResults.mDroppedStruckRolls[dieName] = secondRollPair.second
                    } else {
                        returnResults.mRollResults[dieName] = secondRollPair.first
                        returnResults.mDroppedRolls[dieName] = secondRollPair.second
                        returnResults.mStruckRollResults[dieName] = rollPair.first
                        returnResults.mDroppedStruckRolls[dieName] = rollPair.second
                    }
                }
            }
        }
        return returnResults
    }

    // Produces a pair of lists, a list of taken rolls, and a list of dropped rolls.
    private fun produceRollLists(die: Die, properties: RollProperties) : Pair<MutableList<Int>,MutableList<Int>> {

        if(properties.mDieCount == 0)
        {
            return Pair(mutableListOf(), mutableListOf())
        }

        val returnList = mutableListOf<Int>()
        val dropList = mutableListOf<Int>()

        for (rollNum in 0 until properties.mDieCount) {
            returnList.add(die.roll())
        }

        val retPair = when {
            properties.mDropHighLow == 0 -> {Pair(returnList, dropList)}
            abs(properties.mDieCount) <= abs(properties.mDropHighLow) -> {Pair(dropList, returnList)}
            else -> {
                for(dropIndex in 0 until abs(properties.mDropHighLow)) {
                    val ejectedValue = if (properties.mDropHighLow < 0) {
                        returnList.max()
                    } else {
                        returnList.min()
                    }
                    returnList.remove(ejectedValue!!)
                    dropList.add(ejectedValue)
                }
                Pair(returnList, dropList)
            }
        }

        if(properties.mModifier != 0) {
            retPair.first.add(properties.mModifier)
        }

        return retPair
    }

    fun average() : Float
    {
        var dieAverage = mModifier.toFloat()
        val innerDies = mDieMap
        for(diePropertyPair in innerDies)
        {
            dieAverage += diePropertyPair.key.average() * diePropertyPair.value.mDieCount + diePropertyPair.value.mModifier
        }
        return dieAverage
    }

    fun displayInHex(): Boolean {
        // Only display hex when you start with "0x" and have more characters after that.
        return if(mRollName.isNotEmpty()) {
            mRollName.length > (dieDisplayInHexID.length) && mRollName.startsWith(
                dieDisplayInHexID
            )
        }
        else
        {
            var displayInHex = true
            val innerDies = mDieMap
            for(dieCountPair in innerDies)
            {
                if(!dieCountPair.key.displayInHex())
                {
                    displayInHex = false
                }
            }
            displayInHex
        }
    }

    fun getDisplayName() : String
    {
        return mRollName
    }

    fun getDetailedRollName() : String
    {
        val innerDies = mDieMap

        var returnString = ""

        if(innerDies.isEmpty())
        {
            return returnString
        }

        for(diePropertyPair in innerDies)
        {
            returnString += if(diePropertyPair.key.getDisplayName().startsWith("d"))
            {
                String.format("%d%s",diePropertyPair.value.mDieCount,diePropertyPair.key.getDisplayName())
            }
            else
            {
                String.format("%dx%s", diePropertyPair.value.mDieCount,diePropertyPair.key.getDisplayName())
            }

            returnString += when(diePropertyPair.value.mAdvantageDisadvantage) {
                rollAdvantageValue -> "(Advantage)"
                rollDisadvantageValue -> "(Disadvantage)"
                else -> ""
            }

            returnString += if(diePropertyPair.value.mDropHighLow != 0) {
                val dropString = getDropDiceString(diePropertyPair.value.mDropHighLow)
                "($dropString)"
            } else {
                ""
            }

            returnString += if(diePropertyPair.value.mModifier != 0) {
                getModifierString(diePropertyPair.value.mModifier)
            } else {
                ""
            }

            returnString += "+"
        }

        returnString = returnString.removeRange(returnString.length - 1, returnString.length)

        if(mModifier != 0) {
            returnString += getModifierString(mModifier)
        }

        return returnString
    }

    fun getInfo(): String {
        var returnString = String.format("Rolls %s",getDetailedRollName())

        returnString += if(displayInHex())
        {
            String.format("\nAverage of 0x%x", average().toInt())
        }
        else
        {
            String.format("\nAverage of %d", average().toInt())
        }

        return returnString
    }

    fun getImageID(): Int {
        return R.drawable.ic_cubes
    }
}