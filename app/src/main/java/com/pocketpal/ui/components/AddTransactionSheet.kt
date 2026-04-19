package com.pocketpal.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.pocketpal.data.local.CategoryEntity
import com.pocketpal.ui.screens.home.AddTransactionState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionSheet(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    state: AddTransactionState,
    expenseCategories: List<CategoryEntity>,
    incomeCategories: List<CategoryEntity>,
    onAmountChange: (String) -> Unit,
    onCategoryChange: (String) -> Unit,
    onTypeChange: (String) -> Unit,
    onNoteChange: (String) -> Unit,
    onDateChange: (String) -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (isVisible) {
        ModalBottomSheet(
            onDismissRequest = onDismiss,
            modifier = modifier,
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 32.dp)
            ) {
                // Header
                Text(
                    text = "Add Transaction",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Transaction Type Tabs
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = state.type == "EXPENSE",
                        onClick = { onTypeChange("EXPENSE") },
                        label = { Text("Expense") },
                        modifier = Modifier.weight(1f),
                        leadingIcon = if (state.type == "EXPENSE") {
                            { Icon(Icons.Default.Check, contentDescription = null, Modifier.size(18.dp)) }
                        } else null
                    )
                    FilterChip(
                        selected = state.type == "INCOME",
                        onClick = { onTypeChange("INCOME") },
                        label = { Text("Income") },
                        modifier = Modifier.weight(1f),
                        leadingIcon = if (state.type == "INCOME") {
                            { Icon(Icons.Default.Check, contentDescription = null, Modifier.size(18.dp)) }
                        } else null
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Amount Input
                OutlinedTextField(
                    value = state.amount,
                    onValueChange = { newValue ->
                        // Allow only valid decimal numbers
                        if (newValue.isEmpty() || newValue.matches(Regex("^\\d*\\.?\\d{0,2}$"))) {
                            onAmountChange(newValue)
                        }
                    },
                    label = { Text("Amount") },
                    leadingIcon = { Text("$") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    isError = state.errorMessage != null && state.amount.isBlank()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Category Selection
                Text(
                    text = "Category",
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                val selectedCategories = if (state.type == "EXPENSE") expenseCategories else incomeCategories
                
                if (selectedCategories.isNotEmpty()) {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(4),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.height(160.dp)
                    ) {
                        items(selectedCategories) { category ->
                            CategoryChip(
                                category = category,
                                isSelected = state.category == category.name,
                                onClick = { onCategoryChange(category.name) }
                            )
                        }
                    }
                } else {
                    Text(
                        text = "No categories available",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Note Input
                OutlinedTextField(
                    value = state.note,
                    onValueChange = onNoteChange,
                    label = { Text("Note (optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 2
                )

                // Error Message
                state.errorMessage?.let { error ->
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Save Button
                Button(
                    onClick = onSave,
                    enabled = !state.isLoading && state.amount.isNotBlank() && state.category.isNotBlank(),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Save Transaction")
                    }
                }
            }
        }
    }
}

@Composable
private fun CategoryChip(
    category: CategoryEntity,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    FilterChip(
        selected = isSelected,
        onClick = onClick,
        label = {
            Text(
                text = category.name,
                style = MaterialTheme.typography.labelSmall,
                maxLines = 1
            )
        },
        modifier = Modifier.fillMaxWidth()
    )
}