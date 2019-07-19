package com.fialasfiasco.customdiceroller.data

import java.lang.RuntimeException

class DieLoadError : RuntimeException()

const val dieDisplayInHexID = "0x"

abstract class Die
{
    abstract fun saveToString() : String

    abstract fun roll() : Int

    abstract fun average() : Float

    abstract fun displayInHex() : Boolean

    abstract fun getName() : String

    abstract fun getInfo() : String

    abstract fun getImageID() : Int
}

// Used by aggregate die to ensure only certain types of die are contained within it.
// Don't want an aggregate die to contain an aggregate die.
abstract class InnerDie : Die()