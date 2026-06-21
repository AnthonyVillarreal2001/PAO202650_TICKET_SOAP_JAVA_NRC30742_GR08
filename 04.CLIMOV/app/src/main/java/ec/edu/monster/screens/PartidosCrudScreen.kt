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
import ec.edu.monster.model.PartidoFutbol
import ec.edu.monster.service.SoapService
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PartidosCrudScreen(navController: NavController) {
    val soapService = remember { SoapService() }
    val scope = rememberCoroutineScope()
    var partidos by remember { mutableStateOf<List<PartidoFutbol>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    fun loadData() {
        isLoading = true
        scope.launch {
            partidos = soapService.listarTodosPartidos()
            isLoading = false
        }
    }

    LaunchedEffect(Unit) { loadData() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Administración de Partidos") },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp)) {
                    items(partidos) { p ->
                        Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                            Row(
                                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(text = "${p.equipoLocal} vs ${p.equipoVisitante}", style = MaterialTheme.typography.titleMedium)
                                    val fmt = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                                    Text(text = "Código: ${p.codigo} | Fecha: ${fmt.format(p.fecha)}", style = MaterialTheme.typography.bodyMedium)
                                }
                                IconButton(onClick = {
                                    scope.launch {
                                        soapService.eliminarPartido(p.codigo)
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
    }
}
