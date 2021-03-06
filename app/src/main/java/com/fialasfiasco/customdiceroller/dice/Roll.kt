package com.fialasfiasco.customdiceroller.dice

import com.fialasfiasco.customdiceroller.helper.*
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
 * mDropHigh - How many die should be dropped from the highest values of the roll
 * mDropLow - How many die should be dropped from the lowest values of the roll
 * mReRollUnder - If the roll us under this value, it will be rerolled once
 * mMinimumRoll - If the roll us under this value, treat it as this value
 * mExplode - If not zero, when the maximum value of a die is rolled, roll an extra die. Repeating.
 */
data class RollProperties(var mDieCount: Int,
                          var mModifier: Int,
                          var mAdvantageDisadvantage: Int,
                          var mDropHigh: Int,
                          var mDropLow: Int,
                          var mKeepHigh: Int,
                          var mKeepLow: Int,
                          var mUseReRoll: Boolean,
                          var mReRoll: Int,
                          var mUseMinimumRoll: Boolean,
                          var mMinimumRoll: Int,
                          var mExplode: Boolean)
{
    constructor() : this(1,
        0,
        0,
        0,
        0,
        0,
        0,
        false,
        0,
        false,
        0,
        false)
}

// Contains up to 4 groups of rolls, high valid, high dropped, low valid, low dropped
// High valid will always be filled out and is the actual values in the results
// High dropped are values that were dropped because of dropping the highest/lowest values
// The corresponding low groups are filled out when advantage or disadvantage are in play.
class RollResults {
    val mRollResults = mutableMapOf<String, MutableList<Int>>()
    val mDroppedRolls = mutableMapOf<String, MutableList<Int>>()
    val mReRolledRolls = mutableMapOf<String, MutableList<Int>>()

    val mStruckRollResults = mutableMapOf<String, MutableList<Int>>()
    val mStruckDroppedRolls = mutableMapOf<String, MutableList<Int>>()
    val mStruckReRolledRolls = mutableMapOf<String, MutableList<Int>>()

    val mRollModifiers = mutableMapOf<String, Int>()

    var mRollMaximumValue = false
    var mRollMinimumValue = false

    fun sortDescending() {
        sortMapList(mRollResults)
        sortMapList(mDroppedRolls)
        sortMapList(mStruckRollResults)
        sortMapList(mStruckDroppedRolls)

        reverseMapList(mRollResults)
        reverseMapList(mDroppedRolls)
        reverseMapList(mStruckRollResults)
        reverseMapList(mStruckDroppedRolls)
    }

