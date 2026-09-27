package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AddComment
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Sms
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.BankAccountEntity
import com.example.data.entity.TransactionEntity
import com.example.ui.components.BankCardItem
import com.example.ui.components.StatCard
import com.example.ui.components.TransactionItem
import com.example.ui.theme.AccentGold
import com.example.ui.theme.ExpenseRed
import com.example.ui.theme.IncomeGreen
import com.example.ui.viewmodel.DashboardUiState
import com.example.ui.viewmodel.MainTab
import com.example.ui.viewmodel.SadeghViewModel
import com.example.util.PersianUtils

@Composable
fun DashboardScreen(
    uiState: DashboardUiState,
    viewModel: SadeghViewModel,
    onNavigateTab: (MainTab) -> Unit,
    onAccountClick: (BankAccountEntity) -> Unit,
    onTransactionClick: (TransactionEntity) -> Unit,
    onRequestSmsPermission: () -> Unit,
    modifier: Modifier = Modifier
) {
    val totalDisplay = if (uiState.isToman) uiState.totalBalanceToman else (uiState.totalBalanceToman * 10)
    val totalIncomeDisplay = if (uiState.isToman) uiState.totalIncomeToman else (uiState.totalIncomeToman * 10)
    val totalExpenseDisplay = if (uiState.isToman) uiState.totalExpenseToman else (uiState.totalExpenseToman * 10)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("dashboard_screen"),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        // Hero Total Balance Banner
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .testTag("total_balance_card"),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFF0D3B36),
                                    Color(0xFF0F2B48),
                                    Color(0xFF081220)
                                )
                            )
                        )
                        .padding(20.dp)
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AccountBalanceWallet,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "مجموع موجودی همه بانک‌ها",
                                        style = MaterialTheme.typography.titleSmall,
                                        color = Color.White.copy(alpha = 0.8f)
                                    )
                                    Text(
                                        text = "${if (uiState.usePersianDigits) PersianUtils.toPersianDigits(uiState.accounts.size.toLong()) else uiState.accounts.size} کارت و حساب فعال",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }

                            // Quick currency indicator
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color.White.copy(alpha = 0.15f))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = if (uiState.isToman) "تومان" else "ریال",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        Text(
                            text = PersianUtils.formatMoney(
                                totalDisplay,
                                uiState.isToman,
                                uiState.usePersianDigits
                            ),
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            letterSpacing = 0.5.sp
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Mini bottom action row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    onRequestSmsPermission()
                                    viewModel.scanInboxSms()
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("scan_sms_button"),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                if (uiState.isScanning) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(16.dp),
                                        color = Color.White,
                                        strokeWidth = 2.dp
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.Refresh,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("اسکن پیامک‌ها", fontWeight = FontWeight.Bold)
                                }
                            }

                            FilledTonalButton(
                                onClick = { viewModel.loadSampleData() },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("sample_sms_button"),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp),
                                    tint = AccentGold
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("پیامک‌های نمونه")
                            }
                        }
                    }
                }
            }
        }

        // Status banner if present
        if (!uiState.statusMessage.isNullOrBlank()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = uiState.statusMessage,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.weight(1f)
                        )
                        TextButton(onClick = { viewModel.clearStatusMessage() }) {
                            Text("متوجه شدم", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }
        }

        // Quick Stats Row (Income & Expense)
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    title = "کل واریزی‌ها",
                    value = PersianUtils.formatMoney(
                        totalIncomeDisplay,
                        uiState.isToman,
                        uiState.usePersianDigits
                    ),
                    icon = Icons.Default.ArrowDownward,
                    accentColor = IncomeGreen,
                    modifier = Modifier.weight(1f)
                )

                StatCard(
                    title = "کل برداشت‌ها",
                    value = PersianUtils.formatMoney(
                        totalExpenseDisplay,
                        uiState.isToman,
                        uiState.usePersianDigits
                    ),
                    icon = Icons.Default.ArrowUpward,
                    accentColor = ExpenseRed,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Bank Cards Carousel Section Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "کارت‌ها و حساب‌های بانکی",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                TextButton(onClick = { onNavigateTab(MainTab.ACCOUNTS) }) {
                    Text("مشاهده همه (${if (uiState.usePersianDigits) PersianUtils.toPersianDigits(uiState.accounts.size.toLong()) else uiState.accounts.size})")
                }
            }
        }

        // Bank Cards Carousel
        item {
            if (uiState.accounts.isEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.CreditCard,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "هنوز کارتی ثبت نشده است",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(onClick = { viewModel.loadSampleData() }) {
                            Text("بارگذاری نمونه بانک‌های ایران")
                        }
                    }
                }
            } else {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(uiState.accounts, key = { it.id }) { account ->
                        BankCardItem(
                            account = account,
                            isToman = uiState.isToman,
                            usePersianDigits = uiState.usePersianDigits,
                            onClick = { onAccountClick(account) }
                        )
                    }
                }
            }
        }

        // Recent Transactions Section Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 20.dp, bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "آخرین تراکنش‌های خوانده شده از پیامک",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                TextButton(onClick = { onNavigateTab(MainTab.TRANSACTIONS) }) {
                    Text("همه تراکنش‌ها")
                }
            }
        }

        // Recent Transactions Items
        val recentTxs = uiState.transactions.take(6)
        if (recentTxs.isEmpty()) {
            item {
                Text(
                    text = "هیچ تراکنشی یافت نشد. دکمه اسکن یا پیامک‌های نمونه را بزنید.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                )
            }
        } else {
            items(recentTxs, key = { it.id }) { tx ->
                Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
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
