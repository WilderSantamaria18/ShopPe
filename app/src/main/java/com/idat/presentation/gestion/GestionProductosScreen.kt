package com.idat.presentation.gestion

import com.idat.presentation.components.*
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.idat.domain.model.Producto
import kotlinx.coroutines.launch

enum class GestionMode { LIST, ADD, EDIT }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GestionProductosScreen(
    navController: NavHostController,
    viewModel: GestionProductosViewModel = hiltViewModel()
) {
    val productos by viewModel.productos.collectAsState()
    var currentMode by remember { mutableStateOf(GestionMode.LIST) }
    var selectedProducto by remember { mutableStateOf<Producto?>(null) }
    
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val pinkPrimary = Color(0xFFAB005A)
    val pinkContainer = Color(0xFFD80073)

    Scaffold(
        topBar = {
            if (currentMode == GestionMode.LIST) {
                TopAppBar(
                    title = { Text("Gestión de Productos", fontWeight = FontWeight.Bold, fontSize = 20.sp) },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = pinkPrimary)
                        }
                    },
                    actions = {
                        IconButton(onClick = { navController.navigate("admin_comprobantes") }) {
                            Icon(Icons.Default.ReceiptLong, contentDescription = "Comprobantes", tint = pinkPrimary)
                        }
                        IconButton(onClick = { navController.navigate("admin_usuarios") }) {
                            Icon(Icons.Default.People, contentDescription = "Usuarios", tint = pinkPrimary)
                        }
                        IconButton(onClick = { /* More options */ }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "Más", tint = Color.Gray)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White.copy(alpha = 0.8f))
                )
            }
        },
        floatingActionButton = {
            if (currentMode == GestionMode.LIST) {
                FloatingActionButton(
                    onClick = { currentMode = GestionMode.ADD },
                    shape = RoundedCornerShape(20.dp),
                    containerColor = Color.Transparent,
                    contentColor = Color.White,
                    modifier = Modifier.size(64.dp)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize().background(brush = Brush.linearGradient(colors = listOf(pinkPrimary, pinkContainer))),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Agregar producto", modifier = Modifier.size(32.dp))
                    }
                }
            }
        },
        bottomBar = {
            if (currentMode == GestionMode.LIST) {
                ShopPeBottomNavBar(
                    currentSelection = "Perfil",
                    onNavigateToCatalogo = { navController.navigate("catalogo") },
                    onNavigateToFavoritos = { navController.navigate("favoritos/fromGestion") },
                    onNavigateToCarrito = { navController.navigate("carrito") },
                    onNavigateToGestion = { /* Already here */ },
                    onNavigateToPedidos = { navController.navigate("mis_pedidos") },
                    onNavigateToAyuda = { navController.navigate("ayuda/fromGestion") },
                    onNavigateToConfiguracion = { navController.navigate("configuracion/fromGestion") },
                    onNavigateToPersonalizacion = { navController.navigate("personalizacion/fromGestion") },
                    onNavigateToDirecciones = { navController.navigate("direcciones") },
                    onCerrarSesion = { 
                        navController.navigate("login") {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color(0xFFFFF8F8)
    ) { paddingValues ->
        Crossfade(targetState = currentMode, label = "ScreenTransition") { mode ->
            when (mode) {
                GestionMode.LIST -> {
                    GestionListaView(
                        paddingValues = paddingValues,
                        productos = productos,
                        onEdit = { 
                            selectedProducto = it
                            currentMode = GestionMode.EDIT
                        },
                        onDelete = { producto ->
                            viewModel.eliminarProducto(producto.id, onSuccess = {
                                scope.launch { snackbarHostState.showSnackbar("Producto eliminado correctamente") }
                            }, onError = { error ->
                                scope.launch { snackbarHostState.showSnackbar(error) }
                            })
                        },
                        onAddClick = { currentMode = GestionMode.ADD }
                    )
                }
                GestionMode.ADD -> {
                    ProductFormView(
                        titulo = "Nuevo Producto",
                        producto = null,
                        onBack = { currentMode = GestionMode.LIST },
                        onSave = { nombre, precio, desc, cat, img, cal, cant ->
                            viewModel.crearProducto(nombre, precio, desc, cat, img, cal, cant, onSuccess = {
                                currentMode = GestionMode.LIST
                                scope.launch { snackbarHostState.showSnackbar("Producto creado") }
                            }, onError = { error ->
                                scope.launch { snackbarHostState.showSnackbar(error) }
                            })
                        }
                    )
                }
                GestionMode.EDIT -> {
                    ProductFormView(
                        titulo = "Editar Producto",
                        producto = selectedProducto,
                        onBack = { currentMode = GestionMode.LIST },
                        onSave = { nombre, precio, desc, cat, img, cal, cant ->
                            selectedProducto?.let { old ->
                                val updated = old.copy(nombre = nombre, precio = precio, descripcion = desc, categoria = cat, imagen = img, calificacion = cal, cantidadCalificaciones = cant)
                                viewModel.actualizarProducto(updated, onSuccess = {
                                    currentMode = GestionMode.LIST
                                    scope.launch { snackbarHostState.showSnackbar("Producto actualizado") }
                                }, onError = { error ->
                                    scope.launch { snackbarHostState.showSnackbar(error) }
                                })
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun GestionListaView(
    paddingValues: PaddingValues,
    productos: List<Producto>,
    onEdit: (Producto) -> Unit,
    onDelete: (Producto) -> Unit,
    onAddClick: () -> Unit
) {
    val pinkPrimary = Color(0xFFAB005A)
    val pinkContainer = Color(0xFFD80073)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Text("Inventario General", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color(0xFF455F88))
            Text("Mis Productos", fontSize = 32.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF27171C), letterSpacing = (-1).sp)
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(32.dp))
                    .background(Color(0xFFFFF0F2))
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Inventory2, contentDescription = null, tint = pinkPrimary, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("${productos.size} Items", fontWeight = FontWeight.Bold, color = Color(0xFF5A3F47))
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        items(productos) { producto ->
            ProductItemPill(producto, onEdit = { onEdit(producto) }, onDelete = { onDelete(producto) })
        }

        item {
            // Suggestion Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
                    .clip(RoundedCornerShape(32.dp))
                    .border(2.dp, Brush.linearGradient(listOf(Color(0xFFE2BDC6), Color(0xFFE2BDC6).copy(alpha = 0.3f))), RoundedCornerShape(32.dp))
                    .background(Color.White.copy(alpha = 0.4f))
                    .clickable { onAddClick() }
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.AddCircle, contentDescription = null, tint = Color(0xFFE2BDC6), modifier = Modifier.size(48.dp))
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("¿Tienes algo nuevo?", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text("Agrega un producto más a tu catálogo", fontSize = 12.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = onAddClick,
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        contentPadding = PaddingValues()
                    ) {
                        Box(
                            modifier = Modifier.background(brush = Brush.linearGradient(colors = listOf(pinkPrimary, pinkContainer))).padding(horizontal = 32.dp, vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Subir Producto", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

@Composable
fun ProductItemPill(producto: Producto, onEdit: () -> Unit, onDelete: () -> Unit) {
    val pinkPrimary = Color(0xFFAB005A)
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(CircleShape)
            .background(Color.White)
            .shadow(
                elevation = 8.dp,
                shape = CircleShape,
                ambientColor = Color(0xFF27171C).copy(alpha = 0.05f),
                spotColor = Color(0xFF27171C).copy(alpha = 0.05f)
            )
            .padding(8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            AsyncImage(
                model = producto.imagen,
                contentDescription = producto.nombre,
                modifier = Modifier.size(96.dp).clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("ID: #SP-${producto.id.toString().takeLast(4)}", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF725000), letterSpacing = 1.sp)
                Text(producto.nombre, fontWeight = FontWeight.Bold, fontSize = 16.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text("S/ ${producto.precio}", fontWeight = FontWeight.Black, fontSize = 18.sp, color = Color(0xFFD80073))
            }
            Column(modifier = Modifier.padding(end = 16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                IconButton(onClick = onEdit, modifier = Modifier.size(40.dp).background(Color(0xFFFFD9E2), CircleShape)) {
                    Icon(Icons.Default.Edit, contentDescription = "Editar", tint = pinkPrimary, modifier = Modifier.size(18.dp))
                }
                IconButton(onClick = onDelete, modifier = Modifier.size(40.dp).background(Color(0xFFFFDAD6), CircleShape)) {
                    Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color.Red, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductFormView(
    titulo: String,
    producto: Producto?,
    onBack: () -> Unit,
    onSave: (String, Double, String, String, String, Double, Int) -> Unit
) {
    var nombre by remember { mutableStateOf(producto?.nombre ?: "") }
    var precio by remember { mutableStateOf(producto?.precio?.toString() ?: "") }
    var descripcion by remember { mutableStateOf(producto?.descripcion ?: "") }
    var categoria by remember { mutableStateOf(producto?.categoria ?: "Hogar y Decoración") }
    var imagen by remember { mutableStateOf(producto?.imagen ?: "") }
    var calificacion by remember { mutableStateOf(producto?.calificacion?.toString() ?: "5.0") }
    var cantidadCalificaciones by remember { mutableStateOf(producto?.cantidadCalificaciones?.toString() ?: "0") }

    val pinkPrimary = Color(0xFFAB005A)
    val pinkContainer = Color(0xFFD80073)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestión de Productos", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = pinkPrimary)
                    }
                },
                actions = {
                    IconButton(onClick = { /* More */ }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Más", tint = Color.Gray)
                    }
                }
            )
        },
        containerColor = Color(0xFFFFF8F8)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Column {
                Text(titulo, fontSize = 32.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = (-1).sp)
                Text("Complete los detalles a continuación para añadir una nueva pieza.", fontSize = 14.sp, color = Color.Gray)
            }

            // Identity Card
            Column(
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(24.dp)).background(Color.White).padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                FormFieldLabel("Nombre del Producto")
                FormTextField(value = nombre, onValueChange = { nombre = it }, placeholder = "Ej. Jarrón de Cerámica")

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Column(modifier = Modifier.weight(1f)) {
                        FormFieldLabel("Precio (S/)")
                        FormTextField(value = precio, onValueChange = { precio = it }, placeholder = "0.00", isNumber = true, prefix = "S/")
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        FormFieldLabel("Categoría")
                        CategorySelector(selected = categoria, onSelect = { categoria = it })
                    }
                }
            }

            // Description & Image Card
            Column(
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(24.dp)).background(Color(0xFFFFF0F2)).padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                FormFieldLabel("Descripción")
                FormTextField(value = descripcion, onValueChange = { descripcion = it }, placeholder = "Historia y materiales...", singleLine = false, minLines = 4)

                FormFieldLabel("URL de Imagen")
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Box(modifier = Modifier.weight(1f)) {
                        FormTextField(value = imagen, onValueChange = { imagen = it }, placeholder = "https://...")
                    }
                    Box(modifier = Modifier.size(56.dp).clip(RoundedCornerShape(12.dp)).background(Color(0xFFF8DBE2))) {
                        AsyncImage(model = imagen, contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                    }
                }
            }

            // Metrics
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                MetricBox(modifier = Modifier.weight(1f), label = "Calificación", value = calificacion, onValueChange = { calificacion = it }, icon = Icons.Default.Star)
                MetricBox(modifier = Modifier.weight(1f), label = "Reseñas", value = cantidadCalificaciones, onValueChange = { cantidadCalificaciones = it }, icon = Icons.Default.ChatBubble)
            }

            // Save Button
            Button(
                onClick = { 
                    val p = precio.toDoubleOrNull() ?: 0.0
                    val c = calificacion.toDoubleOrNull() ?: 5.0
                    val r = cantidadCalificaciones.toIntOrNull() ?: 0
                    onSave(nombre, p, descripcion, categoria, imagen, c, r)
                },
                modifier = Modifier.fillMaxWidth().height(64.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                contentPadding = PaddingValues()
            ) {
                Box(
                    modifier = Modifier.fillMaxSize().background(brush = Brush.linearGradient(colors = listOf(pinkPrimary, pinkContainer))),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Guardar Producto", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
            }
            
            Text("Este producto será visible inmediatamente en el catálogo.", textAlign = TextAlign.Center, fontSize = 11.sp, color = Color.Gray, modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp))
        }
    }
}

// Composable components moved to SharedProductComponents.kt