    fun sortAscending() {
        sortMapList(mRollResults)
        sortMapList(mDroppedRolls)
        sortMapList(mStruckRollResults)
        sortMapList(mStruckDroppedRolls)
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

class Roll(private val mRollName: String, private val mRollCategory: String)
{
    private var mDieMap = mutableMapOf<String, RollProperties>()

    fun clone(newRollName: String, newRollCategory: String) : Roll
    {
        val retRoll = Roll(newRollName, newRollCategory)

        for(diePair in getDice())
        {
            if(diePair.value.mDieCount != 0) {
                retRoll.addDieToRoll(diePair.key, diePair.value)
            }
        }

        return retRoll
    }

    fun addDieToRoll(die: Die, properties: RollProperties)
    {
        val newDieProperties = RollProperties()
        newDieProperties.mDieCount = properties.mDieCount
        newDieProperties.mModifier = properties.mModifier
        newDieProperties.mAdvantageDisadvantage = properties.mAdvantageDisadvantage
        newDieProperties.mDropHigh = properties.mDropHigh
        newDieProperties.mDropLow = properties.mDropLow
        newDieProperties.mKeepHigh = properties.mKeepHigh
        newDieProperties.mKeepLow = properties.mKeepLow
        newDieProperties.mUseReRoll = properties.mUseReRoll
        newDieProperties.mReRoll = properties.mReRoll
        newDieProperties.mUseMinimumRoll = properties.mUseMinimumRoll
        newDieProperties.mMinimumRoll = properties.mMinimumRoll
        newDieProperties.mExplode = properties.mExplode

        mDieMap[die.saveToString()] = newDieProperties
    }

    fun removeDieFromRoll(die: Die) : Boolean
    {
        return mDieMap.remove(die.saveToString()) != null
    }

    fun containsDie(die: Die) : Boolean
    {
        return mDieMap.containsKey(die.saveToString())
    }

    fun getTotalDiceInRoll() : Int
    {
        var numDice = 0

        for(diePair in mDieMap)
        {
            numDice += abs(diePair.value.mDieCount)
        }

        return numDice
    }

    fun saveToString(): String
    {
        // Aggregate
        var saveString = String.format("%s", aggregateRollStringStart)
        // (Splitter)Name
        saveString += String.format("%s%s", saveSplitStrings[rollSplitStringIndex], mRollName)
        // (Splitter)Category
        saveString += String.format("%s%s", saveSplitStrings[rollCategorySplitStringIndex], mRollCategory)

        // (Repeat)
        for(roll in mDieMap)
        {
            // (Splitter)DieString
            saveString += String.format("%s%s", saveSplitStrings[rollSplitStringIndex], roll.key)
            // (Splitter)Count
            saveString += String.format("%s%d", saveSplitStrings[rollSplitStringIndex], roll.value.mDieCount)
            // (Splitter)Modifier
            saveString += String.format("%s%d", saveSplitStrings[rollPropertiesSplitStringIndex], roll.value.mModifier)
            // (Splitter)AdvantageDisadvantage
            saveString += String.format("%s%d", saveSplitStrings[rollPropertiesSplitStringIndex], roll.value.mAdvantageDisadvantage)
            // (Splitter)DropHigh
            saveString += String.format("%s%d", saveSplitStrings[rollPropertiesSplitStringIndex], roll.value.mDropHigh)
            // (Splitter)DropLow
            saveString += String.format("%s%d", saveSplitStrings[rollPropertiesSplitStringIndex], roll.value.mDropLow)
            // (Splitter)DropHigh
            saveString += String.format("%s%d", saveSplitStrings[rollPropertiesSplitStringIndex], roll.value.mKeepHigh)
            // (Splitter)DropLow
            saveString += String.format("%s%d", saveSplitStrings[rollPropertiesSplitStringIndex], roll.value.mKeepLow)
            // (Splitter)UseReRoll
            saveString += String.format("%s%b", saveSplitStrings[rollPropertiesSplitStringIndex], roll.value.mUseReRoll)
            // (Splitter)ReRollUnder
            saveString += String.format("%s%d", saveSplitStrings[rollPropertiesSplitStringIndex], roll.value.mReRoll)
            // (Splitter)UseMinimumRoll
            saveString += String.format("%s%b", saveSplitStrings[rollPropertiesSplitStringIndex], roll.value.mUseMinimumRoll)
            // (Splitter)MinimumRoll
            saveString += String.format("%s%d", saveSplitStrings[rollPropertiesSplitStringIndex], roll.value.mMinimumRoll)
            // (Splitter)Explode
            saveString += String.format("%s%b", saveSplitStrings[rollPropertiesSplitStringIndex], roll.value.mExplode)
        }

        return saveString
    }

    fun getDice() : Map<Die, RollProperties>
    {
        val outputMap = mutableMapOf<Die, RollProperties>()

        for(diePair in mDieMap)
        {
            outputMap[DieFactory().createUnknownDie(diePair.key)] = diePair.value
        }

        return outputMap
    }

    fun overrideDieAt(die: Die, position: Int) : Boolean {
        val dieList = mDieMap.toList().toMutableList()

        val possibleDie = dieList.elementAtOrNull(position)

        return if(possibleDie != null) {
            dieList[position] = Pair(die.saveToString(),possibleDie.second)
            mDieMap = dieList.toMap().toMutableMap()
            true
        } else {
            false
        }
    }

    fun getDieAt(position: Int) : Die
    {
        val possibleDie = mDieMap.toList().elementAtOrNull(position)

        return if(possibleDie != null)
        {
            DieFactory().createUnknownDie(possibleDie.first)
        } else {
            MinMaxDie("INVALID", 0,0)
        }
    }

    fun moveDieUp(position: Int) : Boolean
    {
        // Can't move something up when its already at the top
        // Or if there is nothing
        // Or if there is only one thing
        // Or if its past where we can access
        if(position <= 0 || mDieMap.isEmpty() || mDieMap.size == 1 || position >= mDieMap.size)
        {
            return false
        }

        val dieList = mDieMap.toList().toMutableList()

        val displacedDie = dieList.removeAt(position)
        dieList.add(position - 1, displacedDie)

        mDieMap = dieList.toMap().toMutableMap()

        return true
    }

    fun moveDieDown(position: Int) : Boolean
    {
        // Can't move something down when its already at the top
        // Or if there is nothing
        // Or if there is only one thing
        // Or if its past where we can access
        if(position < 0 || mDieMap.isEmpty() || mDieMap.size == 1 || position >= mDieMap.size - 1)
        {
            return false
        }

        val dieList = mDieMap.toList().toMutableList()

        val displacedDie = dieList.removeAt(position)
        dieList.add(position + 1, displacedDie)

        mDieMap = dieList.toMap().toMutableMap()

        return true
    }


    fun getRollPropertiesAt(position: Int) : RollProperties
    {
        val possibleDie = mDieMap.toList().elementAtOrNull(position)

        return possibleDie?.second ?: RollProperties()
    }

    fun roll() : RollResults
    {
        val returnResults = RollResults()

        for(diePair in mDieMap) {

            val die = DieFactory().createUnknownDie(diePair.key)
            val dieSaveString = die.saveToString()
            val properties = diePair.value

            val rollPair = produceRollLists(die, properties)

            returnResults.mRollModifiers[dieSaveString] = properties.mModifier

            when
            {
                properties.mAdvantageDisadvantage == rollDisadvantageValue -> {
                    val secondRollPair = produceRollLists(die, properties)
                    if(rollPair.first.sum() < secondRollPair.first.sum()) {
                        returnResults.mRollResults[dieSaveString] = rollPair.first
                        returnResults.mDroppedRolls[dieSaveString] = rollPair.second
                        returnResults.mReRolledRolls[dieSaveString] = rollPair.third
                        returnResults.mStruckRollResults[dieSaveString] = secondRollPair.first
                        returnResults.mStruckDroppedRolls[dieSaveString] = secondRollPair.second
                        returnResults.mStruckReRolledRolls[dieSaveString] = secondRollPair.third
                    } else {
                        returnResults.mRollResults[dieSaveString] = secondRollPair.first
                        returnResults.mDroppedRolls[dieSaveString] = secondRollPair.second
                        returnResults.mReRolledRolls[dieSaveString] = secondRollPair.third
                        returnResults.mStruckRollResults[dieSaveString] = rollPair.first
                        returnResults.mStruckDroppedRolls[dieSaveString] = rollPair.second
                        returnResults.mStruckReRolledRolls[dieSaveString] = rollPair.third
                    }
                }
                properties.mAdvantageDisadvantage == rollNaturalValue -> {
                    returnResults.mRollResults[dieSaveString] = rollPair.first
                    returnResults.mDroppedRolls[dieSaveString] = rollPair.second
                    returnResults.mReRolledRolls[dieSaveString] = rollPair.third
                    returnResults.mStruckRollResults[dieSaveString] = mutableListOf()
                    returnResults.mStruckDroppedRolls[dieSaveString] = mutableListOf()
                    returnResults.mStruckReRolledRolls[dieSaveString] = mutableListOf()
                }
                properties.mAdvantageDisadvantage == rollAdvantageValue -> {
                    val secondRollPair = produceRollLists(die, properties)
                    if(rollPair.first.sum() > secondRollPair.first.sum()) {
                        returnResults.mRollResults[dieSaveString] = rollPair.first
                        returnResults.mDroppedRolls[dieSaveString] = rollPair.second
                        returnResults.mReRolledRolls[dieSaveString] = rollPair.third
                        returnResults.mStruckRollResults[dieSaveString] = secondRollPair.first
                        returnResults.mStruckDroppedRolls[dieSaveString] = secondRollPair.second
                        returnResults.mStruckReRolledRolls[dieSaveString] = secondRollPair.third
                    } else {
                        returnResults.mRollResults[dieSaveString] = secondRollPair.first
                        returnResults.mDroppedRolls[dieSaveString] = secondRollPair.second
                        returnResults.mReRolledRolls[dieSaveString] = secondRollPair.third
                        returnResults.mStruckRollResults[dieSaveString] = rollPair.first
                        returnResults.mStruckDroppedRolls[dieSaveString] = rollPair.second
                        returnResults.mStruckReRolledRolls[dieSaveString] = rollPair.third
                    }
                }
            }
        }

        // Check for rolling critical success or critical failures
        val d20SaveString = SimpleDie("d20", 20).saveToString()
        if(returnResults.mRollResults.containsKey(d20SaveString)) {
            val results = returnResults.mRollResults.getValue(d20SaveString)
            if(results.size == 1) {
                if(results[0] == 20) {
                    returnResults.mRollMaximumValue = true
                } else if(results[0] == 1) {
                    returnResults.mRollMinimumValue = true
                }
            }
        }

        return returnResults
    }

    // Produces an array of 3 lists, a list of taken values, and a list of dropped values, and a list of rerolled values
    private fun produceRollLists(die: Die, properties: RollProperties) : Triple<MutableList<Int>,MutableList<Int>,MutableList<Int>> {

        val keepList = mutableListOf<Int>()
        val dropList = mutableListOf<Int>()
        val reRollList = mutableListOf<Int>()

        // No dice to roll, return empty lists.
        if(properties.mDieCount == 0)
        {
            return Triple(keepList, dropList, reRollList)
        }

        // Roll all of the dice and add them to the return list.
        var rollNum = 0
        while (rollNum < abs(properties.mDieCount)) {
            var dieRoll = die.roll()

            // If we are set to explode, have the maximum value, and actually have a range, roll an extra die
            if(properties.mExplode && dieRoll == die.max() && die.max() != die.min()) {
                rollNum -= 1
            }

            // If we have a minimum value, drop anything less.
            if(properties.mUseMinimumRoll && dieRoll < properties.mMinimumRoll)
            {
                reRollList.add(dieRoll)
                dieRoll = properties.mMinimumRoll
            }

            // If we use reRolls, reRoll under the threshold.
            if(properties.mUseReRoll && dieRoll <= properties.mReRoll)
            {
                reRollList.add(dieRoll)
                dieRoll = die.roll()
            }

            if(properties.mDieCount > 0) {
                keepList.add(dieRoll)
            } else {
                keepList.add(-dieRoll)
            }
            rollNum += 1
        }

        // Drop high values
        if(keepList.size <= properties.mDropHigh) {
            dropList.addAll(keepList)
            keepList.clear()
        } else {
            for(dropIndex in 0 until properties.mDropHigh) {
                val ejectedValue = keepList.max()
                keepList.remove(ejectedValue!!)
                dropList.add(ejectedValue)
            }
        }

        // Drop low values
        if(keepList.size <= properties.mDropLow) {
            dropList.addAll(keepList)
            keepList.clear()
        } else {
            for(dropIndex in 0 until properties.mDropLow) {
                val ejectedValue = keepList.min()
                keepList.remove(ejectedValue!!)
                dropList.add(ejectedValue)
            }
        }

        // Only do keep high/low when you have those properties
        if(properties.mKeepHigh != 0 || properties.mKeepLow != 0) {
            // Only keep going if we have more rolls than what we want to keep
            if(keepList.size > (properties.mKeepHigh + properties.mKeepLow)) {
                val numberToDrop = keepList.size - (properties.mKeepHigh + properties.mKeepLow)
                val indexToDrop = properties.mKeepLow
                val tempSorted = keepList.sortedBy {it}
                for(dropIndex in 0 until numberToDrop) {
                    val ejectedValue = tempSorted[indexToDrop + dropIndex]
                    keepList.remove(ejectedValue)
                    dropList.add(ejectedValue)
                }
            }
        }

        return Triple(keepList, dropList, reRollList)
    }

    fun average() : Float
    {
        var dieAverage = 0f
        val innerDies = mDieMap
        for(diePropertyPair in innerDies)
        {
            dieAverage += DieFactory().createUnknownDie(diePropertyPair.key).average() * diePropertyPair.value.mDieCount + diePropertyPair.value.mModifier
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
            for(diePropertyPair in innerDies)
            {
                if(!DieFactory().createUnknownDie(diePropertyPair.key).displayInHex())
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

    fun getCategoryName() : String
    {
        return mRollCategory
    }

    fun getDetailedRollName() : String
    {
        val innerDies = mDieMap

        var returnString = ""

        if(innerDies.isEmpty())
        {
            return returnString
        }

        val defaultProps = RollProperties()

        var firstDie = true

        for(diePropertyPair in innerDies)
        {
            // Don't add the "+" to the first item. Only add "+" to positive count items.
            if(firstDie) {
                firstDie = false
            } else if(diePropertyPair.value.mDieCount > 0) {
                returnString += "+"
            }

            val die = DieFactory().createUnknownDie(diePropertyPair.key)
            returnString += if(die.getDisplayName().startsWith("d"))
            {
                String.format("%d%s",diePropertyPair.value.mDieCount,die.getDisplayName())
            }
            else
            {
                String.format("%dx%s", diePropertyPair.value.mDieCount,die.getDisplayName())
            }

            returnString += when(diePropertyPair.value.mAdvantageDisadvantage) {
                rollAdvantageValue -> "(Advantage)"
                rollDisadvantageValue -> "(Disadvantage)"
                else -> ""
            }

            returnString += if(diePropertyPair.value.mDropHigh != 0) {
                val dropString = getDropHighString(diePropertyPair.value.mDropHigh)
                "($dropString)"
            } else {
                ""
            }

            returnString += if(diePropertyPair.value.mDropLow != 0) {
                val dropString = getDropLowString(diePropertyPair.value.mDropLow)
                "($dropString)"
            } else {
                ""
            }

            returnString += if(diePropertyPair.value.mKeepHigh != 0) {
                val keepString = getKeepHighString(diePropertyPair.value.mKeepHigh)
                "($keepString)"
            } else {
                ""
            }

            returnString += if(diePropertyPair.value.mKeepLow != 0) {
                val keepString = getKeepLowString(diePropertyPair.value.mKeepLow)
                "($keepString)"
            } else {
                ""
            }

            returnString += if(diePropertyPair.value.mModifier != 0) {
                getModifierString(diePropertyPair.value.mModifier)
            } else {
                ""
            }

            returnString += if(diePropertyPair.value.mUseReRoll != defaultProps.mUseReRoll
                && diePropertyPair.value.mReRoll != defaultProps.mReRoll)
            {
                val reRollString = getReRollString(diePropertyPair.value.mReRoll)
                "($reRollString)"
            } else {
                ""
            }

            returnString += if(diePropertyPair.value.mUseMinimumRoll != defaultProps.mUseMinimumRoll
                && diePropertyPair.value.mMinimumRoll != defaultProps.mMinimumRoll)
            {
                val minString = getMinimumDieValueString(diePropertyPair.value.mMinimumRoll)
                "($minString)"
            } else {
                ""
            }

            returnString += if(diePropertyPair.value.mExplode != defaultProps.mExplode) {
                "(Explode)"
            } else {
                ""
            }
        }

        return returnString
    }
}