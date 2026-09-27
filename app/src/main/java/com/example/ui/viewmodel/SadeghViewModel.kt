package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.SadeghDatabase
import com.example.data.entity.BankAccountEntity
import com.example.data.entity.TransactionEntity
import com.example.data.repository.BankRepository
import com.example.data.sms.SmsBankParser
import com.example.model.ParsedSmsResult
import com.example.model.TransactionType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class MainTab(val title: String) {
    DASHBOARD("داشبورد"),
    ACCOUNTS("بانک‌ها"),
    TRANSACTIONS("تراکنش‌ها"),
    ANALYTICS("تحلیل مالی"),
    TOOLS("ابزار پیامک")
}

data class DashboardUiState(
    val accounts: List<BankAccountEntity> = emptyList(),
    val transactions: List<TransactionEntity> = emptyList(),
    val totalBalanceToman: Long = 0L,
    val totalIncomeToman: Long = 0L,
    val totalExpenseToman: Long = 0L,
    val isToman: Boolean = true,
    val usePersianDigits: Boolean = true,
    val currentTab: MainTab = MainTab.DASHBOARD,
    val searchQuery: String = "",
    val bankFilter: String? = null,
    val typeFilter: TransactionType? = null,
    val isScanning: Boolean = false,
    val statusMessage: String? = null,
    val testSmsInput: String = "",
    val testSmsResult: ParsedSmsResult? = null,
    val activeTransactionDetail: TransactionEntity? = null,
    val activeAccountDetail: BankAccountEntity? = null
)

class SadeghViewModel(application: Application) : AndroidViewModel(application) {

    private val db = SadeghDatabase.getInstance(application)
    private val repository = BankRepository(db.bankAccountDao(), db.transactionDao(), application)

    private val _isToman = MutableStateFlow(true)
    private val _usePersianDigits = MutableStateFlow(true)
    private val _currentTab = MutableStateFlow(MainTab.DASHBOARD)
    private val _searchQuery = MutableStateFlow("")
    private val _bankFilter = MutableStateFlow<String?>(null)
    private val _typeFilter = MutableStateFlow<TransactionType?>(null)
    private val _isScanning = MutableStateFlow(false)
    private val _statusMessage = MutableStateFlow<String?>(null)
    private val _testSmsInput = MutableStateFlow("")
    private val _testSmsResult = MutableStateFlow<ParsedSmsResult?>(null)
    private val _activeTransactionDetail = MutableStateFlow<TransactionEntity?>(null)
    private val _activeAccountDetail = MutableStateFlow<BankAccountEntity?>(null)

