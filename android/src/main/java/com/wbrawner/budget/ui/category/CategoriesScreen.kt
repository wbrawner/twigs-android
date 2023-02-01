package com.wbrawner.budget.ui.category

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.wbrawner.budget.ui.TwigsScaffold
import com.wbrawner.budget.ui.base.TwigsApp
import com.wbrawner.twigs.shared.Store
import com.wbrawner.twigs.shared.category.Category
import com.wbrawner.twigs.shared.category.CategoryAction
import java.util.*
import kotlin.math.abs
import kotlin.math.max

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriesScreen(store: Store) {
    val scrollState = rememberLazyListState()
    TwigsScaffold(
        store = store,
        title = "Categories",
        onClickFab = {
            store.dispatch(CategoryAction.NewCategoryClicked)
        }
    ) {
        val state by store.state.collectAsState()
        state.categories?.let { categories ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it),
                state = scrollState
            ) {
                items(categories, key = { c -> c.id!! }) { category ->
                    CategoryListItem(category, state.categoryBalances?.get(category.id!!)) {
                        store.dispatch(CategoryAction.SelectCategory(category.id))
                    }
                }
            }
        } ?: Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        if (state.editingCategory) {
            CategoryFormDialog(store = store)
        }
    }
}

@Composable
fun CategoryListItem(category: Category, balance: Long?, onClick: (Category) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(category) }
            .padding(8.dp)
            .heightIn(min = 56.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = spacedBy(4.dp)
        ) {
            Text(category.title, style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(8.dp))
            balance?.let {
                val denominator = remember { max(abs(it), abs(category.amount)).toFloat() }
                val progress =
                    remember { if (denominator == 0f) 0f else abs(it).toFloat() / denominator }
                Log.d(
                    "Twigs",
                    "Category ${category.title} amount: $denominator balance: $it progress: $progress"
                )
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth(),
                    progress = progress,
                    color = if (category.expense) Color.Red else Color.Green
                )
            } ?: LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }
    }
}

@Composable
@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_NO)
@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
fun CategoryListItem_Preview() {
    TwigsApp {
        CategoryListItem(
            category = Category(
                title = "Groceries",
                amount = 150000,
                budgetId = "budgetId",
                expense = true,
            ),
            balance = null
        ) {}
    }
}