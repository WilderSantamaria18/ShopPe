package com.idat.presentation.navigation


import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import com.google.firebase.auth.FirebaseAuth
import com.idat.core.auth.AdminAccess
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.idat.presentation.login.LoginScreen
import com.idat.presentation.registro.RegistroScreen
import com.idat.presentation.catalogo.CatalogoScreen
import com.idat.presentation.carrito.CarritoScreen
import com.idat.presentation.detalle.DetalleScreen
import com.idat.presentation.favoritos.FavoritosScreen
import com.idat.presentation.personalizacion.PersonalizacionScreen
import com.idat.presentation.configuracion.ConfiguracionScreen
import com.idat.presentation.gestion.GestionProductosScreen
import com.idat.presentation.ayuda.AyudaScreen
import com.idat.presentation.pago.PagoScreen
import com.idat.presentation.pago.PedidoConfirmadoScreen
import com.idat.presentation.pedidos.MisPedidosScreen
import com.idat.presentation.pedidos.DetallePedidoScreen
import com.idat.presentation.pedidos.MisComprobantesScreen
import com.idat.presentation.gestion.AdminComprobantesScreen


@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "login") {
        composable("login") { LoginScreen(navController) }
        composable("registro") { RegistroScreen(navController) }
        composable("catalogo") { CatalogoScreen(navController) }
        composable("carrito") { CarritoScreen(navController) }
        composable("favoritos/{from}") { FavoritosScreen(navController) }
        composable("personalizacion/{from}") { PersonalizacionScreen(navController) }
        composable("configuracion/{from}") { ConfiguracionScreen(navController) }
        composable("gestion/{from}") {
            val isAdmin = AdminAccess.isAdminEmail(FirebaseAuth.getInstance().currentUser?.email)
            if (isAdmin) {
                GestionProductosScreen(navController)
            } else {
                AdminOnlyScreen(
                    onBackToCatalog = {
                        navController.navigate("catalogo") {
                            popUpTo("catalogo") { inclusive = true }
                        }
                    }
                )
            }
        }
        composable("ayuda/{from}") { AyudaScreen(navController) }
        composable("admin_comprobantes") {
            val isAdmin = AdminAccess.isAdminEmail(FirebaseAuth.getInstance().currentUser?.email)
            if (isAdmin) {
                AdminComprobantesScreen(navController)
            } else {
                AdminOnlyScreen(onBackToCatalog = { navController.navigate("catalogo") })
            }
        }
        composable("pago") { PagoScreen(navController) }
        composable(
            route = "pedidoConfirmado/{pedidoId}",
            arguments = listOf(navArgument("pedidoId") { type = NavType.StringType })
        ) { backStackEntry ->
            val pedidoId = backStackEntry.arguments?.getString("pedidoId") ?: ""
            PedidoConfirmadoScreen(navController, pedidoId)
        }
        composable("mis_pedidos") { MisPedidosScreen(navController) }
        composable("mis_comprobantes") { MisComprobantesScreen(navController) }
        composable("direcciones") { 
            com.idat.presentation.direcciones.MisDireccionesScreen(navController) 
        }
        composable(
            route = "detalle_pedido/{pedidoId}",
            arguments = listOf(navArgument("pedidoId") { type = NavType.StringType })
        ) { backStackEntry ->
            val pedidoId = backStackEntry.arguments?.getString("pedidoId") ?: ""
            DetallePedidoScreen(navController, pedidoId)
        }
        composable(
            route = "detalle/{productoId}",
            arguments = listOf(navArgument("productoId") { type = NavType.IntType })
        ) { backStackEntry ->
            val productoId = backStackEntry.arguments?.getInt("productoId") ?: 0
            DetalleScreen(navController, productoId)
        }
    }

}

@Composable
private fun AdminOnlyScreen(onBackToCatalog: () -> Unit) {
    Scaffold { paddingValues ->
        androidx.compose.foundation.layout.Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(24.dp)
        ) {
            Text(
                text = "Panel de administración",
                style = MaterialTheme.typography.headlineSmall
            )
            Text(
                text = "Solo el correo administrador puede gestionar productos.",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(top = 8.dp)
            )
            Button(
                onClick = onBackToCatalog,
                modifier = Modifier
                    .padding(top = 24.dp)
                    .fillMaxWidth()
            ) {
                Text("Volver al catálogo")
            }
        }
    }
}
