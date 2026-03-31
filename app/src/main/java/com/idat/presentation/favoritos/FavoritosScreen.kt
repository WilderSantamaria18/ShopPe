package com.idat.presentation.favoritos

import android.annotation.SuppressLint
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.idat.domain.model.Producto

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun FavoritosScreen(
    navController: NavHostController,
    viewModel: FavoritosViewModel = hiltViewModel()
) {
    val productos by viewModel.favoritos.collectAsState()
    
    val pinkPrimary = Color(0xFFAB005A)
    val pinkContainer = Color(0xFFD80073)
    val surfaceColor = Color(0xFFFFF8F8)
    val onSurface = Color(0xFF27171C)
    val surfaceContainerLow = Color(0xFFFFF0F2)

    Scaffold(
        containerColor = surfaceColor,
        topBar = {
            TopAppBar(
                title = {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text(
                            "Mis Favoritos",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = onSurface,
                            letterSpacing = (-0.5).sp
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = pinkPrimary)
                    }
                },
                actions = {
                    Spacer(modifier = Modifier.width(48.dp)) // For centering
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White.copy(alpha = 0.8f))
            )
        },
        bottomBar = {
            FavoritosBottomNavBar(navController)
        }
    ) { paddingValues ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            if (productos.isEmpty()) {
                item(span = { GridItemSpan(2) }) {
                   EmptyFavoritesState()
                }
            } else {
                items(productos, key = { it.id }) { producto ->
                    FavoritoCard(
                        producto = producto,
                        onFavoriteClick = { viewModel.eliminarDeFavoritos(producto.id) },
                        onAddToCart = { viewModel.agregarAlCarrito(producto) }
                    )
                }
            }

            // Discovery Section
            item(span = { GridItemSpan(2) }) {
                Column(modifier = Modifier.padding(top = 16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Recomendaciones para ti",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = onSurface,
                            letterSpacing = (-0.5).sp
                        )
                        Text(
                            "Ver todo",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = pinkPrimary
                        )
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    LazyRow(
                        modifier = Modifier.padding(horizontal = 0.dp),
                        horizontalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        item {
                            RecommendationCard(
                                title = "Pañuelo de Seda Pura",
                                price = "S/ 120.00",
                                imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuChQBCAye48yleXP8toUwkxWZoZC-z4xVCmDr7l9q0WpJF6S9WFkl6gP9u5i2HUPXqeMJzA0Dh63TRsmAxqQ-Ovu1uTdhB-OK7hhGNxDLN4-yb37Fi3TLy40IYCptTWoqKRoZcY7Nlj-8m2l2TsT3I11SSdh9Y5ueCVWecGhrZJAoAiZiiiBQ_syeqMZdfilet7Q9oTd3Kr6fxOuQprFAv1DnK92ZbW9VwIT8hmmNyMno5STNX5qhfldE0hYB5Wbs1A8Vy8ZvjW0ME"
                            )
                        }
                        item {
                            RecommendationCard(
                                title = "Bolso de Cuero Artisan",
                                price = "S/ 580.00",
                                imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuA-oped9eCT4L1yEhjVuu17ly8FAf8hlEjUMB6KY-2dM_K9gNDGNQUUb591dVlUtxkSBtgX6akSUO6VRAtXUWTmsD_6sWTKyr9Pa1juJLiVDTwykdn6fgiZdJ--dzt9EPJ23ENIRWPCt7UD6gunWPMS2S_twg_rajZFXbYaUoMZ4UdIWO1oWlBZ_XrrUrPf1GXwV9gtOHHWA2qtY1bJCHAGXLpOmlnmdZAMjwWaEOX7I-y-H4n2rfzD0JvyRDzpOOWs5gxoQhuyBiI"
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(100.dp))
                }
            }
        }
    }
}

@Composable
fun FavoritoCard(
    producto: Producto,
    onFavoriteClick: () -> Unit,
    onAddToCart: () -> Unit
) {
    val pinkPrimary = Color(0xFFAB005A)
    val pinkContainer = Color(0xFFD80073)

    Column(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(3f / 4f)
                .clip(RoundedCornerShape(24.dp))
                .background(Color(0xFFFFF0F2))
        ) {
            AsyncImage(
                model = producto.imagen,
                contentDescription = producto.nombre,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            
            IconButton(
                onClick = onFavoriteClick,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(12.dp)
                    .size(36.dp)
                    .background(Color.White.copy(alpha = 0.9f), CircleShape)
            ) {
                Icon(
                    Icons.Default.Favorite,
                    contentDescription = null,
                    tint = pinkPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        
        Text(
            producto.nombre,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = Color(0xFF27171C)
        )
        
        Text(
            "S/ ${String.format("%.2f", producto.precio)}",
            fontSize = 18.sp,
            fontWeight = FontWeight.ExtraBold,
            color = pinkPrimary,
            modifier = Modifier.padding(top = 4.dp)
        )

        Button(
            onClick = onAddToCart,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp)
                .height(48.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
            contentPadding = PaddingValues()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Brush.linearGradient(listOf(pinkPrimary, pinkContainer))),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.ShoppingBag, contentDescription = null, modifier = Modifier.size(18.dp), tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Añadir", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
        }
    }
}

@Composable
fun RecommendationCard(title: String, price: String, imageUrl: String) {
    Row(
        modifier = Modifier
            .width(280.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(Color(0xFFFFF0F2))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = imageUrl,
            contentDescription = title,
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(16.dp)),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF27171C), maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(price, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = Color(0xFFAB005A), modifier = Modifier.padding(top = 4.dp))
            Text(
                "DESCUBRIR",
                fontSize = 10.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFF455F88),
                letterSpacing = 1.sp,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
fun EmptyFavoritesState() {
    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = 60.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            Icons.Default.FavoriteBorder,
            contentDescription = null,
            modifier = Modifier.size(100.dp),
            tint = Color(0xFFE2BDC6)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text("Tu lista está vacía", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF27171C))
        Text("Explora nuestra tienda y guarda tus productos favoritos aquí.", textAlign = TextAlign.Center, fontSize = 14.sp, color = Color(0xFF8E6F77), modifier = Modifier.padding(start = 32.dp, top = 8.dp, end = 32.dp))
    }
}

@Composable
fun FavoritosBottomNavBar(navController: NavHostController) {
    val pinkPrimary = Color(0xFFAB005A)
    val inactiveColor = Color(0xFF8E6F77)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 24.dp, start = 12.dp, end = 12.dp)
            .clip(RoundedCornerShape(32.dp))
            .background(Color.White.copy(alpha = 0.95f))
            .padding(8.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
            NavItem(Icons.Default.Explore, "Descubrir", false) { navController.navigate("catalogo") }
            NavItem(Icons.Default.Search, "Buscar", false) { navController.navigate("catalogo") }
            NavItem(Icons.Default.Favorite, "Favoritos", true) { /* Already here */ }
            NavItem(Icons.Default.ShoppingBag, "Bolsa", false) { navController.navigate("carrito") }
            NavItem(Icons.Default.Person, "Perfil", false) { /* Profile */ }
        }
    }
}

@Composable
fun NavItem(icon: ImageVector, label: String, isActive: Boolean, onClick: () -> Unit) {
    val pinkPrimary = Color(0xFFAB005A)
    val inactiveColor = Color(0xFF8E6F77)
    
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (isActive) Color(0xFFFFE8ED) else Color.Transparent)
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = if (isActive) pinkPrimary else inactiveColor,
            modifier = Modifier.size(24.dp)
        )
        Text(
            label,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = if (isActive) pinkPrimary else inactiveColor,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}
