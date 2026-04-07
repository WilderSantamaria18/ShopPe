package com.idat.presentation.tarjetas

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.idat.domain.model.Tarjeta
import com.idat.presentation.direcciones.DireccionesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MisTarjetasScreen(
    navController: NavHostController,
    viewModel: DireccionesViewModel = hiltViewModel()
) {
    val tarjetas by viewModel.tarjetas.collectAsState()
    val pinkPrimary = MaterialTheme.colorScheme.primary

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Métodos de Pago", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = pinkPrimary)
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* Implementar diálogo para añadir tarjeta */ },
                containerColor = pinkPrimary,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Añadir Tarjeta")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
        ) {
            Text(
                text = "Tus Tarjetas Guardadas",
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                modifier = Modifier.padding(vertical = 16.dp)
            )
            
            Text(
                text = "Estas son las tarjetas que has usado en tus compras anteriores. Puedes gestionarlas aquí.",
                color = Color.Gray,
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            if (tarjetas.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.CreditCardOff, 
                            contentDescription = null, 
                            modifier = Modifier.size(64.dp), 
                            tint = Color.LightGray
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("No tienes tarjetas guardadas", color = Color.Gray)
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 100.dp)
                ) {
                    items(tarjetas) { tarjeta ->
                        TarjetaItem(
                            tarjeta = tarjeta,
                            onDelete = { viewModel.deleteTarjeta(tarjeta.id) },
                            pinkPrimary = pinkPrimary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TarjetaItem(
    tarjeta: Tarjeta,
    onDelete: () -> Unit,
    pinkPrimary: Color
) {
    val isDark = MaterialTheme.colorScheme.surface == Color(0xFF140C0E)
    val containerColor = if (isDark) Color(0xFF1F1215) else Color.White
    val outlineColor = if (isDark) Color(0xFF442B2F) else Color(0xFFE5D1D5)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(containerColor)
            .border(1.dp, outlineColor, RoundedCornerShape(24.dp))
            .padding(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(pinkPrimary.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (tarjeta.tipo.lowercase().contains("visa")) Icons.Default.CreditCard else Icons.Default.Payment,
                        contentDescription = null,
                        tint = pinkPrimary
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = tarjeta.numeroEnmascarado, 
                        fontWeight = FontWeight.Bold, 
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = tarjeta.titular.uppercase(), 
                        fontSize = 12.sp, 
                        color = Color.Gray
                    )
                }
            }
            
            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Default.DeleteSweep, 
                    contentDescription = "Eliminar", 
                    tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f)
                )
            }
        }
    }
}