    val uiState: StateFlow<DashboardUiState> = combine(
        repository.accountsFlow,
        repository.transactionsFlow,
        _isToman,
        _usePersianDigits,
        _currentTab,
        _searchQuery,
        _bankFilter,
        _typeFilter,
        _isScanning,
        _statusMessage,
        _testSmsInput,
        _testSmsResult,
        _activeTransactionDetail,
        _activeAccountDetail
    ) { args: Array<Any?> ->
        @Suppress("UNCHECKED_CAST")
        val accounts = args[0] as List<BankAccountEntity>
        @Suppress("UNCHECKED_CAST")
        val txs = args[1] as List<TransactionEntity>
        val isToman = args[2] as Boolean
        val usePersianDigits = args[3] as Boolean
        val currentTab = args[4] as MainTab
        val searchQuery = args[5] as String
        val bankFilter = args[6] as String?
        val typeFilter = args[7] as TransactionType?
        val isScanning = args[8] as Boolean
        val statusMessage = args[9] as String?
        val testSmsInput = args[10] as String
        val testSmsResult = args[11] as ParsedSmsResult?
        val activeTx = args[12] as TransactionEntity?
        val activeAcc = args[13] as BankAccountEntity?

        val totalBalance = accounts.sumOf { it.currentBalanceToman }

        var income = 0L
        var expense = 0L
        for (tx in txs) {
            val type = try {
                TransactionType.valueOf(tx.type)
            } catch (e: Exception) {
                TransactionType.UNKNOWN
            }
            if (type.isIncome) {
                income += tx.amountToman
            } else {
                expense += tx.amountToman
            }
        }

        DashboardUiState(
            accounts = accounts,
            transactions = txs,
            totalBalanceToman = totalBalance,
            totalIncomeToman = income,
            totalExpenseToman = expense,
            isToman = isToman,
            usePersianDigits = usePersianDigits,
            currentTab = currentTab,
            searchQuery = searchQuery,
            bankFilter = bankFilter,
            typeFilter = typeFilter,
            isScanning = isScanning,
            statusMessage = statusMessage,
            testSmsInput = testSmsInput,
            testSmsResult = testSmsResult,
            activeTransactionDetail = activeTx,
            activeAccountDetail = activeAcc
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DashboardUiState()
    )

    init {
        // If the database is completely empty on first launch, load initial sample data
        // so the user can immediately see the app's capability without waiting for SMS
        viewModelScope.launch {
            if (db.bankAccountDao().getAllAccountsFlow().stateIn(viewModelScope).value.isEmpty()) {
                repository.loadSampleData()
            }
        }
    }

    fun setTab(tab: MainTab) {
        _currentTab.value = tab
    }

    fun toggleCurrency() {
        _isToman.value = !_isToman.value
    }

    fun togglePersianDigits() {
        _usePersianDigits.value = !_usePersianDigits.value
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setBankFilter(bankCode: String?) {
        _bankFilter.value = bankCode
    }

    fun setTypeFilter(type: TransactionType?) {
        _typeFilter.value = type
    }

    fun setTestSmsInput(input: String) {
        _testSmsInput.value = input
        if (input.isNotBlank()) {
            _testSmsResult.value = SmsBankParser.parse(input)
        } else {
            _testSmsResult.value = null
        }
    }

    fun saveTestSms() {
        val result = _testSmsResult.value ?: return
        viewModelScope.launch {
            val saved = repository.saveParsedSms(result)
            if (saved) {
                _statusMessage.value = "پیامک با موفقیت ذخیره و مانده حساب بروزرسانی شد"
                _testSmsInput.value = ""
                _testSmsResult.value = null
            } else {
                _statusMessage.value = "این پیامک قبلاً ثبت شده است"
            }
        }
    }

    fun scanInboxSms() {
        viewModelScope.launch {
            _isScanning.value = true
            _statusMessage.value = "در حال جستجو و اسکن پیامک‌های بانکی..."
            try {
                val count = repository.scanInboxSms()
                _statusMessage.value = if (count > 0) {
                    "تعداد $count پیامک بانکی جدید با موفقیت اضافه شد"
                } else {
                    "پیامک بانکی جدیدی یافت نشد"
                }
            } catch (e: Exception) {
                _statusMessage.value = "خطا در اسکن پیامک‌ها: ${e.localizedMessage}"
            } finally {
                _isScanning.value = false
            }
        }
    }

    fun loadSampleData() {
        viewModelScope.launch {
            _isScanning.value = true
            val count = repository.loadSampleData()
            _isScanning.value = false
            _statusMessage.value = "پیامک‌های نمونه بانکی با موفقیت بارگذاری شدند ($count مورد جدید)"
        }
    }

    fun clearAllData() {
        viewModelScope.launch {
            repository.clearAllData()
            _statusMessage.value = "تمام اطلاعات با موفقیت پاکسازی شد"
        }
    }

    fun clearStatusMessage() {
        _statusMessage.value = null
    }

    fun showTransactionDetail(tx: TransactionEntity?) {
        _activeTransactionDetail.value = tx
    }

    fun showAccountDetail(account: BankAccountEntity?) {
        _activeAccountDetail.value = account
    }

    fun deleteTransaction(id: Long) {
        viewModelScope.launch {
            repository.deleteTransaction(id)
            _activeTransactionDetail.value = null
            _statusMessage.value = "تراکنش حذف شد"
        }
    }

    fun deleteAccount(id: Long) {
        viewModelScope.launch {
            repository.deleteAccount(id)
            _activeAccountDetail.value = null
            _statusMessage.value = "کارت بانکی حذف شد"
        }
    }
}
