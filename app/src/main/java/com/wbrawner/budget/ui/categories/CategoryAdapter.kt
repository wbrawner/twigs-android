package com.wbrawner.budget.ui.categories

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import com.wbrawner.budget.R
import com.wbrawner.budget.common.account.Account
import com.wbrawner.budget.common.category.Category
import com.wbrawner.budget.ui.autoDispose
import com.wbrawner.budget.ui.categories.AddEditCategoryActivity.Companion.EXTRA_ACCOUNT_ID
import com.wbrawner.budget.ui.categories.AddEditCategoryActivity.Companion.EXTRA_CATEGORY_ID
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class CategoryAdapter(
        private val activity: Activity,
        private val account: Account,
        private val data: List<Category>,
        private val viewModel: CategoryViewModel
) : androidx.recyclerview.widget.RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.list_item_category, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val category = data[position]
        holder.title?.text = category.title
        // TODO: Format according to account's currency
        holder.amount?.text = String.format("${'$'}%.02f", category.amount / 100.0f)
        holder.progress?.max = category.amount.toInt()
        viewModel.getBalance(category.id!!)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { balance, _ ->
                    holder.progress?.isIndeterminate = false
                    holder.progress?.setProgress(
                            (-1 * balance).toInt(),
                            true
                    )
                    holder.amount?.text = holder.itemView.context.getString(
                            R.string.balance_remaning,
                            (category.amount + balance) / 100.0f
                    )
                }
                .autoDispose(holder.disposables)
        holder.itemView.setOnClickListener {
            startActivity(
                    activity,
                    Intent(it.context.applicationContext, AddEditCategoryActivity::class.java)
                            .apply {
                                putExtra(EXTRA_ACCOUNT_ID, account.id!!)
                                putExtra(EXTRA_CATEGORY_ID, category.id!!)
                            },
                    null
            )
        }
    }

    override fun onViewRecycled(holder: ViewHolder) {
        holder.disposables.dispose()
        holder.disposables.clear()
        super.onViewRecycled(holder)
    }

    inner class ViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        val title: TextView? = itemView.findViewById(R.id.category_title)
        val amount: TextView? = itemView.findViewById(R.id.category_amount)
        val progress: ProgressBar? = itemView.findViewById(R.id.category_progress)
        val disposables = CompositeDisposable()
    }
}
