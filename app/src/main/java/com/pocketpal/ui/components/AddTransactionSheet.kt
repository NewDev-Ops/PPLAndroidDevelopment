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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.pocketpal.data.local.AccountEntity
import com.pocketpal.data.local.CategoryEntity
import com.pocketpal.ui.screens.home.AddTransactionState
import com.pocketpal.ui.theme.PocketPalShapes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionSheet(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    state: AddTransactionState,
    expenseCategories: List<CategoryEntity>,
    incomeCategories: List<CategoryEntity>,
    accounts: List<AccountEntity>,
    onAmountChange: (String) -> Unit,
    onCategoryChange: (String) -> Unit,
    onTypeChange: (String) -> Unit,
    onNoteChange: (String) -> Unit,
    onDateChange: (String) -> Unit,
    onAccountChange: (String) -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (isVisible) {
        ModalBottomSheet(
            onDismissRequest = onDismiss,
            modifier = modifier,
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surfaceContainerLow,
                shape = PocketPalShapes.extraLarge
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                        .padding(bottom = 32.dp)
                ) {
                    Text(
                        text = "Add Transaction",
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier.padding(bottom = 20.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        FilterChip(
                            selected = state.type == "EXPENSE",
                            onClick = { onTypeChange("EXPENSE") },
                            label = { Text("Expense", style = MaterialTheme.typography.labelLarge) },
                            modifier = Modifier.weight(1f),
                            shape = PocketPalShapes.extraLarge,
                            leadingIcon = if (state.type == "EXPENSE") {
                                { Icon(Icons.Default.Check, contentDescription = null, Modifier.size(18.dp)) }
                            } else null
                        )
                        FilterChip(
                            selected = state.type == "INCOME",
                            onClick = { onTypeChange("INCOME") },
                            label = { Text("Income", style = MaterialTheme.typography.labelLarge) },
                            modifier = Modifier.weight(1f),
                            shape = PocketPalShapes.extraLarge,
                            leadingIcon = if (state.type == "INCOME") {
                                { Icon(Icons.Default.Check, contentDescription = null, Modifier.size(18.dp)) }
                            } else null
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    OutlinedTextField(
                        value = state.amount,
                        onValueChange = { newValue ->
                            if (newValue.isEmpty() || newValue.matches(Regex("^\\d*\\.?\\d{0,2}$"))) {
                                onAmountChange(newValue)
                            }
                        },
                        label = { Text("Amount", style = MaterialTheme.typography.bodyLarge) },
                        textStyle = MaterialTheme.typography.headlineMedium,
                        leadingIcon = { Text("$", style = MaterialTheme.typography.headlineMedium) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = PocketPalShapes.extraLarge,
                        isError = state.errorMessage != null && state.amount.isBlank()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Account",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    if (accounts.isNotEmpty()) {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.height(100.dp)
                        ) {
                            items(accounts) { account ->
                                AccountChip(
                                    account = account,
                                    isSelected = state.accountId == account.id,
                                    onClick = { onAccountChange(account.id) }
                                )
                            }
                        }
                    } else {
                        Text(
                            text = "No accounts available",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Category",
                        style = MaterialTheme.typography.titleMedium,
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

                    OutlinedTextField(
                        value = state.note,
                        onValueChange = onNoteChange,
                        label = { Text("Note (optional)", style = MaterialTheme.typography.bodyLarge) },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 2,
                        shape = PocketPalShapes.extraLarge
                    )

                    state.errorMessage?.let { error ->
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = error,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = onSave,
                        enabled = !state.isLoading && state.amount.isNotBlank() && state.category.isNotBlank() && state.accountId.isNotBlank(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = PocketPalShapes.extraLarge
                    ) {
                        if (state.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("Save Transaction", style = MaterialTheme.typography.titleMedium)
                        }
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
        modifier = Modifier.fillMaxWidth(),
        shape = PocketPalShapes.extraLarge
    )
}

@Composable
private fun AccountChip(
    account: AccountEntity,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    FilterChip(
        selected = isSelected,
        onClick = onClick,
        label = {
            Text(
                text = account.name,
                style = MaterialTheme.typography.labelMedium,
                maxLines = 1
            )
        },
        modifier = Modifier.fillMaxWidth(),
        shape = PocketPalShapes.extraLarge
    )
}