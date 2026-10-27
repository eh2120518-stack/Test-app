package com.example.data.repository

import com.example.data.local.AppointmentDao
import com.example.data.local.AppointmentEntity
import com.example.data.local.FavoriteSalonEntity
import com.example.data.model.AppointmentStatus
import com.example.data.model.PakistaniCity
import com.example.data.model.Salon
import com.example.data.model.SalonGenderType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlin.random.Random

class SalonRepository(private val appointmentDao: AppointmentDao) {

    fun getAllSalons(): List<Salon> = SalonCatalog.sampleSalons

    fun getSalonById(id: String): Salon? = SalonCatalog.getSalonById(id)

    fun filterSalons(
        city: PakistaniCity?,
        genderType: SalonGenderType?,
        searchQuery: String
    ): List<Salon> {
        return SalonCatalog.sampleSalons.filter { salon ->
            val matchesCity = (city == null || salon.city == city)
            val matchesGender = (genderType == null || genderType == SalonGenderType.ALL || salon.genderType == genderType || salon.genderType == SalonGenderType.UNISEX)
            val matchesQuery = if (searchQuery.isBlank()) true else {
                val q = searchQuery.trim().lowercase()
                salon.name.lowercase().contains(q) ||
                salon.areaAddress.lowercase().contains(q) ||
                salon.city.displayName.lowercase().contains(q) ||
                salon.tagline.lowercase().contains(q) ||
                salon.services.any { it.name.lowercase().contains(q) || it.nameUrdu.contains(q) }
            }
            matchesCity && matchesGender && matchesQuery
        }
    }

    val allAppointments: Flow<List<AppointmentEntity>> = appointmentDao.getAllAppointments()
    val allFavorites: Flow<List<FavoriteSalonEntity>> = appointmentDao.getAllFavorites()

    suspend fun createAppointment(
        salon: Salon,
        serviceNames: String,
        totalPricePkr: Int,
        totalDurationMinutes: Int,
        stylistName: String,
        appointmentDate: String,
        appointmentTime: String,
        customerName: String,
        customerPhone: String,
        customerNotes: String,
        paymentMethodTitle: String
    ): Long {
        val randomNum = Random.nextInt(10000, 99999)
        val bookingRef = "PK-ZEB-$randomNum"
        val entity = AppointmentEntity(
            bookingReference = bookingRef,
            salonId = salon.id,
            salonName = salon.name,
            salonCity = salon.city.displayName,
            salonArea = salon.areaAddress,
            salonPhone = salon.phone,
            serviceNames = serviceNames,
            totalPricePkr = totalPricePkr,
            totalDurationMinutes = totalDurationMinutes,
            stylistName = stylistName,
            appointmentDate = appointmentDate,
            appointmentTime = appointmentTime,
            customerName = customerName,
            customerPhone = customerPhone,
            customerNotes = customerNotes,
            paymentMethod = paymentMethodTitle,
            status = AppointmentStatus.CONFIRMED.name
        )
        return appointmentDao.insertAppointment(entity)
    }

    suspend fun cancelAppointment(appointmentId: Long) {
        appointmentDao.updateStatus(appointmentId, AppointmentStatus.CANCELLED.name)
    }

    suspend fun markCompleted(appointmentId: Long) {
        appointmentDao.updateStatus(appointmentId, AppointmentStatus.COMPLETED.name)
    }

    suspend fun rescheduleAppointment(appointmentId: Long, newDate: String, newTime: String) {
        appointmentDao.reschedule(appointmentId, newDate, newTime)
        appointmentDao.updateStatus(appointmentId, AppointmentStatus.CONFIRMED.name)
    }

    suspend fun toggleFavorite(salonId: String, isCurrentlyFav: Boolean) {
        if (isCurrentlyFav) {
            appointmentDao.removeFavorite(salonId)
        } else {
            appointmentDao.addFavorite(FavoriteSalonEntity(salonId = salonId))
        }
    }

    suspend fun seedInitialDataIfEmpty() {
        val currentList = appointmentDao.getAllAppointments().first()
        if (currentList.isEmpty()) {
            val sampleSalon = SalonCatalog.sampleSalons.first()
            appointmentDao.insertAppointment(
                AppointmentEntity(
                    bookingReference = "PK-HDR-84920",
                    salonId = sampleSalon.id,
                    salonName = sampleSalon.name,
                    salonCity = sampleSalon.city.displayName,
                    salonArea = sampleSalon.areaAddress,
                    salonPhone = sampleSalon.phone,
                    serviceNames = "Haider Signature Haircut & Styling + Beard Setting",
                    totalPricePkr = 650,
                    totalDurationMinutes = 45,
                    stylistName = "Ustad Haider Ali",
                    appointmentDate = "Tomorrow, 25 Oct",
                    appointmentTime = "04:30 PM",
                    customerName = "Chaudhry Bilal",
                    customerPhone = "0300-4567890",
                    customerNotes = "Skin fade and razor sharp beard lining",
                    paymentMethod = "Cash at Counter",
                    status = AppointmentStatus.CONFIRMED.name
                )
            )
            // Seed Haider Salon as favorite
            appointmentDao.addFavorite(FavoriteSalonEntity("haider_salon_naindowal"))
        }
    }
}
