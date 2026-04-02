package com.idat.presentation.direcciones

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.idat.domain.model.Direccion
import com.idat.presentation.components.*
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.permissions.isGranted
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun MisDireccionesScreen(
    navController: NavHostController,
    direccionesViewModel: DireccionesViewModel = hiltViewModel()
) {
    val direcciones by direccionesViewModel.direcciones.collectAsState()
    val isLoading by direccionesViewModel.isLoading.collectAsState()
    val error by direccionesViewModel.error.collectAsState()
    val ubicacionActual by direccionesViewModel.ubicacionActual.collectAsState()
    val estaCargandoUbicacion by direccionesViewModel.estaCargandoUbicacion.collectAsState()

    val locationPermissionsState = rememberMultiplePermissionsState(
        listOf(
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )

    var showAddressSheet by remember { mutableStateOf(false) }
    var showLocationDialog by remember { mutableStateOf(false) }
    var selectedDireccion by remember { mutableStateOf<Direccion?>(null) }

    val sheetState = rememberModalBottomSheetState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Monitorizar cambios en la ubicación para mostrar el diálogo
    LaunchedEffect(ubicacionActual) {
        if (ubicacionActual != "Detectando ubicación..." && 
            !ubicacionActual.startsWith("Error") && 
            ubicacionActual != "Permiso denegado" &&
            ubicacionActual != "Ubicación no encontrada") {
            showLocationDialog = true
        }
    }

    val pinkPrimary = Color(0xFFAB005A)
    val pinkContainer = Color(0xFFD80073)
    val surfaceContainerLow = Color(0xFFFFF0F2)
    val surfaceVariant = Color(0xFFF8DBE2)

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Mis Direcciones",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = pinkPrimary)
                    }
                },
                actions = {
                    IconButton(onClick = { /* More options */ }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Más", tint = Color.Gray)
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    selectedDireccion = null
                    showAddressSheet = true
                },
                containerColor = pinkContainer,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.padding(bottom = 16.dp, end = 8.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Añadir dirección", modifier = Modifier.size(28.dp))
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp)
            ) {
                item {
                    Column(modifier = Modifier.padding(bottom = 8.dp)) {
                        Text(
                            text = "Tus Lugares",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = (-1).sp
                        )
                        Text(
                            text = "Gestiona tus puntos de entrega para una compra más rápida.",
                            color = Color.Gray,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }

                items(direcciones) { direccion ->
                    AddressCard(
                        direccion = direccion,
                        onEdit = {
                            selectedDireccion = direccion
                            showAddressSheet = true
                        },
                        onDelete = { direccionesViewModel.deleteDireccion(direccion.id) },
                        onSetDefault = { direccionesViewModel.setPredeterminada(direccion.id) },
                        pinkPrimary = pinkPrimary,
                        pinkContainer = pinkContainer,
                        surfaceContainerLow = surfaceContainerLow
                    )
                }

                item {
                    // Decorative Map Preview
                    MapPreviewCard(
                        pinkPrimary = pinkPrimary,
                        currentAddress = ubicacionActual,
                        isLoading = estaCargandoUbicacion,
                        onGetLocation = {
                            if (locationPermissionsState.allPermissionsGranted) {
                                direccionesViewModel.fetchCurrentLocation()
                            } else {
                                locationPermissionsState.launchMultiplePermissionRequest()
                            }
                        }
                    )
                }
            }

            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = pinkPrimary)
            }
        }

        if (showAddressSheet) {
            ModalBottomSheet(
                onDismissRequest = { showAddressSheet = false },
                sheetState = sheetState,
                containerColor = Color.White,
                shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
            ) {
                AddressForm(
                    direccion = selectedDireccion,
                    currentLocation = ubicacionActual,
                    onSave = {
                        direccionesViewModel.saveDireccion(it)
                        showAddressSheet = false
                    },
                    onCancel = { showAddressSheet = false },
                    pinkPrimary = pinkPrimary
                )
            }
        }

        if (showLocationDialog) {
            AlertDialog(
                onDismissRequest = { showLocationDialog = false },
                title = { Text("Ubicación Detectada", fontWeight = FontWeight.Bold) },
                text = { Text("¿Deseas añadir \"$ubicacionActual\" como una nueva dirección?") },
                confirmButton = {
                    Button(
                        onClick = {
                            showLocationDialog = false
                            selectedDireccion = null
                            showAddressSheet = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = pinkPrimary)
                    ) {
                        Text("Sí, añadir")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showLocationDialog = false }) {
                        Text("Cancelar", color = Color.Gray)
                    }
                },
                containerColor = Color.White,
                shape = RoundedCornerShape(24.dp)
            )
        }
    }
}

