package com.fialasfiasco.customdiceroller.data

import com.fialasfiasco.customdiceroller.R

const val aggregateDieStringStart = "Aggregate"
const val aggregateDieSplitString = ";"

class AggregateDie(val mInnerDie : InnerDie, val mDieCount: Int) : Die()
{

    init {
        if(mDieCount < 0)
        {
            throw DieLoadError()
        }
    }

    override fun saveToString(): String
    {
        // Aggregate;InnerDieString;Number
        return String.format("%s;%s;%d",
            aggregateDieStringStart,
            mInnerDie.saveToString(),
            mDieCount)
    }

    override fun roll() : List<Int>
    {
        val rollList = mutableListOf<Int>()
        for(rollNum in 0 until mDieCount)
        {
            val innerRoll = mInnerDie.roll()
            for(roll in innerRoll)
            {
                rollList.add(roll)
            }
        }
        return rollList
    }

    override fun average() : Float
    {
        return mDieCount * mInnerDie.average()
    }

    override fun displayInHex(): Boolean {
        return mInnerDie.displayInHex()
    }

    override fun getName() : String
    {
        val innerName = mInnerDie.getName()

        return if(innerName.startsWith("d"))
        {
            String.format("%d%s",mDieCount,mInnerDie.getName())
        }
        else
        {
            String.format("%dx%s", mDieCount, mInnerDie.getName())
        }
    }

    override fun getInfo(): String {
        return String.format("Rolls %d %s dice\nAverage of %d", mDieCount, mInnerDie.getName(), average().toInt())
    }

    override fun getImageID(): Int {
        return R.drawable.ic_cubes
    }
}