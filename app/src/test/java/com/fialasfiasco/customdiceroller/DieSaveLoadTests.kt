package com.fialasfiasco.customdiceroller

import com.fialasfiasco.customdiceroller.dice.*
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class DieSaveLoadTests {

    @Test
    fun createSaveLoad_SimpleDie() {
        try {
            val die = SimpleDie("d20", 20)
            val dieSaveString = die.saveToString()
            val recreatedDie = DieFactory().createUnknownDie(dieSaveString)
            assertEquals(die.getDisplayName(), recreatedDie.getDisplayName())
            assert(recreatedDie is SimpleDie)
        } catch (error : DieLoadError) {
            assert(false)
        }
    }

    @Test
    fun loadSimpleDieString() {
        try {
            for(dieString in simpleDieStrings) {
                val recreatedDie = DieFactory().createUnknownDie(dieString)
                assert(recreatedDie is SimpleDie)
            }
        } catch (error : DieLoadError) {
            assert(false)
        }
    }

    @Test
    fun createSaveLoad_MinMaxDie() {
        try {
            val die = MinMaxDie("d20", 1, 20)
            val dieSaveString = die.saveToString()
            val recreatedDie = DieFactory().createUnknownDie(dieSaveString)
            assertEquals(die.getDisplayName(), recreatedDie.getDisplayName())
            assert(recreatedDie is MinMaxDie)
        } catch (error : DieLoadError) {
            assert(false)
        }
    }

    @Test
    fun loadMinMaxDieString() {
        try {
            for(dieString in minMaxDieStrings) {
                val recreatedDie = DieFactory().createUnknownDie(dieString)
                assert(recreatedDie is MinMaxDie)
            }
        } catch (error : DieLoadError) {
            assert(false)
        }
    }

    @Test
    fun createSaveLoad_ImbalancedDie() {
        try {
            val die = ImbalancedDie("fib", listOf(1,1,2,3,5,8))
            val dieSaveString = die.saveToString()
            val recreatedDie = DieFactory().createUnknownDie(dieSaveString)
            assertEquals(die.getDisplayName(), recreatedDie.getDisplayName())
            assert(recreatedDie is ImbalancedDie)
        } catch (error : DieLoadError) {
            assert(false)
        }
    }

    @Test
    fun loadImbalancedDieString() {
        try {
            for(dieString in imbalancedDieStrings) {
                val recreatedDie = DieFactory().createUnknownDie(dieString)
                assert(recreatedDie is ImbalancedDie)
            }
        } catch (error : DieLoadError) {
            assert(false)
        }
    }

    companion object {
        val simpleDieStrings = arrayOf(
            "Simple__DIE_SAVE_STRING_SPLITTER__d20__DIE_SAVE_STRING_SPLITTER__20", // Version 1.7.7
            "Simple__DIE_SAVE_STRING_SPLITTER__d55__DIE_SAVE_STRING_SPLITTER__55", // Version 1.7.7
            "Simple__DIE_SPLIT__d20__DIE_SPLIT__20" // Version 1.7.8
        )
        val minMaxDieStrings = arrayOf(
            "MinMax__DIE_SAVE_STRING_SPLITTER__d20__DIE_SAVE_STRING_SPLITTER__1__DIE_SAVE_STRING_SPLITTER__20", // Version 1.7.7
            "MinMax__DIE_SAVE_STRING_SPLITTER__d55__DIE_SAVE_STRING_SPLITTER__1__DIE_SAVE_STRING_SPLITTER__55", // Version 1.7.7
            "MinMax__DIE_SPLIT__d20__DIE_SPLIT__1__DIE_SPLIT__20" // Version 1.7.8
        )
        val imbalancedDieStrings = arrayOf(
            "Imbalanced__DIE_SAVE_STRING_SPLITTER__fib__DIE_SAVE_STRING_SPLITTER__1__FACE_DIE_SAVE_STRING_SPLITTER__1__FACE_DIE_SAVE_STRING_SPLITTER__2__FACE_DIE_SAVE_STRING_SPLITTER__3__FACE_DIE_SAVE_STRING_SPLITTER__5__FACE_DIE_SAVE_STRING_SPLITTER__8", // Version 1.7.7
            "Imbalanced__DIE_SPLIT__fib__DIE_SPLIT__1__FACE_SPLIT__1__FACE_SPLIT__2__FACE_SPLIT__3__FACE_SPLIT__5__FACE_SPLIT__8" // Version 1.7.8
        )
    }
}
