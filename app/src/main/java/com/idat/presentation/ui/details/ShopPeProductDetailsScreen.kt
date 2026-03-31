package com.idat.presentation.ui.details

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.idat.domain.model.Producto
import com.idat.presentation.components.ProductItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShopPeProductDetailsScreen(
    producto: Producto,
    onBackClick: () -> Unit = {},
    onAddToCart: () -> Unit = {}
) {
    var isFavorite by remember { mutableStateOf(false) }
    var selectedFinish by remember { mutableStateOf(0) }
    var selectedSize by remember { mutableStateOf(0) }
    
    val scrollState = rememberScrollState()
    val isDark = MaterialTheme.colorScheme.surface == Color(0xFF140C0E)
    
    val surfaceContainerLow = if (isDark) Color(0xFF1F1215) else Color(0xFFFFF0F2)
    val surfaceContainerHigh = if (isDark) Color(0xFF332025) else Color(0xFFFEE1E7)
    val outlineColor = if (isDark) Color(0xFFA88991) else Color(0xFF8E6F77)
    
    val finishColors = listOf(
        Color(0xFFF5F5DC), // Beige/Natural
        Color(0xFFD2B48C), // Tan
        Color(0xFF8B4513), // SaddleBrown
        Color(0xFF2F4F4F)  // DarkSlateGray
    )

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "ShopPe",
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = (-1).sp
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier
                            .padding(8.dp)
                            .background(surfaceContainerHigh, shape = CircleShape)
                    ) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = MaterialTheme.colorScheme.primary)
                    }
                },
                actions = {
                    IconButton(
                        onClick = { isFavorite = !isFavorite },
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .background(surfaceContainerHigh, shape = CircleShape)
                    ) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder, 
                            contentDescription = "Favorite", 
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(
                        onClick = { /* Share */ },
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .background(surfaceContainerHigh, shape = CircleShape)
                    ) {
                        Icon(imageVector = Icons.Default.Share, contentDescription = "Share", tint = MaterialTheme.colorScheme.primary)
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
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Main Product Image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(4f / 5f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(surfaceContainerLow)
            ) {
                AsyncImage(
                    model = producto.imagen,
                    contentDescription = producto.nombre,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    modifier = Modifier
                        .padding(16.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.tertiary)
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        "NEW ARRIVAL",
                        color = MaterialTheme.colorScheme.onTertiary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Image Gallery (thumbnails)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Secondary images (placeholders using main image for now)
                for (i in 0..2) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(surfaceContainerHigh)
                    ) {
                        AsyncImage(
                            model = producto.imagen,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                        if (i == 2) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("+2", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Breadcrumbs
            Text(
                text = "CURATED COLLECTIONS / ${producto.categoria.uppercase()}",
                color = MaterialTheme.colorScheme.secondary,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Title
            Text(
                text = producto.nombre,
                fontSize = 32.sp,
                lineHeight = 40.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurface,
                letterSpacing = (-1).sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Pricing
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = "$${producto.precio}",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "$${producto.precio * 1.2}", // Just generating an old price
                    fontSize = 18.sp,
                    color = outlineColor,
                    textDecoration = TextDecoration.LineThrough,
                    modifier = Modifier.padding(bottom = 2.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Bento Features
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                FeatureBentoPanel(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.Eco,
                    title = "MATERIAL",
                    subtitle = "100% Baby Alpaca",
                    surfaceContainerLow = surfaceContainerLow
                )
                FeatureBentoPanel(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.AutoAwesome,
                    title = "QUALITY",
                    subtitle = "Hand-loomed in Peru",
                    surfaceContainerLow = surfaceContainerLow
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Finish Selection
            Text(
                text = "SELECT FINISH",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                finishColors.forEachIndexed { index, color ->
                    val isSelected = selectedFinish == index
                    val strokeColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(color)
                            .border(2.dp, strokeColor, CircleShape)
                            .clickable { selectedFinish = index }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Dimensions Selection
            Text(
                text = "DIMENSIONS",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                DimensionButton(
                    text = "Standard 50\" x 70\"",
                    isSelected = selectedSize == 0,
                    onClick = { selectedSize = 0 },
                    surfaceContainerHigh = surfaceContainerHigh
                )
                DimensionButton(
                    text = "Large 60\" x 90\"",
                    isSelected = selectedSize == 1,
                    onClick = { selectedSize = 1 },
                    surfaceContainerHigh = surfaceContainerHigh
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            // CTA
            Button(
                onClick = onAddToCart,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                contentPadding = PaddingValues()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.primaryContainer)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Add to Bag — $${producto.precio}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.LocalShipping,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Complimentary global carbon-neutral shipping",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.secondary,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(64.dp))

            // Details section
            Text(
                text = "Product Details",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                letterSpacing = (-0.5).sp
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = producto.descripcion.ifEmpty { "Our Artisan Alpaca Heritage Throw is more than a blanket; it is a testament to centuries of Peruvian weaving traditions. Sourced ethically from high-altitude highlands, each fiber is meticulously selected for its hollow core—providing exceptional warmth while remaining surprisingly lightweight." },
                fontSize = 16.sp,
                lineHeight = 24.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Care Instructions
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(surfaceContainerLow)
                    .padding(32.dp)
            ) {
                Column {
                    Text(
                        text = "Care Instructions",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    CareInstructionRow(Icons.Default.DryCleaning, "Professional dry clean recommended")
                    Spacer(modifier = Modifier.height(16.dp))
                    CareInstructionRow(Icons.Default.Air, "Air outdoors periodically to refresh fibers")
                    Spacer(modifier = Modifier.height(16.dp))
                    CareInstructionRow(Icons.Default.WaterDrop, "Spot clean with cold water and mild detergent")
                }
            }

            Spacer(modifier = Modifier.height(64.dp))

            // Complete the look (Simulated related products)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = "Complete the Look",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    letterSpacing = (-0.5).sp
                )
                Text(
                    text = "EXPLORE ALL",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    letterSpacing = 2.sp,
                    textDecoration = TextDecoration.Underline
                )
            }
            Spacer(modifier = Modifier.height(24.dp))

            // We mimic a related items row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    ProductItem(producto = producto.copy(precio = 48.0, nombre = "Palo Santo Soy Candle"), showTag = false)
                }
                Box(modifier = Modifier.weight(1f)) {
                    ProductItem(producto = producto.copy(precio = 85.0, nombre = "Terra Sculptural Vase"), showTag = false)
                }
            }

            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
fun FeatureBentoPanel(
    modifier: Modifier = Modifier,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    surfaceContainerLow: Color = Color.LightGray
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(surfaceContainerLow)
            .padding(20.dp)
    ) {
        Column {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = title,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.secondary,
                letterSpacing = 2.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = subtitle,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun DimensionButton(text: String, isSelected: Boolean, onClick: () -> Unit, surfaceContainerHigh: Color = Color.LightGray) {
    val backgroundColor = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.05f) else surfaceContainerHigh
    val textColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
    val borderColor = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f) else Color.Transparent

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .border(1.dp, borderColor, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(horizontal = 24.dp, vertical = 12.dp)
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = 14.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
        )
    }
}

@Composable
fun CareInstructionRow(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = text,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
