package com.wbrawner.budget.ui.categories

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.Observer
import android.content.Intent
import android.support.v4.content.ContextCompat.startActivity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import com.wbrawner.budget.R
import com.wbrawner.budget.ui.categories.AddEditCategoryActivity.Companion.EXTRA_CATEGORY_ID
import com.wbrawner.budget.data.model.Category

class CategoryAdapter(
        private val lifecycleOwner: LifecycleOwner,
        private val data: List<Category>,
        private val viewModel: CategoryViewModel
) : RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.list_item_category, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val category = data[position]
        holder.title?.text = category.name
        holder.amount?.text = String.format("${'$'}%.02f", category.amount / 100.0f)
        viewModel.getCurrentBalance(category.id!!)
                .observe(lifecycleOwner, Observer<Int> { balance ->
                    holder.progress?.isIndeterminate = false
                    if (balance == null) {
                        holder.progress?.progress = 0
                    } else {
                        holder.progress?.max = category.amount
                        holder.progress?.setProgress(
                                -1 * balance,
                                true
                        )
                        holder.amount?.text = holder.itemView.context.getString(
                                R.string.balance_remaning,
                                (category.amount + balance) / 100.0f
                        )
                    }
                })
        holder.itemView.setOnClickListener {
            startActivity(
                    it.context.applicationContext,
                    Intent(it.context.applicationContext, AddEditCategoryActivity::class.java)
                            .apply {
                                putExtra(EXTRA_CATEGORY_ID, category.id)
                            },
                    null
            )
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView? = itemView.findViewById(R.id.category_title)
        val amount: TextView? = itemView.findViewById(R.id.category_amount)
        val progress: ProgressBar? = itemView.findViewById(R.id.category_progress)
    }
}
