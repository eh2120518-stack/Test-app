package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.EditCalendar
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.AppointmentEntity
import com.example.data.model.AppointmentStatus
import com.example.ui.AppScreen
import com.example.ui.SalonViewModel
import com.example.ui.theme.AccentGold

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AdminDashboardScreen(
    viewModel: SalonViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val appointments by viewModel.allAppointments.collectAsState()
    val queueState by viewModel.salonQueueState.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()

    var selectedFilterIndex by remember { mutableIntStateOf(0) }
    val filterTabs = listOf("All Bookings", "Confirmed / Active", "Completed", "Cancelled")

    // State for Add Walk-In Customer dialog
    var showWalkInDialog by remember { mutableStateOf(false) }
    var walkInName by remember { mutableStateOf("") }
    var walkInPhone by remember { mutableStateOf("0300-") }
    var walkInServiceIndex by remember { mutableIntStateOf(0) }

    val allServices = viewModel.allServices

    val filteredAppointments = when (selectedFilterIndex) {
        1 -> appointments.filter { it.status == AppointmentStatus.CONFIRMED.name }
        2 -> appointments.filter { it.status == AppointmentStatus.COMPLETED.name }
        3 -> appointments.filter { it.status == AppointmentStatus.CANCELLED.name }
        else -> appointments
    }

    val confirmedCount = appointments.count { it.status == AppointmentStatus.CONFIRMED.name }
    val completedCount = appointments.count { it.status == AppointmentStatus.COMPLETED.name }
    val totalRevenue = appointments
        .filter { it.status == AppointmentStatus.COMPLETED.name || it.status == AppointmentStatus.CONFIRMED.name }
        .sumOf { it.totalPricePkr }

    BackHandler {
        viewModel.navigateBack()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Admin Control Panel",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                color = AccentGold,
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = "ADMIN",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = "استاد حیدر سیلون مینجمنٹ (Naindowal, Lalamusa)",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    // Preview customer app button
                    IconButton(
                        onClick = { viewModel.navigateTo(AppScreen.Home) },
                        modifier = Modifier.testTag("admin_preview_customer_btn")
                    ) {
                        Icon(
                            Icons.Default.Visibility,
                            contentDescription = "Customer View",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    // Logout / Switch button
                    IconButton(
                        onClick = {
                            viewModel.logout()
                            Toast.makeText(context, "Admin Logged Out", Toast.LENGTH_SHORT).show()
                            viewModel.navigateTo(AppScreen.Home)
                        },
                        modifier = Modifier.testTag("admin_logout_btn")
                    ) {
                        Icon(
                            Icons.Default.Logout,
                            contentDescription = "Logout",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // 1. Admin Identity Card & Quick Switch Bar
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, AccentGold.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF2E7D32)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AdminPanelSettings,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(26.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = currentUser?.name ?: "Ustad Haider Ali",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                                Text(
                                    text = "مالک و استاد ہیئر سٹائلسٹ (Master Access)",
                                    fontSize = 11.sp,
                                    color = AccentGold,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = "فون: 0300-8451920",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Button(
                            onClick = { showWalkInDialog = true },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("admin_add_walkin_btn")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("+ واک ان", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // 2. Business Overview Stats
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StatCard(
                        title = "Active Queue",
                        urdu = "موجودہ قطار",
                        value = "$confirmedCount",
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Completed",
                        urdu = "مکمل خدمات",
                        value = "$completedCount",
                        containerColor = Color(0xFFE8F5E9),
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Est. Revenue",
                        urdu = "تخمینہ آمدنی",
                        value = "Rs $totalRevenue",
                        containerColor = Color(0xFFFFF8E1),
                        modifier = Modifier.weight(1.3f)
                    )
                }
            }

            // 3. Live Salon Queue & Chair Controls
            item {
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.Speed,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Live Salon Queue & Chairs",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                            }

                            // Salon Status
                            Surface(
                                color = when (queueState.operatingStatus) {
                                    "OPEN" -> Color(0xFFE8F5E9)
                                    "PRAYER_BREAK" -> Color(0xFFFFF3E0)
                                    else -> Color(0xFFFFEBEE)
                                },
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = when (queueState.operatingStatus) {
                                        "OPEN" -> "کھلا ہے Open"
                                        "PRAYER_BREAK" -> "نماز وقفہ Break"
                                        else -> "بند Closed"
                                    },
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = when (queueState.operatingStatus) {
                                        "OPEN" -> Color(0xFF2E7D32)
                                        "PRAYER_BREAK" -> Color(0xFFE65100)
                                        else -> Color(0xFFC62828)
                                    },
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Controls Row: Active Chairs & Waiting Customers
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Chairs Stepper
                            Surface(
                                modifier = Modifier.weight(1f),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(10.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text("Active Chairs (کرسیاں)", fontSize = 11.sp, fontWeight = FontWeight.Medium)
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        modifier = Modifier.padding(top = 4.dp)
                                    ) {
                                        IconButton(
                                            onClick = { viewModel.updateQueueChairs(-1) },
                                            modifier = Modifier.size(28.dp).testTag("admin_chair_minus")
                                        ) {
                                            Text("-", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                                        }
                                        Text(
                                            text = "${queueState.activeChairs}",
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        IconButton(
                                            onClick = { viewModel.updateQueueChairs(+1) },
                                            modifier = Modifier.size(28.dp).testTag("admin_chair_plus")
                                        ) {
                                            Text("+", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }

                            // Waiting Stepper
                            Surface(
                                modifier = Modifier.weight(1f),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(10.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text("Waiting (انتظار میں گاہک)", fontSize = 11.sp, fontWeight = FontWeight.Medium)
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        modifier = Modifier.padding(top = 4.dp)
                                    ) {
                                        IconButton(
                                            onClick = { viewModel.updateQueueWaiting(-1) },
                                            modifier = Modifier.size(28.dp).testTag("admin_wait_minus")
                                        ) {
                                            Text("-", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                                        }
                                        Text(
                                            text = "${queueState.customersWaiting}",
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        IconButton(
                                            onClick = { viewModel.updateQueueWaiting(+1) },
                                            modifier = Modifier.size(28.dp).testTag("admin_wait_plus")
                                        ) {
                                            Text("+", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Wait Time chips
                        Text(
                            text = "Estimated Wait Time (گاہکوں کو دکھانے کے لیے ویٹنگ ٹائم):",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf(10, 15, 20, 30, 45).forEach { mins ->
                                val isSelected = queueState.estimatedWaitMinutes == mins
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { viewModel.updateQueueWaitTime(mins) },
                                    label = { Text("${mins}m") },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                                        selectedLabelColor = Color.White
                                    ),
                                    modifier = Modifier.testTag("admin_chip_${mins}m")
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Status Toggles
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            OutlinedButton(
                                onClick = { viewModel.updateSalonOperatingStatus("OPEN") },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = if (queueState.operatingStatus == "OPEN") Color(0xFFE8F5E9) else Color.Transparent
                                )
                            ) {
                                Text("کھلا ہے", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }

                            OutlinedButton(
                                onClick = { viewModel.updateSalonOperatingStatus("PRAYER_BREAK") },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = if (queueState.operatingStatus == "PRAYER_BREAK") Color(0xFFFFF3E0) else Color.Transparent
                                )
                            ) {
                                Text("نماز وقفہ", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }

                            OutlinedButton(
                                onClick = { viewModel.updateSalonOperatingStatus("CLOSED") },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = if (queueState.operatingStatus == "CLOSED") Color(0xFFFFEBEE) else Color.Transparent
                                )
                            ) {
                                Text("بند", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // 4. Appointments Filter Tabs
            item {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Customer Appointments (${filteredAppointments.size})",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "کسٹمر بکنگز ریکارڈ",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    ScrollableTabRow(
                        selectedTabIndex = selectedFilterIndex,
                        containerColor = MaterialTheme.colorScheme.surface,
                        edgePadding = 0.dp
                    ) {
                        filterTabs.forEachIndexed { index, title ->
                            Tab(
                                selected = selectedFilterIndex == index,
                                onClick = { selectedFilterIndex = index },
                                text = {
                                    Text(
                                        text = title,
                                        fontWeight = if (selectedFilterIndex == index) FontWeight.Bold else FontWeight.Normal,
                                        fontSize = 12.sp
                                    )
                                }
                            )
                        }
                    }
                }
            }

            // 5. Customer Appointments List
            if (filteredAppointments.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Default.ContentCut,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                modifier = Modifier.size(40.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "اس کیٹیگری میں فی الحال کوئی اپوائنٹمنٹ نہیں ہے",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                items(filteredAppointments, key = { it.id }) { appt ->
                    AdminAppointmentCard(
                        appointment = appt,
                        onMarkCompleted = {
                            viewModel.markAppointmentCompleted(appt.id)
                            Toast.makeText(context, "بکنگ مکمل کر دی گئی! Rs ${appt.totalPricePkr}", Toast.LENGTH_SHORT).show()
                        },
                        onReschedule = {
                            viewModel.openRescheduleDialog(appt)
                        },
                        onCancel = {
                            viewModel.cancelAppointment(appt.id)
                            Toast.makeText(context, "بکنگ منسوخ کر دی گئی", Toast.LENGTH_SHORT).show()
                        },
                        onCall = {
                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${appt.customerPhone}"))
                            context.startActivity(intent)
                        },
                        onWhatsApp = {
                            val cleanPhone = appt.customerPhone.replace("-", "").replace(" ", "")
                            val pkrPhone = if (cleanPhone.startsWith("0")) "92" + cleanPhone.substring(1) else cleanPhone
                            val msg = "السلام علیکم ${appt.customerName}! حیدر سیلون نیندووال لالہ موسیٰ سے استاد حیدر۔ آپ کی ${appt.serviceNames} کی اپوائنٹمنٹ برائے ${appt.appointmentDate} بوقت ${appt.appointmentTime} کنفرم ہے۔ شکریہ!"
                            val uri = Uri.parse("https://api.whatsapp.com/send?phone=$pkrPhone&text=${Uri.encode(msg)}")
                            val intent = Intent(Intent.ACTION_VIEW, uri)
                            context.startActivity(intent)
                        }
                    )
                }
            }
        }
    }

    // Dialog for Adding Walk-In Customer
    if (showWalkInDialog) {
        AlertDialog(
            onDismissRequest = { showWalkInDialog = false },
            title = {
                Text(
                    text = "واک ان کسٹمر شامل کریں (Add Walk-In)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "دکان پر آنے والے گاہک کو قطار اور سسٹم میں درج کریں:",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    OutlinedTextField(
                        value = walkInName,
                        onValueChange = { walkInName = it },
                        label = { Text("Customer Name (گاہک کا نام)") },
                        placeholder = { Text("مثال: بلال گجر / محمد زید") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("walkin_input_name")
                    )

                    OutlinedTextField(
                        value = walkInPhone,
                        onValueChange = { walkInPhone = it },
                        label = { Text("Phone Number (اختیاری)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("walkin_input_phone")
                    )

                    Text("سروس منتخب کریں (Select Service):", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        for ((index, svc) in allServices.take(5).withIndex()) {
                            FilterChip(
                                selected = walkInServiceIndex == index,
                                onClick = { walkInServiceIndex = index },
                                label = { Text("${svc.nameUrdu} (Rs ${svc.pricePkr})", fontSize = 11.sp) }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val chosenService = allServices.getOrNull(walkInServiceIndex) ?: allServices.first()
                        val finalName = walkInName.ifBlank { "Walk-In Customer" }
                        val finalPhone = walkInPhone.ifBlank { "0300-0000000" }

                        viewModel.addWalkInAppointment(
                            customerName = finalName,
                            customerPhone = finalPhone,
                            service = chosenService
                        )
                        Toast.makeText(context, "$finalName کو قطار میں شامل کر دیا گیا!", Toast.LENGTH_SHORT).show()
                        showWalkInDialog = false
                        walkInName = ""
                    },
                    modifier = Modifier.testTag("walkin_confirm_btn")
                ) {
                    Text("شامل کریں (Add to Queue)", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showWalkInDialog = false }) {
                    Text("منسوخ Cancel")
                }
            }
        )
    }
}

@Composable
private fun StatCard(
    title: String,
    urdu: String,
    value: String,
    containerColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = containerColor,
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = title, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
            Text(text = urdu, fontSize = 9.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                fontSize = 17.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun AdminAppointmentCard(
    appointment: AppointmentEntity,
    onMarkCompleted: () -> Unit,
    onReschedule: () -> Unit,
    onCancel: () -> Unit,
    onCall: () -> Unit,
    onWhatsApp: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        border = BorderStroke(
            1.dp,
            if (appointment.status == AppointmentStatus.CONFIRMED.name)
                MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
            else
                MaterialTheme.colorScheme.outlineVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Customer Header & Token
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = appointment.customerName.take(1).uppercase(),
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 16.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = appointment.customerName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        Text(
                            text = "📞 ${appointment.customerPhone}",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                // Status Badge
                Surface(
                    color = when (appointment.status) {
                        AppointmentStatus.CONFIRMED.name -> Color(0xFFE8F5E9)
                        AppointmentStatus.COMPLETED.name -> Color(0xFFE3F2FD)
                        else -> Color(0xFFFFEBEE)
                    },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = when (appointment.status) {
                            AppointmentStatus.CONFIRMED.name -> "Active کنفرم"
                            AppointmentStatus.COMPLETED.name -> "Completed مکمل"
                            else -> "Cancelled منسوخ"
                        },
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = when (appointment.status) {
                            AppointmentStatus.CONFIRMED.name -> Color(0xFF2E7D32)
                            AppointmentStatus.COMPLETED.name -> Color(0xFF1565C0)
                            else -> Color(0xFFC62828)
                        },
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Service details & Price
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = appointment.serviceNames,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "سٹائلسٹ: ${appointment.stylistName} • ٹوکن: ${appointment.bookingReference}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Text(
                    text = "Rs ${appointment.totalPricePkr}",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Date & Time
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Schedule,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${appointment.appointmentDate} • ${appointment.appointmentTime}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (appointment.customerNotes.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "نوٹ: \"${appointment.customerNotes}\"",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.85f)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Admin Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Call Button
                OutlinedButton(
                    onClick = onCall,
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("admin_call_${appointment.id}")
                ) {
                    Icon(Icons.Default.Call, contentDescription = "Call", modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("کال", fontSize = 11.sp)
                }

                // WhatsApp Button
                OutlinedButton(
                    onClick = onWhatsApp,
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFF2E7D32)
                    ),
                    modifier = Modifier.testTag("admin_whatsapp_${appointment.id}")
                ) {
                    Text("WhatsApp", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                if (appointment.status == AppointmentStatus.CONFIRMED.name) {
                    // Reschedule
                    OutlinedButton(
                        onClick = onReschedule,
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.EditCalendar, contentDescription = null, modifier = Modifier.size(14.dp))
                    }

                    // Complete Service Button
                    Button(
                        onClick = onMarkCompleted,
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                        modifier = Modifier.weight(1f).testTag("admin_complete_${appointment.id}")
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("مکمل کریں", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                } else if (appointment.status == AppointmentStatus.COMPLETED.name) {
                    Row(
                        modifier = Modifier.weight(1f),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Color(0xFF2E7D32),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "خدمت مکمل ہو چکی",
                            fontSize = 11.sp,
                            color = Color(0xFF2E7D32),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
