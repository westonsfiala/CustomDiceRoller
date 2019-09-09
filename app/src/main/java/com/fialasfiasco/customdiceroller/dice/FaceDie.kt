package com.fialasfiasco.customdiceroller.dice

import com.fialasfiasco.customdiceroller.R
import com.fialasfiasco.customdiceroller.data.MIN_BOUNDING_VALUE
import com.fialasfiasco.customdiceroller.data.MAX_BOUNDING_VALUE
import kotlin.random.Random

const val faceDieStringStart = "Face"

class FaceDie(private val mDieName: String, private val mFaces : List<Int>) : Die()
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

    override fun saveToString() : String
    {
        var returnString = String.format("%s", faceDieStringStart)

        returnString += String.format("%s%s",
            saveSplitStrings[dieSplitStringIndex], mDieName)

        returnString += String.format("%s%s",
            saveSplitStrings[dieSplitStringIndex], mFaces.joinToString(saveSplitStrings[faceDieSplitStringIndex]))

        return returnString
    }

    override fun roll() :  Int
    {
        return mFaces[Random.Default.nextInt(0, mFaces.size)]
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

    override fun displayInHex(): Boolean {
        // Only display hex when you start with "0x" and have more characters after that.
        return mDieName.length > (dieDisplayInHexID.length) && mDieName.startsWith(
            dieDisplayInHexID
        )
    }

    override fun getDisplayName() : String
    {
        return mDieName
    }

    override fun getInfo() : String
    {
        return String.format("Rolls one of the following numbers: %s\nAverage of %d", mFaces.joinToString(), average().toInt())
    }

    override fun getImageID() : Int
    {
        return R.drawable.ic_unknown
    }
}