package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.AppointmentEntity
import com.example.data.model.AppointmentStatus
import com.example.ui.AppScreen
import com.example.ui.SalonViewModel
import com.example.ui.components.DigitalPassVoucherCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyAppointmentsScreen(
    viewModel: SalonViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.navigateBack()
    }

    val context = LocalContext.current
    val appointments by viewModel.allAppointments.collectAsState()
    var selectedFilterIndex by remember { mutableIntStateOf(0) }

    val filterOptions = listOf("All", "Upcoming", "Completed", "Cancelled")

    val filteredList = when (selectedFilterIndex) {
        1 -> appointments.filter { it.status == AppointmentStatus.CONFIRMED.name }
        2 -> appointments.filter { it.status == AppointmentStatus.COMPLETED.name }
        3 -> appointments.filter { it.status == AppointmentStatus.CANCELLED.name }
        else -> appointments
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("My Appointments", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text("میری بکنگز", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            ScrollableTabRow(
                selectedTabIndex = selectedFilterIndex,
                containerColor = MaterialTheme.colorScheme.surface,
                edgePadding = 16.dp
            ) {
                filterOptions.forEachIndexed { index, title ->
                    val count = when (index) {
                        1 -> appointments.count { it.status == AppointmentStatus.CONFIRMED.name }
                        2 -> appointments.count { it.status == AppointmentStatus.COMPLETED.name }
                        3 -> appointments.count { it.status == AppointmentStatus.CANCELLED.name }
                        else -> appointments.size
                    }
                    Tab(
                        selected = selectedFilterIndex == index,
                        onClick = { selectedFilterIndex = index },
                        text = {
                            Text(
                                text = "$title ($count)",
                                fontWeight = if (selectedFilterIndex == index) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    )
                }
            }

            if (filteredList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                            modifier = Modifier.size(56.dp)
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = "No appointments in this category",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Text(
                            text = "Book a haircut, facial, or makeover in seconds",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { viewModel.navigateTo(AppScreen.Home) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            ),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.ContentCut, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Explore Pakistani Salons")
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(filteredList, key = { it.id }) { item ->
                        DigitalPassVoucherCard(
                            appointment = item,
                            onCallClick = {
                                val intent = Intent(Intent.ACTION_DIAL).apply {
                                    data = Uri.parse("tel:${item.salonPhone}")
                                }
                                context.startActivity(intent)
                            },
                            onRescheduleClick = {
                                viewModel.openRescheduleDialog(item)
                            },
                            onCancelClick = {
                                viewModel.cancelAppointment(item.id)
                            }
                        )
                    }
                }
            }
        }
    }
}
