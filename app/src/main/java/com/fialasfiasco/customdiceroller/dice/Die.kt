package com.fialasfiasco.customdiceroller.dice

import java.lang.RuntimeException

class DieLoadError : RuntimeException()

const val dieDisplayInHexID = "0x"

abstract class Die
{
    abstract fun saveToString() : String

    abstract fun roll() : Int

    abstract fun max() : Int

    abstract fun min() : Int

    abstract fun average() : Float

    abstract fun displayInHex() : Boolean

    abstract fun getDisplayName() : String

    abstract fun getInfo() : String

    abstract fun getImageID() : Int
}