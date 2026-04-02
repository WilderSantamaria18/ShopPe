package com.idat.presentation.pago

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.idat.domain.model.Pedido
import com.idat.domain.repository.PedidoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import javax.inject.Inject
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Table
import com.itextpdf.layout.element.Cell
import com.itextpdf.layout.property.TextAlignment
import com.itextpdf.layout.property.UnitValue
import java.text.SimpleDateFormat
import java.util.*

@HiltViewModel
class PedidoConfirmadoViewModel @Inject constructor(
    private val repository: PedidoRepository
) : ViewModel() {

    private val _pedido = MutableStateFlow<Pedido?>(null)
    val pedido: StateFlow<Pedido?> = _pedido.asStateFlow()

    private val _isGeneratingPdf = MutableStateFlow(false)
    val isGeneratingPdf: StateFlow<Boolean> = _isGeneratingPdf.asStateFlow()

    fun cargarPedido(pedidoId: String) {
        repository.getPedidoById(pedidoId)
            .onEach { _pedido.value = it }
            .launchIn(viewModelScope)
    }

    fun generarPdf(context: Context, onComplete: (String) -> Unit, onError: (String) -> Unit) {
        val currentPedido = _pedido.value ?: return
        
        viewModelScope.launch {
            _isGeneratingPdf.value = true
            try {
                val fileName = "Boleta_ShopPe_${currentPedido.id}.pdf"
                
                withContext(Dispatchers.IO) {
                    val outputStream: OutputStream?
                    val finalFile: File?
                    
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        val contentValues = ContentValues().apply {
                            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                            put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
                            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                        }
                        val contentResolver = context.contentResolver
                        val uri = contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
                        outputStream = uri?.let { contentResolver.openOutputStream(it) }
                        finalFile = null // MediaStore handles it
                    } else {
                        val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                        val file = File(downloadsDir, fileName)
                        outputStream = FileOutputStream(file)
                        finalFile = file
                    }

                    if (outputStream != null) {
                        val writer = PdfWriter(outputStream)
                        val pdf = PdfDocument(writer)
                        val document = Document(pdf)

                        // --- HEADER ---
                        val headerTable = Table(UnitValue.createPointArray(floatArrayOf(50f, 50f))).useAllAvailableWidth()
                        
                        // Left Column (Emitter)
                        val leftCell = Cell().setBorder(com.itextpdf.layout.borders.Border.NO_BORDER)
                        leftCell.add(Paragraph("ShopPe S.A.").setBold().setFontSize(18f))
                        leftCell.add(Paragraph("Razón Social: ShopPe S.A."))
                        leftCell.add(Paragraph("Domicilio Comercial: Calle ShopPe 123, Lima"))
                        leftCell.add(Paragraph("Condición IVA: Responsable Inscripto"))
                        headerTable.addCell(leftCell)
                        
                        // Right Column (Boleta Info)
                        val rightCell = Cell().setBorder(com.itextpdf.layout.borders.Border.NO_BORDER).setTextAlignment(TextAlignment.RIGHT)
                        rightCell.add(Paragraph("BOLETA DE VENTA").setBold().setFontSize(22f))
                        rightCell.add(Paragraph("Serie: B001  Nro. Comp: ${currentPedido.numComprobante.ifEmpty { currentPedido.id.takeLast(8).uppercase() }}"))
                        val dateStr = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(currentPedido.fecha))
                        rightCell.add(Paragraph("Fecha de Emisión: $dateStr"))
                        rightCell.add(Paragraph("CUIT: 30-12345678-9"))
                        headerTable.addCell(rightCell)
                        
                        document.add(headerTable)
                        document.add(Paragraph("\n"))

                        // --- CLIENT INFO ---
                        document.add(Paragraph("DATOS DEL CLIENTE").setBold().setFontSize(12f))
                        val clientTable = Table(UnitValue.createPercentArray(floatArrayOf(50f, 50f))).useAllAvailableWidth()
                        clientTable.addCell(Cell().add(Paragraph("Email: ${currentPedido.clienteEmail}")))
                        clientTable.addCell(Cell().add(Paragraph("Nombre: ${currentPedido.clienteNombre}")))
                        document.add(clientTable)
                        document.add(Paragraph("\n"))

                        // --- ITEMS TABLE ---
                        val itemsTable = Table(UnitValue.createPercentArray(floatArrayOf(10f, 50f, 10f, 15f, 15f))).useAllAvailableWidth()
                        itemsTable.addHeaderCell(Cell().add(Paragraph("Cant").setBold()))
                        itemsTable.addHeaderCell(Cell().add(Paragraph("Descripción").setBold()))
                        itemsTable.addHeaderCell(Cell().add(Paragraph("U.M.").setBold()))
                        itemsTable.addHeaderCell(Cell().add(Paragraph("Prec. Unit").setBold()))
                        itemsTable.addHeaderCell(Cell().add(Paragraph("Subtotal").setBold()))

                        currentPedido.items.forEach { item ->
                            itemsTable.addCell(Cell().add(Paragraph(item.cantidad.toString())))
                            itemsTable.addCell(Cell().add(Paragraph(item.nombre)))
                            itemsTable.addCell(Cell().add(Paragraph("Unidad")))
                            itemsTable.addCell(Cell().add(Paragraph("S/ ${String.format("%.2f", item.precio)}")))
                            itemsTable.addCell(Cell().add(Paragraph("S/ ${String.format("%.2f", item.precio * item.cantidad)}")))
                        }
                        document.add(itemsTable)

                        // --- TOTALS ---
                        document.add(Paragraph("\n"))
                        val totalPara = Paragraph("TOTAL A PAGAR: S/ ${String.format("%.2f", currentPedido.total)}")
                            .setBold()
                            .setFontSize(16f)
                            .setTextAlignment(TextAlignment.RIGHT)
                        document.add(totalPara)
                        
                        document.add(Paragraph("\nGenerado por ShopPe SDK").setFontSize(8f).setTextAlignment(TextAlignment.CENTER))

                        document.close()
                        withContext(Dispatchers.Main) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                // For modern Android, we don't return absolute path easily, 
                                // but we suggest user to check downloads.
                                onComplete("Archivo guardado en Descargas")
                            } else {
                                onComplete(finalFile?.absolutePath ?: "Archivo guardado")
                            }
                        }
                    } else {
                        throw Exception("No se pudo crear el archivo de salida")
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    onError("Error al generar boleta: ${e.localizedMessage}")
                }
            } finally {
                _isGeneratingPdf.value = false
            }
        }
    }

    fun abrirComprobante(context: Context, pedidoId: String) {
        val fileName = "Boleta_ShopPe_$pedidoId.pdf"
        
        // Determinar qué archivo existe
        val publicFile = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName)
        val privateFile = File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), fileName)
        
        val targetFile = when {
            publicFile.exists() -> publicFile
            privateFile.exists() -> privateFile
            else -> null
        }

        if (targetFile != null) {
            try {
                val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", targetFile)
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    setDataAndType(uri, "application/pdf")
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(Intent.createChooser(intent, "Abrir Boleta con..."))
            } catch (e: SecurityException) {
                android.widget.Toast.makeText(context, "Error de seguridad: ${e.message}", android.widget.Toast.LENGTH_LONG).show()
            } catch (e: android.content.ActivityNotFoundException) {
                android.widget.Toast.makeText(context, "No hay una aplicación para abrir PDFs.", android.widget.Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                android.widget.Toast.makeText(context, "Error al abrir: ${e.message}", android.widget.Toast.LENGTH_SHORT).show()
            }
        } else {
            android.widget.Toast.makeText(context, "El archivo ya no existe en el almacenamiento.", android.widget.Toast.LENGTH_SHORT).show()
        }
    }
}
