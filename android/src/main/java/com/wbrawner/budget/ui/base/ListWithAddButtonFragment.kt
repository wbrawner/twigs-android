package com.wbrawner.budget.ui.base

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.wbrawner.budget.AsyncState
import com.wbrawner.budget.AsyncViewModel
import com.wbrawner.budget.R
import com.wbrawner.budget.common.Identifiable
import com.wbrawner.budget.ui.hideFabOnScroll
import kotlinx.android.synthetic.main.fragment_list_with_add_button.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

abstract class ListWithAddButtonFragment<Data : Identifiable, ViewModel : AsyncViewModel<List<Data>>> : Fragment() {
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_list_with_add_button, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView.layoutManager = LinearLayoutManager(view.context)
        recyclerView.hideFabOnScroll(addFab)
        val adapter = BindableAdapter(constructors, diffUtilItemCallback)
        recyclerView.adapter = adapter
        addFab.setOnClickListener {
            addItem()
        }
        noItemsTextView.setText(noItemsStringRes)
        lifecycleScope.launch {
            viewModel.state.collect { state ->
                when (state) {
                    is AsyncState.Loading -> {
                        progressBar.visibility = View.VISIBLE
                        listContainer.visibility = View.GONE
                        noItemsTextView.visibility = View.GONE
                    }
                    is AsyncState.Success -> {
                        progressBar.visibility = View.GONE
                        listContainer.visibility = View.VISIBLE
                        noItemsTextView.visibility = View.GONE
                        adapter.submitList(state.data.map { bindData(it) })
                    }
                    is AsyncState.Error -> {
                        // TODO: Show an error message
                        progressBar.visibility = View.GONE
                        listContainer.visibility = View.GONE
                        noItemsTextView.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        reloadItems()
    }

    abstract val viewModel: ViewModel
    abstract fun reloadItems()
    @get:StringRes
    abstract val noItemsStringRes: Int
    abstract fun addItem()
    abstract fun bindData(data: Data): BindableData<Data>
    abstract val constructors: Map<Int, (View) -> BindableAdapter.BindableViewHolder<Data>>
    open val diffUtilItemCallback: DiffUtil.ItemCallback<BindableData<Data>> = object: DiffUtil.ItemCallback<BindableData<Data>>() {
        override fun areItemsTheSame(oldItem: BindableData<Data>, newItem: BindableData<Data>): Boolean {
            return oldItem.data.id === newItem.data.id
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: BindableData<Data>, newItem: BindableData<Data>): Boolean {
            return oldItem.data == newItem.data
        }
    }
}
