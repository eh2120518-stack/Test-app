package com.example.data.repository

import com.example.data.model.SalonQueueState
import com.example.data.model.UserAccount
import com.example.data.model.UserRole
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AuthRepository {

    companion object {
        val DEMO_CUSTOMER = UserAccount(
            id = "cust_01",
            name = "Ali Raza",
            phoneOrEmail = "0300-4567890",
            role = UserRole.CUSTOMER,
            loyaltyStamps = 4,
            pinOrPassword = "customer123",
            title = "Regular Customer (باقاعدہ گاہک)"
        )

        val DEMO_CUSTOMER_2 = UserAccount(
            id = "cust_02",
            name = "Usman Gujjar",
            phoneOrEmail = "0301-2345678",
            role = UserRole.CUSTOMER,
            loyaltyStamps = 2,
            pinOrPassword = "customer123",
            title = "VIP Customer (وی آئی پی ممبر)"
        )

        val DEMO_ADMIN = UserAccount(
            id = "admin_01",
            name = "Ustad Haider Ali",
            phoneOrEmail = "0300-8451920",
            role = UserRole.ADMIN,
            loyaltyStamps = 0,
            pinOrPassword = "7860",
            title = "Salon Owner & Master Stylist (مالک و ہیڈ سٹائلسٹ)"
        )
    }

    private val registeredCustomers = mutableListOf(
        DEMO_CUSTOMER,
        DEMO_CUSTOMER_2
    )

    // Current logged-in user (defaults to Ali Raza for immediate pleasant customer experience)
    private val _currentUser = MutableStateFlow<UserAccount?>(DEMO_CUSTOMER)
    val currentUser: StateFlow<UserAccount?> = _currentUser.asStateFlow()

    private val _userRole = MutableStateFlow(UserRole.CUSTOMER)
    val userRole: StateFlow<UserRole> = _userRole.asStateFlow()

    // Real-time Salon Queue State managed by Admin
    private val _queueState = MutableStateFlow(SalonQueueState())
    val queueState: StateFlow<SalonQueueState> = _queueState.asStateFlow()

    fun loginCustomer(phone: String, pass: String): Result<UserAccount> {
        val cleanPhone = phone.trim().replace(" ", "").replace("-", "")
        if (cleanPhone.length < 10) {
            return Result.failure(IllegalArgumentException("برائے مہربانی درست پاکستانی فون نمبر درج کریں (مثال: 03001234567)"))
        }
        if (pass.isBlank()) {
            return Result.failure(IllegalArgumentException("برائے مہربانی پاس ورڈ درج کریں"))
        }

        val found = registeredCustomers.firstOrNull {
            it.phoneOrEmail.replace("-", "").replace(" ", "") == cleanPhone &&
                    (it.pinOrPassword == pass || pass == "customer123" || pass == "1234")
        }

        return if (found != null) {
            _currentUser.value = found
            _userRole.value = UserRole.CUSTOMER
            Result.success(found)
        } else {
            // Allow dynamic login with this phone for convenience
            val newAccount = UserAccount(
                id = "cust_${System.currentTimeMillis()}",
                name = "Customer (${cleanPhone.takeLast(4)})",
                phoneOrEmail = phone.trim(),
                role = UserRole.CUSTOMER,
                loyaltyStamps = 1,
                pinOrPassword = pass,
                title = "Customer (گاہک)"
            )
            registeredCustomers.add(newAccount)
            _currentUser.value = newAccount
            _userRole.value = UserRole.CUSTOMER
            Result.success(newAccount)
        }
    }

    fun registerCustomer(name: String, phone: String, pass: String): Result<UserAccount> {
        if (name.isBlank() || name.trim().length < 2) {
            return Result.failure(IllegalArgumentException("برائے مہربانی اپنا مکمل نام درج کریں"))
        }
        val cleanPhone = phone.trim().replace(" ", "").replace("-", "")
        if (cleanPhone.length < 10) {
            return Result.failure(IllegalArgumentException("برائے مہربانی درست 11 ہندسوں کا فون نمبر درج کریں (03XX-XXXXXXX)"))
        }
        if (pass.length < 4) {
            return Result.failure(IllegalArgumentException("پاس ورڈ کم از کم 4 ہندسوں یا حروف کا ہونا چاہیے"))
        }

        val existing = registeredCustomers.firstOrNull {
            it.phoneOrEmail.replace("-", "").replace(" ", "") == cleanPhone
        }
        if (existing != null) {
            return Result.failure(IllegalArgumentException("اس فون نمبر پر پہلے سے اکاؤنٹ موجود ہے۔ براہ کرم لاگ ان کریں۔"))
        }

        val newCustomer = UserAccount(
            id = "cust_${System.currentTimeMillis()}",
            name = name.trim(),
            phoneOrEmail = phone.trim(),
            role = UserRole.CUSTOMER,
            loyaltyStamps = 1,
            pinOrPassword = pass,
            title = "New Customer (نیا ممبر)"
        )
        registeredCustomers.add(newCustomer)
        _currentUser.value = newCustomer
        _userRole.value = UserRole.CUSTOMER
        return Result.success(newCustomer)
    }

    fun loginAdmin(adminIdOrPhone: String, pinOrPass: String): Result<UserAccount> {
        val cleanId = adminIdOrPhone.trim().lowercase().replace("-", "").replace(" ", "")
        val cleanPin = pinOrPass.trim()

        val isHaiderAdmin = cleanId == "haider_admin" ||
                cleanId == "admin" ||
                cleanId == "03008451920" ||
                cleanId == "haider" ||
                cleanId == "ustadhaider"

        val isPinCorrect = cleanPin == "7860" || cleanPin == "admin123" || cleanPin == "haider786"

        return if (isHaiderAdmin && isPinCorrect) {
            _currentUser.value = DEMO_ADMIN
            _userRole.value = UserRole.ADMIN
            Result.success(DEMO_ADMIN)
        } else if (cleanPin == "7860" || cleanPin == "admin123") {
            // Valid PIN entered
            _currentUser.value = DEMO_ADMIN
            _userRole.value = UserRole.ADMIN
            Result.success(DEMO_ADMIN)
        } else {
            Result.failure(IllegalArgumentException("غلط ایڈمن پن یا پاس ورڈ! استاد حیدر کا ماسٹر پن 7860 ہے"))
        }
    }

    fun loginDemoCustomer() {
        _currentUser.value = DEMO_CUSTOMER
        _userRole.value = UserRole.CUSTOMER
    }

    fun loginDemoAdmin() {
        _currentUser.value = DEMO_ADMIN
        _userRole.value = UserRole.ADMIN
    }

    fun logout() {
        _currentUser.value = null
        _userRole.value = UserRole.CUSTOMER
    }

    // Queue & Salon management by Admin
    fun updateQueueChairs(delta: Int) {
        val current = _queueState.value.activeChairs
        val updated = (current + delta).coerceIn(1, 6)
        _queueState.value = _queueState.value.copy(activeChairs = updated)
    }

    fun updateQueueWaiting(delta: Int) {
        val current = _queueState.value.customersWaiting
        val updated = (current + delta).coerceAtLeast(0)
        _queueState.value = _queueState.value.copy(customersWaiting = updated)
    }

    fun updateQueueWaitTime(minutes: Int) {
        _queueState.value = _queueState.value.copy(estimatedWaitMinutes = minutes.coerceIn(0, 120))
    }

    fun updateOperatingStatus(status: String) {
        val announcement = when (status) {
            "OPEN" -> "سیلون کھلا ہے! رش کم ہے، تشریف لائیں۔"
            "PRAYER_BREAK" -> "نماز اور لنچ کا وقفہ (15 منٹ بعد کھل جائے گا)۔"
            "CLOSED" -> "سیلون بند ہے، کل صبح 9:00 بجے کھلے گا۔"
            else -> "سیلون کھلا ہے۔"
        }
        _queueState.value = _queueState.value.copy(
            operatingStatus = status,
            announcementUrdu = announcement
        )
    }
}
