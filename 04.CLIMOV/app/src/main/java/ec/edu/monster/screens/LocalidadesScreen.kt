package ec.edu.monster.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import ec.edu.monster.model.AsientoOcupado
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
    var asientosOcupados by remember { mutableStateOf<List<AsientoOcupado>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    // Cart state: Map<CodigoLocalidad, Cantidad>
    var cart by remember { mutableStateOf<Map<String, Int>>(emptyMap()) }
    var showCheckout by remember { mutableStateOf(false) }

    LaunchedEffect(codigoPartido) {
        coroutineScope.launch {
            localidades = soapService.listarLocalidadesDisponibles(codigoPartido)
            asientosOcupados = soapService.obtenerAsientosOcupados(codigoPartido)
            isLoading = false
        }
    }

    val cartTotalItems = cart.values.sum()
    val subtotal = cart.entries.sumOf { entry ->
        val loc = localidades.find { it.codigoLocalidad == entry.key }
        (loc?.precio ?: 0.0) * entry.value
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Comprar Boletos") },
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
        },
        bottomBar = {
            if (cartTotalItems > 0 && !showCheckout) {
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Asientos: $cartTotalItems", fontWeight = FontWeight.Bold)
                            Text("Subtotal: $${String.format("%.2f", subtotal)}")
                        }
                        Button(onClick = { showCheckout = true }) {
                            Text("Pagar")
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (localidades.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No hay localidades disponibles.")
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // MASUP Interactive Map
                Text(
                    text = "Toca una localidad para añadirla al carrito:",
                    modifier = Modifier.padding(16.dp),
                    fontWeight = FontWeight.Bold
                )
                
                InteractiveStadiumMap(
                    asientos = asientosOcupados,
                    localidades = localidades,
                    cart = cart,
                    modifier = Modifier.fillMaxWidth().height(250.dp).padding(horizontal = 16.dp),
                    onSectionTapped = { locName ->
                        val loc = localidades.find { it.codigoLocalidad.uppercase() == locName }
                        if (loc != null) {
                            val inCart = cart[loc.codigoLocalidad] ?: 0
                            if (inCart < loc.disponibilidad) {
                                val m = cart.toMutableMap()
                                m[loc.codigoLocalidad] = inCart + 1
                                cart = m
                            }
                        }
                    }
                )

                Divider(modifier = Modifier.padding(vertical = 8.dp))

                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    val cartItems = cart.filter { it.value > 0 }.toList()
                    if (cartItems.isEmpty()) {
                        item {
                            Text("Tu carrito está vacío. Toca el estadio para añadir boletos.", color = Color.Gray, modifier = Modifier.padding(16.dp))
                        }
                    } else {
                        items(cartItems) { (codigoLoc, cant) ->
                            val localidad = localidades.find { it.codigoLocalidad == codigoLoc }
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(text = codigoLoc, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                                        Text(text = "$${String.format("%.2f", (localidad?.precio ?: 0.0) * cant)}", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                                    }
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        IconButton(onClick = {
                                            val m = cart.toMutableMap()
                                            if (cant == 1) m.remove(codigoLoc) else m[codigoLoc] = cant - 1
                                            cart = m
                                        }) { Icon(Icons.Filled.Remove, contentDescription = "Menos") }
                                        Text("$cant", fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        if (showCheckout) {
            CheckoutDialog(
                cart = cart,
                localidades = localidades,
                usuario = usuario,
                codigoPartido = codigoPartido,
                soapService = soapService,
                onDismiss = { showCheckout = false },
                onSuccess = { navController.navigateUp() }
            )
        }
    }
}

@Composable
fun InteractiveStadiumMap(
    asientos: List<AsientoOcupado>,
    localidades: List<LocalidadPartido>,
    cart: Map<String, Int>,
    modifier: Modifier = Modifier,
    onSectionTapped: (String) -> Unit
) {
    val locNorte = localidades.find { it.codigoLocalidad.contains("GEN", ignoreCase = true) }?.codigoLocalidad ?: "NORTE"
    val locSur = localidades.find { it.codigoLocalidad.contains("GVI", ignoreCase = true) }?.codigoLocalidad ?: "SUR"
    val locEste = localidades.find { it.codigoLocalidad.contains("PAL", ignoreCase = true) }?.codigoLocalidad ?: "ESTE"
    val locOeste = localidades.find { it.codigoLocalidad.contains("TRI", ignoreCase = true) }?.codigoLocalidad ?: "OESTE"

    val counts = mutableMapOf<String, Int>()
    asientos.forEach { a ->
        counts[a.loc.uppercase()] = (counts[a.loc.uppercase()] ?: 0) + a.cant
    }

    val cartCounts = mutableMapOf<String, Int>()
    cart.forEach { (loc, cant) ->
        cartCounts[loc.uppercase()] = cant
    }

    val norteOcup = (counts[locNorte.uppercase()] ?: 0) + (cartCounts[locNorte.uppercase()] ?: 0)
    val surOcup = (counts[locSur.uppercase()] ?: 0) + (cartCounts[locSur.uppercase()] ?: 0)
    val esteOcup = (counts[locEste.uppercase()] ?: 0) + (cartCounts[locEste.uppercase()] ?: 0)
    val oesteOcup = (counts[locOeste.uppercase()] ?: 0) + (cartCounts[locOeste.uppercase()] ?: 0)

    val maxNS = 40
    val maxEO = 25

    Canvas(modifier = modifier.pointerInput(Unit) {
        detectTapGestures { offset ->
            val w = size.width
            val h = size.height
            val seatSize = w / 25f
            val padding = seatSize / 4f

            val nStartX = (w - (20 * (seatSize + padding))) / 2f
            val nEndY = 2 * (seatSize + padding)
            if (offset.y <= nEndY && offset.x >= nStartX && offset.x <= nStartX + 20 * (seatSize + padding)) {
                onSectionTapped(locNorte)
            }

            val sStartY = h - (2 * (seatSize + padding))
            if (offset.y >= sStartY && offset.x >= nStartX && offset.x <= nStartX + 20 * (seatSize + padding)) {
                onSectionTapped(locSur)
            }

            val eStartX = w - (5 * (seatSize + padding))
            val eStartY = (h - (5 * (seatSize + padding))) / 2f
            val eEndY = eStartY + 5 * (seatSize + padding)
            if (offset.x >= eStartX && offset.y >= eStartY && offset.y <= eEndY) {
                onSectionTapped(locEste)
            }

            val oEndX = 5 * (seatSize + padding)
            if (offset.x <= oEndX && offset.y >= eStartY && offset.y <= eEndY) {
                onSectionTapped(locOeste)
            }
        }
    }) {
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

        // Norte (Top)
        val nStartX = (w - (20 * (seatSize + padding))) / 2f
        drawSection(nStartX, 0f, 20, 2, norteOcup, maxNS)

        // Sur (Bottom)
        val sStartY = h - (2 * (seatSize + padding))
        drawSection(nStartX, sStartY, 20, 2, surOcup, maxNS)

        // Este (Right)
        val eStartX = w - (5 * (seatSize + padding))
        val eStartY = (h - (5 * (seatSize + padding))) / 2f
        drawSection(eStartX, eStartY, 5, 5, esteOcup, maxEO)

        // Oeste (Left)
        drawSection(0f, eStartY, 5, 5, oesteOcup, maxEO)
        
        // Pitch
        val pitchW = w - (10 * (seatSize + padding)) - 40f
        val pitchH = h - (4 * (seatSize + padding)) - 40f
        if (pitchW > 0 && pitchH > 0) {
            drawRect(
                color = Color(0xFF1B5E20),
                topLeft = Offset(5 * (seatSize + padding) + 20f, 2 * (seatSize + padding) + 20f),
                size = Size(pitchW, pitchH)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutDialog(
    cart: Map<String, Int>,
    localidades: List<LocalidadPartido>,
    usuario: String,
    codigoPartido: String,
    soapService: SoapService,
    onDismiss: () -> Unit,
    onSuccess: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var isPurchasing by remember { mutableStateOf(false) }
    var compraResultado by remember { mutableStateOf<CompraResultado?>(null) }

    var paymentMethod by remember { mutableStateOf("Efectivo") }
    var mesesCredito by remember { mutableStateOf(6f) } // 3 to 12

    val subtotal = cart.entries.sumOf { entry ->
        val loc = localidades.find { it.codigoLocalidad == entry.key }
        (loc?.precio ?: 0.0) * entry.value
    }

    val hasDiscount = paymentMethod == "Efectivo"
    val discount = if (hasDiscount) subtotal * 0.12 else 0.0
    val afterDiscount = subtotal - discount
    val iva = afterDiscount * 0.12
    val finalTotal = afterDiscount + iva

    Dialog(
        onDismissRequest = { if (!isPurchasing) onDismiss() },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(modifier = Modifier.padding(16.dp).fillMaxSize()) {
                Text("Resumen de Compra", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))

                // Cart items
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(cart.entries.toList()) { entry ->
                        val loc = localidades.find { it.codigoLocalidad == entry.key }
                        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("${entry.value}x ${entry.key}")
                            Text("$${String.format("%.2f", (loc?.precio ?: 0.0) * entry.value)}")
                        }
                    }
                }
                
                Divider(modifier = Modifier.padding(vertical = 8.dp))
                
                // Select Cliente
                var expanded by remember { mutableStateOf(false) }
                var selectedClienteId by remember { mutableStateOf(usuario) }
                var clientes by remember { mutableStateOf<List<ec.edu.monster.model.Cliente>>(emptyList()) }
                
                LaunchedEffect(Unit) {
                    clientes = soapService.listarClientes()
                    if (clientes.none { it.idCliente == usuario } && clientes.isNotEmpty()) {
                        selectedClienteId = clientes[0].idCliente
                    }
                }

                Text("Cliente para Factura:", fontWeight = FontWeight.Bold)
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                ) {
                    val displayValue = if (selectedClienteId.isBlank()) "Seleccione un cliente" else {
                        val c = clientes.find { it.idCliente == selectedClienteId }
                        if (c != null) "${c.idCliente} - ${c.nombres} ${c.apellidos}" else selectedClienteId
                    }
                    OutlinedTextField(
                        value = displayValue,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        clientes.forEach { c ->
                            DropdownMenuItem(
                                text = { Text("${c.idCliente} - ${c.nombres} ${c.apellidos}") },
                                onClick = { selectedClienteId = c.idCliente; expanded = false }
                            )
                        }
                    }
                }
                
                Divider(modifier = Modifier.padding(vertical = 8.dp))
                
                // Payment Method
                Text("Método de Pago:", fontWeight = FontWeight.Bold)
                Row(modifier = Modifier.fillMaxWidth()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(selected = paymentMethod == "Efectivo", onClick = { paymentMethod = "Efectivo" })
                        Text("Efectivo (-12%)")
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(selected = paymentMethod == "Crédito", onClick = { paymentMethod = "Crédito" })
                        Text("Crédito")
                    }
                }

                if (paymentMethod == "Crédito") {
                    Text("Plazo en Meses: ${mesesCredito.toInt()}")
                    Slider(
                        value = mesesCredito,
                        onValueChange = { mesesCredito = it },
                        valueRange = 3f..12f,
                        steps = 8
                    )
                }

                Divider(modifier = Modifier.padding(vertical = 8.dp))

                // Totals
                Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.End) {
                    Text("Subtotal: $${String.format("%.2f", subtotal)}")
                    if (hasDiscount) {
                        Text("Descuento Efectivo: -$${String.format("%.2f", discount)}", color = Color(0xFF388E3C))
                    }
                    Text("IVA (12%): $${String.format("%.2f", iva)}")
                    Text("TOTAL: $${String.format("%.2f", finalTotal)}", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (compraResultado != null) {
                    val isSuccess = compraResultado!!.estado == 1
                    Text(
                        text = compraResultado!!.mensaje,
                        color = if (isSuccess) Color(0xFF388E3C) else MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = { if (isSuccess) onSuccess() else compraResultado = null }, modifier = Modifier.fillMaxWidth()) {
                        Text(if (isSuccess) "Finalizar" else "Reintentar")
                    }
                } else {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                        OutlinedButton(onClick = onDismiss, enabled = !isPurchasing) { Text("Cancelar") }
                        Button(
                            onClick = {
                                isPurchasing = true
                                scope.launch {
                                    val csvLocales = cart.keys.joinToString(",")
                                    val csvCants = cart.values.joinToString(",")
                                    val res = soapService.comprarBoletosMultiples(codigoPartido, csvLocales, csvCants, selectedClienteId)
                                    if (res.estado == 1 && paymentMethod == "Crédito") {
                                        // Save amortizaciones
                                        soapService.guardarAmortizaciones(selectedClienteId, finalTotal, mesesCredito.toInt())
                                    }
                                    compraResultado = res
                                    isPurchasing = false
                                }
                            },
                            enabled = !isPurchasing
                        ) {
                            if (isPurchasing) CircularProgressIndicator(modifier = Modifier.size(24.dp))
                            else Text("Confirmar Pago")
                        }
                    }
                }
            }
        }
    }
}
