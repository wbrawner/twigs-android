package com.wbrawner.budget.ui.base

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

class BindableAdapter<Data>(
        private val constructors: Map<Int, (view: View) -> BindableViewHolder<Data>>,
        diffUtilItemCallback: DiffUtil.ItemCallback<BindableData<Data>>
) : ListAdapter<BindableData<Data>, BindableAdapter.BindableViewHolder<Data>>(diffUtilItemCallback) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
            : BindableViewHolder<Data> = constructors[viewType]
            ?.invoke(LayoutInflater.from(parent.context).inflate(viewType, parent, false))
            ?: throw IllegalStateException("Attempted to create ViewHolder without proper constructor provided")

    override fun onBindViewHolder(holder: BindableViewHolder<Data>, position: Int) {
        holder.onBind(getItem(position))
    }

    override fun onViewRecycled(holder: BindableViewHolder<Data>) {
        holder.onUnbind()
    }

    abstract class BindableViewHolder<T>(itemView: View) : RecyclerView.ViewHolder(itemView), Bindable<BindableData<T>>

    abstract class CoroutineViewHolder<T>(itemView: View) : BindableViewHolder<T>(itemView), CoroutineScope {
        override val coroutineContext: CoroutineContext = Dispatchers.Main

        override fun onUnbind() {
            coroutineContext[Job]?.cancel()
            super.onUnbind()
        }
    }
}

interface Bindable<T> {
    fun onBind(item: T) {}
    fun onUnbind() {}
}

interface BindableData<T> {
    val data: T
    @get:LayoutRes
    val viewType: Int
}