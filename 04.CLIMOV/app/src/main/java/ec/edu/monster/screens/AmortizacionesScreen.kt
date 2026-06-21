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
import androidx.navigation.NavController
import ec.edu.monster.model.Amortizacion
import ec.edu.monster.service.SoapService
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AmortizacionesScreen(navController: NavController) {
    val soapService = remember { SoapService() }
    val scope = rememberCoroutineScope()
    
    var amortizaciones by remember { mutableStateOf<List<Amortizacion>>(emptyList()) }
    var clientes by remember { mutableStateOf<List<ec.edu.monster.model.Cliente>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var filterId by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    fun loadData() {
        isLoading = true
        scope.launch {
            if (clientes.isEmpty()) {
                clientes = soapService.listarClientes()
            }
            if (filterId.isBlank()) {
                amortizaciones = soapService.listarTodasLasAmortizaciones()
            } else {
                amortizaciones = soapService.listarAmortizaciones(filterId)
            }
            isLoading = false
        }
    }

    LaunchedEffect(Unit) { loadData() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tabla de Amortizaciones") },
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
                Button(onClick = { loadData() }, modifier = Modifier.height(56.dp).padding(top = 8.dp)) {
                    Icon(Icons.Filled.Search, contentDescription = "Buscar")
                }
            }

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (amortizaciones.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No hay amortizaciones registradas.")
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(8.dp)) {
                    // Header
                    item {
                        Row(modifier = Modifier.fillMaxWidth().background(Color.LightGray).padding(8.dp)) {
                            Text("C", modifier = Modifier.weight(0.5f), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Text("F.Pago", modifier = Modifier.weight(1.5f), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Text("Cuota", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Text("Intrs", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Text("Capital", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Text("Saldo", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                    
                    val fmt = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    
                    items(amortizaciones) { a ->
                        Card(modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)) {
                            Row(modifier = Modifier.padding(8.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                Text("${a.numCuota}", modifier = Modifier.weight(0.5f), fontSize = 12.sp)
                                Text(if(a.fechaPago != null) fmt.format(a.fechaPago) else "", modifier = Modifier.weight(1.5f), fontSize = 12.sp)
                                Text(String.format("%.2f", a.valorCuota), modifier = Modifier.weight(1f), fontSize = 12.sp)
                                Text(String.format("%.2f", a.interesCuota), modifier = Modifier.weight(1f), fontSize = 12.sp)
                                Text(String.format("%.2f", a.capitalCuota), modifier = Modifier.weight(1f), fontSize = 12.sp)
                                Text(String.format("%.2f", a.saldo), modifier = Modifier.weight(1f), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}
