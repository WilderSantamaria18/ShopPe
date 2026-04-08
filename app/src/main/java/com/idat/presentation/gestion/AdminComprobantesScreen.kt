package com.idat.presentation.gestion

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.idat.presentation.pago.PedidoConfirmadoViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminComprobantesScreen(
    navController: NavHostController,
    adminViewModel: AdminComprobantesViewModel = hiltViewModel(),
    pdfViewModel: PedidoConfirmadoViewModel = hiltViewModel()
) {
    val filteredPedidos by adminViewModel.filteredPedidos.collectAsState()
    val searchQuery by adminViewModel.searchQuery.collectAsState()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Panel Boletas (Admin)", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            val statusFilter by adminViewModel.statusFilter.collectAsState()
            
            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { adminViewModel.onSearchQueryChange(it) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Buscar por cliente o boleta...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))
            
            // Filter Chips
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val statuses = listOf("Pendiente", "Confirmado", "Entregado")
                statuses.forEach { status ->
                    FilterChip(
                        selected = statusFilter == status,
                        onClick = { adminViewModel.onStatusFilterChange(status) },
                        label = { Text(status, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (filteredPedidos.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No se encontraron comprobantes.")
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredPedidos) { pedido ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        ) {
                            Column(Modifier.padding(16.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column {
                                        Text(
                                            text = pedido.numComprobante.ifEmpty { "Boleta Pendiente" },
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp
                                        )
                                        Text(
                                            text = "Cliente: ${pedido.clienteNombre}",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                        Text(
                                            text = pedido.clienteEmail,
                                            fontSize = 12.sp,
                                            color = Color.Gray
                                        )
                                    }
                                    IconButton(onClick = {
                                        scope.launch {
                                            val result = snackbarHostState.showSnackbar(
                                                message = "Boleta para ${pedido.clienteNombre} lista",
                                                actionLabel = "ABRIR",
                                                duration = SnackbarDuration.Long
                                            )
                                            if (result == SnackbarResult.ActionPerformed) {
                                                pdfViewModel.abrirComprobante(context, pedido.id)
                                            }
                                        }
                                        pdfViewModel.cargarPedido(pedido.id)
                                        pdfViewModel.generarPdf(
                                            context,
                                            onComplete = { 
                                                pdfViewModel.abrirComprobante(context, pedido.id)
                                            },
                                            onError = { error ->
                                                // handle error
                                            }
                                        )
                                    }) {
                                        Icon(Icons.Default.PictureAsPdf, contentDescription = "Ver Boleta", tint = Color(0xFFAB005A))
                                    }
                                }
                                
                                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f))
                                
                                Row(
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date(pedido.fecha)),
                                            fontSize = 12.sp,
                                            color = Color.Gray
                                        )
                                        Text(
                                            text = pedido.estado.uppercase(),
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (pedido.estado == "Pendiente") Color(0xFFAB005A) else Color(0xFF007A33),
                                            modifier = Modifier.padding(top = 2.dp)
                                        )
                                    }
                                    Text(
                                        text = "S/ ${String.format("%.2f", pedido.total)}",
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 16.sp,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }

                                // --- Status Selector (Admin Feature) ---
                                Spacer(modifier = Modifier.height(12.dp))
                                Text("Cambiar Estado:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    val statusOptions = listOf("Pendiente", "En camino", "Entregado")
                                    statusOptions.forEach { option ->
                                        val isSelected = pedido.estado.equals(option, ignoreCase = true)
                                        SuggestionChip(
                                            onClick = { 
                                                if (!isSelected) {
                                                    adminViewModel.actualizarEstadoPedido(pedido.id, option)
                                                }
                                            },
                                            label = { Text(option, fontSize = 10.sp) },
                                            colors = SuggestionChipDefaults.suggestionChipColors(
                                                containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
                                                labelColor = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else Color.Gray
                                            ),
                                            border = if (isSelected) null else BorderStroke(1.dp, Color.LightGray)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
