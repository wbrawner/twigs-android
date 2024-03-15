package com.wbrawner.twigs.android.ui.category

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.isShiftPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.wbrawner.twigs.android.ui.base.TwigsApp
import com.wbrawner.twigs.android.ui.transaction.toDecimalString
import com.wbrawner.twigs.shared.Store
import com.wbrawner.twigs.shared.category.Category
import com.wbrawner.twigs.shared.category.CategoryAction

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun CategoryFormDialog(store: Store) {
    Dialog(
        onDismissRequest = { store.dispatch(CategoryAction.CancelEditCategory) },
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        CategoryForm(store)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryForm(store: Store) {
    val state by store.state.collectAsState()
    val category = remember {
        val defaultCategory = Category(
            title = "",
            amount = 0L,
            expense = true,
            budgetId = state.selectedBudget!!,
        )
        if (state.selectedCategory.isNullOrBlank()) {
            defaultCategory
        } else {
            state.categories?.first { it.id == state.selectedCategory } ?: defaultCategory
        }
    }
    val (title, setTitle) = remember { mutableStateOf(category.title) }
    val (description, setDescription) = remember { mutableStateOf(category.description ?: "") }
    val (amount, setAmount) = remember { mutableStateOf(category.amount.toDecimalString()) }
    val (expense, setExpense) = remember { mutableStateOf(category.expense) }
    val (archived, setArchived) = remember { mutableStateOf(category.archived) }
    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = { store.dispatch(CategoryAction.CancelEditCategory) }) {
                        Icon(Icons.Default.Close, "Cancel")
                    }
                },
                title = {
                    Text(if (category.id.isNullOrBlank()) "New Category" else "Edit Category")
                }
            )
        }
    ) {
        CategoryForm(
            modifier = Modifier.padding(it),
            title = title,
            setTitle = setTitle,
            description = description,
            setDescription = setDescription,
            amount = amount,
            setAmount = setAmount,
            expense = expense,
            setExpense = setExpense,
            archived = archived,
            setArchived = setArchived
        ) {
            store.dispatch(
                category.id?.let { id ->
                    CategoryAction.UpdateCategory(
                        id = id,
                        title = title,
                        description = description,
                        amount = (amount.toDouble() * 100).toLong(),
                        expense = expense,
                        archived = archived
                    )
                } ?: CategoryAction.CreateCategory(
                    title = title,
                    description = description,
                    amount = (amount.toDouble() * 100).toLong(),
                    expense = expense,
                    archived = archived
                )
            )
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun CategoryForm(
    modifier: Modifier,
    title: String,
    setTitle: (String) -> Unit,
    description: String,
    setDescription: (String) -> Unit,
    amount: String,
    setAmount: (String) -> Unit,
    expense: Boolean,
    setExpense: (Boolean) -> Unit,
    archived: Boolean,
    setArchived: (Boolean) -> Unit,
    save: () -> Unit
) {
    val scrollState = rememberScrollState()
    val (titleInput, descriptionInput, amountInput) = FocusRequester.createRefs()
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = spacedBy(8.dp, Alignment.Top),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
//        if (error.isNotBlank()) {
//            Text(text = error, color = Color.Red)
//        }
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(titleInput)
                .onPreviewKeyEvent {
                    if (it.key == Key.Tab && !it.isShiftPressed) {
                        descriptionInput.requestFocus()
                        true
                    } else {
                        false
                    }
                },
            value = title,
            onValueChange = setTitle,
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Text,
                capitalization = KeyboardCapitalization.Words,
                imeAction = ImeAction.Next
            ),
            label = { Text("Title") },
            keyboardActions = KeyboardActions(onNext = {
                descriptionInput.requestFocus()
            }),
            maxLines = 1
        )
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(descriptionInput)
                .onPreviewKeyEvent {
                    if (it.key == Key.Tab) {
                        if (it.isShiftPressed) {
                            titleInput.requestFocus()
                        } else {
                            amountInput.requestFocus()
                        }
                        true
                    } else {
                        false
                    }
                },
            value = description,
            onValueChange = setDescription,
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Text,
                capitalization = KeyboardCapitalization.Sentences,
                imeAction = ImeAction.Next
            ),
            label = { Text("Description") },
            keyboardActions = KeyboardActions(onNext = {
                amountInput.requestFocus()
            }),
        )
        val keyboardController = LocalSoftwareKeyboardController.current
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(amountInput)
                .onPreviewKeyEvent {
                    if (it.key == Key.Tab && it.isShiftPressed) {
                        descriptionInput.requestFocus()
                        true
                    } else {
                        false
                    }
                },
            value = amount,
            onValueChange = setAmount,
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Decimal,
                imeAction = ImeAction.Next
            ),
            label = { Text("Amount") },
            keyboardActions = KeyboardActions(onNext = {
                keyboardController?.hide()
            }),
        )
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = spacedBy(8.dp)) {
            Row(
                modifier = Modifier.clickable {
                    setExpense(true)
                },
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(selected = expense, onClick = { setExpense(true) })
                Text(text = "Expense")
            }
            Row(
                modifier = Modifier.clickable {
                    setExpense(false)
                },
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(selected = !expense, onClick = { setExpense(false) })
                Text(text = "Income")
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { setArchived(!archived) },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(checked = archived, onCheckedChange = { setArchived(!archived) })
            Text("Archived")
        }
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = save
        ) {
            Text("Save")
        }
    }
}

@Composable
@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_NO)
@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
fun CategoryForm_Preview() {
    TwigsApp {
        CategoryForm(store = Store(reducers = emptyList()))
    }
}