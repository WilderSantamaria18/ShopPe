package com.idat.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.idat.domain.model.Producto

import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme

@Composable
fun ProductItem(
    producto: Producto,
    modifier: Modifier = Modifier,
    isFavorite: Boolean = false,
    onFavoriteClick: () -> Unit = {},
    onAddToCart: () -> Unit = {},
    onClick: () -> Unit = {},
    showTag: Boolean = false,
    tagText: String = "NEW IN"
) {
    val isDark = MaterialTheme.colorScheme.surface == Color(0xFF140C0E)
    
    // Contraste dinámico según el tema
    val imageContainerColor = if (isDark) Color(0xFF1F1215) else Color(0xFFF8F0F2)
    val outlineColor = if (isDark) Color(0xFF442B2F) else Color(0xFFE5D1D5)
    
    val tertiaryFixedColor = if (isDark) Color(0xFF3C2900) else Color(0xFFFFDEA8)
    val onTertiaryFixedColor = if (isDark) Color(0xFFFFDEA8) else Color(0xFF271900)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(3f / 4f)
                .clip(RoundedCornerShape(12.dp))
                .background(imageContainerColor)
                .border(
                    width = 0.5.dp,
                    color = outlineColor,
                    shape = RoundedCornerShape(12.dp)
                )
        ) {
            AsyncImage(
                model = producto.imagen,
                contentDescription = producto.nombre,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Favorite Button
            IconButton(
                onClick = onFavoriteClick,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .size(36.dp)
                    .background(
                        color = (if (isDark) Color.Black else Color.White).copy(alpha = 0.7f), 
                        shape = RoundedCornerShape(percent = 50)
                    )
            ) {
                Icon(
                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Favorite",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }

            // Optional Tag
            if (showTag) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(12.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (tagText.uppercase() == "LIMITED") tertiaryFixedColor else MaterialTheme.colorScheme.primaryContainer)
                        .padding(horizontal = 6.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = tagText.uppercase(),
                        color = if (tagText.uppercase() == "LIMITED") onTertiaryFixedColor else MaterialTheme.colorScheme.onPrimary,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = producto.nombre,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            lineHeight = 18.sp
        )

        Spacer(modifier = Modifier.height(4.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$${producto.precio}",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            IconButton(
                onClick = onAddToCart,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.AddShoppingCart,
                    contentDescription = "Add to Cart",
                    tint = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
}
