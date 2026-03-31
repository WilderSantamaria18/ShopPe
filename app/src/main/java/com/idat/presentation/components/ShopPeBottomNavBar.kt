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
    onNavigateToFavoritos: () -> Unit = {},
    onNavigateToPersonalizacion: () -> Unit = {},
    onNavigateToConfiguracion: () -> Unit = {},
    onNavigateToGestion: () -> Unit = {},
    onNavigateToAyuda: () -> Unit = {},
    onNavigateToCarrito: () -> Unit = {},
    onCerrarSesion: () -> Unit = {}
) {
    var showProfileMenu by remember { mutableStateOf(false) }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(32.dp))
            .background(MaterialTheme.colorScheme.background.copy(alpha = 0.9f))
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        NavBarItemSquare(
            icon = Icons.Default.Explore, label = "EXPLORAR",
            isSelected = currentSelection == "Explore", onClick = { onTabSelected("Explore") }
        )
        NavBarItemSquare(
            icon = Icons.Default.ShoppingCart, label = "CARRITO",
            isSelected = currentSelection == "Bag", onClick = { onTabSelected("Bag"); onNavigateToCarrito() }
        )
        // Gestión is now titled "PRODUCTOS"
        NavBarItemSquare(
            icon = Icons.Default.Inventory, label = "PRODUCTOS",
            isSelected = currentSelection == "Gestion", onClick = { onTabSelected("Gestion"); onNavigateToGestion() }
        )
        
        // Profile Item with Dropdown Menu
        Box {
            NavBarItemSquare(
                icon = Icons.Default.Person, label = "PERFIL",
                isSelected = currentSelection == "Perfil" || showProfileMenu, onClick = { showProfileMenu = true }
            )
            
            DropdownMenu(
                expanded = showProfileMenu,
                onDismissRequest = { showProfileMenu = false },
                modifier = Modifier.background(MaterialTheme.colorScheme.surfaceVariant) // Approximate SurfaceContainerHighest
            ) {
                DropdownMenuItem(
                    text = { Text("Favoritos", color = MaterialTheme.colorScheme.onSurface) },
                    onClick = { showProfileMenu = false; onNavigateToFavoritos() },
                    leadingIcon = { Icon(Icons.Default.FavoriteBorder, contentDescription = null, tint = MaterialTheme.colorScheme.secondary) }
                )
                DropdownMenuItem(
                    text = { Text("Personalización", color = MaterialTheme.colorScheme.onSurface) },
                    onClick = { showProfileMenu = false; onNavigateToPersonalizacion() },
                    leadingIcon = { Icon(Icons.Default.AutoFixHigh, contentDescription = null, tint = MaterialTheme.colorScheme.secondary) }
                )
                DropdownMenuItem(
                    text = { Text("Ajustes", color = MaterialTheme.colorScheme.onSurface) },
                    onClick = { showProfileMenu = false; onNavigateToConfiguracion() },
                    leadingIcon = { Icon(Icons.Default.Settings, contentDescription = null, tint = MaterialTheme.colorScheme.secondary) }
                )
                DropdownMenuItem(
                    text = { Text("Ayuda", color = MaterialTheme.colorScheme.onSurface) },
                    onClick = { showProfileMenu = false; onNavigateToAyuda() },
                    leadingIcon = { Icon(Icons.Default.HelpOutline, contentDescription = null, tint = MaterialTheme.colorScheme.secondary) }
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

@Composable
fun NavBarItemSquare(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val containerModifier = if (isSelected) {
        Modifier
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.primaryContainer)
                )
            )
    } else {
        Modifier.background(Color.Transparent)
    }

    val contentColor = if (isSelected) {
        Color.White
    } else {
        MaterialTheme.colorScheme.secondary
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .size(72.dp) // Square dimensions
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .then(containerModifier)
            .padding(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = contentColor,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = label,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = contentColor,
            letterSpacing = 0.5.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
    }
}
