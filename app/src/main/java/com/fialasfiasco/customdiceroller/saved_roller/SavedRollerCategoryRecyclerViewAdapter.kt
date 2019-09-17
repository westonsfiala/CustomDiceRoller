package com.fialasfiasco.customdiceroller.saved_roller

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DividerItemDecoration
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
        holder.mShowHideLayout.setOnClickListener {
            if(holder.mInnerRecyclerView.visibility == RecyclerView.VISIBLE) {
                holder.mShowHideImage.rotation = 90f
                holder.mInnerRecyclerView.visibility = RecyclerView.GONE
                pageViewModel.setCategoryExpanded(categoryName, false)
            } else {
                holder.mShowHideImage.rotation = 0f
                holder.mInnerRecyclerView.visibility = RecyclerView.VISIBLE
                pageViewModel.setCategoryExpanded(categoryName, true)
            }
        }

        if(pageViewModel.getCategoryExpanded(categoryName)) {
            holder.mShowHideImage.rotation = 0f
            holder.mInnerRecyclerView.visibility = RecyclerView.VISIBLE
        } else {
            holder.mShowHideImage.rotation = 90f
            holder.mInnerRecyclerView.visibility = RecyclerView.GONE
        }
    }

    private fun setupInnerRecyclerView(holder: SavedRollCategoryViewHolder, categoryName: String) {
        holder.mInnerRecyclerView.layoutManager = LinearLayoutManager(context)
        holder.mInnerRecyclerView.adapter = SavedRollerRecyclerViewAdapter(context, categoryName, pageViewModel, listener)
        holder.mInnerRecyclerView.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
    }

    override fun getItemCount(): Int = pageViewModel.getNumSavedRollCategories()

    inner class SavedRollCategoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val mCategoryText: TextView = view.categoryText
        val mShowHideImage: ImageView = view.showHideImageView
        val mShowHideLayout: ConstraintLayout = view.showHideLayout
        val mInnerRecyclerView: RecyclerView = view.savedRollViewRecycler
    }
}
