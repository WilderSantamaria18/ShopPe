package com.idat.presentation.configuracion

import android.Manifest
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun ConfiguracionScreen(
    navController: NavHostController,
    viewModel: ConfiguracionViewModel = hiltViewModel()
) {
    val email by viewModel.email.collectAsState()
    val usuarioState by viewModel.usuario.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val successMessage by viewModel.successMessage.collectAsState()
    val isUploading by viewModel.isUploading.collectAsState()
    val isLoadingLocation by viewModel.isLoadingLocation.collectAsState()
    val ubicacionDetectada by viewModel.ubicacionDetectada.collectAsState()
    
    val snackbarHostState = remember { SnackbarHostState() }
    val focusManager = LocalFocusManager.current

    // Permiso de Ubicación
    val locationPermissionState = rememberPermissionState(
        permission = Manifest.permission.ACCESS_FINE_LOCATION
    )

    // Launcher para seleccionar imagen
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.subirFotoPerfil(it) }
    }

    // Campos del formulario
    var nombre by remember { mutableStateOf("") }
    var apellido by remember { mutableStateOf("") }
    var dni by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var direccion by remember { mutableStateOf("") }
    var distrito by remember { mutableStateOf("") }
    var departamento by remember { mutableStateOf("") }
    var fotoUrl by remember { mutableStateOf("") }

    var showPasswordDialog by remember { mutableStateOf(false) }

    // Cargar datos iniciales
    LaunchedEffect(usuarioState) {
        usuarioState?.let {
            nombre = it.nombre
            apellido = it.apellido
            dni = it.dni
            telefono = it.telefono
            direccion = it.direccion
            distrito = it.distrito
            departamento = it.departamento
            
            val firebaseUser = FirebaseAuth.getInstance().currentUser
            fotoUrl = if (it.fotoUrl.isNotEmpty()) it.fotoUrl else firebaseUser?.photoUrl?.toString() ?: ""
        }
    }

    // Actualizar campos cuando el GPS detecte algo
    LaunchedEffect(ubicacionDetectada) {
        ubicacionDetectada?.let { (nuevoDistrito, nuevoDepartamento) ->
            if (nuevoDistrito.isNotEmpty()) distrito = nuevoDistrito
            if (nuevoDepartamento.isNotEmpty()) departamento = nuevoDepartamento
        }
    }

    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            snackbarHostState.showSnackbar(message = it, duration = SnackbarDuration.Long)
            viewModel.clearMessages()
        }
    }

    LaunchedEffect(successMessage) {
        successMessage?.let {
            snackbarHostState.showSnackbar(message = it, duration = SnackbarDuration.Short)
            viewModel.clearMessages()
            // Regresar a la pantalla anterior después de guardar con éxito
            navController.popBackStack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi Perfil", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        viewModel.guardarPerfil(nombre, apellido, dni, telefono, direccion, distrito, departamento)
                    }) {
                        Icon(Icons.Default.Save, contentDescription = "Guardar", tint = Color(0xFFAB005A))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            // Cabecera con Foto
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(110.dp)
                        .clickable { if (!isUploading) galleryLauncher.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    if (fotoUrl.isNotEmpty()) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(fotoUrl)
                                .crossfade(true)
                                .build(),
                            contentDescription = "Foto de perfil",
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFAB005A).copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (nombre.isNotEmpty()) nombre.take(1).uppercase() else email.take(1).uppercase(),
                                style = MaterialTheme.typography.displaySmall,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFAB005A)
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFAB005A))
                            .padding(6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isUploading) {
                            CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp, modifier = Modifier.size(20.dp))
                        } else {
                            Icon(Icons.Default.CameraAlt, contentDescription = "Cambiar foto", tint = Color.White, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }

            Text(
                text = "Datos Personales",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFAB005A)
            )

            OutlinedTextField(
                value = email,
                onValueChange = { },
                label = { Text("Correo Electrónico") },
                readOnly = true,
                modifier = Modifier.fillMaxWidth(),
                enabled = false,
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) }
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombres") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(androidx.compose.ui.focus.FocusDirection.Next) })
                )
                OutlinedTextField(
                    value = apellido,
                    onValueChange = { apellido = it },
                    label = { Text("Apellidos") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(androidx.compose.ui.focus.FocusDirection.Next) })
                )
            }

            OutlinedTextField(
                value = dni,
                onValueChange = { if (it.length <= 8) dni = it },
                label = { Text("DNI") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(androidx.compose.ui.focus.FocusDirection.Next) })
            )

            OutlinedTextField(
                value = telefono,
                onValueChange = { telefono = it },
                label = { Text("Teléfono de contacto") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone, imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(androidx.compose.ui.focus.FocusDirection.Next) })
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            // Sección Ubicación con Botón GPS
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Ubicación de Envío",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFAB005A)
                )
                
                TextButton(
                    onClick = {
                        if (locationPermissionState.status.isGranted) {
                            viewModel.obtenerUbicacionActual()
                        } else {
                            locationPermissionState.launchPermissionRequest()
                        }
                    },
                    enabled = !isLoadingLocation
                ) {
                    if (isLoadingLocation) {
                        CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp, color = Color(0xFFAB005A))
                    } else {
                        Icon(Icons.Default.MyLocation, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Usar mi ubicación", style = MaterialTheme.typography.labelLarge)
                    }
                }
            }

            OutlinedTextField(
                value = direccion,
                onValueChange = { direccion = it },
                label = { Text("Dirección (Calle / Av / Jr)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(androidx.compose.ui.focus.FocusDirection.Next) })
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = distrito,
                    onValueChange = { distrito = it },
                    label = { Text("Distrito") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(androidx.compose.ui.focus.FocusDirection.Next) })
                )
                OutlinedTextField(
                    value = departamento,
                    onValueChange = { departamento = it },
                    label = { Text("Departamento") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() })
                )
            }

            val isGoogleUser = FirebaseAuth.getInstance().currentUser?.providerData?.any { it.providerId == "google.com" } ?: false
            
            if (!isGoogleUser) {
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { showPasswordDialog = true },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                ) {
                    Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Cambiar Contraseña")
                }
            }

            Button(
                onClick = { viewModel.guardarPerfil(nombre, apellido, dni, telefono, direccion, distrito, departamento) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFAB005A))
            ) {
                Text("Actualizar Perfil", fontWeight = FontWeight.Bold)
            }
        }
    }

    if (showPasswordDialog) {
        CambiarPasswordDialog(
            onDismiss = { showPasswordDialog = false },
            onConfirm = { currentPassword, newPassword ->
                viewModel.cambiarPassword(currentPassword, newPassword)
                showPasswordDialog = false
            }
        )
    }
}

