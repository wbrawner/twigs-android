package com.wbrawner.budget.categories

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.wbrawner.budget.R
import com.wbrawner.budget.data.model.Category
import kotlinx.android.synthetic.main.activity_add_edit_category.*

class AddEditCategoryActivity : AppCompatActivity() {
    lateinit var viewModel: CategoryViewModel
    var id: Int? = null
    var menu: Menu? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_category)
        setSupportActionBar(action_bar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        viewModel = ViewModelProviders.of(this).get(CategoryViewModel::class.java)

        if (intent?.hasExtra(EXTRA_CATEGORY_ID) == false) {
            setTitle(R.string.title_add_category)
            return
        }

        viewModel.getCategory(intent!!.extras!!.getInt(EXTRA_CATEGORY_ID))
                .observe(this, Observer<Category> { category ->
                    if (category == null) {
                        menu?.findItem(R.id.action_delete)?.isVisible = false
                        return@Observer
                    }
                    id = category.id
                    setTitle(R.string.title_edit_category)
                    menu?.findItem(R.id.action_delete)?.isVisible = true
                    edit_category_name.setText(category.name)
                    edit_category_amount.setText(String.format("%.02f", category.amount))
                })
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_add_edit, menu)
        if (id != null) {
            menu?.findItem(R.id.action_delete)?.isVisible = true
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> onBackPressed()
            R.id.action_save -> {
                if (!validateFields()) return true
                viewModel.saveCategory(Category(
                        id = id,
                        name = edit_category_name.text.toString(),
                        amount = edit_category_amount.text.toString().toDouble(),
                        color = Color.parseColor("#FF0000"),
                        repeat = "never"
                ))
                finish()
            }
            R.id.action_delete -> {
                viewModel.deleteCategoryById(this@AddEditCategoryActivity.id!!)
                finish()
            }
        }
        return true
    }


    private fun validateFields(): Boolean {
        var errors = false
        if (edit_category_name.text.isEmpty()) {
            edit_category_name.error = getString(R.string.required_field_name)
            errors = true
        }

        if (edit_category_amount.text.isEmpty()) {
            edit_category_amount.error = getString(R.string.required_field_amount)
            errors = true
        }

        return !errors
    }


    companion object {
        const val EXTRA_CATEGORY_ID = "EXTRA_CATEGORY_ID"
    }
}
