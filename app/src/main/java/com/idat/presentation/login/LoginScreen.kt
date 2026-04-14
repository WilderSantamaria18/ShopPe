package com.idat.presentation.login

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.idat.R

@Composable
fun LoginScreen(navController: NavController, viewModel: LoginViewModel = hiltViewModel()) {

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var showWelcomeDialog by remember { mutableStateOf(false) }

    val pinkPrimary = MaterialTheme.colorScheme.primary

    val loginExitoso by viewModel.loginExitoso.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val nombreUsuarioState by viewModel.nombreUsuario.collectAsState()
    val rememberMe by viewModel.rememberMe.collectAsState()
    val savedEmail by viewModel.savedEmail.collectAsState()
    val savedPassword by viewModel.savedPassword.collectAsState()
    
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val focusManager = LocalFocusManager.current

    // ... (resto del código de efectos y Google Sign-In se mantiene igual)

    // Cargar datos guardados si existen
    LaunchedEffect(savedEmail, savedPassword) {
        if (savedEmail.isNotEmpty()) email = savedEmail
        if (savedPassword.isNotEmpty()) password = savedPassword
    }

    val gso = remember {
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("663208236763-ooknq4ajtkj3bkrknien9q52lf3p9utm.apps.googleusercontent.com")
            .requestEmail()
            .build()
    }

    val googleSignInClient = remember { GoogleSignIn.getClient(context, gso) }

    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                viewModel.loginWithGoogleCredential(SignInResult(credential = credential))
            } catch (e: ApiException) {
                viewModel.loginWithGoogleCredential(
                    SignInResult(errorMessage = "Error al iniciar sesión con Google: ${e.message}")
                )
            }
        }
    }

    if (loginExitoso) {
        showWelcomeDialog = true
    }

    errorMessage?.let { message ->
        LaunchedEffect(message) {
            snackbarHostState.showSnackbar(message = message, duration = SnackbarDuration.Long)
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState()),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.9f) // Aumentamos un poco el ancho disponible
                    .padding(horizontal = 24.dp, vertical = 48.dp), // Reducimos padding lateral de 32 a 24
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo_shoppe),
                    contentDescription = "Logo ShopPe",
                    modifier = Modifier
                        .size(150.dp) // Aumentamos un poco el tamaño contenedor
                        .padding(bottom = 16.dp),
                    contentScale = androidx.compose.ui.layout.ContentScale.Fit // Asegura que quepa completo
                )

                Text(
                    text = "Usuario o Correo electrónico",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(androidx.compose.ui.focus.FocusDirection.Down) }
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = pinkPrimary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                        cursorColor = pinkPrimary,
                        focusedTextColor = MaterialTheme.colorScheme.onBackground,
                        unfocusedTextColor = MaterialTheme.colorScheme.onBackground
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Contraseña",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                )

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp),
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { focusManager.clearFocus() }
                    ),
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = if (passwordVisible) "Ocultar" else "Mostrar",
                                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = pinkPrimary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                        cursorColor = pinkPrimary,
                        focusedTextColor = MaterialTheme.colorScheme.onBackground,
                        unfocusedTextColor = MaterialTheme.colorScheme.onBackground
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { viewModel.setRememberMe(!rememberMe) }
                    ) {
                        Checkbox(
                            checked = rememberMe,
                            onCheckedChange = { viewModel.setRememberMe(it) },
                            colors = CheckboxDefaults.colors(
                                checkedColor = pinkPrimary,
                                uncheckedColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                            )
                        )
                        Text(
                            text = "Recuérdame",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    
                    Text(
                        text = "¿Olvidaste tu contraseña?",
                        style = MaterialTheme.typography.bodySmall,
                        color = pinkPrimary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .clickable { /* Acción */ }
                            .padding(horizontal = 4.dp, vertical = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { viewModel.iniciarSesion(email, password) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = pinkPrimary,
                        contentColor = Color.White
                    )
                ) {
                    Text("Iniciar sesión", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    HorizontalDivider(modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f))
                    Text("O", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                    HorizontalDivider(modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f))
                }

                Spacer(modifier = Modifier.height(20.dp))

                OutlinedButton(
                    onClick = {
                        googleSignInClient.signOut().addOnCompleteListener {
                            val signInIntent = googleSignInClient.signInIntent
                            googleSignInLauncher.launch(signInIntent)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f))
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_google),
                        contentDescription = "Google",
                        modifier = Modifier.size(20.dp),
                        tint = Color.Unspecified
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Continuar con Google",
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "¿No tienes cuenta?",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    TextButton(onClick = { navController.navigate("registro") }) {
                        Text(
                            text = "Regístrate",
                            style = MaterialTheme.typography.bodyMedium,
                            color = pinkPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }

    // Modal de Bienvenida
    if (showWelcomeDialog) {
        Dialog(onDismissRequest = { }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(pinkPrimary.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            modifier = Modifier.size(50.dp),
                            tint = pinkPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "¡Bienvenido a ShopPe!",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Hola, $nombreUsuarioState. Qué gusto tenerte de vuelta.",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    Button(
                        onClick = {
                            showWelcomeDialog = false
                            navController.navigate("catalogo") {
                                popUpTo("login") { inclusive = true }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = pinkPrimary)
                    ) {
                        Text("Empezar a comprar", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
