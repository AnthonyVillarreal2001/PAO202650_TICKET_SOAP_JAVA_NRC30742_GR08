package ec.edu.monster.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import ec.edu.monster.model.Estadio
import ec.edu.monster.service.SoapService
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EstadiosScreen(navController: NavController) {
    val soapService = remember { SoapService() }
    val scope = rememberCoroutineScope()
    var estadios by remember { mutableStateOf<List<Estadio>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var showDialog by remember { mutableStateOf(false) }

    // Form states
    var idEstadio by remember { mutableStateOf("") }
    var nombreEstadio by remember { mutableStateOf("") }
    var ciudad by remember { mutableStateOf("") }
    var capacidad by remember { mutableStateOf("") }

    fun loadData() {
        isLoading = true
        scope.launch {
            estadios = soapService.listarEstadios()
            isLoading = false
        }
    }

    LaunchedEffect(Unit) { loadData() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Administración de Estadios") },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                idEstadio = ""
                nombreEstadio = ""
                ciudad = ""
                capacidad = ""
                showDialog = true
            }) {
                Icon(Icons.Filled.Add, contentDescription = "Añadir Estadio")
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
                    items(estadios) { estadio ->
                        Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                            Row(
                                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(text = estadio.nombre, style = MaterialTheme.typography.titleMedium)
                                    Text(text = "Ciudad: ${estadio.pais} | Aforo: ${estadio.aforo}", style = MaterialTheme.typography.bodyMedium)
                                }
                                IconButton(onClick = {
                                    scope.launch {
                                        soapService.eliminarEstadio(estadio.codigo)
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
                title = { Text("Nuevo Estadio") },
                text = {
                    Column {
                        OutlinedTextField(
                            value = idEstadio,
                            onValueChange = { idEstadio = it },
                            label = { Text("ID Estadio") },
                            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                        )
                        OutlinedTextField(
                            value = nombreEstadio,
                            onValueChange = { nombreEstadio = it },
                            label = { Text("Nombre") },
                            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                        )
                        OutlinedTextField(
                            value = ciudad,
                            onValueChange = { ciudad = it },
                            label = { Text("Ciudad/País") },
                            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                        )
                        OutlinedTextField(
                            value = capacidad,
                            onValueChange = { capacidad = it },
                            label = { Text("Aforo") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        val cap = capacidad.toIntOrNull() ?: 0
                        scope.launch {
                            soapService.guardarEstadio(Estadio(codigo = idEstadio, nombre = nombreEstadio, pais = ciudad, aforo = cap))
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
