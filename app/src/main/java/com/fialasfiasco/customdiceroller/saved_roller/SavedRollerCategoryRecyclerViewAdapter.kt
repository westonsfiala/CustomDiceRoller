package com.fialasfiasco.customdiceroller.saved_roller

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.view.animation.ScaleAnimation
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fialasfiasco.customdiceroller.R
import com.fialasfiasco.customdiceroller.data.PageViewModel
import kotlinx.android.synthetic.main.holder_saved_roll_category.view.*

/**
 * [RecyclerView.Adapter] that can display a [SavedRollCategoryViewHolder]
 */
class SavedRollerCategoryRecyclerViewAdapter(private val context: Context,
                                             private val pageViewModel: PageViewModel,
                                             private val listener: SavedRollerRecyclerViewAdapter.OnSavedRollViewInteractionListener
) :
    RecyclerView.Adapter<SavedRollerCategoryRecyclerViewAdapter.SavedRollCategoryViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SavedRollCategoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.holder_saved_roll_category, parent, false)
        return SavedRollCategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: SavedRollCategoryViewHolder, position: Int) {
        val rollCategoryName = pageViewModel.getSavedRollCategory(position)

        holder.mCategoryText.text = rollCategoryName
        setupShowHideButton(holder, rollCategoryName)
        setupInnerRecyclerView(holder, rollCategoryName)
    }

    private fun setupShowHideButton(holder: SavedRollCategoryViewHolder, categoryName: String) {

        val contractCategories = ScaleAnimation(1f,1f, 1f, 0f, 0f, 0f)

        contractCategories.duration = 300

        contractCategories.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(arg0: Animation) {}
            override fun onAnimationRepeat(arg0: Animation) {}
            override fun onAnimationEnd(arg0: Animation) {
                holder.mInnerRecyclerView.visibility = RecyclerView.GONE
            }
        })

        val expandCategories = ScaleAnimation(1f,1f, 0f, 1f, 0f, 0f)

        expandCategories.duration = 300

        expandCategories.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(arg0: Animation) {
                holder.mInnerRecyclerView.visibility = RecyclerView.VISIBLE
            }
            override fun onAnimationRepeat(arg0: Animation) {}
            override fun onAnimationEnd(arg0: Animation) {}
        })

        val isExpanded = pageViewModel.getCategoryExpanded(categoryName)

        // You have to do this because it deals with the whole activity being reloaded, and the fragment being reloaded.
        // If you don't it will have small glitches when you either go to far away tabs or close and reopen the app.
        holder.mShowHideImage.rotation = if(isExpanded) {180f} else {90f}
        val rotateStart = if(isExpanded) {-90f} else {0f}
        val rotateEnd = if(isExpanded) {0f} else {90f}

        val rotateHideShow = RotateAnimation(rotateStart, rotateEnd,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f)

        rotateHideShow.duration = 300
        rotateHideShow.fillAfter = true

        val unRotateHideShow = RotateAnimation(rotateEnd, rotateStart,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f)

        unRotateHideShow.duration = 300
        unRotateHideShow.fillAfter = true

        holder.mShowHideLayout.setOnClickListener {
            if(holder.mInnerRecyclerView.visibility == RecyclerView.VISIBLE) {

                holder.mShowHideImage.startAnimation(unRotateHideShow)
                holder.mInnerRecyclerView.startAnimation(contractCategories)

                pageViewModel.setCategoryExpanded(categoryName, false)
            } else {

                holder.mShowHideImage.startAnimation(rotateHideShow)
                holder.mInnerRecyclerView.startAnimation(expandCategories)

                pageViewModel.setCategoryExpanded(categoryName, true)
            }
        }


        if(pageViewModel.getCategoryExpanded(categoryName)) {
            holder.mInnerRecyclerView.visibility = RecyclerView.VISIBLE
        } else {
            holder.mInnerRecyclerView.visibility = RecyclerView.GONE
        }
    }

    private fun setupInnerRecyclerView(holder: SavedRollCategoryViewHolder, categoryName: String) {
        holder.mInnerRecyclerView.layoutManager = LinearLayoutManager(context)
        holder.mInnerRecyclerView.adapter = SavedRollerRecyclerViewAdapter(context, categoryName, pageViewModel, listener)
    }

    override fun getItemCount(): Int = pageViewModel.getNumSavedRollCategories()

    inner class SavedRollCategoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val mCategoryText: TextView = view.categoryText
        val mShowHideImage: ImageView = view.showHideImageView
        val mShowHideLayout: ConstraintLayout = view.showHideLayout
        val mInnerRecyclerView: RecyclerView = view.savedRollViewRecycler
    }
}
