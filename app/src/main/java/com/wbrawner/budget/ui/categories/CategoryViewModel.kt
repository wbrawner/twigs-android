package com.wbrawner.budget.ui.categories

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import com.wbrawner.budget.AllowanceApplication
import com.wbrawner.budget.data.model.Category
import com.wbrawner.budget.data.CategoryRepository

class CategoryViewModel(application: Application): AndroidViewModel(application) {
    private val categoryRepo = CategoryRepository((application as AllowanceApplication).categoryDao)

    fun getCategory(id: Int): LiveData<Category> = categoryRepo.getCategory(id)

    fun getCategories(): LiveData<List<Category>> = categoryRepo.getCategories()

    fun saveCategory(category: Category) = categoryRepo.save(category)

    fun deleteCategory(category: Category) = categoryRepo.delete(category)

    fun deleteCategoryById(id: Int) = categoryRepo.deleteById(id)

    fun getCurrentBalance(id: Int) = categoryRepo.getCurrentBalance(id)

    fun getTransactions(id: Int) = categoryRepo.getTransactions(id)
}
