package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.AppointmentEntity
import com.example.data.model.AppointmentStatus
import com.example.data.model.PakistaniCity
import com.example.data.model.PaymentMethod
import com.example.data.model.Salon
import com.example.data.model.SalonGenderType
import com.example.data.model.SalonService
import com.example.data.model.Stylist
import com.example.data.repository.SalonCatalog
import com.example.data.repository.SalonRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

import com.example.data.model.SalonQueueState
import com.example.data.model.UserAccount
import com.example.data.model.UserRole
import com.example.data.repository.AuthRepository

sealed class AppScreen {
    data object Home : AppScreen()
    data object Services : AppScreen()
    data object Search : AppScreen()
    data class SalonDetail(val salonId: String) : AppScreen()
    data class BookingFlow(val salonId: String, val step: Int = 1) : AppScreen()
    data object MyAppointments : AppScreen()
    data object BookAppointment : AppScreen()
    data class PassVoucher(val appointment: AppointmentEntity) : AppScreen()
    data object Favorites : AppScreen()
    data class Auth(val targetTab: Int = 0) : AppScreen()
    data object AdminDashboard : AppScreen()
    data object CustomerProfile : AppScreen()
}

data class BookingDraftState(
    val salon: Salon? = null,
    val selectedServices: Set<SalonService> = emptySet(),
    val selectedStylist: Stylist? = null,
    val selectedDate: String = "",
    val selectedTimeSlot: String = "",
    val customerName: String = "",
    val customerPhone: String = "",
    val customerNotes: String = "",
    val paymentMethod: PaymentMethod = PaymentMethod.CASH_ON_ARRIVAL,
    val isSubmitting: Boolean = false,
    val confirmedAppointment: AppointmentEntity? = null
)

class SalonViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: SalonRepository
    private val authRepository = AuthRepository()

    val currentUser: StateFlow<UserAccount?> = authRepository.currentUser
    val userRole: StateFlow<UserRole> = authRepository.userRole
    val salonQueueState: StateFlow<SalonQueueState> = authRepository.queueState

    init {
        val db = AppDatabase.getDatabase(application)
        repository = SalonRepository(db.appointmentDao())
        viewModelScope.launch {
            repository.seedInitialDataIfEmpty()
        }
    }

    private val _currentScreen = MutableStateFlow<AppScreen>(AppScreen.Home)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    private val _screenBackStack = mutableListOf<AppScreen>()

    private val _selectedCity = MutableStateFlow<PakistaniCity?>(null)
    val selectedCity: StateFlow<PakistaniCity?> = _selectedCity.asStateFlow()

    private val _selectedGenderType = MutableStateFlow(SalonGenderType.ALL)
    val selectedGenderType: StateFlow<SalonGenderType> = _selectedGenderType.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val filteredSalons: StateFlow<List<Salon>> = combine(
        _selectedCity,
        _selectedGenderType,
        _searchQuery
    ) { city, gender, query ->
        repository.filterSalons(city, gender, query)
    }.stateIn(viewModelScope, SharingStarted.Lazily, repository.getAllSalons())

    val allAppointments: StateFlow<List<AppointmentEntity>> = repository.allAppointments
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val favoriteIds: StateFlow<Set<String>> = repository.allFavorites
        .map { list -> list.map { it.salonId }.toSet() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

    val favoriteSalons: StateFlow<List<Salon>> = combine(favoriteIds) { ids ->
        val idSet = ids[0]
        SalonCatalog.sampleSalons.filter { idSet.contains(it.id) }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val _bookingDraft = MutableStateFlow(BookingDraftState())
    val bookingDraft: StateFlow<BookingDraftState> = _bookingDraft.asStateFlow()

    // Rescheduling state
    private val _reschedulingAppointment = MutableStateFlow<AppointmentEntity?>(null)
    val reschedulingAppointment: StateFlow<AppointmentEntity?> = _reschedulingAppointment.asStateFlow()

    // Available booking dates (today + next 7 days in Pakistani context)
    val availableDates: List<String> by lazy {
        val list = mutableListOf<String>()
        val calendar = Calendar.getInstance()
        val format = SimpleDateFormat("EEE, dd MMM", Locale.ENGLISH)
        list.add("Today, " + SimpleDateFormat("dd MMM", Locale.ENGLISH).format(calendar.time))
        calendar.add(Calendar.DAY_OF_YEAR, 1)
        list.add("Tomorrow, " + SimpleDateFormat("dd MMM", Locale.ENGLISH).format(calendar.time))
        for (i in 2..6) {
            calendar.add(Calendar.DAY_OF_YEAR, 1)
            list.add(format.format(calendar.time))
        }
        list
    }

    // Available booking slots
    val availableTimeSlots: List<String> = listOf(
        "11:30 AM",
        "12:30 PM",
        "02:00 PM",
        "03:30 PM",
        "04:30 PM",
        "05:30 PM",
        "06:30 PM",
        "07:30 PM",
        "08:30 PM"
    )

    fun navigateTo(screen: AppScreen) {
        if (_currentScreen.value != screen) {
            _screenBackStack.add(_currentScreen.value)
            _currentScreen.value = screen
        }
    }

    fun navigateBack(): Boolean {
        if (_screenBackStack.isNotEmpty()) {
            _currentScreen.value = _screenBackStack.removeAt(_screenBackStack.size - 1)
            return true
        }
        if (_currentScreen.value != AppScreen.Home) {
            _currentScreen.value = AppScreen.Home
            return true
        }
        return false
    }

    fun selectCity(city: PakistaniCity?) {
        _selectedCity.value = city
    }

    fun selectGenderType(genderType: SalonGenderType) {
        _selectedGenderType.value = genderType
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun openSalonDetail(salonId: String) {
        navigateTo(AppScreen.SalonDetail(salonId))
    }

    fun startBooking(salon: Salon, preselectedService: SalonService? = null) {
        val initialServices = if (preselectedService != null) setOf(preselectedService) else emptySet()
        val defaultStylist = salon.stylists.firstOrNull()
        _bookingDraft.value = BookingDraftState(
            salon = salon,
            selectedServices = initialServices,
            selectedStylist = defaultStylist,
            selectedDate = availableDates.firstOrNull() ?: "Today",
            selectedTimeSlot = availableTimeSlots.getOrNull(2) ?: "02:00 PM",
            customerName = "Ali Raza",
            customerPhone = "0300-4567890",
            customerNotes = "",
            paymentMethod = PaymentMethod.CASH_ON_ARRIVAL
        )
        navigateTo(AppScreen.BookingFlow(salon.id, step = 1))
    }

    fun toggleServiceInDraft(service: SalonService) {
        val current = _bookingDraft.value.selectedServices
        val updated = if (current.contains(service)) {
            current - service
        } else {
            current + service
        }
        _bookingDraft.value = _bookingDraft.value.copy(selectedServices = updated)
    }

    fun selectStylistInDraft(stylist: Stylist) {
        _bookingDraft.value = _bookingDraft.value.copy(selectedStylist = stylist)
    }

    fun selectDateInDraft(date: String) {
        _bookingDraft.value = _bookingDraft.value.copy(selectedDate = date)
    }

    fun selectTimeSlotInDraft(timeSlot: String) {
        _bookingDraft.value = _bookingDraft.value.copy(selectedTimeSlot = timeSlot)
    }

    fun updateCustomerDetails(name: String, phone: String, notes: String) {
        _bookingDraft.value = _bookingDraft.value.copy(
            customerName = name,
            customerPhone = phone,
            customerNotes = notes
        )
    }

    fun selectPaymentMethodInDraft(paymentMethod: PaymentMethod) {
        _bookingDraft.value = _bookingDraft.value.copy(paymentMethod = paymentMethod)
    }

    fun advanceBookingStep(nextStep: Int) {
        val salon = _bookingDraft.value.salon ?: return
        navigateTo(AppScreen.BookingFlow(salon.id, step = nextStep))
    }

    fun confirmAppointment() {
        val draft = _bookingDraft.value
        val salon = draft.salon ?: return
        val services = draft.selectedServices.toList()
        if (services.isEmpty()) return

        val totalPkr = services.sumOf { it.pricePkr }
        val totalDuration = services.sumOf { it.durationMinutes }
        val serviceNames = services.joinToString(" + ") { it.name }
        val stylistName = draft.selectedStylist?.name ?: "Any Senior Stylist"

        viewModelScope.launch {
            _bookingDraft.value = _bookingDraft.value.copy(isSubmitting = true)
            val insertedId = repository.createAppointment(
                salon = salon,
                serviceNames = serviceNames,
                totalPricePkr = totalPkr,
                totalDurationMinutes = totalDuration,
                stylistName = stylistName,
                appointmentDate = draft.selectedDate,
                appointmentTime = draft.selectedTimeSlot,
                customerName = draft.customerName.ifBlank { "Valued Customer" },
                customerPhone = draft.customerPhone.ifBlank { "0300-1234567" },
                customerNotes = draft.customerNotes,
                paymentMethodTitle = draft.paymentMethod.title
            )

            val created = AppointmentEntity(
                id = insertedId,
                bookingReference = "PK-ZEB-${(10000..99999).random()}",
                salonId = salon.id,
                salonName = salon.name,
                salonCity = salon.city.displayName,
                salonArea = salon.areaAddress,
                salonPhone = salon.phone,
                serviceNames = serviceNames,
                totalPricePkr = totalPkr,
                totalDurationMinutes = totalDuration,
                stylistName = stylistName,
                appointmentDate = draft.selectedDate,
                appointmentTime = draft.selectedTimeSlot,
                customerName = draft.customerName.ifBlank { "Valued Customer" },
                customerPhone = draft.customerPhone.ifBlank { "0300-1234567" },
                customerNotes = draft.customerNotes,
                paymentMethod = draft.paymentMethod.title,
                status = AppointmentStatus.CONFIRMED.name
            )

            _bookingDraft.value = _bookingDraft.value.copy(
                isSubmitting = false,
                confirmedAppointment = created
            )
            // Navigate to confirmation step (Step 4)
            navigateTo(AppScreen.BookingFlow(salon.id, step = 4))
        }
    }

    fun cancelAppointment(appointmentId: Long) {
        viewModelScope.launch {
            repository.cancelAppointment(appointmentId)
        }
    }

    fun openRescheduleDialog(appointment: AppointmentEntity) {
        _reschedulingAppointment.value = appointment
    }

    fun closeRescheduleDialog() {
        _reschedulingAppointment.value = null
    }

    fun confirmReschedule(appointmentId: Long, newDate: String, newTime: String) {
        viewModelScope.launch {
            repository.rescheduleAppointment(appointmentId, newDate, newTime)
            _reschedulingAppointment.value = null
        }
    }

    val haiderSalon: Salon
        get() = SalonCatalog.sampleSalons.firstOrNull { it.id == "haider_salon_naindowal" }
            ?: SalonCatalog.sampleSalons.first()

    val allServices: List<SalonService>
        get() = haiderSalon.services

    fun openAppointmentForm(preselectedService: SalonService? = null) {
        val currentSelected = if (preselectedService != null) {
            setOf(preselectedService)
        } else if (_bookingDraft.value.selectedServices.isNotEmpty()) {
            _bookingDraft.value.selectedServices
        } else {
            // Default select the first popular haircut if none selected yet
            haiderSalon.services.firstOrNull()?.let { setOf(it) } ?: emptySet()
        }

        val defaultStylist = _bookingDraft.value.selectedStylist ?: haiderSalon.stylists.firstOrNull()
        val defaultDate = if (_bookingDraft.value.selectedDate.isNotBlank()) _bookingDraft.value.selectedDate else (availableDates.firstOrNull() ?: "Today")
        val defaultTime = if (_bookingDraft.value.selectedTimeSlot.isNotBlank()) _bookingDraft.value.selectedTimeSlot else (availableTimeSlots.getOrNull(2) ?: "02:00 PM")
        val loggedInName = currentUser.value?.name ?: "Ali Raza"
        val loggedInPhone = currentUser.value?.phoneOrEmail ?: "0300-4567890"
        val defaultName = if (_bookingDraft.value.customerName.isNotBlank()) _bookingDraft.value.customerName else loggedInName
        val defaultPhone = if (_bookingDraft.value.customerPhone.isNotBlank()) _bookingDraft.value.customerPhone else loggedInPhone

        _bookingDraft.value = _bookingDraft.value.copy(
            salon = haiderSalon,
            selectedServices = currentSelected,
            selectedStylist = defaultStylist,
            selectedDate = defaultDate,
            selectedTimeSlot = defaultTime,
            customerName = defaultName,
            customerPhone = defaultPhone,
            customerNotes = _bookingDraft.value.customerNotes,
            paymentMethod = _bookingDraft.value.paymentMethod,
            isSubmitting = false,
            confirmedAppointment = null
        )
        navigateTo(AppScreen.BookAppointment)
    }

    suspend fun submitAppointmentFromForm(): AppointmentEntity? {
        val draft = _bookingDraft.value
        val salon = draft.salon ?: haiderSalon
        val services = draft.selectedServices.toList()
        if (services.isEmpty()) return null

        val totalPkr = services.sumOf { it.pricePkr }
        val totalDuration = services.sumOf { it.durationMinutes }
        val serviceNames = services.joinToString(" + ") { it.name }
        val stylistName = draft.selectedStylist?.name ?: "Ustad Haider Ali"
        val bookingRef = "PK-HDR-${(10000..99999).random()}"

        _bookingDraft.value = _bookingDraft.value.copy(isSubmitting = true)

        val insertedId = repository.createAppointment(
            salon = salon,
            serviceNames = serviceNames,
            totalPricePkr = totalPkr,
            totalDurationMinutes = totalDuration,
            stylistName = stylistName,
            appointmentDate = draft.selectedDate,
            appointmentTime = draft.selectedTimeSlot,
            customerName = draft.customerName.ifBlank { "Customer" },
            customerPhone = draft.customerPhone.ifBlank { "0300-1234567" },
            customerNotes = draft.customerNotes,
            paymentMethodTitle = draft.paymentMethod.title
        )

        val created = AppointmentEntity(
            id = insertedId,
            bookingReference = bookingRef,
            salonId = salon.id,
            salonName = salon.name,
            salonCity = salon.city.displayName,
            salonArea = salon.areaAddress,
            salonPhone = salon.phone,
            serviceNames = serviceNames,
            totalPricePkr = totalPkr,
            totalDurationMinutes = totalDuration,
            stylistName = stylistName,
            appointmentDate = draft.selectedDate,
            appointmentTime = draft.selectedTimeSlot,
            customerName = draft.customerName.ifBlank { "Customer" },
            customerPhone = draft.customerPhone.ifBlank { "0300-1234567" },
            customerNotes = draft.customerNotes,
            paymentMethod = draft.paymentMethod.title,
            status = AppointmentStatus.CONFIRMED.name
        )

        _bookingDraft.value = _bookingDraft.value.copy(
            isSubmitting = false,
            confirmedAppointment = created
        )
        return created
    }

    fun resetConfirmedAppointment() {
        _bookingDraft.value = _bookingDraft.value.copy(confirmedAppointment = null)
    }

    fun bookSpecificService(service: SalonService) {
        openAppointmentForm(service)
    }

    fun bookWithStylist(stylist: Stylist) {
        val currentSelected = if (_bookingDraft.value.selectedServices.isNotEmpty()) {
            _bookingDraft.value.selectedServices
        } else {
            haiderSalon.services.firstOrNull()?.let { setOf(it) } ?: emptySet()
        }
        _bookingDraft.value = _bookingDraft.value.copy(
            salon = haiderSalon,
            selectedServices = currentSelected,
            selectedStylist = stylist,
            selectedDate = if (_bookingDraft.value.selectedDate.isNotBlank()) _bookingDraft.value.selectedDate else (availableDates.firstOrNull() ?: "Today"),
            selectedTimeSlot = if (_bookingDraft.value.selectedTimeSlot.isNotBlank()) _bookingDraft.value.selectedTimeSlot else (availableTimeSlots.getOrNull(2) ?: "02:00 PM"),
            customerName = if (_bookingDraft.value.customerName.isNotBlank()) _bookingDraft.value.customerName else "Customer",
            customerPhone = if (_bookingDraft.value.customerPhone.isNotBlank()) _bookingDraft.value.customerPhone else "0300-1234567"
        )
        navigateTo(AppScreen.BookAppointment)
    }

    fun toggleFavorite(salonId: String) {
        val isFav = favoriteIds.value.contains(salonId)
        viewModelScope.launch {
            repository.toggleFavorite(salonId, isFav)
        }
    }

    // Authentication methods
    fun loginCustomer(phone: String, pass: String): Result<UserAccount> {
        return authRepository.loginCustomer(phone, pass)
    }

    fun registerCustomer(name: String, phone: String, pass: String): Result<UserAccount> {
        return authRepository.registerCustomer(name, phone, pass)
    }

    fun loginAdmin(adminId: String, pinOrPass: String): Result<UserAccount> {
        return authRepository.loginAdmin(adminId, pinOrPass)
    }

    fun loginDemoCustomer() {
        authRepository.loginDemoCustomer()
    }

    fun loginDemoAdmin() {
        authRepository.loginDemoAdmin()
    }

    fun logout() {
        authRepository.logout()
    }

    // Admin Queue & Salon Management
    fun updateQueueChairs(delta: Int) {
        authRepository.updateQueueChairs(delta)
    }

    fun updateQueueWaiting(delta: Int) {
        authRepository.updateQueueWaiting(delta)
    }

    fun updateQueueWaitTime(minutes: Int) {
        authRepository.updateQueueWaitTime(minutes)
    }

    fun updateSalonOperatingStatus(status: String) {
        authRepository.updateOperatingStatus(status)
    }

    fun markAppointmentCompleted(appointmentId: Long) {
        viewModelScope.launch {
            repository.markCompleted(appointmentId)
        }
    }

    fun addWalkInAppointment(customerName: String, customerPhone: String, service: SalonService): kotlinx.coroutines.Job {
        return viewModelScope.launch {
            addWalkInAppointmentDirect(customerName, customerPhone, service)
        }
    }

    suspend fun addWalkInAppointmentDirect(customerName: String, customerPhone: String, service: SalonService): Long {
        return repository.createAppointment(
            salon = haiderSalon,
            serviceNames = service.name,
            totalPricePkr = service.pricePkr,
            totalDurationMinutes = service.durationMinutes,
            stylistName = "Ustad Haider Ali",
            appointmentDate = "Today (Walk-In)",
            appointmentTime = "Immediate (اگلی کرسی)",
            customerName = customerName,
            customerPhone = customerPhone,
            customerNotes = "Walk-in customer at shop",
            paymentMethodTitle = "Cash at Counter"
        )
    }
}
