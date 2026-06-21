package ec.edu.monster.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import ec.edu.monster.model.DetalleFactura
import ec.edu.monster.model.Factura
import ec.edu.monster.service.SoapService
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FacturasScreen(navController: NavController) {
    val soapService = remember { SoapService() }
    val scope = rememberCoroutineScope()
    
    var facturas by remember { mutableStateOf<List<Factura>>(emptyList()) }
    var clientes by remember { mutableStateOf<List<ec.edu.monster.model.Cliente>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var filterId by remember { mutableStateOf("") }
    
    var selectedFactura by remember { mutableStateOf<Factura?>(null) }
    var detallesFactura by remember { mutableStateOf<List<DetalleFactura>>(emptyList()) }
    var isDetallesLoading by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }

    fun loadFacturas() {
        isLoading = true
        scope.launch {
            if (clientes.isEmpty()) {
                clientes = soapService.listarClientes()
            }
            if (filterId.isBlank()) {
                facturas = soapService.listarTodasLasFacturas()
            } else {
                facturas = soapService.listarFacturas(filterId)
            }
            isLoading = false
        }
    }

    LaunchedEffect(Unit) { loadFacturas() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Visor de Facturas SRÍ") },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded },
                    modifier = Modifier.weight(1f)
                ) {
                    val displayValue = if (filterId.isBlank()) "Todos los Clientes" else {
                        val c = clientes.find { it.idCliente == filterId }
                        if (c != null) "${c.idCliente} - ${c.nombres} ${c.apellidos}" else filterId
                    }
                    OutlinedTextField(
                        value = displayValue,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Filtrar por Cliente") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Todos los Clientes") },
                            onClick = { filterId = ""; expanded = false }
                        )
                        clientes.forEach { c ->
                            DropdownMenuItem(
                                text = { Text("${c.idCliente} - ${c.nombres} ${c.apellidos}") },
                                onClick = { filterId = c.idCliente; expanded = false }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = { loadFacturas() }, modifier = Modifier.height(56.dp).padding(top = 8.dp)) {
                    Icon(Icons.Filled.Search, contentDescription = "Buscar")
                }
            }

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp)) {
                    items(facturas) { f ->
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            onClick = {
                                selectedFactura = f
                                isDetallesLoading = true
                                scope.launch {
                                    detallesFactura = soapService.obtenerDetallesFactura(f.idFactura)
                                    isDetallesLoading = false
                                }
                            }
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(text = "Factura N° ${f.idFactura}", style = MaterialTheme.typography.titleMedium)
                                    Text(text = "Cliente: ${f.idCliente}", style = MaterialTheme.typography.bodyMedium)
                                    val fmt = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                                    Text(text = "Fecha: ${if(f.fechaEmision != null) fmt.format(f.fechaEmision) else "N/A"}", style = MaterialTheme.typography.bodySmall)
                                }
                                Text(
                                    text = "$${String.format("%.2f", f.total)}",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (selectedFactura != null) {
        Dialog(
            onDismissRequest = { selectedFactura = null },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Surface(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                shape = MaterialTheme.shapes.large,
                color = MaterialTheme.colorScheme.surface
            ) {
                Column(modifier = Modifier.padding(16.dp).fillMaxSize()) {
                    Text("FACTURA ELECTRÓNICA SRÍ", fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.CenterHorizontally))
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                    
                    val f = selectedFactura!!
                    Text("Número: ${f.idFactura}", fontWeight = FontWeight.Bold)
                    Text("Cliente CI: ${f.idCliente}")
                    val fmt = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                    Text("Fecha: ${if(f.fechaEmision != null) fmt.format(f.fechaEmision) else "N/A"}")
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    if (isDetallesLoading) {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                    } else {
                        LazyColumn(modifier = Modifier.weight(1f)) {
                            item {
                                Row(modifier = Modifier.fillMaxWidth().background(Color.LightGray).padding(4.dp)) {
                                    Text("Cant", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold)
                                    Text("Loc", modifier = Modifier.weight(2f), fontWeight = FontWeight.Bold)
                                    Text("V.Unit", modifier = Modifier.weight(1.5f), fontWeight = FontWeight.Bold)
                                    Text("V.Total", modifier = Modifier.weight(1.5f), fontWeight = FontWeight.Bold)
                                }
                            }
                            items(detallesFactura) { d ->
                                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                                    Text("${d.cantidad}", modifier = Modifier.weight(1f))
                                    Text(d.codigoLocalidad, modifier = Modifier.weight(2f))
                                    Text(String.format("%.2f", d.precioUnitario), modifier = Modifier.weight(1.5f))
                                    Text(String.format("%.2f", d.totalDetalle), modifier = Modifier.weight(1.5f))
                                }
                            }
                        }
                        
                        Divider(modifier = Modifier.padding(vertical = 8.dp))
                        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.End) {
                            Text("Subtotal: $${String.format("%.2f", f.subtotal)}")
                            Text("IVA 12%: $${String.format("%.2f", f.iva)}")
                            Text("TOTAL: $${String.format("%.2f", f.total)}", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { selectedFactura = null },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Cerrar")
                    }
                }
            }
        }
    }
}
