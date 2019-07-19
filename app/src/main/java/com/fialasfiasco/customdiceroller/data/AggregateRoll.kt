package com.fialasfiasco.customdiceroller.data

import com.fialasfiasco.customdiceroller.R
import com.fialasfiasco.customdiceroller.helper.getModifierString

const val aggregateRollStringStart = "Aggregate"
const val aggregateRollSplitString = ";"

class AggregateRoll(private val mRollName: String, val mModifier : Int) : Die()
{
    private val mDieMap = mutableMapOf<String, Int>()

    fun addDieToRoll(innerDie: InnerDie, dieCount: Int)
    {
        mDieMap[innerDie.saveToString()] = dieCount
    }

    fun getTotalDiceInRoll() : Int
    {
        var numDice = 0

        for(diePair in mDieMap)
        {
            numDice += diePair.value
        }

        return numDice
    }

    override fun saveToString(): String
    {
        // Aggregate;Name;Modifier;InnerDieString;DieCount(Repeat)
        var saveString = String.format("%s%s%d",
            aggregateRollStringStart,
            aggregateRollSplitString, mModifier)

        for(roll in mDieMap)
        {
            saveString += String.format("%s%s%s%d",
                aggregateRollSplitString, roll.key,
                aggregateRollSplitString, roll.value)
        }

        return saveString
    }

    fun getInnerDice() : Map<InnerDie,Int>
    {
        val returnMap = mutableMapOf<InnerDie,Int>()

        for(die in mDieMap) {

            try {
                val innerDie = DieFactory().createUnknownInnerDie(die.key)

                returnMap[innerDie] = die.value
            }
            // Just throw away errors.
            catch (error : DieLoadError) {}
        }
        return returnMap
    }

    fun splitRoll() : Map<String,MutableList<Int>>
    {
        val returnMap = mutableMapOf<String,MutableList<Int>>()

        for(innerDiePair in getInnerDice()) {
            val singleDieRollList = mutableListOf<Int>()
            for (rollNum in 0 until innerDiePair.value) {
                singleDieRollList.add(innerDiePair.key.roll())
            }
            returnMap[innerDiePair.key.getName()] = singleDieRollList
        }
        return returnMap
    }

    override fun roll():  Int
    {
        var rollValue = 0

        for(innerRoll in splitRoll())
        {
            rollValue += innerRoll.value.sum()
        }

        return rollValue
    }

    override fun average() : Float
    {
        var dieAverage = 0f
        val innerDies = getInnerDice()
        for(dieCountPair in innerDies)
        {
            dieAverage += dieCountPair.key.average() * dieCountPair.value
        }
        return dieAverage
    }

    override fun displayInHex(): Boolean {
        // Only display hex when you start with "0x" and have more characters after that.
        return if(mRollName.isNotEmpty()) {
            mRollName.length > (dieDisplayInHexID.length) && mRollName.startsWith(dieDisplayInHexID)
        }
        else
        {
            var displayInHex = true
            val innerDies = getInnerDice()
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

    override fun getName() : String
    {
        return mRollName
    }

    fun getDetailedRollName() : String
    {
        val innerDies = getInnerDice()

        var returnString = ""

        for(dieCountPair in innerDies)
        {
            returnString += if(dieCountPair.key.getName().startsWith("d"))
            {
                String.format("%d%s+",dieCountPair.value,dieCountPair.key.getName())
            }
            else
            {
                returnString += String.format("%dx%s+", dieCountPair.value,dieCountPair.key.getName())
            }
        }

        returnString = returnString.removeRange(returnString.length - 1, returnString.length)

        if(mModifier != 0) {
            returnString += getModifierString(mModifier)
        }

        return returnString
    }

    override fun getInfo(): String {
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

    override fun getImageID(): Int {
        return R.drawable.ic_cubes
    }
}