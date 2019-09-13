package com.fialasfiasco.customdiceroller.dice

import java.lang.RuntimeException

class DieLoadError : RuntimeException()

const val dieDisplayInHexID = "0x"

abstract class Die(protected var mDieName: String)
{
    abstract fun saveToString() : String

    abstract fun roll() : Int

    abstract fun max() : Int

    abstract fun min() : Int

    abstract fun average() : Float

    fun displayInHex() : Boolean {
        // Only display hex when you start with "0x" and have more characters after that.
        return mDieName.length > (dieDisplayInHexID.length) && mDieName.startsWith(
            dieDisplayInHexID
        )
    }

    fun getDisplayName() : String
    {
        return mDieName
    }

    abstract fun getInfo() : String

    abstract fun getImageID() : Int
}