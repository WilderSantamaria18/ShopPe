package com.idat.presentation.pedidos

import androidx.compose.foundation.*
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
fun DetallePedidoScreen(
    navController: NavHostController,
    pedidoId: String,
    viewModel: MisPedidosViewModel = hiltViewModel()
) {
    val pedidos by viewModel.pedidos.collectAsState()
    val pedido = pedidos.find { it.id == pedidoId }

    val pinkPrimary = Color(0xFFAB005A)
    val surfaceColor = Color(0xFFFFF8F8)

    Scaffold(
        containerColor = surfaceColor,
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Detalle del Pedido", 
                        fontWeight = FontWeight.ExtraBold, 
                        fontSize = 20.sp, 
                        color = Color(0xFF27171C)
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = pinkPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { paddingValues ->
        if (pedido == null) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = pinkPrimary)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp),
                contentPadding = PaddingValues(bottom = 32.dp)
            ) {
                // Order Info Card
                item {
                    OrderInfoSection(pedido)
                }

                // Status Timeline Placeholder
                item {
                    StatusTimeline(pedido.estado)
                }

                // Items List
                item {
                    Text(
                        "Artículos (${pedido.items.size})",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 18.sp,
                        color = Color(0xFF27171C),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                items(pedido.items) { item ->
                    OrderDetailItemCard(item.nombre, item.precio, item.cantidad, item.imagen)
                }

                // Summary Card
                item {
                    OrderSummaryCard(pedido.total)
                }

                // Footer Action
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = pinkPrimary)
                    ) {
                        Text("Regresar a mis pedidos", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun OrderInfoSection(pedido: Pedido) {
    val pinkPrimary = Color(0xFFAB005A)
    val (statusLabel, statusBg, statusText) = when (pedido.estado.lowercase()) {
        "entregado" -> Triple("ENTREGADO", Color(0xFFE6F4EA), Color(0xFF1E8E3E))
        "pendiente" -> Triple("PENDIENTE", Color(0xFFFFF7E0), Color(0xFFF2994A))
        "procesando" -> Triple("PROCESANDO", Color(0xFFFFF7E0), Color(0xFFF09300))
        "cancelado" -> Triple("CANCELADO", Color(0xFFFFEAEA), Color(0xFFD93025))
        else -> Triple("EN CAMINO", Color(0xFFFFE8ED), pinkPrimary)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(Color.White)
            .padding(24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    "Pedido #${pedido.id.uppercase()}",
                    fontWeight = FontWeight.Black,
                    fontSize = 18.sp,
                    color = Color(0xFF27171C)
                )
                Text(
                    SimpleDateFormat("dd MMM, yyyy - hh:mm a", Locale.getDefault()).format(Date(pedido.fecha)),
                    fontSize = 13.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            
            Surface(
                color = statusBg,
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    statusLabel,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    color = statusText,
                    letterSpacing = 0.5.sp
                )
            }
        }
    }
}

@Composable
fun StatusTimeline(estado: String) {
    val pinkPrimary = Color(0xFFAB005A)
    val inactiveColor = Color(0xFFEEEAEB)

    val steps = listOf("Pendiente", "Procesando", "En camino", "Entregado")
    val currentStepIndex = steps.indexOfFirst { it.equals(estado, ignoreCase = true) }.coerceAtLeast(0)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(Color.White)
            .padding(24.dp)
    ) {
        Text("Estado del Pedido", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF27171C))
        Spacer(modifier = Modifier.height(20.dp))
        
        steps.forEachIndexed { index, step ->
            val isActive = index <= currentStepIndex
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(if (isActive) pinkPrimary else inactiveColor),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isActive) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
                        }
                    }
                    if (index < steps.size - 1) {
                        Box(
                            modifier = Modifier
                                .width(2.dp)
                                .height(30.dp)
                                .background(if (index < currentStepIndex) pinkPrimary else inactiveColor)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.padding(bottom = if (index < steps.size - 1) 30.dp else 0.dp)) {
                    Text(
                        step,
                        fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
                        color = if (isActive) Color(0xFF27171C) else Color.Gray,
                        fontSize = 14.sp
                    )
                    if (isActive && index == currentStepIndex) {
                        Text("Actual", fontSize = 11.sp, color = pinkPrimary, fontWeight = FontWeight.Medium)
                    }
                }
            }
        }
    }
}

@Composable
fun OrderDetailItemCard(nombre: String, precio: Double, cantidad: Int, imagen: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(Color.White)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = imagen,
            contentDescription = null,
            modifier = Modifier
                .size(70.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFFFFF0F2)),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(nombre, fontWeight = FontWeight.Bold, fontSize = 15.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text("S/ ${String.format("%.2f", precio)} x $cantidad", fontSize = 13.sp, color = Color.Gray)
        }
        Text(
            "S/ ${String.format("%.2f", precio * cantidad)}",
            fontWeight = FontWeight.ExtraBold,
            fontSize = 16.sp,
            color = Color(0xFFAB005A)
        )
    }
}

@Composable
fun OrderSummaryCard(total: Double) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(Color(0xFF27171C))
            .padding(24.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Subtotal", color = Color.White.copy(alpha = 0.6f), fontSize = 14.sp)
            Text("S/ ${String.format("%.2f", total)}", color = Color.White, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Envío", color = Color.White.copy(alpha = 0.6f), fontSize = 14.sp)
            Text("Gratis", color = Color(0xFF4CAF50), fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(16.dp))
        Divider(color = Color.White.copy(alpha = 0.1f))
        Spacer(modifier = Modifier.height(16.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("Total", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Text("S/ ${String.format("%.2f", total)}", color = Color.White, fontWeight = FontWeight.Black, fontSize = 24.sp)
        }
    }
}
