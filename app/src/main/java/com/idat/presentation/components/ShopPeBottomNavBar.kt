package com.idat.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ShopPeBottomNavBar(
    modifier: Modifier = Modifier,
    currentSelection: String = "Explore",
    onTabSelected: (String) -> Unit = {},
    // Navbar Actions
    onNavigateToCatalogo: () -> Unit = {},
    onNavigateToFavoritos: () -> Unit = {},
    onNavigateToCarrito: () -> Unit = {},
    onNavigateToGestion: () -> Unit = {},
    onNavigateToPedidos: () -> Unit = {},
    onNavigateToAyuda: () -> Unit = {},
    onNavigateToConfiguracion: () -> Unit = {},
    onNavigateToPersonalizacion: () -> Unit = {},
    onCerrarSesion: () -> Unit = {}
) {
    val pinkPrimary = Color(0xFFAB005A)
    val inactiveColor = Color(0xFF8E6F77)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 24.dp, start = 12.dp, end = 12.dp)
            .clip(RoundedCornerShape(32.dp))
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.95f))
            .shadow(
                elevation = 16.dp,
                shape = RoundedCornerShape(32.dp),
                ambientColor = pinkPrimary.copy(alpha = 0.1f),
                spotColor = pinkPrimary.copy(alpha = 0.1f)
            )
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavItem(
                icon = Icons.Default.Explore,
                label = "Descubrir",
                isActive = currentSelection == "Explore",
                onClick = { onTabSelected("Explore"); onNavigateToCatalogo() }
            )
            NavItem(
                icon = Icons.Default.Search,
                label = "Buscar",
                isActive = currentSelection == "Search",
                onClick = { onTabSelected("Search"); onNavigateToCatalogo() } // Placeholder: opens catalog
            )
            NavItem(
                icon = if (currentSelection == "Favoritos") Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                label = "Mis Favoritos",
                isActive = currentSelection == "Favoritos",
                onClick = { onTabSelected("Favoritos"); onNavigateToFavoritos() }
            )
            NavItem(
                icon = Icons.Default.ShoppingBag,
                label = "Bolsa",
                isActive = currentSelection == "Bag",
                onClick = { onTabSelected("Bag"); onNavigateToCarrito() }
            )
            
            // Profile context with a simple long press for logout or just a button to Gestion/Profile
            var showProfileMenu by remember { mutableStateOf(false) }
            Box {
                NavItem(
                    icon = Icons.Default.Person,
                    label = "Perfil",
                    isActive = currentSelection == "Perfil" || showProfileMenu,
                    onClick = { showProfileMenu = true }
                )
                
                DropdownMenu(
                    expanded = showProfileMenu,
                    onDismissRequest = { showProfileMenu = false },
                    modifier = Modifier.background(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    DropdownMenuItem(
                        text = { Text("Mis Pedidos", color = MaterialTheme.colorScheme.onSurface) },
                        onClick = { showProfileMenu = false; onNavigateToPedidos() },
                        leadingIcon = { Icon(Icons.Default.ReceiptLong, contentDescription = null, tint = pinkPrimary) }
                    )
                    DropdownMenuItem(
                        text = { Text("Personalización", color = MaterialTheme.colorScheme.onSurface) },
                        onClick = { showProfileMenu = false; onNavigateToPersonalizacion() },
                        leadingIcon = { Icon(Icons.Default.AutoFixHigh, contentDescription = null, tint = pinkPrimary) }
                    )
                    DropdownMenuItem(
                        text = { Text("Configuración", color = MaterialTheme.colorScheme.onSurface) },
                        onClick = { showProfileMenu = false; onNavigateToConfiguracion() },
                        leadingIcon = { Icon(Icons.Default.Settings, contentDescription = null, tint = pinkPrimary) }
                    )
                    DropdownMenuItem(
                        text = { Text("Gestión de Inventario", color = MaterialTheme.colorScheme.onSurface) },
                        onClick = { showProfileMenu = false; onNavigateToGestion() },
                        leadingIcon = { Icon(Icons.Default.Inventory, contentDescription = null, tint = pinkPrimary) }
                    )
                    DropdownMenuItem(
                        text = { Text("Ayuda", color = MaterialTheme.colorScheme.onSurface) },
                        onClick = { showProfileMenu = false; onNavigateToAyuda() },
                        leadingIcon = { Icon(Icons.Default.HelpOutline, contentDescription = null, tint = pinkPrimary) }
                    )
                    Divider(modifier = Modifier.padding(vertical = 4.dp))
                    DropdownMenuItem(
                        text = { Text("Cerrar Sesión", color = MaterialTheme.colorScheme.primary) },
                        onClick = { showProfileMenu = false; onCerrarSesion() },
                        leadingIcon = { Icon(Icons.Default.Logout, contentDescription = null, tint = MaterialTheme.colorScheme.primary) }
                    )
                }
            }
        }
    }
}

@Composable
fun NavItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    isActive: Boolean,
    onClick: () -> Unit
) {
    val pinkPrimary = Color(0xFFAB005A)
    val inactiveColor = Color(0xFF8E6F77)
    
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (isActive) Color(0xFFFFE8ED) else Color.Transparent)
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isActive) pinkPrimary else inactiveColor,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = if (isActive) pinkPrimary else inactiveColor,
            modifier = Modifier.padding(top = 4.dp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
