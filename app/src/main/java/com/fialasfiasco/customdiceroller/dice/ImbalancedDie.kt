package com.fialasfiasco.customdiceroller.dice

import com.fialasfiasco.customdiceroller.R
import com.fialasfiasco.customdiceroller.data.DIE_UNKNOWN
import com.fialasfiasco.customdiceroller.data.MIN_BOUNDING_VALUE
import com.fialasfiasco.customdiceroller.data.MAX_BOUNDING_VALUE
import kotlin.random.Random

const val imbalancedDieStringStart = "Imbalanced"

class ImbalancedDie(dieName: String, private val mFaces : List<Int>) : Die(dieName)
{

    init {
        if(mDieName.isEmpty() || mFaces.isEmpty())
        {
            throw DieLoadError()
        }

        if(mFaces.max()!! > MAX_BOUNDING_VALUE || mFaces.min()!! < MIN_BOUNDING_VALUE)
        {
            throw DieLoadError()
        }
    }

    override fun clone(newName: String): Die {
        return ImbalancedDie(newName, mFaces)
    }

    override fun saveToString() : String
    {
        var returnString = String.format("%s", imbalancedDieStringStart)

        returnString += String.format("%s%s",
            saveSplitStrings[dieSplitStringIndex], mDieName)

        returnString += String.format("%s%s",
            saveSplitStrings[dieSplitStringIndex], mFaces.joinToString(saveSplitStrings[imbalancedDieSplitStringIndex]))

        return returnString
    }

    override fun roll() :  Int
    {
        return mFaces[Random.Default.nextInt(0, mFaces.size)]
    }

    fun getFaces() : List<Int> {
        return mFaces
    }

    override fun max(): Int {
        return mFaces.max()!!
    }

    override fun min(): Int {
        return mFaces.min()!!
    }

    override fun average() : Float
    {
        return mFaces.average().toFloat()
    }

    override fun getInfo() : String
    {
        return String.format("Rolls one of the following numbers: %s\nAverage of %d", mFaces.joinToString(), average().toInt())
    }

    override fun getImageID() : Int
    {
        return DIE_UNKNOWN
    }
}