package com.fialasfiasco.customdiceroller.data

import android.content.Context
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.widget.Toast
import androidx.appcompat.view.ContextThemeWrapper
import androidx.core.content.res.ResourcesCompat
import com.fialasfiasco.customdiceroller.R

const val DIE_UNKNOWN = -1
const val DIE_FATE = 0
const val DIE_2 = 2
const val DIE_3 = 3
const val DIE_4 = 4
const val DIE_6 = 6
const val DIE_8 = 8
const val DIE_10 = 10
const val DIE_12 = 12
const val DIE_20 = 20
const val DIE_100 = 100

fun getThemeFromString(context: Context, themeString: String) : Int{
    return when (themeString) {
        context.getString(R.string.white_theme) -> R.style.DefaultColor
        context.getString(R.string.fire_theme) -> R.style.FireGradientColor
        context.getString(R.string.forest_theme) -> R.style.ForestGradientColor
        context.getString(R.string.beach_theme) -> R.style.BeachGradientColor
        context.getString(R.string.rgb_theme) -> R.style.RGBGradientColor

        context.getString(R.string.gold_theme) -> R.style.GoldGradientColor
        context.getString(R.string.steel_theme) -> R.style.SteelGradientColor

        context.getString(R.string.creamsicle_theme) -> R.style.CreamsicleGradientColor
        context.getString(R.string.mint_chocolate_theme) -> R.style.MintChocolateGradientColor
        context.getString(R.string.rainbow_sherbert_theme) -> R.style.RainbowSherbertGradientColor
        context.getString(R.string.superman_theme) -> R.style.SupermanGradientColor
        else -> R.style.DefaultColor
    }
}

fun testTheme(context: Context, themeString: String) : Boolean {
    val themeID = getThemeFromString(context, themeString)
    val dieDrawableID = when(themeID) {
        R.style.DefaultColor -> R.drawable.ic_unknown_white
        // The else & default case are the same.
        else -> R.drawable.ic_unknown
    }

    return try {
        val contextWrapper = ContextThemeWrapper(context, themeID)
        ResourcesCompat.getDrawable( context.resources,  dieDrawableID, contextWrapper.theme )!!
        true
    } catch (error : Resources.NotFoundException) {
        Toast.makeText(context, "Error occurred while applying dice theme", Toast.LENGTH_SHORT).show()
        false
    }
}

class ThemedDieImageGetter(private val context: Context, private val pageViewModel: PageViewModel) {

    fun getDieDrawable(imageID: Int) : Drawable {
        val dieDrawableID = when(pageViewModel.getTheme()) {
            // TODO: Insert more cases when the image IDs will change
            R.style.DefaultColor -> {
                when (imageID) {
                    DIE_UNKNOWN -> R.drawable.ic_unknown_white
                    DIE_FATE -> R.drawable.ic_fate_white
                    DIE_2 -> R.drawable.ic_d2_white
                    DIE_3 -> R.drawable.ic_d3_white
                    DIE_4 -> R.drawable.ic_d4_white
                    DIE_6 -> R.drawable.ic_d6_white
                    DIE_8 -> R.drawable.ic_d8_white
                    DIE_10 -> R.drawable.ic_d10_white
                    DIE_12 -> R.drawable.ic_d12_white
                    DIE_20 -> R.drawable.ic_d20_white
                    DIE_100 -> R.drawable.ic_d100_white
                    else -> R.drawable.ic_unknown_white
                }
            }

            // The else & default case are the same.
            else -> {
                when (imageID) {
                    DIE_UNKNOWN -> R.drawable.ic_unknown
                    DIE_FATE -> R.drawable.ic_fate
                    DIE_2 -> R.drawable.ic_d2
                    DIE_3 -> R.drawable.ic_d3
                    DIE_4 -> R.drawable.ic_d4
                    DIE_6 -> R.drawable.ic_d6
                    DIE_8 -> R.drawable.ic_d8
                    DIE_10 -> R.drawable.ic_d10
                    DIE_12 -> R.drawable.ic_d12
                    DIE_20 -> R.drawable.ic_d20
                    DIE_100 -> R.drawable.ic_d100
                    else -> R.drawable.ic_unknown
                }
            }
        }

        return try {
            val contextWrapper = ContextThemeWrapper(context, pageViewModel.getTheme())
            ResourcesCompat.getDrawable( context.resources,  dieDrawableID, contextWrapper.theme )!!
        } catch (error : Resources.NotFoundException) {
            pageViewModel.setTheme(R.style.DefaultColor)
            Toast.makeText(context, "Error occurred while applying dice theme", Toast.LENGTH_SHORT).show()
            val contextWrapper = ContextThemeWrapper(context, pageViewModel.getTheme())
            return ResourcesCompat.getDrawable( context.resources,  dieDrawableID, contextWrapper.theme )!!
        }
    }
}