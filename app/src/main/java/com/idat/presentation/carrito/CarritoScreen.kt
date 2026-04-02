package com.idat.presentation.carrito

import com.idat.presentation.components.ShopPeBottomNavBar
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.idat.domain.model.ItemCarrito

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarritoScreen(
    navController: NavHostController,
    viewModel: CarritoViewModel = hiltViewModel()
) {
    val productos by viewModel.productos.collectAsState()
    val total = productos.sumOf { it.precio * it.cantidad }
    val itemCount = productos.sumOf { it.cantidad }
    
    val isDark = MaterialTheme.colorScheme.surface == Color(0xFF140C0E)
    val outlineColor = if (isDark) Color(0xFFA88991) else Color(0xFF8E6F77)
    val surfaceContainerLowest = if (isDark) Color(0xFF140C0E) else Color.White
    val surfaceContainerLow = if (isDark) Color(0xFF1F1215) else Color(0xFFFFF0F2)

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface,
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = "Tu Bolsa", 
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        letterSpacing = (-0.5).sp,
                        fontSize = 20.sp
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = MaterialTheme.colorScheme.primary)
                    }
                },
                actions = {
                    Text(
                        text = "$itemCount Items",
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(end = 16.dp)
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
                )
            )
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp))
                    .background(surfaceContainerLowest.copy(alpha = 0.95f))
            ) {
                // Fixed Bottom Action Area (Checkout Summary)
                if (productos.isNotEmpty()) {
                    Column(
                        modifier = Modifier.padding(start = 32.dp, end = 32.dp, top = 24.dp, bottom = 12.dp)
                    ) {
                        // Summary lines
                        Row(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Total", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onSurface)
                            Text("S/ ${String.format("%.2f", total)}", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onSurface)
                        }

                        // CTA Button
                        Button(
                            onClick = { navController.navigate("pago") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = CircleShape,
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                            contentPadding = PaddingValues()
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        brush = Brush.linearGradient(
                                            colors = listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.primaryContainer)
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "Proceder al Pago",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    }
                }

                // Standard Navigation Bar
                ShopPeBottomNavBar(
                    currentSelection = "Bag",
                    onNavigateToCatalogo = { navController.navigate("catalogo") },
                    onNavigateToFavoritos = { navController.navigate("favoritos/fromCart") },
                    onNavigateToCarrito = { /* Already here */ },
                    onNavigateToGestion = { navController.navigate("gestion/fromCart") },
                    onNavigateToPedidos = { navController.navigate("mis_pedidos") },
                    onNavigateToAyuda = { navController.navigate("ayuda/fromCart") },
                    onNavigateToConfiguracion = { navController.navigate("configuracion/fromCart") },
                    onNavigateToPersonalizacion = { navController.navigate("personalizacion/fromCart") },
                    onNavigateToDirecciones = { navController.navigate("direcciones") },
                    onCerrarSesion = { 
                        navController.navigate("login") {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }
        }
    ) { paddingValues ->
        if (productos.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Icon(
                        Icons.Default.ShoppingCart,
                        contentDescription = "Bolsa vacía",
                        modifier = Modifier.size(100.dp),
                        tint = outlineColor.copy(alpha = 0.4f)
                    )
                    Text(
                        text = "Your Bag is Empty",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Add some curated items to your collection.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        } else {
            Box(modifier = Modifier.fillMaxSize()) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(horizontal = 24.dp),
                    contentPadding = PaddingValues(top = 16.dp, bottom = 260.dp), // Extra space for checkout bottom sheet
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    items(productos, key = { it.id }) { item ->
                        ProductoCarritoItem(
                            item = item,
                            onEliminar = { viewModel.eliminarDelCarrito(item.id) },
                            onIncrementar = { viewModel.incrementarCantidad(item) },
                            onDecrementar = { viewModel.decrementarCantidad(item) },
                            surfaceContainerLow = surfaceContainerLow
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(32.dp))
                        RecommendedSetSection(surfaceContainerLow)
                    }
                }

            }
        }
    }
}

@Composable
fun ProductoCarritoItem(
    item: ItemCarrito,
    onEliminar: () -> Unit,
    onIncrementar: () -> Unit,
    onDecrementar: () -> Unit,
    surfaceContainerLow: Color = Color.LightGray
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Product Image
        Box(
            modifier = Modifier
                .width(128.dp)
                .height(160.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(surfaceContainerLow)
        ) {
            AsyncImage(
                model = item.imagen,
                contentDescription = item.nombre,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        // Product Details
        Column(
            modifier = Modifier
                .weight(1f)
                .height(160.dp)
                .padding(vertical = 4.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top area (Title + Delete)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.nombre,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        letterSpacing = (-0.5).sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Talla: Estándar", // Mock variants
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Medium
                    )
                }
                
                IconButton(
                    onClick = onEliminar, 
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline, 
                        contentDescription = "Eliminar", 
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Bottom area (Price + Quantity Selector)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "S/ ${item.precio}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                // Quantity Pill Selector
                Row(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Remove, 
                        contentDescription = "Minus",
                        modifier = Modifier.size(16.dp).clickable { onDecrementar() },
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${item.cantidad}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Icon(
                        imageVector = Icons.Default.Add, 
                        contentDescription = "Plus",
                        modifier = Modifier.size(16.dp).clickable { onIncrementar() },
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@Composable
fun RecommendedSetSection(surfaceContainerLow: Color = Color.LightGray) {
    Column {
        Text(
            text = "COMPLETA TU SET",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 2.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 24.dp)
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            RecommendedItemBento(
                modifier = Modifier.weight(1f),
                title = "Silk Mist Candle",
                price = "S/ 42.00",
                surfaceContainerLow = surfaceContainerLow
            )
            RecommendedItemBento(
                modifier = Modifier.weight(1f),
                title = "Cloud Cotton Set",
                price = "S/ 65.00",
                surfaceContainerLow = surfaceContainerLow
            )
        }
    }
}

@Composable
fun RecommendedItemBento(modifier: Modifier = Modifier, title: String, price: String, surfaceContainerLow: Color = Color.LightGray) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(surfaceContainerLow)
            .padding(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            // Fake image holder
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            )
            Column {
                Text(
                    text = title, 
                    fontSize = 12.sp, 
                    fontWeight = FontWeight.Bold, 
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = price, 
                    fontSize = 12.sp, 
                    fontWeight = FontWeight.Bold, 
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
