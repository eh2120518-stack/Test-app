package com.example.ui.screens

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserRole
import com.example.ui.AppScreen
import com.example.ui.SalonViewModel
import com.example.ui.theme.AccentGold

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(
    viewModel: SalonViewModel,
    initialTab: Int = 0,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val currentUser by viewModel.currentUser.collectAsState()
    val userRole by viewModel.userRole.collectAsState()

    var selectedTabIndex by remember { mutableIntStateOf(initialTab) }

    // Customer Form State
    var isRegisterMode by remember { mutableStateOf(false) }
    var customerNameInput by remember { mutableStateOf("") }
    var customerPhoneInput by remember { mutableStateOf("0300-4567890") }
    var customerPasswordInput by remember { mutableStateOf("customer123") }
    var customerPasswordVisible by remember { mutableStateOf(false) }
    var customerErrorMessage by remember { mutableStateOf<String?>(null) }

    // Admin Form State
    var adminIdInput by remember { mutableStateOf("haider_admin") }
    var adminPinInput by remember { mutableStateOf("7860") }
    var adminPinVisible by remember { mutableStateOf(false) }
    var adminErrorMessage by remember { mutableStateOf<String?>(null) }

    BackHandler {
        viewModel.navigateBack()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = if (selectedTabIndex == 0) "Customer Login" else "Admin Portal",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Text(
                            text = if (selectedTabIndex == 0) "گاہک لاگ ان اور رجسٹریشن" else "سیلون ایڈمن و مالک پورٹل",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateBack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
        ) {
            // Salon Branding Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.88f)
                            )
                        )
                    )
                    .padding(horizontal = 20.dp, vertical = 20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(AccentGold.copy(alpha = 0.22f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCut,
                            contentDescription = "Salon Logo",
                            tint = AccentGold,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column {
                        Text(
                            text = "Haider Salon & Grooming",
                            color = Color.White,
                            fontSize = 19.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "حیدر سیلون • ولج نیندووال، لالہ موسیٰ",
                            color = AccentGold,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "Separate Logins for Customer & Salon Staff",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 11.sp
                        )
                    }
                }
            }

            // Dual Role Selection Tabs
            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                        color = if (selectedTabIndex == 0) MaterialTheme.colorScheme.primary else AccentGold
                    )
                }
            ) {
                Tab(
                    selected = selectedTabIndex == 0,
                    onClick = {
                        selectedTabIndex = 0
                        customerErrorMessage = null
                    },
                    modifier = Modifier.testTag("tab_customer"),
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "Customer Login",
                                    fontWeight = if (selectedTabIndex == 0) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 13.sp
                                )
                                Text(
                                    text = "گاہک لاگ ان",
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                )

                Tab(
                    selected = selectedTabIndex == 1,
                    onClick = {
                        selectedTabIndex = 1
                        adminErrorMessage = null
                    },
                    modifier = Modifier.testTag("tab_admin"),
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.AdminPanelSettings,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp),
                                tint = if (selectedTabIndex == 1) AccentGold else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "Admin Portal",
                                    fontWeight = if (selectedTabIndex == 1) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 13.sp,
                                    color = if (selectedTabIndex == 1) AccentGold else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "ایڈمن پورٹل",
                                    fontSize = 10.sp,
                                    color = if (selectedTabIndex == 1) AccentGold else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // TAB CONTENT
            if (selectedTabIndex == 0) {
                // ==================== CUSTOMER LOGIN & REGISTER ====================
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp)
                        ) {
                            // Sub-header
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (isRegisterMode) "Create New Account" else "Customer Sign In",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )

                                TextButton(
                                    onClick = {
                                        isRegisterMode = !isRegisterMode
                                        customerErrorMessage = null
                                    }
                                ) {
                                    Text(
                                        text = if (isRegisterMode) "Existing User? Login" else "+ New? Register",
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 12.sp
                                    )
                                }
                            }

                            Text(
                                text = if (isRegisterMode)
                                    "رجسٹریشن کے بعد آپ براہ راست اپوائنٹمنٹ بک اور ٹریک کر سکیں گے"
                                else
                                    "اپنا فون نمبر اور پاس ورڈ درج کر کے اپنے کسٹمر اکاؤنٹ میں لاگ ان ہوں",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )

                            // Name field (only in register mode)
                            AnimatedVisibility(visible = isRegisterMode) {
                                Column {
                                    OutlinedTextField(
                                        value = customerNameInput,
                                        onValueChange = { customerNameInput = it },
                                        label = { Text("Customer Full Name (مکمل نام)") },
                                        placeholder = { Text("e.g. Chaudhry Bilal / Ali Raza") },
                                        leadingIcon = {
                                            Icon(Icons.Default.Person, contentDescription = null)
                                        },
                                        singleLine = true,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("input_customer_name"),
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                }
                            }

                            // Phone Number field
                            OutlinedTextField(
                                value = customerPhoneInput,
                                onValueChange = { customerPhoneInput = it },
                                label = { Text("Phone Number (فون نمبر)") },
                                placeholder = { Text("0300-1234567") },
                                leadingIcon = {
                                    Icon(Icons.Default.Phone, contentDescription = null)
                                },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("input_customer_phone"),
                                shape = RoundedCornerShape(12.dp)
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // Password field
                            OutlinedTextField(
                                value = customerPasswordInput,
                                onValueChange = { customerPasswordInput = it },
                                label = { Text("Password (پاس ورڈ)") },
                                placeholder = { Text("Enter password") },
                                leadingIcon = {
                                    Icon(Icons.Default.Lock, contentDescription = null)
                                },
                                trailingIcon = {
                                    IconButton(onClick = { customerPasswordVisible = !customerPasswordVisible }) {
                                        Icon(
                                            if (customerPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                            contentDescription = "Toggle password visibility"
                                        )
                                    }
                                },
                                visualTransformation = if (customerPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("input_customer_password"),
                                shape = RoundedCornerShape(12.dp)
                            )

                            // Error Message
                            customerErrorMessage?.let { err ->
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "⚠️ $err",
                                    color = MaterialTheme.colorScheme.error,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            Spacer(modifier = Modifier.height(18.dp))

                            // Main Customer Submit Button
                            Button(
                                onClick = {
                                    customerErrorMessage = null
                                    if (isRegisterMode) {
                                        val result = viewModel.registerCustomer(
                                            customerNameInput,
                                            customerPhoneInput,
                                            customerPasswordInput
                                        )
                                        result.onSuccess {
                                            Toast.makeText(context, "خوش آمدید ${it.name}! اکاؤنٹ بن گیا", Toast.LENGTH_SHORT).show()
                                            viewModel.navigateTo(AppScreen.Home)
                                        }.onFailure {
                                            customerErrorMessage = it.message ?: "رجسٹریشن مکمل نہ ہو سکی"
                                        }
                                    } else {
                                        val result = viewModel.loginCustomer(
                                            customerPhoneInput,
                                            customerPasswordInput
                                        )
                                        result.onSuccess {
                                            Toast.makeText(context, "خوش آمدید ${it.name}! لاگ ان کامیاب", Toast.LENGTH_SHORT).show()
                                            viewModel.navigateTo(AppScreen.Home)
                                        }.onFailure {
                                            customerErrorMessage = it.message ?: "لاگ ان نہ ہو سکا"
                                        }
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("customer_login_btn"),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary
                                )
                            ) {
                                Text(
                                    text = if (isRegisterMode) "اکاؤنٹ بنائیں / Register as Customer" else "لاگ ان کریں / Login as Customer",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Quick 1-Tap Demo Customer Buttons
                            OutlinedButton(
                                onClick = {
                                    viewModel.loginDemoCustomer()
                                    Toast.makeText(context, "Logged in as Ali Raza (Customer)", Toast.LENGTH_SHORT).show()
                                    viewModel.navigateTo(AppScreen.Home)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(46.dp)
                                    .testTag("demo_customer_btn"),
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
                            ) {
                                Icon(
                                    Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "فوری لاگ ان (علی رضا - 0300-4567890)",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Customer Benefits Card
                    ElevatedCard(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.elevatedCardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f)
                        ),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.Star,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "کسٹمر اکاؤنٹ کے فوائد (Customer Benefits)",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "• ہر بال کٹوانے پر لائلٹی سٹیمپ (6th کٹنگ پر 50% رعایت)\n" +
                                        "• اپنی تمام گزشتہ اور آئندہ بکنگز کا ریکارڈ اور ڈیجیٹل واؤچر\n" +
                                        "• سیلون لائیو ویٹنگ ٹائم اور قطار کا اسٹیٹس چیک کریں",
                                fontSize = 12.sp,
                                lineHeight = 18.sp,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Continue as guest
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        TextButton(
                            onClick = { viewModel.navigateTo(AppScreen.Home) }
                        ) {
                            Text(
                                text = "بغیر لاگ ان جاری رکھیں (Browse as Guest)",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                // ==================== ADMIN / SALON OWNER PORTAL ====================
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(3.dp),
                        border = BorderStroke(1.dp, AccentGold.copy(alpha = 0.4f))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp)
                        ) {
                            // Admin Security Header
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(AccentGold.copy(alpha = 0.18f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Security,
                                        contentDescription = "Admin Security",
                                        tint = AccentGold,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column {
                                    Text(
                                        text = "Ustad Haider - Admin Access",
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "سیلون مینیجر اور مالک کنٹرول پینل",
                                        fontSize = 12.sp,
                                        color = AccentGold,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Restricted Warning Banner
                            Surface(
                                color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.35f),
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.25f))
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.Lock,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.error,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "یہ پورٹل صرف استاد حیدر اور دکان سٹاف کے لیے ہے تاکہ وہ کسٹمر اپوائنٹمنٹس اور سیلون کی قطار مینج کر سکیں۔",
                                        fontSize = 11.sp,
                                        lineHeight = 16.sp,
                                        color = MaterialTheme.colorScheme.onErrorContainer
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Admin ID
                            OutlinedTextField(
                                value = adminIdInput,
                                onValueChange = { adminIdInput = it },
                                label = { Text("Admin ID / Phone") },
                                placeholder = { Text("haider_admin or 0300-8451920") },
                                leadingIcon = {
                                    Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = AccentGold)
                                },
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("input_admin_id"),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = AccentGold,
                                    focusedLabelColor = AccentGold
                                )
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // Admin PIN
                            OutlinedTextField(
                                value = adminPinInput,
                                onValueChange = { adminPinInput = it },
                                label = { Text("Admin Secret PIN / Passcode (ایڈمن پن)") },
                                placeholder = { Text("Enter Master PIN (Default: 7860)") },
                                leadingIcon = {
                                    Icon(Icons.Default.Lock, contentDescription = null)
                                },
                                trailingIcon = {
                                    IconButton(onClick = { adminPinVisible = !adminPinVisible }) {
                                        Icon(
                                            if (adminPinVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                            contentDescription = "Toggle PIN visibility"
                                        )
                                    }
                                },
                                visualTransformation = if (adminPinVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("input_admin_pin"),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = AccentGold,
                                    focusedLabelColor = AccentGold
                                )
                            )

                            adminErrorMessage?.let { err ->
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "⚠️ $err",
                                    color = MaterialTheme.colorScheme.error,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            Spacer(modifier = Modifier.height(18.dp))

                            // Admin Submit Button
                            Button(
                                onClick = {
                                    adminErrorMessage = null
                                    val result = viewModel.loginAdmin(adminIdInput, adminPinInput)
                                    result.onSuccess {
                                        Toast.makeText(context, "خوش آمدید استاد حیدر علی! ایڈمن ڈیش بورڈ کھل گیا", Toast.LENGTH_SHORT).show()
                                        viewModel.navigateTo(AppScreen.AdminDashboard)
                                    }.onFailure {
                                        adminErrorMessage = it.message ?: "غلط ایڈمن پن یا شناختی کوڈ"
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("admin_login_btn"),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF2E7D32)
                                )
                            ) {
                                Icon(Icons.Default.AdminPanelSettings, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "ایڈمن پینل کھولیں / Open Admin Panel",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = Color.White
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Quick 1-Tap Admin Login Demo Button
                            OutlinedButton(
                                onClick = {
                                    viewModel.loginDemoAdmin()
                                    Toast.makeText(context, "Logged in as Ustad Haider (Admin)", Toast.LENGTH_SHORT).show()
                                    viewModel.navigateTo(AppScreen.AdminDashboard)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(46.dp)
                                    .testTag("demo_admin_btn"),
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, AccentGold)
                            ) {
                                Icon(
                                    Icons.Default.Security,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = AccentGold
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "فوری ایڈمن لاگ ان (استاد حیدر - پن: 7860)",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Admin Powers Summary Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        ),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "ایڈمن اختیارات (Admin Dashboard Features):",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "1. تمام کسٹمرز کی اپوائنٹمنٹس دیکھیں اور اسٹیٹس (مکمل / منسوخ) کریں\n" +
                                        "2. واک ان کسٹمرز کو براہ راست قطار میں داخل کریں\n" +
                                        "3. دکان کی کرسیاں اور ویٹنگ ٹائم فوری ایڈجسٹ کریں\n" +
                                        "4. یومیہ تخمینہ آمدنی (Daily Revenue in PKR) چیک کریں\n" +
                                        "5. کسٹمر کو فوری کال یا واٹس ایپ پر پیغام بھیجیں",
                                fontSize = 12.sp,
                                lineHeight = 18.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}
