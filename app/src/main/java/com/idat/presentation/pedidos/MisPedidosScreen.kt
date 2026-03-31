package com.idat.presentation.pedidos

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.idat.domain.model.Pedido
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MisPedidosScreen(
    navController: NavHostController,
    viewModel: MisPedidosViewModel = hiltViewModel()
) {
    val filteredPedidos by viewModel.filteredPedidos.collectAsState(emptyList())
    val selectedFilter by viewModel.selectedFilter.collectAsState()
    val totalMes by viewModel.totalGastoMes.collectAsState(0.0)

    val pinkPrimary = Color(0xFFAB005A)
    val pinkContainer = Color(0xFFD80073)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Pedidos", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = pinkPrimary) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = pinkPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White.copy(alpha = 0.8f))
            )
        },
        bottomBar = {
            PedidosBottomNavBar(navController)
        },
        containerColor = Color(0xFFFFF8F8)
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
                FilterTabs(
                    selected = selectedFilter,
                    onSelect = { viewModel.setFilter(it) }
                )
            }

            items(filteredPedidos) { pedido ->
                OrderCard(pedido)
            }

            item {
                Text("Resumen de compras", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF27171C))
                Spacer(modifier = Modifier.height(16.dp))
                
                // Spending Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(Brush.linearGradient(listOf(pinkPrimary, pinkContainer)))
                        .padding(24.dp)
                ) {
                    Column {
                        Text("GASTO TOTAL DEL MES", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White.copy(alpha = 0.8f), letterSpacing = 1.sp)
                        Text("S/ ${String.format("%.2f", totalMes)}", fontSize = 32.sp, fontWeight = FontWeight.Black, color = Color.White)
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.TrendingUp, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("8.2% más que el mes pasado", fontSize = 12.sp, color = Color.White.copy(alpha = 0.9f))
                        }
                    }
                }
                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }
}

@Composable
fun OrderCard(pedido: Pedido) {
    val statusColor = when (pedido.estado.lowercase()) {
        "entregado" -> Color(0xFF4CAF50)
        "procesando" -> Color(0xFFFFA000)
        "cancelado" -> Color(0xFFF44336)
        else -> Color(0xFFAB005A)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Pedido #${pedido.id.takeLast(6).uppercase()}", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF27171C))
                    Text(
                        SimpleDateFormat("dd MMM, yyyy", Locale.getDefault()).format(Date(pedido.fecha)),
                        fontSize = 13.sp,
                        color = Color.Gray
                    )
                }
                Surface(
                    color = statusColor.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        pedido.estado.uppercase(),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = statusColor
                    )
                }
            }

            Divider(modifier = Modifier.padding(vertical = 16.dp), color = Color(0xFFF5F5F5))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFFFDE7EB)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.ShoppingBag, contentDescription = null, tint = Color(0xFFAB005A))
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("${pedido.items.size} productos", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text("Total pagado: S/ ${String.format("%.2f", pedido.total)}", color = Color(0xFFAB005A), fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                }
                Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.LightGray)
            }
        }
    }
}

@Composable
fun FilterTabs(selected: String, onSelect: (String) -> Unit) {
    val filters = listOf("Todos", "Procesando", "Entregado", "Cancelado")
    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        items(filters) { filter ->
            val isSelected = filter == selected
            val bgColor by animateColorAsState(if (isSelected) Color(0xFFAB005A) else Color.White)
            val textColor by animateColorAsState(if (isSelected) Color.White else Color(0xFF8E6F77))

            Surface(
                modifier = Modifier.clickable { onSelect(filter) },
                shape = RoundedCornerShape(16.dp),
                color = bgColor,
                border = if (!isSelected) BorderStroke(1.dp, Color(0xFFE2BDC6)) else null
            ) {
                Text(
                    filter,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )
            }
        }
    }
}

@Composable
fun PedidosBottomNavBar(navController: NavHostController) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 24.dp, start = 12.dp, end = 12.dp)
            .clip(RoundedCornerShape(32.dp))
            .background(Color.White.copy(alpha = 0.95f))
            .padding(8.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
            BottomNavItem(Icons.Default.Explore, "Descubrir", false) { navController.navigate("catalogo") }
            BottomNavItem(Icons.Default.Search, "Buscar", false) { /* Search */ }
            BottomNavItem(Icons.Default.FavoriteBorder, "Favoritos", false) { navController.navigate("favoritos") }
            BottomNavItem(Icons.Default.ShoppingBag, "Bolsa", false) { navController.navigate("carrito") }
            BottomNavItem(Icons.Default.ReceiptLong, "Pedidos", true) { /* Already here */ }
        }
    }
}

@Composable
fun BottomNavItem(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, isActive: Boolean, onClick: () -> Unit) {
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
        Icon(icon, null, tint = if (isActive) pinkPrimary else inactiveColor, modifier = Modifier.size(24.dp))
        Text(label, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = if (isActive) pinkPrimary else inactiveColor)
    }
}
