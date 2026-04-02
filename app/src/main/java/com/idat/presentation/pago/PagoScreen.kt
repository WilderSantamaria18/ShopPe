package com.idat.presentation.pago

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Contactless
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun PagoScreen(
    navController: NavHostController,
    viewModel: PagoViewModel = hiltViewModel()
) {
    var selectedMethod by remember { mutableStateOf("card") } // "card" or "yape"
    
    val totalAmount by viewModel.totalAmount.collectAsState()
    
    // Form State
    var cardNumber by remember { mutableStateOf("") }
    var expiryDate by remember { mutableStateOf("") }
    var cvv by remember { mutableStateOf("") }
    var cardHolderName by remember { mutableStateOf("") }
    
    var showLoading by remember { mutableStateOf(false) }

    // Validation
    val isFormValid = if (selectedMethod == "card") {
        cardNumber.filter { it.isDigit() }.length >= 16 && 
        expiryDate.length >= 5 && 
        cvv.length >= 3 && 
        cardHolderName.isNotBlank()
    } else {
        true // Yape is always "valid" as it's just showing a QR
    }

    val isDark = MaterialTheme.colorScheme.surface == Color(0xFF140C0E)
    val surfaceContainerLow = if (isDark) Color(0xFF1F1215) else Color(0xFFFFF0F2)
    val surfaceContainerLowest = if (isDark) Color(0xFF140C0E) else Color.White
    val surfaceContainerHigh = if (isDark) Color(0xFF332025) else Color(0xFFFEE1E7)
    val outlineVariant = if (isDark) Color(0xFF8E6F77).copy(alpha = 0.5f) else Color(0xFFE2BDC6)

    // Loading Simulation
    if (showLoading) {
        LaunchedEffect(Unit) {
            viewModel.procesarPago(
                onSuccess = { id ->
                    showLoading = false
                    navController.navigate("pedidoConfirmado/$id")
                },
                onError = { error ->
                    showLoading = false
                    // Handle error (e.g. snackbar)
                }
            )
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Método de Pago",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        letterSpacing = (-0.5).sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = MaterialTheme.colorScheme.primary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)
                )
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp)
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                // Payment Selection
                Text(
                    "Selecciona cómo pagar",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                PaymentMethodCard(
                    title = "Tarjeta de Crédito/Débito",
                    subtitle = "Visa, Mastercard, Amex",
                    icon = Icons.Default.Contactless,
                    isSelected = selectedMethod == "card",
                    onClick = { selectedMethod = "card" },
                    surfaceContainerLow = surfaceContainerLow,
                    surfaceContainerLowest = surfaceContainerLowest,
                    outlineVariant = outlineVariant
                )

                Spacer(modifier = Modifier.height(16.dp))

                PaymentMethodCard(
                    title = "Pago con QR (Yape)",
                    subtitle = "Escanea y paga al instante",
                    icon = Icons.Default.QrCode2,
                    isSelected = selectedMethod == "yape",
                    onClick = { selectedMethod = "yape" },
                    surfaceContainerLow = surfaceContainerLow,
                    surfaceContainerLowest = surfaceContainerLowest,
                    outlineVariant = outlineVariant
                )

                Spacer(modifier = Modifier.height(32.dp))

                AnimatedVisibility(visible = selectedMethod == "card") {
                    CardFormArea(
                        surfaceContainerLow, 
                        surfaceContainerLowest, 
                        outlineVariant,
                        cardNumber, { input ->
                            val digits = input.filter { it.isDigit() }
                            if (digits.length <= 16) cardNumber = digits
                        },
                        expiryDate, { input ->
                            val digits = input.filter { it.isDigit() || it == '/' }
                            if (digits.length <= 5) {
                                // Simple MM/YY format auto-slash
                                val formatted = if (digits.length == 2 && !expiryDate.contains("/") && input.length > expiryDate.length) {
                                    "$digits/"
                                } else {
                                    digits
                                }
                                expiryDate = formatted
                            }
                        },
                        cvv, { input ->
                            val digits = input.filter { it.isDigit() }
                            if (digits.length <= 3) cvv = digits
                        },
                        cardHolderName, { cardHolderName = it }
                    )
                }

                AnimatedVisibility(visible = selectedMethod == "yape") {
                    YapeInstructionArea(surfaceContainerHigh)
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Order Summary Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.05f))
                        .border(1.dp, MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f), RoundedCornerShape(24.dp))
                        .padding(24.dp)
                ) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Column {
                            Text("Total a pagar", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("S/ ${String.format("%.2f", totalAmount)}", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primaryContainer, letterSpacing = (-1).sp)
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f))
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text("1 ITEM", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primaryContainer, letterSpacing = 1.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(160.dp))
            }

            // Bottom Action Area
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.95f))
                        .padding(24.dp)
                ) {
                    // Progress lines
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.height(4.dp).width(32.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primaryContainer))
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(modifier = Modifier.height(4.dp).width(32.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primaryContainer))
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(modifier = Modifier.height(4.dp).width(32.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primaryContainer))
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(modifier = Modifier.height(4.dp).width(32.dp).clip(CircleShape).background(outlineVariant))
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Button(
                        onClick = { if (isFormValid) showLoading = true },
                        enabled = isFormValid,
                        modifier = Modifier.fillMaxWidth().height(60.dp),
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent,
                            disabledContainerColor = Color.Gray.copy(alpha = 0.3f)
                        ),
                        contentPadding = PaddingValues()
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    brush = Brush.linearGradient(
                                        colors = if (isFormValid) 
                                            listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.primaryContainer)
                                        else 
                                            listOf(Color.Gray, Color.LightGray)
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Confirmar Pago", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.White)
                            }
                        }
                    }
                }
            }

            // Loading Modal Overlay
            if (showLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.5f))
                        .clickable(enabled = false) {},
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(
                            modifier = Modifier.padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Procesando Pago...", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PaymentMethodCard(
    title: String, subtitle: String, icon: ImageVector,
    isSelected: Boolean, onClick: () -> Unit,
    surfaceContainerLow: Color, surfaceContainerLowest: Color, outlineVariant: Color
) {
    val borderColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(32.dp))
            .background(surfaceContainerLowest)
            .border(2.dp, borderColor, RoundedCornerShape(32.dp))
            .clickable { onClick() }
            .padding(20.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(48.dp).clip(RoundedCornerShape(12.dp)).background(surfaceContainerLow),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(title, fontWeight = FontWeight.SemiBold, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface)
                    Text(subtitle, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            // Radio button custom
            Box(
                modifier = Modifier.size(24.dp).clip(CircleShape).border(2.dp, if (isSelected) MaterialTheme.colorScheme.primaryContainer else outlineVariant, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (isSelected) {
                    Box(modifier = Modifier.size(12.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primaryContainer))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardFormArea(
    surfaceContainerLow: Color, 
    surfaceContainerLowest: Color, 
    outlineVariant: Color,
    cardNumber: String, onCardNumberChange: (String) -> Unit,
    expiryDate: String, onExpiryDateChange: (String) -> Unit,
    cvv: String, onCvvChange: (String) -> Unit,
    cardHolderName: String, onCardHolderNameChange: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(surfaceContainerLow)
            .padding(24.dp)
    ) {
        Column {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Detalles de la Tarjeta", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                Icon(Icons.Default.Contactless, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Spacer(modifier = Modifier.height(24.dp))
            
            Text("NÚMERO DE TARJETA", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurfaceVariant, letterSpacing = 1.sp)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = cardNumber,
                onValueChange = onCardNumberChange,
                placeholder = { Text("0000 0000 0000 0000", color = outlineVariant) },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = surfaceContainerLowest, unfocusedContainerColor = surfaceContainerLowest, unfocusedBorderColor = Color.Transparent, focusedBorderColor = MaterialTheme.colorScheme.primaryContainer
                ),
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("VENCIMIENTO", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurfaceVariant, letterSpacing = 1.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = expiryDate,
                        onValueChange = onExpiryDateChange,
                        placeholder = { Text("MM/YY", color = outlineVariant) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = surfaceContainerLowest,
                            unfocusedContainerColor = surfaceContainerLowest,
                            unfocusedBorderColor = Color.Transparent,
                            focusedBorderColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text("CVV", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurfaceVariant, letterSpacing = 1.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = cvv,
                        onValueChange = onCvvChange,
                        placeholder = { Text("***", color = outlineVariant) },
                        modifier = Modifier.fillMaxWidth(),
                        trailingIcon = { Icon(Icons.Default.Help, tint = outlineVariant, contentDescription = null) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = surfaceContainerLowest,
                            unfocusedContainerColor = surfaceContainerLowest,
                            unfocusedBorderColor = Color.Transparent,
                            focusedBorderColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("NOMBRE EN LA TARJETA", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurfaceVariant, letterSpacing = 1.sp)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = cardHolderName,
                onValueChange = onCardHolderNameChange,
                placeholder = { Text("Ej: JUAN PEREZ", color = outlineVariant) },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = surfaceContainerLowest,
                    unfocusedContainerColor = surfaceContainerLowest,
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor = MaterialTheme.colorScheme.primaryContainer
                ),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.width(4.dp))
                Text("PAGO SEGURO ENCRIPTADO POR SHOPPE SECURE", fontSize = 10.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurfaceVariant, letterSpacing = 1.sp)
            }
        }
    }
}

@Composable
fun YapeInstructionArea(surfaceContainerHigh: Color) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(surfaceContainerHigh)
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Box(modifier = Modifier.size(160.dp).background(Color.White, RoundedCornerShape(16.dp)).padding(16.dp), contentAlignment = Alignment.Center) {
                Image(painter = painterResource(id = com.idat.R.drawable.yape_qr), contentDescription = "QR Code Yape", modifier = Modifier.fillMaxSize())
            }
            Text("Escanea el código", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
            Text("Abre tu app Yape, selecciona 'Yapear con QR' y escanea este código para finalizar.", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
        }
    }
}
