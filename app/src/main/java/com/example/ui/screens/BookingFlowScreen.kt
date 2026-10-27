package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PaymentMethod
import com.example.ui.AppScreen
import com.example.ui.SalonViewModel
import com.example.ui.components.DigitalPassVoucherCard
import com.example.ui.components.ServiceSelectableCard
import com.example.ui.components.StylistSelectCard
import com.example.ui.theme.AccentGold
import com.example.ui.theme.StatusGreen

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun BookingFlowScreen(
    step: Int,
    viewModel: SalonViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        if (step > 1 && step < 4) {
            viewModel.advanceBookingStep(step - 1)
        } else {
            viewModel.navigateBack()
        }
    }

    val context = LocalContext.current
    val draft by viewModel.bookingDraft.collectAsState()
    val salon = draft.salon

    if (salon == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No salon selected")
        }
        return
    }

    val totalPkr = draft.selectedServices.sumOf { it.pricePkr }
    val totalMinutes = draft.selectedServices.sumOf { it.durationMinutes }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = when (step) {
                                1 -> "1. Select Services & Stylist"
                                2 -> "2. Choose Date & Time"
                                3 -> "3. Customer & Payment"
                                else -> "Booking Confirmed! مبارک ہو"
                            },
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = salon.name,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    if (step < 4) {
                        IconButton(onClick = {
                            if (step > 1) {
                                viewModel.advanceBookingStep(step - 1)
                            } else {
                                viewModel.navigateBack()
                            }
                        }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        bottomBar = {
            if (step < 4) {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp,
                    shadowElevation = 8.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Total (${draft.selectedServices.size} services)",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "₨ $totalPkr",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Button(
                            onClick = {
                                when (step) {
                                    1 -> {
                                        if (draft.selectedServices.isNotEmpty()) {
                                            viewModel.advanceBookingStep(2)
                                        }
                                    }
                                    2 -> {
                                        if (draft.selectedDate.isNotBlank() && draft.selectedTimeSlot.isNotBlank()) {
                                            viewModel.advanceBookingStep(3)
                                        }
                                    }
                                    3 -> {
                                        viewModel.confirmAppointment()
                                    }
                                }
                            },
                            enabled = when (step) {
                                1 -> draft.selectedServices.isNotEmpty()
                                2 -> draft.selectedDate.isNotBlank() && draft.selectedTimeSlot.isNotBlank()
                                3 -> !draft.isSubmitting && draft.customerName.isNotBlank() && draft.customerPhone.isNotBlank()
                                else -> true
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            ),
                            modifier = Modifier.testTag("booking_continue_btn")
                        ) {
                            if (draft.isSubmitting) {
                                CircularProgressIndicator(
                                    color = Color.White,
                                    modifier = Modifier.size(18.dp),
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Confirming...")
                            } else {
                                Text(
                                    text = when (step) {
                                        1 -> "Next: Date & Time"
                                        2 -> "Next: Details & Pay"
                                        3 -> "Confirm Appointment • بک کریں"
                                        else -> "Done"
                                    },
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Step Progress Indicator
            LinearProgressIndicator(
                progress = { step / 4f },
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp)
            ) {
                when (step) {
                    1 -> {
                        // STEP 1: Select Services & Stylist
                        item {
                            Text(
                                text = "Choose Services / سروس منتخب کریں",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Select one or more services for your appointment",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                        }

                        items(salon.services, key = { it.id }) { service ->
                            val isSelected = draft.selectedServices.contains(service)
                            ServiceSelectableCard(
                                service = service,
                                isSelected = isSelected,
                                onToggle = { viewModel.toggleServiceInDraft(service) },
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                        }

                        item {
                            Spacer(modifier = Modifier.height(20.dp))
                            Text(
                                text = "Preferred Stylist / آرٹسٹ کا انتخاب",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Select an expert stylist or choose any available",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(10.dp))

                            LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                items(salon.stylists) { stylist ->
                                    val isSelected = draft.selectedStylist?.id == stylist.id
                                    StylistSelectCard(
                                        stylist = stylist,
                                        isSelected = isSelected,
                                        onSelect = { viewModel.selectStylistInDraft(stylist) }
                                    )
                                }
                            }
                        }
                    }

                    2 -> {
                        // STEP 2: Date & Time Slot
                        item {
                            Text(
                                text = "Select Appointment Date / تاریخ",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(10.dp))

                            LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                items(viewModel.availableDates) { dateStr ->
                                    val isSelected = draft.selectedDate == dateStr
                                    Card(
                                        onClick = { viewModel.selectDateInDraft(dateStr) },
                                        shape = RoundedCornerShape(14.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                                        ),
                                        border = if (isSelected) {
                                            BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary)
                                        } else {
                                            BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                                        },
                                        modifier = Modifier
                                            .width(115.dp)
                                            .testTag("date_chip_$dateStr")
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(12.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.CalendarMonth,
                                                contentDescription = null,
                                                tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                                modifier = Modifier.size(20.dp)
                                            )
                                            Spacer(modifier = Modifier.height(6.dp))
                                            Text(
                                                text = dateStr.substringBefore(","),
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                            )
                                            Text(
                                                text = dateStr.substringAfter(", ").trim(),
                                                fontSize = 11.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            Text(
                                text = "Select Time Slot / وقت",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Estimated duration: $totalMinutes minutes",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                viewModel.availableTimeSlots.forEach { slot ->
                                    val isSelected = draft.selectedTimeSlot == slot
                                    Surface(
                                        onClick = { viewModel.selectTimeSlotInDraft(slot) },
                                        shape = RoundedCornerShape(10.dp),
                                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                                        border = androidx.compose.foundation.BorderStroke(
                                            1.dp,
                                            if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                                        ),
                                        modifier = Modifier.testTag("time_slot_$slot")
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.AccessTime,
                                                contentDescription = null,
                                                tint = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                                modifier = Modifier.size(14.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = slot,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    3 -> {
                        // STEP 3: Customer Details & Pakistani Payment Method
                        item {
                            Text(
                                text = "Customer Information / کسٹمر معلومات",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = draft.customerName,
                                onValueChange = { viewModel.updateCustomerDetails(it, draft.customerPhone, draft.customerNotes) },
                                label = { Text("Full Name (پورا نام)") },
                                placeholder = { Text("e.g. Ali Raza / Fatima Zahra") },
                                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("customer_name_input"),
                                singleLine = true
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedTextField(
                                value = draft.customerPhone,
                                onValueChange = { viewModel.updateCustomerDetails(draft.customerName, it, draft.customerNotes) },
                                label = { Text("Mobile Number (موبائل نمبر)") },
                                placeholder = { Text("e.g. 0300-1234567") },
                                leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("customer_phone_input"),
                                singleLine = true
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedTextField(
                                value = draft.customerNotes,
                                onValueChange = { viewModel.updateCustomerDetails(draft.customerName, draft.customerPhone, it) },
                                label = { Text("Special Requests / Instructions (اختیاری)") },
                                placeholder = { Text("e.g. Low fade, skin sensitivity, quiet session") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("customer_notes_input"),
                                maxLines = 3
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            Text(
                                text = "Payment Method / ادائیگی کا طریقہ",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            PaymentMethod.entries.forEach { method ->
                                val isSelected = draft.paymentMethod == method
                                Card(
                                    onClick = { viewModel.selectPaymentMethodInDraft(method) },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surface
                                    ),
                                    border = if (isSelected) {
                                        BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary)
                                    } else {
                                        BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                        .testTag("payment_method_${method.name}")
                                ) {
                                    Row(
                                        modifier = Modifier.padding(14.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        RadioButton(
                                            selected = isSelected,
                                            onClick = { viewModel.selectPaymentMethodInDraft(method) }
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text(
                                                text = "${method.title} (${method.urduTitle})",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp
                                            )
                                            Text(
                                                text = method.subtitle,
                                                fontSize = 11.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    4 -> {
                        // STEP 4: Confirmation & Digital Boarding Pass
                        item {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(64.dp)
                                        .clip(CircleShape)
                                        .background(StatusGreen),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Success",
                                        tint = Color.White,
                                        modifier = Modifier.size(36.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Text(
                                    text = "Appointment Confirmed!",
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                                Text(
                                    text = "آپ کی اپوائنٹمنٹ کامیابی سے بک ہو گئی ہے",
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "An SMS confirmation will also be sent to ${draft.customerPhone}",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
                                )
                            }

                            val confirmed = draft.confirmedAppointment
                            if (confirmed != null) {
                                DigitalPassVoucherCard(
                                    appointment = confirmed,
                                    onCallClick = {
                                        val intent = Intent(Intent.ACTION_DIAL).apply {
                                            data = Uri.parse("tel:${confirmed.salonPhone}")
                                        }
                                        context.startActivity(intent)
                                    },
                                    onRescheduleClick = {
                                        viewModel.openRescheduleDialog(confirmed)
                                    },
                                    onCancelClick = {
                                        viewModel.cancelAppointment(confirmed.id)
                                    }
                                )
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                OutlinedButton(
                                    onClick = { viewModel.navigateTo(AppScreen.Home) },
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Explore More Salons")
                                }

                                Button(
                                    onClick = { viewModel.navigateTo(AppScreen.MyAppointments) },
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.primary
                                    ),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("My Bookings")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
