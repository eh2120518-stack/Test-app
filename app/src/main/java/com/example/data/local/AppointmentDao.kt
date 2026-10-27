package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface AppointmentDao {
    @Query("SELECT * FROM appointments ORDER BY id DESC")
    fun getAllAppointments(): Flow<List<AppointmentEntity>>

    @Query("SELECT * FROM appointments ORDER BY id DESC")
    suspend fun getAppointmentsSnapshot(): List<AppointmentEntity>

    @Query("SELECT * FROM appointments WHERE status = :status ORDER BY id DESC")
    fun getAppointmentsByStatus(status: String): Flow<List<AppointmentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAppointment(appointment: AppointmentEntity): Long

    @Update
    suspend fun updateAppointment(appointment: AppointmentEntity)

    @Query("UPDATE appointments SET status = :status WHERE id = :id")
    suspend fun updateStatus(id: Long, status: String)

    @Query("UPDATE appointments SET appointmentDate = :newDate, appointmentTime = :newTime WHERE id = :id")
    suspend fun reschedule(id: Long, newDate: String, newTime: String)

    @Query("DELETE FROM appointments WHERE id = :id")
    suspend fun deleteAppointment(id: Long)

    @Query("SELECT * FROM favorite_salons")
    fun getAllFavorites(): Flow<List<FavoriteSalonEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFavorite(fav: FavoriteSalonEntity)

    @Query("DELETE FROM favorite_salons WHERE salonId = :salonId")
    suspend fun removeFavorite(salonId: String)

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_salons WHERE salonId = :salonId)")
    fun isFavorite(salonId: String): Flow<Boolean>
}
