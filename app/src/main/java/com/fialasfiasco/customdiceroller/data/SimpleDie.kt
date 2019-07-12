package com.fialasfiasco.customdiceroller.data

import com.fialasfiasco.customdiceroller.R
import kotlin.random.Random

const val simpleDieStringStart = "Simple"
const val simpleDieSplitString = ":"

class SimpleDie(private val mDie: Int) : Die()
{

    init {
        if(mDie <= 0)
        {
            throw DieLoadError()
        }
    }

    override fun saveToString() : String
    {
        return String.format("%s:%d", simpleDieStringStart, mDie)
    }

    override fun roll() : List<Int>
    {
        return listOf(Random.Default.nextInt(1, mDie + 1))
    }

    override fun average() : Float
    {
        return (mDie + 1) / 2.0f
    }

    override fun getName() : String
    {
        return String.format("d%d", mDie)
    }

    override fun getInfo() : String
    {
        return String.format("Rolls a number between 1 and %d\nAverage of %d", mDie, average().toInt())
    }

    override fun getImageID() : Int
    {
        return when(mDie)
        {
            2 -> R.drawable.ic_d2
            4 -> R.drawable.ic_d4
            6 -> R.drawable.ic_d6
            8 -> R.drawable.ic_d8
            10 -> R.drawable.ic_d10
            12 -> R.drawable.ic_d12
            20 -> R.drawable.ic_d20
            100 -> R.drawable.ic_d100
            else -> R.drawable.ic_unknown
        }
    }
}