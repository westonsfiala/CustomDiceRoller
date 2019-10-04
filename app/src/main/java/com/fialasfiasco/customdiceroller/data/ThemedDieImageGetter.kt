package com.fialasfiasco.customdiceroller.data

import android.content.Context
import android.graphics.drawable.Drawable
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

class ThemedDieImageGetter(private val context: Context, private val pageViewModel: PageViewModel) {

    fun getDieDrawable(imageID: Int) : Drawable {
        val dieDrawableID = when(pageViewModel.getTheme()) {
            // TODO: Insert more cases when the image IDs will change

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

        val contextWrapper = ContextThemeWrapper(context, pageViewModel.getTheme())

        return ResourcesCompat.getDrawable( context.resources,  dieDrawableID, contextWrapper.theme )!!
    }
}