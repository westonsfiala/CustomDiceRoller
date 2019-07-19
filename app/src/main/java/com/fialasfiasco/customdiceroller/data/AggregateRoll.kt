package com.fialasfiasco.customdiceroller.data

import com.fialasfiasco.customdiceroller.R
import com.fialasfiasco.customdiceroller.helper.getModifierString

const val aggregateDieStringStart = "Aggregate"
const val aggregateDieSplitString = ";"

class AggregateRoll(private val mRollName: String) : Die()
{
    private val mDieMap = mutableMapOf<String, Int>()
    private var mModifier = 0

    fun addDieToRoll(innerDie: InnerDie, dieCount: Int)
    {
        mDieMap[innerDie.saveToString()] = dieCount
    }

    fun setModifier(mod: Int)
    {
        mModifier = mod
    }

    fun getModifier() : Int
    {
        return mModifier
    }

    override fun saveToString(): String
    {
        // Aggregate;InnerDieString;Number(Repeat)
        var saveString = String.format("%s", aggregateDieStringStart)

        for(roll in mDieMap)
        {
            saveString += String.format("%s%s%s%d",
                aggregateDieSplitString, roll.key,
                aggregateDieSplitString, roll.value)
        }

        return saveString
    }

    private fun getInnerDieMap() : Map<InnerDie,Int>
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

    fun aggregateSplitRoll() : Map<String,List<Int>>
    {
        val returnMap = mutableMapOf<String,List<Int>>()

        for(innerDiePair in getInnerDieMap()) {
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

        for(innerRoll in aggregateSplitRoll())
        {
            rollValue += innerRoll.value.sum()
        }

        return rollValue
    }

    override fun average() : Float
    {
        var dieAverage = 0f
        val innerMap = getInnerDieMap()
        for(dieCountPair in innerMap)
        {
            dieAverage += dieCountPair.key.average() * dieCountPair.value
        }
        return dieAverage
    }

    override fun displayInHex(): Boolean {
        // Only display hex when you start with "0x" and have more characters after that.
        return mRollName.length > (dieDisplayInHexID.length) && mRollName.startsWith(dieDisplayInHexID)
    }

    override fun getName() : String
    {
        return mRollName
    }

    fun getDetailedRollName() : String
    {
        val innerMap = getInnerDieMap()

        var returnString = ""

        for(dieCountPair in innerMap)
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