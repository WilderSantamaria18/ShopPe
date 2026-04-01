package com.idat.presentation.navigation


import androidx.compose.runtime.Composable
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
        composable("gestion/{from}") { GestionProductosScreen(navController) }
        composable("ayuda/{from}") { AyudaScreen(navController) }
        composable("pago") { PagoScreen(navController) }
        composable("pedidoConfirmado") { PedidoConfirmadoScreen(navController) }
        composable("mis_pedidos") { MisPedidosScreen(navController) }
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
