package com.example.data.model

enum class PakistaniCity(val displayName: String, val urduName: String) {
    LALAMUSA("Village Naindowal, Lalamusa", "ولج نیندووال، لالہ موسیٰ")
}

enum class SalonGenderType(val label: String, val urduLabel: String) {
    ALL("All Salons", "تمام سیلونز"),
    WOMEN("Women's Salon", "خواتین"),
    MEN("Men's Barbershop", "مردانہ"),
    UNISEX("Unisex / Family", "یونیسیکس")
}

data class SalonService(
    val id: String,
    val name: String,
    val nameUrdu: String,
    val category: String,
    val durationMinutes: Int,
    val pricePkr: Int,
    val description: String
)

data class Stylist(
    val id: String,
    val name: String,
    val role: String,
    val rating: Double,
    val experienceYears: Int
)

data class SalonReview(
    val id: String,
    val authorName: String,
    val city: String,
    val rating: Float,
    val comment: String,
    val date: String
)

data class Salon(
    val id: String,
    val name: String,
    val nameUrdu: String,
    val tagline: String,
    val city: PakistaniCity,
    val areaAddress: String,
    val phone: String,
    val rating: Double,
    val reviewCount: Int,
    val genderType: SalonGenderType,
    val startingPricePkr: Int,
    val openingHours: String,
    val services: List<SalonService>,
    val stylists: List<Stylist>,
    val reviews: List<SalonReview>,
    val facilities: List<String>,
    val isFeatured: Boolean = false
)

enum class PaymentMethod(val title: String, val urduTitle: String, val subtitle: String) {
    CASH_ON_ARRIVAL("Cash at Counter", "کاؤنٹر پر کیش ادائیگی", "Pay in Pakistani Rupees after your appointment"),
    JAZZCASH("JazzCash Mobile Account", "جاز کیش", "Direct 03XX account transfer or QR pay"),
    EASYPAISA("Easypaisa Wallet", "ایزی پیسہ", "Instant wallet payment on confirmation"),
    SADA_NAYA_PAY("SadaPay / NayaPay", "ساداپے / نیاپے", "Zero-fee modern digital account transfer"),
    CARD("Debit / Credit Card", "بینک کارڈ", "Visa, Mastercard, or PayPak accepted")
}

enum class AppointmentStatus {
    CONFIRMED,
    COMPLETED,
    CANCELLED
}
