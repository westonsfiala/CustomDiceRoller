package com.fialasfiasco.customdiceroller.data

import java.lang.RuntimeException

class DieLoadError : RuntimeException()

abstract class Die
{
    abstract fun saveToString() : String

    abstract fun roll() : List<Int>

    abstract fun average() : Float

    abstract fun getName() : String

    abstract fun getInfo() : String

    abstract fun getImageID() : Int
}

// Used by aggregate die to ensure only certain types of die are contained within it.
// Don't want an aggregate die to contain an aggregate die.
abstract class InnerDie : Die()