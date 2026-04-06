package com.idat.presentation.ui.home

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.idat.domain.model.Producto
import com.idat.presentation.components.ProductItem
import com.idat.presentation.components.ShopPeBottomNavBar
import com.idat.presentation.components.shimmerEffect

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ShopPeHomeScreen(
    isLoading: Boolean = true,
    viewMode: String = "grid",
    products: List<Producto> = emptyList(),
    categorias: List<String> = emptyList(),
    categoriaSeleccionada: String = "",
    textoBusqueda: String = "",
    onSearchTextChanged: (String) -> Unit = {},
    onCategorySelected: (String) -> Unit = {},
    onProductClick: (Producto) -> Unit = {},
    onProductFavorite: (Producto) -> Unit = {},
    onAddToCart: (Producto) -> Unit = {},
    isProductFavorite: suspend (Int) -> Boolean = { false },
    // Navbar Actions
    onNavigateToFavoritos: () -> Unit = {},
    onNavigateToGestion: () -> Unit = {},
    onNavigateToAyuda: () -> Unit = {},
    onNavigateToCarrito: () -> Unit = {},
    onNavigateToPedidos: () -> Unit = {},
    onNavigateToConfiguracion: () -> Unit = {},
    onNavigateToPersonalizacion: () -> Unit = {},
    onNavigateToDirecciones: () -> Unit = {},
    onCerrarSesion: () -> Unit = {}
) {
    val isDark = MaterialTheme.colorScheme.surface == Color(0xFF140C0E)
    val surfaceContainerLow = if (isDark) Color(0xFF1F1215) else Color(0xFFFFF0F2)
    val surfaceContainerHigh = if (isDark) Color(0xFF332025) else Color(0xFFFEE1E7)
    val outlineColor = if (isDark) Color(0xFFA88991) else Color(0xFF8E6F77)

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "ShopPe",
                        fontWeight = FontWeight.Black,
                        fontSize = 20.sp,
                        letterSpacing = (-1).sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                navigationIcon = { },
                actions = {
                    IconButton(onClick = { /* Open Search */ }) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Search Bar
            TextField(
                value = textoBusqueda,
                onValueChange = onSearchTextChanged,
                placeholder = { Text("Buscar colección...", color = outlineColor) },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Search, contentDescription = "Search icon", tint = outlineColor)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp)),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = surfaceContainerLow,
                    unfocusedContainerColor = surfaceContainerLow,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = MaterialTheme.colorScheme.primary
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Category Chips
            val scrollState = rememberScrollState()
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(scrollState),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                categorias.forEach { categoria ->
                    CategoryChip(
                        text = categoria, 
                        isSelected = categoriaSeleccionada == categoria,
                        onClick = { onCategorySelected(categoria) },
                        surfaceContainerHigh = surfaceContainerHigh
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Product View
            if (isLoading && products.isEmpty()) {
                if (viewMode == "list") {
                    ShimmerProductList()
                } else {
                    ShimmerProductGrid()
                }
            } else if (products.isEmpty()) {
                Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                    Text("No se encontraron productos", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                if (viewMode == "list") {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(32.dp),
                        contentPadding = PaddingValues(bottom = 120.dp)
                    ) {
                        itemsIndexed(products) { index, producto ->
                            var isFav by remember { mutableStateOf(false) }
                            LaunchedEffect(producto.id) { isFav = isProductFavorite(producto.id) }
                            val showTag = index == 0 || index == 3
                            val tagText = if (index == 0) "LIMITED" else "NEW IN"
                            
                            ProductItem(
                                producto = producto,
                                isFavorite = isFav,
                                onFavoriteClick = {
                                    onProductFavorite(producto)
                                    isFav = !isFav
                                },
                                onClick = { onProductClick(producto) },
                                onAddToCart = { onAddToCart(producto) },
                                showTag = showTag,
                                tagText = tagText
                            )
                        }
                    }
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalArrangement = Arrangement.spacedBy(32.dp),
                        contentPadding = PaddingValues(bottom = 120.dp)
                    ) {
                        itemsIndexed(products) { index, producto ->
                            var isFav by remember { mutableStateOf(false) }
                            LaunchedEffect(producto.id) { isFav = isProductFavorite(producto.id) }
                            val showTag = index == 0 || index == 3
                            val tagText = if (index == 0) "LIMITED" else "NEW IN"
                            
                            ProductItem(
                                producto = producto,
                                isFavorite = isFav,
                                onFavoriteClick = {
                                    onProductFavorite(producto)
                                    isFav = !isFav
                                },
                                onClick = { onProductClick(producto) },
                                onAddToCart = { onAddToCart(producto) },
                                showTag = showTag,
                                tagText = tagText
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryChip(text: String, isSelected: Boolean = false, onClick: () -> Unit = {}, surfaceContainerHigh: Color = Color.LightGray) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(24.dp))
            .clickable { onClick() }
            .background(if (isSelected) MaterialTheme.colorScheme.secondary else surfaceContainerHigh)
            .padding(horizontal = 24.dp, vertical = 8.dp)
    ) {
        Text(
            text = text,
            color = if (isSelected) MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun ShimmerProductGrid() {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(32.dp),
        contentPadding = PaddingValues(bottom = 120.dp)
    ) {
        items(4) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Box(modifier = Modifier.fillMaxWidth().aspectRatio(3f / 4f).clip(RoundedCornerShape(12.dp)).shimmerEffect())
                Spacer(modifier = Modifier.height(12.dp))
                Box(modifier = Modifier.fillMaxWidth().height(16.dp).clip(RoundedCornerShape(4.dp)).shimmerEffect())
                Spacer(modifier = Modifier.height(4.dp))
                Box(modifier = Modifier.fillMaxWidth(0.5f).height(16.dp).clip(RoundedCornerShape(4.dp)).shimmerEffect())
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.width(60.dp).height(20.dp).clip(RoundedCornerShape(4.dp)).shimmerEffect())
                    Box(modifier = Modifier.size(24.dp).clip(RoundedCornerShape(12.dp)).shimmerEffect())
                }
            }
        }
    }
}

@Composable
fun ShimmerProductList() {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(32.dp),
        contentPadding = PaddingValues(bottom = 120.dp)
    ) {
        items(3) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Box(modifier = Modifier.fillMaxWidth().aspectRatio(4f / 3f).clip(RoundedCornerShape(12.dp)).shimmerEffect())
                Spacer(modifier = Modifier.height(12.dp))
                Box(modifier = Modifier.fillMaxWidth().height(16.dp).clip(RoundedCornerShape(4.dp)).shimmerEffect())
                Spacer(modifier = Modifier.height(4.dp))
                Box(modifier = Modifier.fillMaxWidth(0.5f).height(16.dp).clip(RoundedCornerShape(4.dp)).shimmerEffect())
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.width(60.dp).height(20.dp).clip(RoundedCornerShape(4.dp)).shimmerEffect())
                    Box(modifier = Modifier.size(24.dp).clip(RoundedCornerShape(12.dp)).shimmerEffect())
                }
            }
        }
    }
}
