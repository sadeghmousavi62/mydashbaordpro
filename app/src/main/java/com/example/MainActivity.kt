package com.example

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.QueryStats
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Sms
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.QueryStats
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.Sms
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.AccountDetailDialog
import com.example.ui.components.TransactionDetailDialog
import com.example.ui.screens.AccountsScreen
import com.example.ui.screens.AnalyticsScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.SmsToolsScreen
import com.example.ui.screens.TransactionsScreen
import com.example.ui.theme.SadeghTheme
import com.example.ui.viewmodel.MainTab
import com.example.ui.viewmodel.SadeghViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState

class MainActivity : ComponentActivity() {

    private val viewModel: SadeghViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            SadeghTheme(darkTheme = true) {
                // Persian is an RTL language
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    val context = LocalContext.current
                    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

                    // Accompanist Multiple Permissions State for SMS reading and receiving
                    val smsPermissionsState = rememberMultiplePermissionsState(
                        permissions = listOf(
                            Manifest.permission.READ_SMS,
                            Manifest.permission.RECEIVE_SMS
                        )
                    )

                    var showRationaleDialog by remember { mutableStateOf(false) }
                    var showSettingsDialog by remember { mutableStateOf(false) }

                    fun requestSmsPermissions() {
                        when {
                            smsPermissionsState.allPermissionsGranted -> {
                                viewModel.scanInboxSms()
                            }
                            smsPermissionsState.shouldShowRationale -> {
                                showRationaleDialog = true
                            }
                            else -> {
                                smsPermissionsState.launchMultiplePermissionRequest()
                            }
                        }
                    }

                    // Auto-scan once when all permissions are newly granted
                    LaunchedEffect(smsPermissionsState.allPermissionsGranted) {
                        if (smsPermissionsState.allPermissionsGranted) {
                            viewModel.scanInboxSms()
                        }
                    }

                    // BackHandler: if on secondary tab, back button returns to Dashboard
                    BackHandler(enabled = uiState.currentTab != MainTab.DASHBOARD) {
                        viewModel.setTab(MainTab.DASHBOARD)
                    }

                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        topBar = {
                            TopAppBar(
                                title = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(34.dp)
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(MaterialTheme.colorScheme.primary),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.AccountBalance,
                                                contentDescription = null,
                                                tint = Color.Black,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Row(verticalAlignment = Alignment.Bottom) {
                                            Text(
                                                text = "صادق",
                                                style = MaterialTheme.typography.titleLarge,
                                                fontWeight = FontWeight.Black,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "• مانده بانک‌ها",
                                                style = MaterialTheme.typography.labelMedium,
                                                color = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.padding(bottom = 2.dp)
                                            )
                                        }
                                    }
                                },
                                actions = {
                                    // Currency toggle badge
                                    Surface(
                                        onClick = { viewModel.toggleCurrency() },
                                        shape = RoundedCornerShape(16.dp),
                                        color = MaterialTheme.colorScheme.surfaceVariant,
                                        modifier = Modifier.padding(end = 4.dp)
                                    ) {
                                        Text(
                                            text = if (uiState.isToman) "تومان" else "ریال",
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }

                                    // Refresh/Scan icon button
                                    IconButton(
                                        onClick = {
                                            if (!smsPermissionsState.allPermissionsGranted) {
                                                requestSmsPermissions()
                                            } else {
                                                viewModel.scanInboxSms()
                                            }
                                        }
                                    ) {
                                        if (uiState.isScanning) {
                                            CircularProgressIndicator(
                                                modifier = Modifier.size(20.dp),
                                                strokeWidth = 2.dp
                                            )
                                        } else {
                                            Icon(
                                                imageVector = Icons.Default.Refresh,
                                                contentDescription = "اسکن پیامک‌ها"
                                            )
                                        }
                                    }
                                },
                                colors = TopAppBarDefaults.topAppBarColors(
                                    containerColor = MaterialTheme.colorScheme.surface
                                )
                            )
                        },
                        bottomBar = {
                            NavigationBar(
                                containerColor = MaterialTheme.colorScheme.surface,
                                tonalElevation = 8.dp
                            ) {
                                val tabs = listOf(
                                    Triple(MainTab.DASHBOARD, Icons.Filled.Dashboard, Icons.Outlined.Dashboard),
                                    Triple(MainTab.ACCOUNTS, Icons.Filled.CreditCard, Icons.Outlined.CreditCard),
                                    Triple(MainTab.TRANSACTIONS, Icons.Filled.ReceiptLong, Icons.Outlined.ReceiptLong),
                                    Triple(MainTab.ANALYTICS, Icons.Filled.QueryStats, Icons.Outlined.QueryStats),
                                    Triple(MainTab.TOOLS, Icons.Filled.Sms, Icons.Outlined.Sms)
                                )

                                tabs.forEach { (tab, filledIcon, outlinedIcon) ->
                                    val isSelected = uiState.currentTab == tab
                                    NavigationBarItem(
                                        selected = isSelected,
                                        onClick = { viewModel.setTab(tab) },
                                        icon = {
                                            Icon(
                                                imageVector = if (isSelected) filledIcon else outlinedIcon,
                                                contentDescription = tab.title
                                            )
                                        },
                                        label = {
                                            Text(
                                                text = tab.title,
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                            )
                                        },
                                        colors = NavigationBarItemDefaults.colors(
                                            selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                            indicatorColor = MaterialTheme.colorScheme.primaryContainer
                                        )
                                    )
                                }
                            }
                        }
                    ) { innerPadding ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                        ) {
                            AnimatedContent(
                                targetState = uiState.currentTab,
                                label = "ScreenTransition"
                            ) { targetTab ->
                                when (targetTab) {
                                    MainTab.DASHBOARD -> DashboardScreen(
                                        uiState = uiState,
                                        viewModel = viewModel,
                                        onNavigateTab = { viewModel.setTab(it) },
                                        onAccountClick = { viewModel.showAccountDetail(it) },
                                        onTransactionClick = { viewModel.showTransactionDetail(it) },
                                        onRequestSmsPermission = { requestSmsPermissions() }
                                    )
                                    MainTab.ACCOUNTS -> AccountsScreen(
                                        uiState = uiState,
                                        onAccountClick = { viewModel.showAccountDetail(it) }
                                    )
                                    MainTab.TRANSACTIONS -> TransactionsScreen(
                                        uiState = uiState,
                                        viewModel = viewModel,
                                        onTransactionClick = { viewModel.showTransactionDetail(it) }
                                    )
                                    MainTab.ANALYTICS -> AnalyticsScreen(
                                        uiState = uiState
                                    )
                                    MainTab.TOOLS -> SmsToolsScreen(
                                        uiState = uiState,
                                        viewModel = viewModel,
                                        hasSmsPermission = smsPermissionsState.allPermissionsGranted,
                                        onRequestSmsPermission = { requestSmsPermissions() }
                                    )
                                }
                            }
                        }
                    }

                    // Dialog for Transaction Detail
                    uiState.activeTransactionDetail?.let { tx ->
                        TransactionDetailDialog(
                            transaction = tx,
                            isToman = uiState.isToman,
                            usePersianDigits = uiState.usePersianDigits,
                            onDismiss = { viewModel.showTransactionDetail(null) },
                            onDelete = { viewModel.deleteTransaction(it) }
                        )
                    }

                    // Dialog for Account Detail
                    uiState.activeAccountDetail?.let { account ->
                        AccountDetailDialog(
                            account = account,
                            isToman = uiState.isToman,
                            usePersianDigits = uiState.usePersianDigits,
                            onDismiss = { viewModel.showAccountDetail(null) },
                            onDelete = { viewModel.deleteAccount(it) }
                        )
                    }

                    // Rationale Dialog for Accompanist Permissions
                    if (showRationaleDialog) {
                        AlertDialog(
                            onDismissRequest = { showRationaleDialog = false },
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.Security,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(36.dp)
                                )
                            },
                            title = {
                                Text(
                                    text = "دسترسی به پیامک‌های بانکی",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            },
                            text = {
                                Text(
                                    text = "نرم‌افزار «صادق» برای خواندن خودکار پیامک‌های تراکنش و محاسبه مانده حساب‌ها به دسترسی پیامک نیاز دارد. این اطلاعات کاملاً محرمانه بوده و تنها روی حافظه محلی گوشی شما نگهداری می‌شود.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    lineHeight = 22.sp
                                )
                            },
                            confirmButton = {
                                Button(
                                    onClick = {
                                        showRationaleDialog = false
                                        smsPermissionsState.launchMultiplePermissionRequest()
                                    }
                                ) {
                                    Text("اعطای مجوز")
                                }
                            },
                            dismissButton = {
                                TextButton(onClick = { showRationaleDialog = false }) {
                                    Text("انصراف")
                                }
                            }
                        )
                    }

                    // Dialog when permission is permanently denied
                    if (showSettingsDialog) {
                        AlertDialog(
                            onDismissRequest = { showSettingsDialog = false },
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.Settings,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(36.dp)
                                )
                            },
                            title = {
                                Text(
                                    text = "فعالسازی دسترسی از تنظیمات",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            },
                            text = {
                                Text(
                                    text = "دسترسی به پیامک برای برنامه غیرفعال است. جهت خواندن خودکار پیامک‌های بانکی، لطفاً در بخش تنظیمات گوشی مجوز پیامک را به برنامه «صادق» بدهید.",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            },
                            confirmButton = {
                                Button(
                                    onClick = {
                                        showSettingsDialog = false
                                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                            data = Uri.fromParts("package", context.packageName, null)
                                        }
                                        context.startActivity(intent)
                                    }
                                ) {
                                    Text("باز کردن تنظیمات")
                                }
                            },
                            dismissButton = {
                                TextButton(onClick = { showSettingsDialog = false }) {
                                    Text("انصراف")
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}
