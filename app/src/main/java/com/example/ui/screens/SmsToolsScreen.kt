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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Sms
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.BankRegistry
import com.example.ui.theme.ExpenseRed
import com.example.ui.theme.IncomeGreen
import com.example.ui.viewmodel.DashboardUiState
import com.example.ui.viewmodel.SadeghViewModel
import com.example.util.PersianUtils

@Composable
fun SmsToolsScreen(
    uiState: DashboardUiState,
    viewModel: SadeghViewModel,
    hasSmsPermission: Boolean,
    onRequestSmsPermission: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showClearDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("sms_tools_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // SMS Inbox Status Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                )
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Sms,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "وضعیت خواندن پیامک‌ها",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = if (hasSmsPermission) "مجوز پیامک فعال است" else "نیاز به دریافت مجوز پیامک",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (hasSmsPermission) IncomeGreen else ExpenseRed
                                )
                            }
                        }

                        Icon(
                            imageVector = if (hasSmsPermission) Icons.Default.CheckCircle else Icons.Default.Lock,
                            contentDescription = null,
                            tint = if (hasSmsPermission) IncomeGreen else ExpenseRed
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "نرم‌افزار صادق پیامک‌های بانکی را پردازش کرده و مانده حساب‌ها را استخراج می‌کند. اطلاعات مالی شما فقط در حافظه محلی همین گوشی ذخیره می‌شود و به هیچ سروری ارسال نمی‌شود.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (!hasSmsPermission) {
                            Button(
                                onClick = onRequestSmsPermission,
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Security, contentDescription = null)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("درخواست مجوز")
                            }
                        }

                        Button(
                            onClick = {
                                if (!hasSmsPermission) onRequestSmsPermission()
                                viewModel.scanInboxSms()
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            if (uiState.isScanning) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Icon(imageVector = Icons.Default.Refresh, contentDescription = null)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("اسکن پیامک‌های گوشی")
                            }
                        }
                    }
                }
            }
        }

        // Live SMS Parser Playground
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                )
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "آزمایشگر هوشمند پیامک بانکی",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "متن هر پیامک بانکی را اینجا بنویسید یا جای‌گذاری کنید تا در لحظه تحلیل شود:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Quick sample buttons
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(vertical = 4.dp)
                    ) {
                        item {
                            FilledTonalButton(
                                onClick = {
                                    viewModel.setTestSmsInput(
                                        "بانک ملی ایران\nواریز به کارت 6037***4512\nمبلغ: 20,000,000 ریال\nمانده: 165,000,000 ریال\nواریز پایا"
                                    )
                                },
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("نمونه ملی", style = MaterialTheme.typography.labelSmall)
                            }
                        }
                        item {
                            FilledTonalButton(
                                onClick = {
                                    viewModel.setTestSmsInput(
                                        "بانک ملت\nبرداشت خرید پوز\nمبلغ: 3,500,000 ریال\nاز: 6104***8921\nموجودی: 45,200,000 ریال"
                                    )
                                },
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("نمونه ملت", style = MaterialTheme.typography.labelSmall)
                            }
                        }
                        item {
                            FilledTonalButton(
                                onClick = {
                                    viewModel.setTestSmsInput(
                                        "بلوبانک\nانتقال وجه انجام شد.\nمبلغ: 1,200,000 تومان\nمانده: 24,000,000 تومان\nکارت: 6219***1024"
                                    )
                                },
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("نمونه بلوبانک", style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = uiState.testSmsInput,
                        onValueChange = { viewModel.setTestSmsInput(it) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(110.dp)
                            .testTag("sms_test_textarea"),
                        placeholder = { Text("متن پیامک بانکی را اینجا بنویسید...") },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                        )
                    )

                    // Parsed Result Card
                    if (uiState.testSmsResult != null) {
                        val res = uiState.testSmsResult
                        val bank = BankRegistry.getBankByCode(res.bankCode)
                        Spacer(modifier = Modifier.height(12.dp))

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(10.dp)
                                                .clip(CircleShape)
                                                .background(bank.primaryColor)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = res.bankName,
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Text(
                                        text = res.type.persianLabel,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (res.type.isIncome) IncomeGreen else ExpenseRed,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("مبلغ تراکنش:", style = MaterialTheme.typography.bodySmall)
                                    Text(
                                        text = PersianUtils.formatMoney(
                                            res.amountToman,
                                            isToman = true,
                                            uiState.usePersianDigits
                                        ),
                                        fontWeight = FontWeight.Bold,
                                        color = if (res.type.isIncome) IncomeGreen else ExpenseRed
                                    )
                                }

                                if (res.balanceAfterToman != null) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("مانده نهایی حساب:", style = MaterialTheme.typography.bodySmall)
                                        Text(
                                            text = PersianUtils.formatMoney(
                                                res.balanceAfterToman,
                                                isToman = true,
                                                uiState.usePersianDigits
                                            ),
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                if (res.cardNumber != null) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("شماره کارت / حساب:", style = MaterialTheme.typography.bodySmall)
                                        Text(res.cardNumber, fontWeight = FontWeight.Medium)
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Button(
                                    onClick = { viewModel.saveTestSms() },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.primary
                                    )
                                ) {
                                    Icon(imageVector = Icons.Default.Check, contentDescription = null)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("ثبت و بروزرسانی مانده این حساب")
                                }
                            }
                        }
                    }
                }
            }
        }

        // Settings Section
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                )
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "تنظیمات نرم‌افزار صادق",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Currency Switcher (Toman vs Rial)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("واحد نمایش مبالغ", style = MaterialTheme.typography.bodyMedium)
                            Text(
                                text = if (uiState.isToman) "نمایش به تومان (پیش‌فرض)" else "نمایش به ریال",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(if (uiState.isToman) "تومان" else "ریال", style = MaterialTheme.typography.labelMedium)
                            Spacer(modifier = Modifier.width(8.dp))
                            Switch(
                                checked = uiState.isToman,
                                onCheckedChange = { viewModel.toggleCurrency() }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Persian Digits Toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("نمایش ارقام به خط فارسی", style = MaterialTheme.typography.bodyMedium)
                            Text(
                                text = if (uiState.usePersianDigits) "ارقام فارسی (۱۲۳)" else "ارقام انگلیسی (123)",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = uiState.usePersianDigits,
                            onCheckedChange = { viewModel.togglePersianDigits() }
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Data Management
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilledTonalButton(
                            onClick = { viewModel.loadSampleData() },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("بارگذاری نمونه‌ها")
                        }

                        OutlinedButton(
                            onClick = { showClearDialog = true },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = ExpenseRed)
                        ) {
                            Text("پاکسازی داده‌ها")
                        }
                    }
                }
            }
        }
    }

    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = { Text("پاکسازی کل اطلاعات") },
            text = { Text("آیا مطمئن هستید که می‌خواهید تمام حساب‌ها و تراکنش‌های ذخیره شده را پاک کنید؟") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.clearAllData()
                        showClearDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ExpenseRed)
                ) {
                    Text("بله، پاک شود")
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) {
                    Text("انصراف")
                }
            }
        )
    }
}
