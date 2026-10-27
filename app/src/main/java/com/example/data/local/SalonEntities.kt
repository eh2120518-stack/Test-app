package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "appointments")
data class AppointmentEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val bookingReference: String,
    val salonId: String,
    val salonName: String,
    val salonCity: String,
    val salonArea: String,
    val salonPhone: String,
    val serviceNames: String,
    val totalPricePkr: Int,
    val totalDurationMinutes: Int,
    val stylistName: String,
    val appointmentDate: String,
    val appointmentTime: String,
    val customerName: String,
    val customerPhone: String,
    val customerNotes: String,
    val paymentMethod: String,
    val status: String,
    val createdAtTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "favorite_salons")
data class FavoriteSalonEntity(
    @PrimaryKey
    val salonId: String,
    val addedAtTimestamp: Long = System.currentTimeMillis()
)
