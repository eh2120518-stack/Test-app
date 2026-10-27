package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.PakistaniCity
import com.example.data.model.SalonGenderType
import com.example.data.repository.SalonCatalog
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import kotlinx.coroutines.flow.first
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Haider Salon", appName)
    }

    @Test
    fun `verify salon catalog has Pakistani locations`() {
        val salons = SalonCatalog.sampleSalons
        assertEquals(1, salons.size)

        val lalamusaSalons = salons.filter { it.city == PakistaniCity.LALAMUSA }
        assertEquals(1, lalamusaSalons.size)

        val haiderSalon = SalonCatalog.getSalonById("haider_salon_naindowal")
        assertNotNull(haiderSalon)
        assertEquals("Haider Salon (حیدر سیلون)", haiderSalon!!.name)
        assertTrue(haiderSalon.areaAddress.contains("Naindowal"))
        assertTrue(haiderSalon.services.isNotEmpty())
    }

    @Test
    fun `verify services list includes haircuts facials and beard grooming with prices`() {
        val haiderSalon = SalonCatalog.getSalonById("haider_salon_naindowal")!!
        val services = haiderSalon.services

        val haircuts = services.filter { it.category.contains("Haircut", ignoreCase = true) }
        assertTrue("Haircuts should be available", haircuts.isNotEmpty())
        assertTrue("Haircuts should have valid prices", haircuts.all { it.pricePkr > 0 })

        val facials = services.filter { it.category.contains("Facial", ignoreCase = true) }
        assertTrue("Facials should be available", facials.isNotEmpty())
        assertTrue("Facials should have valid prices", facials.all { it.pricePkr > 0 })

        val beards = services.filter { it.category.contains("Beard", ignoreCase = true) || it.category.contains("Shave", ignoreCase = true) }
        assertTrue("Beard grooming should be available", beards.isNotEmpty())
        assertTrue("Beard grooming should have valid prices", beards.all { it.pricePkr > 0 })
    }

    @Test
    fun `verify booking form creates appointment with contact details and selected services`() = kotlinx.coroutines.test.runTest {
        val app = ApplicationProvider.getApplicationContext<android.app.Application>()
        val viewModel = com.example.ui.SalonViewModel(app)

        val haiderSalon = viewModel.haiderSalon
        val haircut = haiderSalon.services.first { it.category.contains("Haircut", ignoreCase = true) }

        // Open appointment form with haircut preselected
        viewModel.openAppointmentForm(haircut)
        assertEquals(com.example.ui.AppScreen.BookAppointment, viewModel.currentScreen.value)

        // Select date, time slot, and update customer contact details
        val chosenDate = viewModel.availableDates.first()
        val chosenTime = viewModel.availableTimeSlots.first()
        viewModel.selectDateInDraft(chosenDate)
        viewModel.selectTimeSlotInDraft(chosenTime)
        viewModel.updateCustomerDetails(
            name = "Muhammad Usman",
            phone = "0301-7654321",
            notes = "Please keep hair length medium"
        )

        // Submit form
        val appointment = viewModel.submitAppointmentFromForm()
        assertNotNull("Created appointment should not be null", appointment)
        assertEquals("Muhammad Usman", appointment!!.customerName)
        assertEquals("0301-7654321", appointment.customerPhone)
        assertEquals(chosenDate, appointment.appointmentDate)
        assertEquals(chosenTime, appointment.appointmentTime)
        assertTrue(appointment.serviceNames.contains(haircut.name))
        assertEquals(haircut.pricePkr, appointment.totalPricePkr)
        assertTrue(appointment.bookingReference.startsWith("PK-HDR-"))
    }

    @Test
    fun `verify customer can book appointment directly with chosen master stylist`() = kotlinx.coroutines.test.runTest {
        val app = ApplicationProvider.getApplicationContext<android.app.Application>()
        val viewModel = com.example.ui.SalonViewModel(app)

        val ustadHaider = viewModel.haiderSalon.stylists.first { it.name.contains("Haider", ignoreCase = true) }
        viewModel.bookWithStylist(ustadHaider)

        assertEquals(com.example.ui.AppScreen.BookAppointment, viewModel.currentScreen.value)
        val draft = viewModel.bookingDraft.value
        assertEquals(ustadHaider, draft.selectedStylist)
        assertTrue(draft.selectedServices.isNotEmpty())
    }

    @Test
    fun `verify customer login and registration work with Customer role`() = kotlinx.coroutines.test.runTest {
        val app = ApplicationProvider.getApplicationContext<android.app.Application>()
        val viewModel = com.example.ui.SalonViewModel(app)

        // Customer Login with demo phone
        val loginResult = viewModel.loginCustomer("0300-4567890", "customer123")
        assertTrue("Customer login should succeed", loginResult.isSuccess)
        val user = loginResult.getOrNull()
        assertNotNull(user)
        assertEquals(com.example.data.model.UserRole.CUSTOMER, user!!.role)
        assertEquals(com.example.data.model.UserRole.CUSTOMER, viewModel.userRole.value)

        // Register new customer
        val regResult = viewModel.registerCustomer(
            name = "Hamza Malik",
            phone = "0321-9876543",
            pass = "pass1234"
        )
        assertTrue("Customer registration should succeed", regResult.isSuccess)
        val newUser = regResult.getOrNull()
        assertNotNull(newUser)
        assertEquals("Hamza Malik", newUser!!.name)
        assertEquals(com.example.data.model.UserRole.CUSTOMER, newUser.role)
    }

    @Test
    fun `verify admin login separates admin portal from customer access with secret PIN`() = kotlinx.coroutines.test.runTest {
        val app = ApplicationProvider.getApplicationContext<android.app.Application>()
        val viewModel = com.example.ui.SalonViewModel(app)

        // Invalid PIN should fail
        val invalidResult = viewModel.loginAdmin("haider_admin", "0000")
        assertTrue("Invalid PIN should be rejected", invalidResult.isFailure)

        // Correct PIN 7860 for Ustad Haider should succeed
        val validResult = viewModel.loginAdmin("haider_admin", "7860")
        assertTrue("Correct PIN 7860 should be accepted", validResult.isSuccess)
        val admin = validResult.getOrNull()
        assertNotNull(admin)
        assertEquals(com.example.data.model.UserRole.ADMIN, admin!!.role)
        assertEquals(com.example.data.model.UserRole.ADMIN, viewModel.userRole.value)

        // Admin queue controls
        viewModel.updateQueueChairs(1)
        assertEquals(4, viewModel.salonQueueState.value.activeChairs)

        viewModel.updateQueueWaitTime(25)
        assertEquals(25, viewModel.salonQueueState.value.estimatedWaitMinutes)

        viewModel.updateSalonOperatingStatus("PRAYER_BREAK")
        assertEquals("PRAYER_BREAK", viewModel.salonQueueState.value.operatingStatus)
    }

    @Test
    fun `verify admin can add walk-in customer directly into live appointments`() = kotlinx.coroutines.test.runTest {
        val app = ApplicationProvider.getApplicationContext<android.app.Application>()
        val viewModel = com.example.ui.SalonViewModel(app)

        val service = viewModel.allServices.first()
        viewModel.addWalkInAppointmentDirect(
            customerName = "Zaid Gujjar",
            customerPhone = "0302-1122334",
            service = service
        )

        val db = com.example.data.local.AppDatabase.getDatabase(app)
        val allFromDb = db.appointmentDao().getAppointmentsSnapshot()
        val walkIn = allFromDb.firstOrNull { it.customerName == "Zaid Gujjar" }
        assertNotNull("Walk-in appointment should exist", walkIn)
        assertEquals(service.pricePkr, walkIn!!.totalPricePkr)
    }
}

