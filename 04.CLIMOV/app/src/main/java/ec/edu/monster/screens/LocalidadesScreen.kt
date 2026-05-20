package ec.edu.monster.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import ec.edu.monster.model.CompraResultado
import ec.edu.monster.model.LocalidadPartido
import ec.edu.monster.service.SoapService
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocalidadesScreen(navController: NavController, codigoPartido: String, usuario: String) {
    val coroutineScope = rememberCoroutineScope()
    val soapService = remember { SoapService() }
    
    var localidades by remember { mutableStateOf<List<LocalidadPartido>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    // Dialog state
    var showPurchaseDialog by remember { mutableStateOf(false) }
    var selectedLocalidad by remember { mutableStateOf<LocalidadPartido?>(null) }
    var cantidad by remember { mutableStateOf("1") }
    var isPurchasing by remember { mutableStateOf(false) }
    var compraResultado by remember { mutableStateOf<CompraResultado?>(null) }

    LaunchedEffect(codigoPartido) {
        coroutineScope.launch {
            localidades = soapService.listarLocalidadesDisponibles(codigoPartido)
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Localidades Disponibles") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (localidades.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No hay localidades disponibles.", fontSize = 18.sp)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(localidades) { localidad ->
                    LocalidadCard(localidad = localidad, onClick = {
                        selectedLocalidad = localidad
                        cantidad = "1"
                        showPurchaseDialog = true
                    })
                }
            }
        }

        if (showPurchaseDialog && selectedLocalidad != null) {
            AlertDialog(
                onDismissRequest = { if (!isPurchasing) showPurchaseDialog = false },
                title = { Text(text = "Comprar Boleto - ${selectedLocalidad!!.codigoLocalidad}") },
                text = {
                    Column {
                        Text(text = "Precio unitario: $${String.format("%.2f", selectedLocalidad!!.precio)}")
                        Text(text = "Disponibles: ${selectedLocalidad!!.disponibilidad}")
                        Spacer(modifier = Modifier.height(16.dp))
                        OutlinedTextField(
                            value = cantidad,
                            onValueChange = { cantidad = it },
                            label = { Text("Cantidad") },
                            singleLine = true,
                            enabled = !isPurchasing
                        )
                        if (compraResultado != null) {
                            Spacer(modifier = Modifier.height(16.dp))
                            val isSuccess = compraResultado!!.estado == 1
                            val color = if (isSuccess) Color(0xFF388E3C) else MaterialTheme.colorScheme.error
                            Text(
                                text = compraResultado!!.mensaje,
                                color = color,
                                fontWeight = FontWeight.Bold
                            )
                            if (isSuccess) {
                                Text("Factura ID: ${compraResultado!!.facturaId}")
                                Text("Subtotal: $${String.format("%.2f", compraResultado!!.subtotal)}")
                                Text("IVA: $${String.format("%.2f", compraResultado!!.iva)}")
                                Text("Total: $${String.format("%.2f", compraResultado!!.total)}", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                },
                confirmButton = {
                    if (compraResultado != null) {
                        Button(onClick = {
                            showPurchaseDialog = false
                            compraResultado = null
                            navController.navigateUp() // go back to match selection
                        }) {
                            Text("Cerrar")
                        }
                    } else {
                        Button(
                            onClick = {
                                val cant = cantidad.toIntOrNull() ?: 0
                                if (cant > 0 && cant <= selectedLocalidad!!.disponibilidad) {
                                    isPurchasing = true
                                    coroutineScope.launch {
                                        compraResultado = soapService.comprarBoleto(codigoPartido, selectedLocalidad!!.codigoLocalidad, cant, usuario)
                                        isPurchasing = false
                                    }
                                }
                            },
                            enabled = !isPurchasing
                        ) {
                            if (isPurchasing) {
                                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                            } else {
                                Text("Confirmar Compra")
                            }
                        }
                    }
                },
                dismissButton = {
                    if (compraResultado == null && !isPurchasing) {
                        OutlinedButton(onClick = { showPurchaseDialog = false }) {
                            Text("Cancelar")
                        }
                    }
                }
            )
        }
    }
}

@Composable
fun LocalidadCard(localidad: LocalidadPartido, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = localidad.codigoLocalidad, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Disponibilidad: ${localidad.disponibilidad}")
            }
            Text(
                text = "$${String.format("%.2f", localidad.precio)}",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
