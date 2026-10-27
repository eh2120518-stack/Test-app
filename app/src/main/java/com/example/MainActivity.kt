package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.ContentCut
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.AppointmentStatus
import com.example.ui.AppScreen
import com.example.ui.SalonViewModel
import com.example.ui.components.RescheduleDialog
import com.example.ui.screens.BookAppointmentScreen
import com.example.ui.screens.BookingFlowScreen
import com.example.ui.screens.FavoritesScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.MyAppointmentsScreen
import com.example.ui.screens.SalonDetailScreen
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.outlined.AdminPanelSettings
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.filled.Preview
import androidx.compose.material.icons.outlined.Preview
import com.example.data.model.UserRole
import com.example.ui.screens.AdminDashboardScreen
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.CustomerProfileScreen
import com.example.ui.screens.SalonSearchScreen
import com.example.ui.screens.ServicesScreen
import com.example.ui.theme.MyApplicationTheme
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val viewModel: SalonViewModel = viewModel()
                MainAppContent(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MainAppContent(viewModel: SalonViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val appointments by viewModel.allAppointments.collectAsState()
    val reschedulingAppointment by viewModel.reschedulingAppointment.collectAsState()
    val userRole by viewModel.userRole.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()

    val confirmedCount = appointments.count { it.status == AppointmentStatus.CONFIRMED.name }

    // Check if bottom bar should be shown
    val showBottomBar = currentScreen is AppScreen.Home ||
            currentScreen is AppScreen.Services ||
            currentScreen is AppScreen.BookAppointment ||
            currentScreen is AppScreen.Search ||
            currentScreen is AppScreen.MyAppointments ||
            currentScreen is AppScreen.Favorites ||
            currentScreen is AppScreen.AdminDashboard ||
            currentScreen is AppScreen.CustomerProfile

    BackHandler(enabled = !showBottomBar) {
        viewModel.navigateBack()
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 4.dp
                ) {
                    if (userRole == UserRole.ADMIN) {
                        // ================= ADMIN BOTTOM NAVIGATION =================
                        NavigationBarItem(
                            selected = currentScreen is AppScreen.AdminDashboard,
                            onClick = { viewModel.navigateTo(AppScreen.AdminDashboard) },
                            icon = {
                                Icon(
                                    if (currentScreen is AppScreen.AdminDashboard) Icons.Filled.AdminPanelSettings else Icons.Outlined.AdminPanelSettings,
                                    contentDescription = "Admin Panel"
                                )
                            },
                            label = { Text("Admin", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer
                            ),
                            modifier = Modifier.testTag("nav_admin_dashboard")
                        )

                        NavigationBarItem(
                            selected = currentScreen is AppScreen.MyAppointments,
                            onClick = { viewModel.navigateTo(AppScreen.MyAppointments) },
                            icon = {
                                if (confirmedCount > 0) {
                                    BadgedBox(badge = {
                                        Badge(containerColor = MaterialTheme.colorScheme.primary) {
                                            Text("$confirmedCount")
                                        }
                                    }) {
                                        Icon(
                                            if (currentScreen is AppScreen.MyAppointments) Icons.Filled.CalendarMonth else Icons.Outlined.CalendarMonth,
                                            contentDescription = "Bookings"
                                        )
                                    }
                                } else {
                                    Icon(
                                        if (currentScreen is AppScreen.MyAppointments) Icons.Filled.CalendarMonth else Icons.Outlined.CalendarMonth,
                                        contentDescription = "Bookings"
                                    )
                                }
                            },
                            label = { Text("Bookings", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer
                            ),
                            modifier = Modifier.testTag("nav_admin_appointments")
                        )

                        NavigationBarItem(
                            selected = currentScreen is AppScreen.Services,
                            onClick = { viewModel.navigateTo(AppScreen.Services) },
                            icon = {
                                Icon(
                                    if (currentScreen is AppScreen.Services) Icons.Filled.ContentCut else Icons.Outlined.ContentCut,
                                    contentDescription = "Services"
                                )
                            },
                            label = { Text("Services", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer
                            ),
                            modifier = Modifier.testTag("nav_admin_services")
                        )

                        NavigationBarItem(
                            selected = currentScreen is AppScreen.Home,
                            onClick = { viewModel.navigateTo(AppScreen.Home) },
                            icon = {
                                Icon(
                                    if (currentScreen is AppScreen.Home) Icons.Filled.Preview else Icons.Outlined.Preview,
                                    contentDescription = "Customer View"
                                )
                            },
                            label = { Text("Shop View", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer
                            ),
                            modifier = Modifier.testTag("nav_admin_customer_view")
                        )

                        NavigationBarItem(
                            selected = currentScreen is AppScreen.Favorites,
                            onClick = { viewModel.navigateTo(AppScreen.Favorites) },
                            icon = {
                                Icon(
                                    if (currentScreen is AppScreen.Favorites) Icons.Filled.Info else Icons.Outlined.Info,
                                    contentDescription = "Salon Info"
                                )
                            },
                            label = { Text("Info", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer
                            ),
                            modifier = Modifier.testTag("nav_admin_info")
                        )
                    } else {
                        // ================= CUSTOMER BOTTOM NAVIGATION =================
                        NavigationBarItem(
                            selected = currentScreen is AppScreen.Home,
                            onClick = { viewModel.navigateTo(AppScreen.Home) },
                            icon = {
                                Icon(
                                    if (currentScreen is AppScreen.Home) Icons.Filled.Home else Icons.Outlined.Home,
                                    contentDescription = "Home"
                                )
                            },
                            label = { Text("Salon", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer
                            ),
                            modifier = Modifier.testTag("nav_home")
                        )

                        NavigationBarItem(
                            selected = currentScreen is AppScreen.Services,
                            onClick = { viewModel.navigateTo(AppScreen.Services) },
                            icon = {
                                Icon(
                                    if (currentScreen is AppScreen.Services) Icons.Filled.ContentCut else Icons.Outlined.ContentCut,
                                    contentDescription = "Services"
                                )
                            },
                            label = { Text("Services", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer
                            ),
                            modifier = Modifier.testTag("nav_services")
                        )

                        NavigationBarItem(
                            selected = currentScreen is AppScreen.BookAppointment,
                            onClick = { viewModel.openAppointmentForm() },
                            icon = {
                                Icon(
                                    if (currentScreen is AppScreen.BookAppointment) Icons.Filled.CalendarToday else Icons.Outlined.CalendarToday,
                                    contentDescription = "Book Appointment"
                                )
                            },
                            label = { Text("Book Now", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer
                            ),
                            modifier = Modifier.testTag("nav_book_form")
                        )

                        NavigationBarItem(
                            selected = currentScreen is AppScreen.MyAppointments,
                            onClick = { viewModel.navigateTo(AppScreen.MyAppointments) },
                            icon = {
                                if (confirmedCount > 0) {
                                    BadgedBox(badge = {
                                        Badge(containerColor = MaterialTheme.colorScheme.primary) {
                                            Text("$confirmedCount")
                                        }
                                    }) {
                                        Icon(
                                            if (currentScreen is AppScreen.MyAppointments) Icons.Filled.CalendarMonth else Icons.Outlined.CalendarMonth,
                                            contentDescription = "Bookings"
                                        )
                                    }
                                } else {
                                    Icon(
                                        if (currentScreen is AppScreen.MyAppointments) Icons.Filled.CalendarMonth else Icons.Outlined.CalendarMonth,
                                        contentDescription = "Bookings"
                                    )
                                }
                            },
                            label = { Text("Bookings", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer
                            ),
                            modifier = Modifier.testTag("nav_appointments")
                        )

                        NavigationBarItem(
                            selected = currentScreen is AppScreen.CustomerProfile || currentScreen is AppScreen.Auth,
                            onClick = {
                                if (currentUser != null) {
                                    viewModel.navigateTo(AppScreen.CustomerProfile)
                                } else {
                                    viewModel.navigateTo(AppScreen.Auth(targetTab = 0))
                                }
                            },
                            icon = {
                                Icon(
                                    if (currentScreen is AppScreen.CustomerProfile || currentScreen is AppScreen.Auth) Icons.Filled.Person else Icons.Outlined.Person,
                                    contentDescription = "Account"
                                )
                            },
                            label = {
                                Text(
                                    text = if (currentUser != null) "Account" else "Login",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer
                            ),
                            modifier = Modifier.testTag("nav_account")
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (val screen = currentScreen) {
                is AppScreen.Home -> HomeScreen(viewModel = viewModel)
                is AppScreen.Services -> ServicesScreen(viewModel = viewModel)
                is AppScreen.BookAppointment -> BookAppointmentScreen(viewModel = viewModel)
                is AppScreen.Search -> SalonSearchScreen(viewModel = viewModel)
                is AppScreen.SalonDetail -> SalonDetailScreen(salonId = screen.salonId, viewModel = viewModel)
                is AppScreen.BookingFlow -> BookingFlowScreen(step = screen.step, viewModel = viewModel)
                is AppScreen.MyAppointments -> MyAppointmentsScreen(viewModel = viewModel)
                is AppScreen.Favorites -> FavoritesScreen(viewModel = viewModel)
                is AppScreen.Auth -> AuthScreen(viewModel = viewModel, initialTab = screen.targetTab)
                is AppScreen.AdminDashboard -> AdminDashboardScreen(viewModel = viewModel)
                is AppScreen.CustomerProfile -> CustomerProfileScreen(viewModel = viewModel)
                is AppScreen.PassVoucher -> {}
            }

            // Reschedule Dialog overlay
            reschedulingAppointment?.let { appointment ->
                RescheduleDialog(
                    appointment = appointment,
                    availableDates = viewModel.availableDates,
                    availableTimeSlots = viewModel.availableTimeSlots,
                    onDismiss = { viewModel.closeRescheduleDialog() },
                    onConfirm = { newDate, newTime ->
                        viewModel.confirmReschedule(appointment.id, newDate, newTime)
                    }
                )
            }
        }
    }
}
