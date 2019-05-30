package com.wbrawner.budget.ui.categories

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NavUtils
import androidx.core.app.TaskStackBuilder
import androidx.lifecycle.ViewModelProviders
import com.wbrawner.budget.AllowanceApplication
import com.wbrawner.budget.R
import com.wbrawner.budget.common.account.Account
import com.wbrawner.budget.common.category.Category
import com.wbrawner.budget.di.BudgetViewModelFactory
import com.wbrawner.budget.ui.autoDispose
import com.wbrawner.budget.ui.fromBackgroundToMain
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_add_edit_category.*
import javax.inject.Inject

class AddEditCategoryActivity : AppCompatActivity() {
    private val disposables = CompositeDisposable()
    private lateinit var account: Account
    @Inject
    lateinit var viewModelFactory: BudgetViewModelFactory
    lateinit var viewModel: CategoryViewModel
    var id: Long? = null
    var menu: Menu? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_category)
        setSupportActionBar(action_bar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (application as AllowanceApplication).appComponent.inject(this)
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(CategoryViewModel::class.java)
        viewModel.getAccount(intent!!.extras!!.getLong(EXTRA_ACCOUNT_ID))
                .fromBackgroundToMain()
                .subscribe { account, err ->
                    if (err != null) {
                        finish()
                        // TODO: Hide a progress spinner and show error message
                        return@subscribe
                    }
                    this@AddEditCategoryActivity.account = account
                    loadCategory()
                }
                .autoDispose(disposables)
    }

    private fun loadCategory() {
        if (intent?.hasExtra(EXTRA_CATEGORY_ID) == false) {
            setTitle(R.string.title_add_category)
            return
        }
        viewModel.getCategory(intent!!.extras!!.getLong(EXTRA_CATEGORY_ID))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { category, err ->
                    if (err != null) {
                        menu?.findItem(R.id.action_delete)?.isVisible = false
                        return@subscribe
                    }
                    id = category.id
                    setTitle(R.string.title_edit_category)
                    menu?.findItem(R.id.action_delete)?.isVisible = true
                    edit_category_name.setText(category.title)
                    edit_category_amount.setText(String.format("%.02f", category.amount / 100.0f))
                }
                .autoDispose(disposables)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_add_edit, menu)
        if (id != null) {
            menu?.findItem(R.id.action_delete)?.isVisible = true
        }
        this.menu = menu
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                val upIntent: Intent? = NavUtils.getParentActivityIntent(this)

                when {
                    upIntent == null -> throw IllegalStateException("No Parent Activity Intent")
                    NavUtils.shouldUpRecreateTask(this, upIntent) || isTaskRoot -> {
                        TaskStackBuilder.create(this)
                                .addNextIntentWithParentStack(upIntent)
                                .startActivities()
                    }
                    else -> {
                        NavUtils.navigateUpTo(this, upIntent)
                    }
                }
            }
            R.id.action_save -> {
                if (!validateFields()) return true
                viewModel.saveCategory(Category(
                        id = id,
                        title = edit_category_name.text.toString(),
                        amount = edit_category_amount.rawValue,
                        accountId = account.id!!
                ))
                        .fromBackgroundToMain()
                        .subscribe { _, err ->
                            finish()
                        }
            }
            R.id.action_delete -> {
                viewModel.deleteCategoryById(this@AddEditCategoryActivity.id!!)
                        .fromBackgroundToMain()
                        .subscribe { _, _ ->
                            finish()
                        }
            }
        }
        return true
    }


    private fun validateFields(): Boolean {
        var errors = false
        if (edit_category_name.text?.isEmpty() == true) {
            edit_category_name.error = getString(R.string.required_field_name)
            errors = true
        }

        if (edit_category_amount.text.isEmpty()) {
            edit_category_amount.error = getString(R.string.required_field_amount)
            errors = true
        }

        return !errors
    }

    override fun onDestroy() {
        disposables.dispose()
        super.onDestroy()
    }

    companion object {
        const val EXTRA_ACCOUNT_ID = "EXTRA_ACCOUNT_ID"
        const val EXTRA_CATEGORY_ID = "EXTRA_CATEGORY_ID"
    }
}