@Composable
fun AddressCard(
    direccion: Direccion,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onSetDefault: () -> Unit,
    pinkPrimary: Color,
    pinkContainer: Color,
    surfaceContainerLow: Color
) {
    val icon = when (direccion.tipoIcono) {
        "work" -> Icons.Default.Work
        "apartment" -> Icons.Default.Apartment
        else -> Icons.Default.Home
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(if (direccion.esPredeterminada) Color.White else surfaceContainerLow)
            .then(
                if (direccion.esPredeterminada) Modifier.border(2.dp, pinkContainer.copy(alpha = 0.2f), RoundedCornerShape(24.dp))
                else Modifier
            )
            .clickable { onSetDefault() }
            .padding(20.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (direccion.esPredeterminada) pinkContainer else pinkContainer.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = if (direccion.esPredeterminada) Color.White else pinkPrimary
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(text = direccion.tag, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        if (direccion.esPredeterminada) {
                            Box(
                                modifier = Modifier
                                    .padding(top = 4.dp)
                                    .clip(RoundedCornerShape(100.dp))
                                    .background(pinkContainer)
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text("PREDETERMINADA", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Black)
                            }
                        }
                    }
                }
                
                Row {
                    IconButton(onClick = onEdit) {
                        Icon(Icons.Default.Edit, contentDescription = "Editar", tint = pinkPrimary, modifier = Modifier.size(20.dp))
                    }
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color.Gray.copy(alpha = 0.5f), modifier = Modifier.size(20.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text(text = direccion.direccion, fontWeight = FontWeight.Medium, fontSize = 15.sp)
            
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(12.dp), tint = Color.Gray)
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = direccion.destinatario, fontSize = 13.sp, color = Color.Gray)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(12.dp), tint = Color.Gray)
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = direccion.telefono, fontSize = 13.sp, color = Color.Gray)
            }
        }
    }
}

@Composable
fun MapPreviewCard(
    pinkPrimary: Color, 
    currentAddress: String, 
    isLoading: Boolean, 
    onGetLocation: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .padding(top = 8.dp)
            .clip(RoundedCornerShape(32.dp))
            .background(Color.LightGray.copy(alpha = 0.2f))
            .clickable { onGetLocation() }
    ) {
        // Mock Map Image
        AsyncImage(
            model = "https://lh3.googleusercontent.com/aida-public/AB6AXuCBaBk4zAqxhc0X0rTRp-NP3UHL9qCnqPtIz6LO-nKf1R0Zg3VnU5uufWDB6rvmjU27K1sCIdFQpD9aAbBC7NMJ_ZoUydW8siwvkHLW4TJtXPK2dpZGrYeSn0i16IvCd4LFUYVSd0aAVceDnG4KerFXPdicnhmAzBZV0i-dCf_lsZPn-YcT1bJivs56i_3H1p0Y1t-US3W6DDSn9ZxcGX2y8lHEMh69G8fXD7LPPTJAzGKbMWfJ87SVSJl1gLFjpVtjnam634kaQGA",
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            alpha = 0.6f
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(Color.Transparent, pinkPrimary.copy(alpha = 0.4f))))
        )
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Tu Ubicación Actual", color = Color.White.copy(alpha = 0.8f), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                if (isLoading) {
                    LinearProgressIndicator(
                        modifier = Modifier.width(100.dp).padding(top = 4.dp),
                        color = Color.White,
                        trackColor = Color.White.copy(alpha = 0.2f)
                    )
                } else {
                    Text(currentAddress, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium, maxLines = 2)
                }
            }
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .padding(8.dp)
                    .clickable { onGetLocation() },
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.MyLocation, contentDescription = null, tint = pinkPrimary, modifier = Modifier.size(18.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddressForm(
    direccion: Direccion?,
    currentLocation: String,
    onSave: (Direccion) -> Unit,
    onCancel: () -> Unit,
    pinkPrimary: Color
) {
    var tag by remember { mutableStateOf(direccion?.tag ?: "") }
    var fullAddress by remember { 
        mutableStateOf(
            direccion?.direccion ?: if (currentLocation != "Detectando ubicación..." && !currentLocation.startsWith("Error")) currentLocation else ""
        ) 
    }
    var name by remember { mutableStateOf(direccion?.destinatario ?: "") }
    var phone by remember { mutableStateOf(direccion?.telefono ?: "") }
    var type by remember { mutableStateOf(direccion?.tipoIcono ?: "home") }
    var isDefault by remember { mutableStateOf(direccion?.esPredeterminada ?: false) }

    Column(
        modifier = Modifier
            .padding(24.dp)
            .fillMaxWidth()
            .padding(bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = if (direccion == null) "Nueva Dirección" else "Editar Dirección",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            TypeChip("Casa", Icons.Default.Home, type == "home", { type = "home" }, pinkPrimary)
            TypeChip("Oficina", Icons.Default.Work, type == "work", { type = "work" }, pinkPrimary)
            TypeChip("Depa", Icons.Default.Apartment, type == "apartment", { type = "apartment" }, pinkPrimary)
        }

        OutlinedTextField(
            value = tag,
            onValueChange = { tag = it },
            label = { Text("Etiqueta (ej: Casa)") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = fullAddress,
            onValueChange = { fullAddress = it },
            label = { Text("Dirección completa") },
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                IconButton(onClick = { fullAddress = currentLocation }) {
                    Icon(Icons.Default.MyLocation, contentDescription = null, tint = pinkPrimary)
                }
            }
        )
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Quién recibe") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text("Teléfono") },
            modifier = Modifier.fillMaxWidth()
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = isDefault, onCheckedChange = { isDefault = it })
            Text("Establecer como predeterminada")
        }

        Button(
            onClick = {
                onSave(
                    Direccion(
                        id = direccion?.id ?: "",
                        tag = tag,
                        direccion = fullAddress,
                        destinatario = name,
                        telefono = phone,
                        esPredeterminada = isDefault,
                        tipoIcono = type
                    )
                )
            },
            enabled = tag.isNotBlank() && fullAddress.isNotBlank(),
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = pinkPrimary),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("Guardar Dirección", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun TypeChip(label: String, icon: ImageVector, isSelected: Boolean, onClick: () -> Unit, pinkPrimary: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) pinkPrimary else Color.LightGray.copy(alpha = 0.2f))
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(16.dp), tint = if (isSelected) Color.White else Color.Gray)
            Text(label, color = if (isSelected) Color.White else Color.Gray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
    }
}
