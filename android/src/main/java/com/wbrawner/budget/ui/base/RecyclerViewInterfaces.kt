package com.wbrawner.budget.ui.base

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlin.coroutines.CoroutineContext

class BindableAdapter<T : BindableState>(
        private val items: List<T>,
        private val constructors: Map<Int, (view: View) -> BindableViewHolder<T>>
) : RecyclerView.Adapter<BindableAdapter.BindableViewHolder<T>>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
            : BindableViewHolder<T> = constructors[viewType]
            ?.invoke(LayoutInflater.from(parent.context).inflate(viewType, parent, false))
            ?: throw IllegalStateException("Attempted to create ViewHolder without proper constructor provided")

    override fun getItemCount(): Int = items.size

    override fun getItemViewType(position: Int): Int = items[position].viewType

    override fun onBindViewHolder(holder: BindableViewHolder<T>, position: Int) {
        holder.onBind(items[position])
    }

    override fun onViewRecycled(holder: BindableViewHolder<T>) {
        holder.onUnbind()
    }

    abstract class BindableViewHolder<T>(itemView: View)
        : RecyclerView.ViewHolder(itemView), Bindable<T>

    abstract class CoroutineViewHolder<T>(itemView: View) : BindableViewHolder<T>(itemView), CoroutineScope {
        override val coroutineContext: CoroutineContext = Dispatchers.Main

        override fun onUnbind() {
            try {
                coroutineContext.cancel()
            } catch (ignored: Exception) {
            }
            super.onUnbind()
        }
    }
}

interface Bindable<T> {
    fun onBind(item: T) {}
    fun onUnbind() {}
}

interface BindableState {
    @get:LayoutRes
    val viewType: Int
}