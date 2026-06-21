package ec.edu.monster.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import ec.edu.monster.model.AsientoOcupado
import ec.edu.monster.model.PartidoFutbol
import ec.edu.monster.model.ResumenVentaLocalidad
import ec.edu.monster.service.SoapService
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReporteScreen(navController: NavController) {
    val coroutineScope = rememberCoroutineScope()
    val soapService = remember { SoapService() }

    var partidos by remember { mutableStateOf<List<PartidoFutbol>>(emptyList()) }
    var selectedPartido by remember { mutableStateOf<PartidoFutbol?>(null) }
    var expanded by remember { mutableStateOf(false) }

    var resumen by remember { mutableStateOf<List<ResumenVentaLocalidad>>(emptyList()) }
    var asientos by remember { mutableStateOf<List<AsientoOcupado>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    fun loadReport(partido: PartidoFutbol?) {
        isLoading = true
        coroutineScope.launch {
            if (partido != null) {
                resumen = soapService.listarResumenVentas(partido.codigo)
                asientos = soapService.obtenerAsientosOcupados(partido.codigo)
            } else {
                resumen = emptyList()
                asientos = emptyList()
            }
            isLoading = false
        }
    }

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            partidos = soapService.listarTodosPartidos()
            if (partidos.isNotEmpty()) {
                selectedPartido = partidos.first()
                loadReport(selectedPartido)
            } else {
                isLoading = false
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Reporte MASUP") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // Match Selection
            Box(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = selectedPartido?.let { "${it.equipoLocal} vs ${it.equipoVisitante}" } ?: "Seleccione un Partido",
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        partidos.forEach { p ->
                            DropdownMenuItem(
                                text = { Text("${p.equipoLocal} vs ${p.equipoVisitante}") },
                                onClick = {
                                    selectedPartido = p
                                    expanded = false
                                    loadReport(p)
                                }
                            )
                        }
                    }
                }
            }

            if (isLoading) {
                Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (selectedPartido != null) {
                // Stadium Map
                Text(
                    text = "Estado del Estadio (MASUP)",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
                StadiumMap(asientos = asientos, modifier = Modifier.fillMaxWidth().height(300.dp).padding(16.dp))
                
                Divider(modifier = Modifier.padding(vertical = 16.dp))

                // Table Header
                Text(
                    text = "Resumen de Ventas",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Localidad", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold)
                    Text("Vendidos", modifier = Modifier.weight(0.5f), fontWeight = FontWeight.Bold, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                    Text("Total ($)", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold, textAlign = androidx.compose.ui.text.style.TextAlign.End)
                }
                Divider()

                // Table Rows
                resumen.forEach { item ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(item.codigoLocalidad, modifier = Modifier.weight(1f))
                        Text(item.vendidos.toString(), modifier = Modifier.weight(0.5f), textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                        Text(String.format("%.2f", item.totalRecaudado), modifier = Modifier.weight(1f), textAlign = androidx.compose.ui.text.style.TextAlign.End)
                    }
                    Divider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 0.5.dp)
                }

                // Totals row
                val totalVendidos = resumen.sumOf { it.vendidos }
                val totalRecaudado = resumen.sumOf { it.totalRecaudado }
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("TOTAL", modifier = Modifier.weight(1f), fontWeight = FontWeight.ExtraBold)
                    Text(totalVendidos.toString(), modifier = Modifier.weight(0.5f), fontWeight = FontWeight.ExtraBold, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                    Text(String.format("%.2f", totalRecaudado), modifier = Modifier.weight(1f), fontWeight = FontWeight.ExtraBold, textAlign = androidx.compose.ui.text.style.TextAlign.End)
                }
            }
        }
    }
}

@Composable
fun StadiumMap(asientos: List<AsientoOcupado>, modifier: Modifier = Modifier) {
    // Determine counts
    val counts = mutableMapOf<String, Int>()
    asientos.forEach { a ->
        counts[a.loc.uppercase()] = (counts[a.loc.uppercase()] ?: 0) + a.cant
    }

    val norteOcup = counts["NORTE"] ?: counts["NOR"] ?: 0
    val surOcup = counts["SUR"] ?: 0
    val esteOcup = counts["ESTE"] ?: counts["EST"] ?: 0
    val oesteOcup = counts["OESTE"] ?: counts["OES"] ?: 0

    val maxNS = 40
    val maxEO = 25

    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        val seatSize = w / 25f
        val padding = seatSize / 4f

        val green = Color(0xFF4CAF50)
        val red = Color(0xFFF44336)

        fun drawSection(startX: Float, startY: Float, cols: Int, rows: Int, occupied: Int, total: Int) {
            var drawn = 0
            for (r in 0 until rows) {
                for (c in 0 until cols) {
                    if (drawn >= total) break
                    val x = startX + c * (seatSize + padding)
                    val y = startY + r * (seatSize + padding)
                    val color = if (drawn < occupied) red else green
                    drawRect(color = color, topLeft = Offset(x, y), size = Size(seatSize, seatSize))
                    drawn++
                }
            }
        }

        // Norte (Top) 2x20
        val nStartX = (w - (20 * (seatSize + padding))) / 2f
        val nStartY = 0f
        drawSection(nStartX, nStartY, 20, 2, norteOcup, maxNS)

        // Sur (Bottom) 2x20
        val sStartY = h - (2 * (seatSize + padding))
        drawSection(nStartX, sStartY, 20, 2, surOcup, maxNS)

        // Este (Right) 5x5
        val eStartX = w - (5 * (seatSize + padding))
        val eStartY = (h - (5 * (seatSize + padding))) / 2f
        drawSection(eStartX, eStartY, 5, 5, esteOcup, maxEO)

        // Oeste (Left) 5x5
        val oStartX = 0f
        drawSection(oStartX, eStartY, 5, 5, oesteOcup, maxEO)
        
        // Pitch (Center)
        val pitchW = w - (10 * (seatSize + padding)) - 40f
        val pitchH = h - (4 * (seatSize + padding)) - 40f
        if (pitchW > 0 && pitchH > 0) {
            drawRect(
                color = Color(0xFF1B5E20),
                topLeft = Offset(oStartX + 5 * (seatSize + padding) + 20f, nStartY + 2 * (seatSize + padding) + 20f),
                size = Size(pitchW, pitchH)
            )
        }
    }
}
