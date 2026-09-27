package com.example.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Balance
import androidx.compose.material.icons.filled.Equalizer
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.BankDistributionBar
import com.example.ui.components.CashflowComparisonCard
import com.example.ui.components.StatCard
import com.example.ui.theme.AccentGold
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.ExpenseRed
import com.example.ui.theme.IncomeGreen
import com.example.ui.viewmodel.DashboardUiState
import com.example.util.PersianUtils

@Composable
fun AnalyticsScreen(
    uiState: DashboardUiState,
    modifier: Modifier = Modifier
) {
    val netSavings = uiState.totalIncomeToman - uiState.totalExpenseToman
    val netDisplay = if (uiState.isToman) netSavings else (netSavings * 10)
    val isNetPositive = netSavings >= 0

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("analytics_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "تحلیل و آمار مالی پیامک‌ها",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        // Cash flow bar comparison
        item {
            CashflowComparisonCard(
                totalIncomeToman = uiState.totalIncomeToman,
                totalExpenseToman = uiState.totalExpenseToman,
                isToman = uiState.isToman,
                usePersianDigits = uiState.usePersianDigits
            )
        }

        // Net balance stat
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    title = "تراز مالی کل",
                    value = PersianUtils.formatMoney(
                        netDisplay,
                        uiState.isToman,
                        uiState.usePersianDigits
                    ),
                    subtitle = if (isNetPositive) "مازاد درآمد نسبت به مخارج" else "کسری یا اضافه برداشت",
                    icon = Icons.Default.Savings,
                    accentColor = if (isNetPositive) IncomeGreen else ExpenseRed,
                    modifier = Modifier.weight(1f)
                )

                StatCard(
                    title = "تعداد کل تراکنش‌ها",
                    value = "${if (uiState.usePersianDigits) PersianUtils.toPersianDigits(uiState.transactions.size.toLong()) else uiState.transactions.size} مورد",
                    subtitle = "${if (uiState.usePersianDigits) PersianUtils.toPersianDigits(uiState.accounts.size.toLong()) else uiState.accounts.size} کارت بانکی",
                    icon = Icons.Default.Receipt,
                    accentColor = AccentGold,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Distribution among banks
        item {
            BankDistributionBar(
                accounts = uiState.accounts,
                totalBalanceToman = uiState.totalBalanceToman,
                usePersianDigits = uiState.usePersianDigits
            )
        }

        // Tips Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row {
                        Text(
                            text = "💡 راهنمای نگهداری و دقت مانده حساب‌ها:",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "نرم‌افزار «صادق» با هر پیامک جدید واریز یا برداشت، مانده اعلام شده توسط بانک را با دقت استخراج و جایگزین می‌کند. در صورتی که بانکی مانده نهایی را در متن پیامک ارسال نکند، نرم‌افزار به صورت هوشمند مبلغ را از مانده قبلی کسر یا به آن اضافه می‌کند.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 22.sp
                    )
                }
            }
        }
    }
}
