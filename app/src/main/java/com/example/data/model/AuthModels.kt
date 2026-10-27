package com.example.data.model

enum class UserRole(val titleEn: String, val titleUrdu: String) {
    CUSTOMER("Customer", "گاہک"),
    ADMIN("Salon Owner / Admin", "سیلون مالک و ایڈمن")
}

data class UserAccount(
    val id: String,
    val name: String,
    val phoneOrEmail: String,
    val role: UserRole,
    val loyaltyStamps: Int = 3,
    val pinOrPassword: String = "",
    val title: String = ""
)

sealed class AuthState {
    data object Unauthenticated : AuthState()
    data class Authenticated(val user: UserAccount) : AuthState()
}

data class SalonQueueState(
    val activeChairs: Int = 3,
    val customersWaiting: Int = 2,
    val estimatedWaitMinutes: Int = 15,
    val isAcceptingWalkIns: Boolean = true,
    val operatingStatus: String = "OPEN", // "OPEN", "PRAYER_BREAK", "CLOSED"
    val announcementUrdu: String = "سیلون کھلا ہے! رش کم ہے، تشریف لائیں۔"
)
