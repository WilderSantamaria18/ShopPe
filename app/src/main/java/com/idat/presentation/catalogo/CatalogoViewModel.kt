package com.idat.presentation.catalogo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.idat.data.local.preferences.UserPreferencesManager
import com.idat.domain.model.Producto
import com.idat.domain.usecase.ObtenerProductosUseCase
import com.idat.domain.repository.ProductoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CatalogoViewModel @Inject constructor(
    private val useCase: ObtenerProductosUseCase,
    private val repository: ProductoRepository,
    private val firebaseAuth: FirebaseAuth,
    private val userPreferencesManager: UserPreferencesManager
) : ViewModel() {

    private val _todosLosProductos = MutableStateFlow<List<Producto>>(emptyList())
    private val _productos = MutableStateFlow<List<Producto>>(emptyList())
    val productos: StateFlow<List<Producto>> = _productos
    
    private val _categoriaSeleccionada = MutableStateFlow("Todas")
    val categoriaSeleccionada: StateFlow<String> = _categoriaSeleccionada
    
    private val _textoBusqueda = MutableStateFlow("")
    val textoBusqueda: StateFlow<String> = _textoBusqueda
    
    private val _categorias = MutableStateFlow<List<String>>(listOf("Todas"))
    val categorias: StateFlow<List<String>> = _categorias
    
    // Mapeo de categorías en español a inglés y viceversa
    private val mapeoCategoriasEspanol = mapOf(
        "women's clothing" to "Ropa de Mujer",
        "men's clothing" to "Ropa de Hombre",
        "electronics" to "Electrónica",
        "jewelery" to "Joyería"
    )
    
    private val mapeoCategoriasIngles = mapOf(
        "Ropa de Mujer" to "women's clothing",
        "Mujer" to "women's clothing",
        "Ropa de Hombre" to "men's clothing",
        "Hombre" to "men's clothing",
        "Electrónica" to "electronics",
        "Joyería" to "jewelery"
    )

    val isDarkTheme = userPreferencesManager.isDarkTheme.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = false
    )

    val viewMode = userPreferencesManager.viewMode.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = "grid"
    )

    fun cargarProductos() {
        viewModelScope.launch {
            val productos = useCase.ejecutar()
            _todosLosProductos.value = productos
            
            // Generar categorías dinámicamente
            val categoriasUnicas = productos
                .map { normalizarCategoria(it.categoria) }
                .distinct()
                .sorted()
            _categorias.value = listOf("Todas") + categoriasUnicas
            
            aplicarFiltros()
        }
    }
    
    private fun normalizarCategoria(categoria: String): String {
        val categoriaLower = categoria.lowercase().trim()
        
        // Mapear desde inglés a español
        mapeoCategoriasEspanol[categoriaLower]?.let { return it }
        
        // Mapear desde español (corto o largo) a español estándar
        when (categoriaLower) {
            "mujer", "ropa de mujer" -> return "Ropa de Mujer"
            "hombre", "ropa de hombre" -> return "Ropa de Hombre"
            "electrónica", "electronica" -> return "Electrónica"
            "joyería", "joyeria" -> return "Joyería"
        }
        
        // Si no hay mapeo, capitalizar la primera letra
        return categoria.trim().replaceFirstChar { it.uppercase() }
    }
    
    fun seleccionarCategoria(categoria: String) {
        _categoriaSeleccionada.value = categoria
        aplicarFiltros()
    }
    
    fun actualizarBusqueda(texto: String) {
        _textoBusqueda.value = texto
        aplicarFiltros()
    }
    
    private fun aplicarFiltros() {
        var productosFiltrados = _todosLosProductos.value
        
        // Filtrar por categoría
        if (_categoriaSeleccionada.value != "Todas") {
            productosFiltrados = productosFiltrados.filter { producto ->
                val categoriaSeleccionada = _categoriaSeleccionada.value
                val categoriaProducto = producto.categoria
                
                // Comparar directamente
                if (categoriaProducto.equals(categoriaSeleccionada, ignoreCase = true)) {
                    return@filter true
                }
                
                // Comparar categoría normalizada
                if (normalizarCategoria(categoriaProducto).equals(categoriaSeleccionada, ignoreCase = true)) {
                    return@filter true
                }
                
                // Intentar mapear de español a inglés
                val categoriaEnIngles = mapeoCategoriasIngles[categoriaSeleccionada]
                if (categoriaEnIngles != null && categoriaProducto.equals(categoriaEnIngles, ignoreCase = true)) {
                    return@filter true
                }
                
                false
            }
        }
        
        // Filtrar por búsqueda
        if (_textoBusqueda.value.isNotBlank()) {
            productosFiltrados = productosFiltrados.filter {
                it.nombre.contains(_textoBusqueda.value, ignoreCase = true)
            }
        }
        
        _productos.value = productosFiltrados
    }

    fun agregarAlCarrito(producto: Producto) {
        viewModelScope.launch {
            repository.agregarProductoAlCarrito(producto)
        }
    }

    fun toggleFavorito(producto: Producto) {
        viewModelScope.launch {
            if (repository.esFavorito(producto.id)) {
                repository.eliminarDeFavoritos(producto.id)
            } else {
                repository.agregarAFavoritos(producto)
            }
        }
    }

    suspend fun esFavorito(productoId: Int): Boolean {
        return repository.esFavorito(productoId)
    }

    fun cerrarSesion() {
        firebaseAuth.signOut()
    }

    fun obtenerEmailUsuario(): String? {
        return firebaseAuth.currentUser?.email
    }
}
