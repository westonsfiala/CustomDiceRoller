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