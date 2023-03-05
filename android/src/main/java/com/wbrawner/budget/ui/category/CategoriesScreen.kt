package com.wbrawner.budget.ui.category

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.wbrawner.budget.ui.TwigsScaffold
import com.wbrawner.budget.ui.base.TwigsApp
import com.wbrawner.budget.ui.transaction.toCurrencyString
import com.wbrawner.twigs.shared.Store
import com.wbrawner.twigs.shared.category.Category
import com.wbrawner.twigs.shared.category.CategoryAction
import com.wbrawner.twigs.shared.category.groupByType
import com.wbrawner.twigs.shared.recurringtransaction.capitalizedName
import java.util.*
import kotlin.math.abs
import kotlin.math.max

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriesScreen(store: Store) {
    val state by store.state.collectAsState()
    val budget = state.selectedBudget?.let { id -> state.budgets?.first { it.id == id } }
    TwigsScaffold(
        store = store,
        title = budget?.name ?: "Select a Budget",
        onClickFab = {
            store.dispatch(CategoryAction.NewCategoryClicked)
        }
    ) {
        state.categories?.let { categories ->
            val categoryGroups = categories.groupByType()
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(it)
                    .verticalScroll(rememberScrollState())
                    .padding(start = 8.dp, end = 8.dp, bottom = 8.dp)
            ) {
                categoryGroups.toSortedMap().forEach { (group, c) ->
                    Text(
                        modifier = Modifier.padding(8.dp),
                        text = group.capitalizedName,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Card {
                        c.forEach { category ->
                            CategoryListItem(category, state.categoryBalances?.get(category.id!!)) {
                                store.dispatch(CategoryAction.SelectCategory(category.id))
                            }
                        }
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(category.title, style = MaterialTheme.typography.bodyLarge)
                balance?.let {
                    Text(
                        (category.amount - abs(it)).toCurrencyString() + " remaining",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            balance?.let {
                val denominator = remember { max(abs(it), abs(category.amount)).toFloat() }
                val progress =
                    remember { if (denominator == 0f) 0f else abs(it).toFloat() / denominator }
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(4.dp)),
                    progress = progress,
                    color = if (category.expense) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                    trackColor = Color.LightGray
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