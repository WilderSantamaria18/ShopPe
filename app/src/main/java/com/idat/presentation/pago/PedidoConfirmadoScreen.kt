package com.idat.presentation.pago

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PedidoConfirmadoScreen(navController: NavHostController) {
    val pinkPrimary = Color(0xFFAB005A)
    val pinkContainer = Color(0xFFD80073)
    val surfaceContainerHigh = Color(0xFFFEE1E7)
    val surfaceContainerLow = Color(0xFFFFF0F2)
    val onSurfaceVariant = Color(0xFF5A3F47)
    val outlineVariant = Color(0xFFE2BDC6)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text("ShopPe", fontWeight = FontWeight.Black, fontSize = 24.sp, letterSpacing = (-1).sp)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = Color.Gray)
                    }
                },
                actions = {
                    IconButton(onClick = { /* Download PDF */ }) {
                        Icon(Icons.Default.FileDownload, contentDescription = "Descargar PDF", tint = Color.Gray)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White.copy(alpha = 0.8f))
            )
        },
        bottomBar = {
            ShopPeBottomNavBar(navController)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            // Confirmation Header
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(surfaceContainerHigh),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = null,
                    modifier = Modifier.size(40.dp),
                    tint = pinkPrimary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                "Pedido Confirmado",
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurface,
                letterSpacing = (-0.5).sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                "Tu pedido ha sido procesado con éxito. ¡Gracias por confiar en el curador digital!",
                fontSize = 14.sp,
                color = onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Order Metadata Bento
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                OrderInfoBox(modifier = Modifier.weight(1f), label = "ORDEN NO.", value = "#SP-897241", valueColor = pinkPrimary, surfaceColor = surfaceContainerLow)
                OrderInfoBox(modifier = Modifier.weight(1f), label = "FECHA", value = "25 Oct 2023", valueColor = MaterialTheme.colorScheme.onSurface, surfaceColor = surfaceContainerLow)
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Articles Section
            Text(
                "Artículos Comprados",
                modifier = Modifier.fillMaxWidth(),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Item 1
            PurchasedItemRow(
                title = "Artisan Alpaca Throw",
                price = "S/ 185.00",
                imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuB9ipTkRIsaX1jPcy-pPc4YVV9DTJKWMMUjrZAiqx5qBcpEyrzfHfMkZqCsshhNaLExU45GqwD8z_XzDjQeG_WR2wREPxw-Rio2E8WYpfcGgPAkxvfOvKxpkbuXfbdzVPs_7JJsr1LzQfYRx5jI3k34mX6vK1VGvfDVKoX3n52a3e0EJimv5Xi6Xq3zMqyLhbSI3PPXAWvkqlJnrK2mttMh7MaHHsVtcsOJYyNLw8yN6cxcOwU0Nto0bEeWYgTZst4pAOlCiRSodD0"
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Item 2
            PurchasedItemRow(
                title = "Valley Clay Vessel",
                price = "S/ 92.00",
                imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuAymZYA3PhH3PRVVKvVp3FsGgFBYPdhZ9J_eONuMrvkwDxzrVenNizIrqOiE8Qp_gDwhdxRtFvUYTlSw7egyLJ83xVU36ooT5xeJz9VU03HUqdvUvPG7KEzFL1qwB4fDLAFNhj5cxLwHWYiZtVrLEGClW3Z8255MlSiO8lZJTys_GYRjorbEC3jbgwe_OP1VARUyouflSQoVqxQj4v_PgaMJs4pcY_YBtU00seP901ZBxwzDVTbOdlFR0MiIWdK3UjQLCES_ax_aSw"
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Price Breakdown Box
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(32.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    .padding(24.dp)
            ) {
                PriceRow("Subtotal", "S/ 234.75", onSurfaceVariant)
                Spacer(modifier = Modifier.height(8.dp))
                PriceRow("IGV (18%)", "S/ 42.25", onSurfaceVariant)
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = outlineVariant.copy(alpha = 0.2f))
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Bottom) {
                    Text("Total Pago", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                    Text("S/ 277.00", fontWeight = FontWeight.Black, fontSize = 24.sp, color = pinkPrimary)
                }

                Spacer(modifier = Modifier.height(24.dp))
                HorizontalDivider(color = outlineVariant.copy(alpha = 0.2f))
                Spacer(modifier = Modifier.height(24.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column {
                        Text("METODO DE PAGO", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray, letterSpacing = 1.sp)
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 4.dp)) {
                            Icon(Icons.Default.CreditCard, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color(0xFF455F88))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Tarjeta de Crédito", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("ESTADO", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray, letterSpacing = 1.sp)
                        Text("Aprobado", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF725000), modifier = Modifier.padding(top = 4.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Verification Section
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .border(2.dp, outlineVariant.copy(alpha = 0.3f), RoundedCornerShape(24.dp))
                    .padding(24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier.size(80.dp).background(Color(0xFFF5F5F5), RoundedCornerShape(12.dp)).padding(8.dp)
                ) {
                    // QR Placeholder
                    Image(
                        painter = painterResource(id = com.idat.R.drawable.yape_qr),
                        contentDescription = "QR Verification",
                        modifier = Modifier.fillMaxSize()
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text("VERIFICAR COMPROBANTE", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text(
                        "Escanea este código para validar la autenticidad de tu boleta electrónica.",
                        fontSize = 11.sp,
                        color = onSurfaceVariant,
                        lineHeight = 14.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Action Buttons
            Button(
                onClick = { /* Download PDF */ },
                modifier = Modifier.fillMaxWidth().height(60.dp),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                contentPadding = PaddingValues()
            ) {
                Box(
                    modifier = Modifier.fillMaxSize().background(brush = Brush.linearGradient(colors = listOf(pinkPrimary, pinkContainer))),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.FileDownload, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Descargar PDF", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { navController.navigate("catalogo") { popUpTo("catalogo") { inclusive = true } } },
                modifier = Modifier.fillMaxWidth().height(60.dp),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF8DBE2))
            ) {
                Text("Volver a la Tienda", color = pinkPrimary, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

@Composable
fun OrderInfoBox(modifier: Modifier, label: String, value: String, valueColor: Color, surfaceColor: Color) {
    Column(
        modifier = modifier
            .clip(CircleShape)
            .background(surfaceColor)
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(label, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray, letterSpacing = 1.sp)
        Text(value, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = valueColor)
    }
}

@Composable
fun PurchasedItemRow(title: String, price: String, imageUrl: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(CircleShape)
            .background(Color.White)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = imageUrl,
            contentDescription = title,
            modifier = Modifier.size(60.dp).clip(CircleShape),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontWeight = FontWeight.Bold, fontSize = 14.sp, maxLines = 1)
            Text("Unidad x 1", fontSize = 12.sp, color = Color.Gray)
        }
        Text(price, fontWeight = FontWeight.Bold, fontSize = 14.sp)
    }
}

@Composable
fun PriceRow(label: String, value: String, color: Color) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, fontSize = 14.sp, color = color)
        Text(value, fontSize = 14.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun ShopPeBottomNavBar(navController: NavHostController) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 24.dp, start = 12.dp, end = 12.dp)
            .clip(RoundedCornerShape(32.dp))
            .background(Color.White.copy(alpha = 0.9f))
            .padding(8.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
            NavBarItem(icon = Icons.Default.Home, label = "Inicio", active = false, onClick = { navController.navigate("catalogo") })
            NavBarItem(icon = Icons.Default.Storefront, label = "Tienda", active = false, onClick = { navController.navigate("catalogo") })
            NavBarItem(icon = Icons.Default.ReceiptLong, label = "Mis Compras", active = true, onClick = { /* Already here */ })
            NavBarItem(icon = Icons.Default.Person, label = "Perfil", active = false, onClick = { /* Navigate to profile */ })
        }
    }
}

@Composable
fun NavBarItem(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, active: Boolean, onClick: () -> Unit = {}) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(if (active) Color(0xFFFFE8ED) else Color.Transparent)
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Icon(icon, contentDescription = null, tint = if (active) Color(0xFFAB005A) else Color.Gray, modifier = Modifier.size(24.dp))
        Text(label, fontSize = 10.sp, fontWeight = if (active) FontWeight.Bold else FontWeight.SemiBold, color = if (active) Color(0xFFAB005A) else Color.Gray)
    }
}
