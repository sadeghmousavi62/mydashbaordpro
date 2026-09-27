package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.entity.TransactionEntity
import com.example.model.BankRegistry
import com.example.model.TransactionType
import com.example.ui.components.TransactionItem
import com.example.ui.viewmodel.DashboardUiState
import com.example.ui.viewmodel.SadeghViewModel
import com.example.util.PersianUtils

@Composable
fun TransactionsScreen(
    uiState: DashboardUiState,
    viewModel: SadeghViewModel,
    onTransactionClick: (TransactionEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    // Filter transactions
    val filteredTransactions = uiState.transactions.filter { tx ->
        val matchesQuery = uiState.searchQuery.isBlank() ||
                tx.bankName.contains(uiState.searchQuery, ignoreCase = true) ||
                (tx.description ?: "").contains(uiState.searchQuery, ignoreCase = true) ||
                (tx.cardNumber ?: "").contains(uiState.searchQuery, ignoreCase = true) ||
                tx.amountToman.toString().contains(PersianUtils.normalizeDigits(uiState.searchQuery))

        val matchesBank = uiState.bankFilter == null || tx.bankCode == uiState.bankFilter

        val matchesType = uiState.typeFilter == null || try {
            TransactionType.valueOf(tx.type) == uiState.typeFilter
        } catch (e: Exception) {
            false
        }

        matchesQuery && matchesBank && matchesType
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("transactions_screen")
    ) {
        // Search Bar
        OutlinedTextField(
            value = uiState.searchQuery,
            onValueChange = { viewModel.setSearchQuery(it) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .testTag("transaction_search_input"),
            placeholder = { Text("جستجو بر اساس بانک، مبلغ یا شرح تراکنش...") },
            leadingIcon = {
                Icon(imageVector = Icons.Default.Search, contentDescription = null)
            },
            trailingIcon = {
                if (uiState.searchQuery.isNotBlank()) {
                    IconButton(onClick = { viewModel.setSearchQuery("") }) {
                        Icon(imageVector = Icons.Default.Clear, contentDescription = "پاک کردن")
                    }
                }
            },
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
            ),
            singleLine = true
        )

        // Filter chips (All / Deposits / Withdrawals)
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                FilterChip(
                    selected = uiState.typeFilter == null,
                    onClick = { viewModel.setTypeFilter(null) },
                    label = { Text("همه") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
            }
            item {
                FilterChip(
                    selected = uiState.typeFilter == TransactionType.DEPOSIT,
                    onClick = {
                        viewModel.setTypeFilter(
                            if (uiState.typeFilter == TransactionType.DEPOSIT) null else TransactionType.DEPOSIT
                        )
                    },
                    label = { Text("واریزی‌ها (+)") }
                )
            }
            item {
                FilterChip(
                    selected = uiState.typeFilter == TransactionType.WITHDRAWAL,
                    onClick = {
                        viewModel.setTypeFilter(
                            if (uiState.typeFilter == TransactionType.WITHDRAWAL) null else TransactionType.WITHDRAWAL
                        )
                    },
                    label = { Text("برداشت‌ها (-)") }
                )
            }
            item {
                FilterChip(
                    selected = uiState.typeFilter == TransactionType.PURCHASE,
                    onClick = {
                        viewModel.setTypeFilter(
                            if (uiState.typeFilter == TransactionType.PURCHASE) null else TransactionType.PURCHASE
                        )
                    },
                    label = { Text("خریدهای پوز") }
                )
            }
        }

        // Bank selection filter chips
        val presentBankCodes = uiState.transactions.map { it.bankCode }.distinct()
        if (presentBankCodes.size > 1) {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 2.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    FilterChip(
                        selected = uiState.bankFilter == null,
                        onClick = { viewModel.setBankFilter(null) },
                        label = { Text("همه بانک‌ها") }
                    )
                }
                items(presentBankCodes) { code ->
                    val bank = BankRegistry.getBankByCode(code)
                    FilterChip(
                        selected = uiState.bankFilter == code,
                        onClick = {
                            viewModel.setBankFilter(if (uiState.bankFilter == code) null else code)
                        },
                        label = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(bank.primaryColor)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(bank.persianName)
                            }
                        }
                    )
                }
            }
        }

        // Transactions List
        if (filteredTransactions.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.ReceiptLong,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "تراکنشی با این مشخصات یافت نشد",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    val count = filteredTransactions.size
                    Text(
                        text = "${if (uiState.usePersianDigits) PersianUtils.toPersianDigits(count.toLong()) else count} تراکنش یافت شد",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }

                items(filteredTransactions, key = { it.id }) { tx ->
                    TransactionItem(
                        transaction = tx,
                        isToman = uiState.isToman,
                        usePersianDigits = uiState.usePersianDigits,
                        onClick = { onTransactionClick(tx) }
                    )
                }
            }
        }
    }
}
