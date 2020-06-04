package com.wbrawner.budget.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import androidx.recyclerview.widget.LinearLayoutManager
import com.wbrawner.budget.R
import com.wbrawner.budget.di.BudgetViewModelFactory
import com.wbrawner.budget.ui.hideFabOnScroll
import com.wbrawner.budget.ui.show
import kotlinx.android.synthetic.main.fragment_list_with_add_button.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

abstract class ListWithAddButtonFragment<T : LoadingViewModel, State: BindableState> : Fragment(), CoroutineScope {
    override val coroutineContext: CoroutineContext = Dispatchers.Main
    @Inject
    lateinit var viewModelFactory: BudgetViewModelFactory
    lateinit var viewModel: T
    private var viewCreated = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this, viewModelFactory).get(viewModelClass)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_list_with_add_button, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.isLoading.observe(viewLifecycleOwner, Observer {
            view.findViewById<ProgressBar?>(R.id.progressBar)?.show(it)
        })
        recyclerView.layoutManager = LinearLayoutManager(view.context)
        recyclerView.hideFabOnScroll(addFab)
        addFab.setOnClickListener {
            addItem()
        }
        reloadItems()
    }

    override fun onStart() {
        super.onStart()
        reloadItems()
    }

    private fun reloadItems() {
        launch {
            if (view == null) return@launch
            val (items, constructors) = loadItems()
            if (items.isEmpty()) {
                recyclerView?.adapter = null
                recyclerView?.visibility = View.GONE
                noItemsTextView?.setText(noItemsStringRes)
                noItemsTextView?.visibility = View.VISIBLE
            } else {
                recyclerView?.adapter = BindableAdapter(items, constructors)
                recyclerView?.visibility = View.VISIBLE
                noItemsTextView?.visibility = View.GONE
            }
        }
    }

    override fun onDestroyView() {
        try {
            coroutineContext.cancel()
        } catch (ignored: Exception) {
        }
        super.onDestroyView()
    }

    abstract val viewModelClass: Class<T>

    @get:StringRes
    abstract val noItemsStringRes: Int

    abstract fun addItem()

    abstract suspend fun loadItems(): Pair<List<State>, Map<Int, (view: View) -> BindableAdapter.BindableViewHolder<State>>>
}

abstract class LoadingViewModel : ViewModel() {
    val isLoading: LiveData<Boolean> = MutableLiveData(true)
    suspend fun <T> showLoader(block: suspend () -> T): T {
        (isLoading as MutableLiveData).postValue(true)
        val result = block.invoke()
        isLoading.postValue(false)
        return result
    }
}