@Composable
fun CambiarPasswordDialog(onDismiss: () -> Unit, onConfirm: (String, String) -> Unit) {
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var currentPasswordVisible by remember { mutableStateOf(false) }
    var newPasswordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Cambiar Contraseña", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = currentPassword, onValueChange = { currentPassword = it }, label = { Text("Contraseña actual") }, singleLine = true,
                    visualTransformation = if (currentPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { currentPasswordVisible = !currentPasswordVisible }) {
                            Icon(if (currentPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff, "Toggle visibility")
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = newPassword, onValueChange = { newPassword = it }, label = { Text("Nueva contraseña") }, singleLine = true,
                    visualTransformation = if (newPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { newPasswordVisible = !newPasswordVisible }) {
                            Icon(if (newPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff, "Toggle visibility")
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = confirmPassword, onValueChange = { confirmPassword = it }, label = { Text("Confirmar contraseña") }, singleLine = true,
                    visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                            Icon(if (confirmPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff, "Toggle visibility")
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    isError = error.isNotEmpty()
                )
                if (error.isNotEmpty()) {
                    Text(text = error, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    when {
                        currentPassword.isEmpty() -> error = "Ingresa tu contraseña actual"
                        newPassword.isEmpty() -> error = "Ingresa una nueva contraseña"
                        newPassword.length < 6 -> error = "La contraseña debe tener al menos 6 caracteres"
                        newPassword != confirmPassword -> error = "Las contraseñas no coinciden"
                        else -> onConfirm(currentPassword, newPassword)
                    }
                }
            ) {
                Text("Cambiar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
