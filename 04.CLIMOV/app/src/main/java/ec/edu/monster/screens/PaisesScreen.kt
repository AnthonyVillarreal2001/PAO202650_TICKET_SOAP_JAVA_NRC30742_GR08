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
import ec.edu.monster.model.Pais
import ec.edu.monster.service.SoapService
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaisesScreen(navController: NavController) {
    val soapService = remember { SoapService() }
    val scope = rememberCoroutineScope()
    var paises by remember { mutableStateOf<List<Pais>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var showDialog by remember { mutableStateOf(false) }

    // Form states
    var idPais by remember { mutableStateOf("") }
    var nombrePais by remember { mutableStateOf("") }

    fun loadData() {
        isLoading = true
        scope.launch {
            paises = soapService.listarPaises()
            isLoading = false
        }
    }

    LaunchedEffect(Unit) { loadData() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Administración de Países") },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                idPais = ""
                nombrePais = ""
                showDialog = true
            }) {
                Icon(Icons.Filled.Add, contentDescription = "Añadir País")
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
                    items(paises) { pais ->
                        Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                            Row(
                                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(text = pais.nombre, style = MaterialTheme.typography.titleMedium)
                                    Text(text = "ID: ${pais.codigo}", style = MaterialTheme.typography.bodyMedium)
                                }
                                IconButton(onClick = {
                                    scope.launch {
                                        soapService.eliminarPais(pais.codigo)
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
                title = { Text("Nuevo País") },
                text = {
                    Column {
                        OutlinedTextField(
                            value = idPais,
                            onValueChange = { idPais = it },
                            label = { Text("ID País") },
                            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                        )
                        OutlinedTextField(
                            value = nombrePais,
                            onValueChange = { nombrePais = it },
                            label = { Text("Nombre") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        scope.launch {
                            soapService.guardarPais(Pais(codigo = idPais, nombre = nombrePais))
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
