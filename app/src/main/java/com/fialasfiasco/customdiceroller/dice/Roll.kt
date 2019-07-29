package com.fialasfiasco.customdiceroller.dice

import com.fialasfiasco.customdiceroller.R
import com.fialasfiasco.customdiceroller.helper.getModifierString

const val aggregateRollStringStart = "Aggregate"
const val aggregateRollSplitString = ";"

/**
 * Class used to hold all the properties that a die that is added to a roll could use
 * mDieCount - How many die should be rolled, can be any natural number.
 * mModifier - What value to add to each roll, can be any natural number
 * mAdvantageDisadvantage - If the roll should have advantage or disadvantage,
 * negative = disadvantage; 0 = natural; positive = advantage
 * mDropHighLow - How many die should be dropped from the roll.
 * negative = drop X low; 0 = natural; positive = drop X high
 */
data class RollProperties(val mDieCount : Int,
                          val mModifier: Int,
                          val mAdvantageDisadvantage : Int,
                          val mDropHighLow : Int)

// Contains up to 4 groups of rolls, high valid, high dropped, low valid, low dropped
// High valid will always be filled out and is the actual values in the results
// High dropped are values that were dropped because of dropping the highest/lowest values
// The corresponding low groups are filled out when advantage or disadvantage are in play.
class RollResults {
    val mHighRollResults = mutableMapOf<String, MutableList<Int>>()
    val mHighDroppedRolls = mutableMapOf<String, MutableList<Int>>()

    val mLowRollResults = mutableMapOf<String, MutableList<Int>>()
    val mLowDroppedRolls = mutableMapOf<String, MutableList<Int>>()

    fun sortDescending() {
        sortMapList(mHighRollResults)
        sortMapList(mHighDroppedRolls)
        sortMapList(mLowRollResults)
        sortMapList(mLowDroppedRolls)

        reverseMapList(mHighRollResults)
        reverseMapList(mHighDroppedRolls)
        reverseMapList(mLowRollResults)
        reverseMapList(mLowDroppedRolls)
    }

    fun sortAscending() {
        sortMapList(mHighRollResults)
        sortMapList(mHighDroppedRolls)
        sortMapList(mLowRollResults)
        sortMapList(mLowDroppedRolls)
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
        // Aggregate;Name;Modifier;DieString;DieCount(Repeat)
        var saveString = String.format("%s%s%s%s%d",
            aggregateRollStringStart,
            aggregateRollSplitString, mRollName,
            aggregateRollSplitString, mModifier)

        for(roll in mDieMap)
        {
            saveString += String.format("%s%s%s%d",
                aggregateRollSplitString, roll.key.saveToString(),
                aggregateRollSplitString, roll.value.mDieCount)
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
                properties.mAdvantageDisadvantage < 0 -> {
                    val secondRollPair = produceRollLists(die, properties)
                    if(rollPair.first.sum() < secondRollPair.first.sum()) {
                        returnResults.mHighRollResults["$dieName(disadvantage)"] = rollPair.first
                        returnResults.mHighDroppedRolls["$dieName(disadvantage dropped)"] = rollPair.second
                        returnResults.mLowRollResults[dieName] = secondRollPair.first
                        returnResults.mLowDroppedRolls["$dieName(dropped)"] = secondRollPair.second
                    } else {
                        returnResults.mHighRollResults["$dieName(disadvantage)"] = secondRollPair.first
                        returnResults.mHighDroppedRolls["$dieName(disadvantage dropped)"] = secondRollPair.second
                        returnResults.mLowRollResults[dieName] = rollPair.first
                        returnResults.mLowDroppedRolls["$dieName(dropped)"] = rollPair.second
                    }
                }
                properties.mAdvantageDisadvantage == 0 -> {
                    returnResults.mHighRollResults[dieName] = rollPair.first
                    returnResults.mHighDroppedRolls["$dieName(dropped)"] = rollPair.second
                }
                properties.mAdvantageDisadvantage > 0 -> {
                    val secondRollPair = produceRollLists(die, properties)
                    if(rollPair.first.sum() > secondRollPair.first.sum()) {
                        returnResults.mHighRollResults["$dieName(advantage)"] = rollPair.first
                        returnResults.mHighDroppedRolls["$dieName(advantage dropped)"] = rollPair.second
                        returnResults.mLowRollResults[dieName] = secondRollPair.first
                        returnResults.mLowDroppedRolls["$dieName(dropped)"] = secondRollPair.second
                    } else {
                        returnResults.mHighRollResults["$dieName(advantage)"] = secondRollPair.first
                        returnResults.mHighDroppedRolls["$dieName(advantage dropped)"] = secondRollPair.second
                        returnResults.mLowRollResults[dieName] = rollPair.first
                        returnResults.mLowDroppedRolls["$dieName(dropped)"] = rollPair.second
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

        return when {
            properties.mDropHighLow == 0 -> {Pair(returnList, dropList)}
            Math.abs(properties.mDieCount) <= Math.abs(properties.mDropHighLow) -> {Pair(dropList, returnList)}
            else -> {
                for(dropIndex in 0 until Math.abs(properties.mDropHighLow)) {
                    val ejectedValue = if (properties.mDropHighLow > 0) {
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
    }

    fun average() : Float
    {
        var dieAverage = mModifier.toFloat()
        val innerDies = mDieMap
        for(dieCountPair in innerDies)
        {
            dieAverage += dieCountPair.key.average() * dieCountPair.value.mDieCount
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
                String.format("%d%s+",diePropertyPair.value.mDieCount,diePropertyPair.key.getDisplayName())
            }
            else
            {
                String.format("%dx%s+", diePropertyPair.value.mDieCount,diePropertyPair.key.getDisplayName())
            }
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