package ec.edu.monster.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import ec.edu.monster.model.Cliente
import ec.edu.monster.service.SoapService
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientesScreen(navController: NavController) {
    val soapService = remember { SoapService() }
    val scope = rememberCoroutineScope()
    var clientes by remember { mutableStateOf<List<Cliente>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var showDialog by remember { mutableStateOf(false) }

    // Form states
    var idCliente by remember { mutableStateOf("") }
    var nombres by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }

    fun loadData() {
        isLoading = true
        scope.launch {
            clientes = soapService.listarClientes()
            isLoading = false
        }
    }

    LaunchedEffect(Unit) { loadData() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Administración de Clientes") },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                idCliente = ""
                nombres = ""
                email = ""
                telefono = ""
                showDialog = true
            }) {
                Icon(Icons.Filled.Add, contentDescription = "Añadir Cliente")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp)) {
                    items(clientes) { c ->
                        Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                            Row(
                                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(text = c.nombres, style = MaterialTheme.typography.titleMedium)
                                    Text(text = "ID: ${c.idCliente} | Email: ${c.email}", style = MaterialTheme.typography.bodyMedium)
                                }
                                IconButton(onClick = {
                                    scope.launch {
                                        soapService.eliminarCliente(c.idCliente)
                                        loadData()
                                    }
                                }) {
                                    Icon(Icons.Filled.Delete, contentDescription = "Eliminar", tint = MaterialTheme.colorScheme.error)
                                }
                            }
                        }
                    }
                }
            }
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Nuevo Cliente") },
                text = {
                    Column {
                        OutlinedTextField(
                            value = idCliente,
                            onValueChange = { idCliente = it },
                            label = { Text("Cédula/ID") },
                            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                        )
                        OutlinedTextField(
                            value = nombres,
                            onValueChange = { nombres = it },
                            label = { Text("Nombres Completos") },
                            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                        )
                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = { Text("Email") },
                            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                        )
                        OutlinedTextField(
                            value = telefono,
                            onValueChange = { telefono = it },
                            label = { Text("Teléfono") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        scope.launch {
                            soapService.guardarCliente(Cliente(idCliente = idCliente, nombres = nombres, email = email, telefono = telefono))
                            showDialog = false
                            loadData()
                        }
                    }) { Text("Guardar") }
                },
                dismissButton = {
                    TextButton(onClick = { showDialog = false }) { Text("Cancelar") }
                }
            )
        }
    }
}
