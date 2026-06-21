package ec.edu.monster.service

import ec.edu.monster.model.CompraResultado
import ec.edu.monster.model.LocalidadPartido
import ec.edu.monster.model.PartidoFutbol
import ec.edu.monster.model.ResumenVentaLocalidad
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.StringReader
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

class SoapService {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    // 10.0.2.2 es la IP especial del emulador para acceder a localhost
    private val baseUrlTicketPremium = "http://192.168.100.94:8080/WS_EurekaBank_Server/WSTicketPremium"
    private val baseUrlFederacion = "http://192.168.100.94:8080/WS_EurekaBank_Server/WSFederacion"
    private val baseUrlCRUD = "http://192.168.100.94:8080/WS_EurekaBank_Server/WSCRUD"
    private val baseUrlCredito = "http://192.168.100.94:8080/WS_EurekaBank_Server/WSCredito"

    companion object {
        private const val NAMESPACE = "http://ws.monster.edu.ec/"
        
        fun escapeXml(value: String?): String {
            if (value == null) return ""
            return value.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;")
        }
    }

    suspend fun validarIngreso(usuario: String, password: String): Boolean = withContext(Dispatchers.IO) {
        try {
            // Normalize inputs: trim and uppercase to match server expectations
            var u = usuario.trim().uppercase()
            var p = password.trim()

            fun call(uCall: String, pCall: String): Boolean {
                val soapEnvelope = """
                    <?xml version="1.0" encoding="UTF-8"?>
                    <S:Envelope xmlns:S="http://schemas.xmlsoap.org/soap/envelope/">
                        <S:Body>
                            <ns2:validarIngreso xmlns:ns2="${NAMESPACE}">
                                <usuario>$uCall</usuario>
                                <password>$pCall</password>
                            </ns2:validarIngreso>
                        </S:Body>
                    </S:Envelope>
                """.trimIndent()

                val request = Request.Builder()
                    .url(baseUrlTicketPremium)
                    .post(soapEnvelope.toRequestBody("text/xml; charset=utf-8".toMediaType()))
                    .addHeader("SOAPAction", "")
                    .build()

                val response = client.newCall(request).execute()
                val responseBody = response.body?.string() ?: ""
                return parseValidarIngresoResponse(responseBody)
            }

            // First attempt with normalized input
            val first = call(u, p)
            if (first) return@withContext true

            // Fallback: common typo mapping (MOSTER -> MONSTER)
            if (u == "MOSTER") {
                val retry = call("MONSTER", p)
                if (retry) return@withContext true
            }

            false
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun listarPartidosDisponibles(): List<PartidoFutbol> = withContext(Dispatchers.IO) {
        try {
            val soapEnvelope = """
                <?xml version="1.0" encoding="UTF-8"?>
                <S:Envelope xmlns:S="http://schemas.xmlsoap.org/soap/envelope/">
                    <S:Body>
                        <ns2:listarPartidosDisponibles xmlns:ns2="${NAMESPACE}"/>
                    </S:Body>
                </S:Envelope>
            """.trimIndent()

            val request = Request.Builder()
                .url(baseUrlFederacion)
                .post(soapEnvelope.toRequestBody("text/xml; charset=utf-8".toMediaType()))
                .addHeader("SOAPAction", "")
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""

            parsePartidosResponse(responseBody)
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun listarLocalidadesDisponibles(codigoPartido: String): List<LocalidadPartido> = withContext(Dispatchers.IO) {
        try {
            val soapEnvelope = """
                <?xml version="1.0" encoding="UTF-8"?>
                <S:Envelope xmlns:S="http://schemas.xmlsoap.org/soap/envelope/">
                    <S:Body>
                        <ns2:listarLocalidadesDisponibles xmlns:ns2="${NAMESPACE}">
                            <codigoPartido>${escapeXml(codigoPartido)}</codigoPartido>
                        </ns2:listarLocalidadesDisponibles>
                    </S:Body>
                </S:Envelope>
            """.trimIndent()

            val request = Request.Builder()
                .url(baseUrlFederacion)
                .post(soapEnvelope.toRequestBody("text/xml; charset=utf-8".toMediaType()))
                .addHeader("SOAPAction", "")
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""

            parseLocalidadesResponse(responseBody)
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun comprarBoletosMultiples(codigoPartido: String, localesCsv: String, cantCsv: String, cliente: String): CompraResultado = withContext(Dispatchers.IO) {
        try {
            val soapEnvelope = """
                <?xml version="1.0" encoding="UTF-8"?>
                <S:Envelope xmlns:S="http://schemas.xmlsoap.org/soap/envelope/">
                    <S:Body>
                        <ns2:comprarBoletosMultiples xmlns:ns2="${NAMESPACE}">
                            <codigoPartido>${escapeXml(codigoPartido)}</codigoPartido>
                            <codigosLocalidades>${escapeXml(localesCsv)}</codigosLocalidades>
                            <cantidades>${escapeXml(cantCsv)}</cantidades>
                            <cliente>${escapeXml(cliente)}</cliente>
                            <vendedor>SISTEMA_MOVIL</vendedor>
                        </ns2:comprarBoletosMultiples>
                    </S:Body>
                </S:Envelope>
            """.trimIndent()

            val request = Request.Builder()
                .url(baseUrlFederacion)
                .post(soapEnvelope.toRequestBody("text/xml; charset=utf-8".toMediaType()))
                .addHeader("SOAPAction", "")
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""

            parseCompraResponse(responseBody)
        } catch (e: Exception) {
            e.printStackTrace()
            CompraResultado(estado = -1, mensaje = "Error de conexión: " + e.message)
        }
    }

    suspend fun guardarAmortizaciones(idCliente: String, montoTotal: Double, mesesPlazo: Int): Boolean = withContext(Dispatchers.IO) {
        try {
            val soapEnvelope = """
                <?xml version="1.0" encoding="UTF-8"?>
                <S:Envelope xmlns:S="http://schemas.xmlsoap.org/soap/envelope/">
                    <S:Body>
                        <ns2:guardarAmortizaciones xmlns:ns2="${NAMESPACE}">
                            <idCliente>${escapeXml(idCliente)}</idCliente>
                            <valorLocalidades>$montoTotal</valorLocalidades>
                            <plazoMeses>$mesesPlazo</plazoMeses>
                        </ns2:guardarAmortizaciones>
                    </S:Body>
                </S:Envelope>
            """.trimIndent()

            val request = Request.Builder()
                .url(baseUrlCredito)
                .post(soapEnvelope.toRequestBody("text/xml; charset=utf-8".toMediaType()))
                .addHeader("SOAPAction", "")
                .build()

            val response = client.newCall(request).execute()
            response.isSuccessful
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun listarResumenVentas(codigoPartido: String): List<ResumenVentaLocalidad> = withContext(Dispatchers.IO) {
        try {
            val bodyContent = if (codigoPartido.isNotBlank()) {
                "<codigoPartido>${escapeXml(codigoPartido)}</codigoPartido>"
            } else ""

            val soapEnvelope = """
                <?xml version="1.0" encoding="UTF-8"?>
                <S:Envelope xmlns:S="http://schemas.xmlsoap.org/soap/envelope/">
                    <S:Body>
                        <ns2:listarResumenVentas xmlns:ns2="${NAMESPACE}">
                            $bodyContent
                        </ns2:listarResumenVentas>
                    </S:Body>
                </S:Envelope>
            """.trimIndent()

            val request = Request.Builder()
                .url(baseUrlFederacion)
                .post(soapEnvelope.toRequestBody("text/xml; charset=utf-8".toMediaType()))
                .addHeader("SOAPAction", "")
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""

            parseResumenResponse(responseBody)
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun listarTodasLasFacturas(): List<ec.edu.monster.model.Factura> = withContext(Dispatchers.IO) {
        try {
            val soapEnvelope = """
                <?xml version="1.0" encoding="UTF-8"?>
                <S:Envelope xmlns:S="http://schemas.xmlsoap.org/soap/envelope/">
                    <S:Body>
                        <ns2:listarTodasLasFacturas xmlns:ns2="${NAMESPACE}">
                            <fecha></fecha>
                            <vendedor></vendedor>
                        </ns2:listarTodasLasFacturas>
                    </S:Body>
                </S:Envelope>
            """.trimIndent()

            val request = Request.Builder()
                .url(baseUrlFederacion)
                .post(soapEnvelope.toRequestBody("text/xml; charset=utf-8".toMediaType()))
                .addHeader("SOAPAction", "")
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""
            parseFacturasResponse(responseBody)
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun listarFacturas(idCliente: String): List<ec.edu.monster.model.Factura> = withContext(Dispatchers.IO) {
        try {
            val soapEnvelope = """
                <?xml version="1.0" encoding="UTF-8"?>
                <S:Envelope xmlns:S="http://schemas.xmlsoap.org/soap/envelope/">
                    <S:Body>
                        <ns2:listarFacturas xmlns:ns2="${NAMESPACE}">
                            <idCliente>${escapeXml(idCliente)}</idCliente>
                        </ns2:listarFacturas>
                    </S:Body>
                </S:Envelope>
            """.trimIndent()

            val request = Request.Builder()
                .url(baseUrlFederacion)
                .post(soapEnvelope.toRequestBody("text/xml; charset=utf-8".toMediaType()))
                .addHeader("SOAPAction", "")
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""
            parseFacturasResponse(responseBody)
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun obtenerDetallesFactura(idFactura: Long): List<ec.edu.monster.model.DetalleFactura> = withContext(Dispatchers.IO) {
        try {
            val soapEnvelope = """
                <?xml version="1.0" encoding="UTF-8"?>
                <S:Envelope xmlns:S="http://schemas.xmlsoap.org/soap/envelope/">
                    <S:Body>
                        <ns2:obtenerDetallesFactura xmlns:ns2="${NAMESPACE}">
                            <idFactura>$idFactura</idFactura>
                        </ns2:obtenerDetallesFactura>
                    </S:Body>
                </S:Envelope>
            """.trimIndent()

            val request = Request.Builder()
                .url(baseUrlFederacion)
                .post(soapEnvelope.toRequestBody("text/xml; charset=utf-8".toMediaType()))
                .addHeader("SOAPAction", "")
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""
            parseDetallesFacturaResponse(responseBody)
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun obtenerAsientosOcupados(codigoPartido: String): List<ec.edu.monster.model.AsientoOcupado> = withContext(Dispatchers.IO) {
        try {
            val soapEnvelope = """
                <?xml version="1.0" encoding="UTF-8"?>
                <S:Envelope xmlns:S="http://schemas.xmlsoap.org/soap/envelope/">
                    <S:Body>
                        <ns2:obtenerAsientosOcupados xmlns:ns2="${NAMESPACE}">
                            <codigoPartido>${escapeXml(codigoPartido)}</codigoPartido>
                        </ns2:obtenerAsientosOcupados>
                    </S:Body>
                </S:Envelope>
            """.trimIndent()

            val request = Request.Builder()
                .url(baseUrlFederacion)
                .post(soapEnvelope.toRequestBody("text/xml; charset=utf-8".toMediaType()))
                .addHeader("SOAPAction", "")
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""
            parseAsientosOcupadosResponse(responseBody)
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun listarTodasLasAmortizaciones(): List<ec.edu.monster.model.Amortizacion> = withContext(Dispatchers.IO) {
        try {
            val soapEnvelope = """
                <?xml version="1.0" encoding="UTF-8"?>
                <S:Envelope xmlns:S="http://schemas.xmlsoap.org/soap/envelope/">
                    <S:Body>
                        <ns2:listarTodasLasAmortizaciones xmlns:ns2="${NAMESPACE}"/>
                    </S:Body>
                </S:Envelope>
            """.trimIndent()

            val request = Request.Builder()
                .url(baseUrlCredito)
                .post(soapEnvelope.toRequestBody("text/xml; charset=utf-8".toMediaType()))
                .addHeader("SOAPAction", "")
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""
            parseAmortizacionesResponse(responseBody)
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun listarAmortizaciones(idCliente: String): List<ec.edu.monster.model.Amortizacion> = withContext(Dispatchers.IO) {
        try {
            val soapEnvelope = """
                <?xml version="1.0" encoding="UTF-8"?>
                <S:Envelope xmlns:S="http://schemas.xmlsoap.org/soap/envelope/">
                    <S:Body>
                        <ns2:listarAmortizaciones xmlns:ns2="${NAMESPACE}">
                            <idCliente>${escapeXml(idCliente)}</idCliente>
                        </ns2:listarAmortizaciones>
                    </S:Body>
                </S:Envelope>
            """.trimIndent()

            val request = Request.Builder()
                .url(baseUrlCredito)
                .post(soapEnvelope.toRequestBody("text/xml; charset=utf-8".toMediaType()))
                .addHeader("SOAPAction", "")
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""
            parseAmortizacionesResponse(responseBody)
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    // ==================== CRUD ====================
    suspend fun listarPaises(): List<ec.edu.monster.model.Pais> = withContext(Dispatchers.IO) {
        try {
            val soapEnvelope = """
                <?xml version="1.0" encoding="UTF-8"?>
                <S:Envelope xmlns:S="http://schemas.xmlsoap.org/soap/envelope/">
                    <S:Body>
                        <ns2:listarPaises xmlns:ns2="${NAMESPACE}"/>
                    </S:Body>
                </S:Envelope>
            """.trimIndent()
            val request = Request.Builder().url(baseUrlCRUD).post(soapEnvelope.toRequestBody("text/xml; charset=utf-8".toMediaType())).addHeader("SOAPAction", "").build()
            val response = client.newCall(request).execute()
            parsePaisesResponse(response.body?.string() ?: "")
        } catch (e: Exception) { emptyList() }
    }

    suspend fun guardarPais(pais: ec.edu.monster.model.Pais): Boolean = withContext(Dispatchers.IO) {
        try {
            val soapEnvelope = """
                <?xml version="1.0" encoding="UTF-8"?>
                <S:Envelope xmlns:S="http://schemas.xmlsoap.org/soap/envelope/">
                    <S:Body>
                        <ns2:guardarPais xmlns:ns2="${NAMESPACE}">
                            <idPais>${escapeXml(pais.codigo)}</idPais>
                            <nombrePais>${escapeXml(pais.nombre)}</nombrePais>
                        </ns2:guardarPais>
                    </S:Body>
                </S:Envelope>
            """.trimIndent()
            val request = Request.Builder().url(baseUrlCRUD).post(soapEnvelope.toRequestBody("text/xml; charset=utf-8".toMediaType())).addHeader("SOAPAction", "").build()
            val response = client.newCall(request).execute()
            (response.body?.string() ?: "").contains("<return>true</return>")
        } catch (e: Exception) { false }
    }

    suspend fun eliminarPais(idPais: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val soapEnvelope = """
                <?xml version="1.0" encoding="UTF-8"?>
                <S:Envelope xmlns:S="http://schemas.xmlsoap.org/soap/envelope/">
                    <S:Body>
                        <ns2:eliminarPais xmlns:ns2="${NAMESPACE}">
                            <idPais>${escapeXml(idPais)}</idPais>
                        </ns2:eliminarPais>
                    </S:Body>
                </S:Envelope>
            """.trimIndent()
            val request = Request.Builder().url(baseUrlCRUD).post(soapEnvelope.toRequestBody("text/xml; charset=utf-8".toMediaType())).addHeader("SOAPAction", "").build()
            val response = client.newCall(request).execute()
            (response.body?.string() ?: "").contains("<return>true</return>")
        } catch (e: Exception) { false }
    }

    suspend fun listarEstadios(): List<ec.edu.monster.model.Estadio> = withContext(Dispatchers.IO) {
        try {
            val soapEnvelope = """
                <?xml version="1.0" encoding="UTF-8"?>
                <S:Envelope xmlns:S="http://schemas.xmlsoap.org/soap/envelope/">
                    <S:Body>
                        <ns2:listarEstadios xmlns:ns2="${NAMESPACE}"/>
                    </S:Body>
                </S:Envelope>
            """.trimIndent()
            val request = Request.Builder().url(baseUrlCRUD).post(soapEnvelope.toRequestBody("text/xml; charset=utf-8".toMediaType())).addHeader("SOAPAction", "").build()
            val response = client.newCall(request).execute()
            parseEstadiosResponse(response.body?.string() ?: "")
        } catch (e: Exception) { emptyList() }
    }

    suspend fun guardarEstadio(estadio: ec.edu.monster.model.Estadio): Boolean = withContext(Dispatchers.IO) {
        try {
            val soapEnvelope = """
                <?xml version="1.0" encoding="UTF-8"?>
                <S:Envelope xmlns:S="http://schemas.xmlsoap.org/soap/envelope/">
                    <S:Body>
                        <ns2:guardarEstadio xmlns:ns2="${NAMESPACE}">
                            <idEstadio>${escapeXml(estadio.codigo)}</idEstadio>
                            <nombreEstadio>${escapeXml(estadio.nombre)}</nombreEstadio>
                            <ciudad>${escapeXml(estadio.pais)}</ciudad>
                            <capacidad>${estadio.aforo}</capacidad>
                        </ns2:guardarEstadio>
                    </S:Body>
                </S:Envelope>
            """.trimIndent()
            val request = Request.Builder().url(baseUrlCRUD).post(soapEnvelope.toRequestBody("text/xml; charset=utf-8".toMediaType())).addHeader("SOAPAction", "").build()
            val response = client.newCall(request).execute()
            (response.body?.string() ?: "").contains("<return>true</return>")
        } catch (e: Exception) { false }
    }

    suspend fun eliminarEstadio(idEstadio: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val soapEnvelope = """
                <?xml version="1.0" encoding="UTF-8"?>
                <S:Envelope xmlns:S="http://schemas.xmlsoap.org/soap/envelope/">
                    <S:Body>
                        <ns2:eliminarEstadio xmlns:ns2="${NAMESPACE}">
                            <idEstadio>${escapeXml(idEstadio)}</idEstadio>
                        </ns2:eliminarEstadio>
                    </S:Body>
                </S:Envelope>
            """.trimIndent()
            val request = Request.Builder().url(baseUrlCRUD).post(soapEnvelope.toRequestBody("text/xml; charset=utf-8".toMediaType())).addHeader("SOAPAction", "").build()
            val response = client.newCall(request).execute()
            (response.body?.string() ?: "").contains("<return>true</return>")
        } catch (e: Exception) { false }
    }

    suspend fun listarClientes(): List<ec.edu.monster.model.Cliente> = withContext(Dispatchers.IO) {
        try {
            val soapEnvelope = """
                <?xml version="1.0" encoding="UTF-8"?>
                <S:Envelope xmlns:S="http://schemas.xmlsoap.org/soap/envelope/">
                    <S:Body>
                        <ns2:listarClientes xmlns:ns2="${NAMESPACE}"/>
                    </S:Body>
                </S:Envelope>
            """.trimIndent()
            val request = Request.Builder().url(baseUrlCRUD).post(soapEnvelope.toRequestBody("text/xml; charset=utf-8".toMediaType())).addHeader("SOAPAction", "").build()
            val response = client.newCall(request).execute()
            parseClientesResponse(response.body?.string() ?: "")
        } catch (e: Exception) { emptyList() }
    }

    suspend fun guardarCliente(cliente: ec.edu.monster.model.Cliente): Boolean = withContext(Dispatchers.IO) {
        try {
            val soapEnvelope = """
                <?xml version="1.0" encoding="UTF-8"?>
                <S:Envelope xmlns:S="http://schemas.xmlsoap.org/soap/envelope/">
                    <S:Body>
                        <ns2:guardarCliente xmlns:ns2="${NAMESPACE}">
                            <idCliente>${escapeXml(cliente.idCliente)}</idCliente>
                            <nombres>${escapeXml(cliente.nombres)}</nombres>
                            <correo>${escapeXml(cliente.email)}</correo>
                            <telefono>${escapeXml(cliente.telefono)}</telefono>
                            <edad>30</edad>
                            <genero>M</genero>
                        </ns2:guardarCliente>
                    </S:Body>
                </S:Envelope>
            """.trimIndent()
            val request = Request.Builder().url(baseUrlCRUD).post(soapEnvelope.toRequestBody("text/xml; charset=utf-8".toMediaType())).addHeader("SOAPAction", "").build()
            val response = client.newCall(request).execute()
            (response.body?.string() ?: "").contains("<return>true</return>")
        } catch (e: Exception) { false }
    }

    suspend fun eliminarCliente(idCliente: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val soapEnvelope = """
                <?xml version="1.0" encoding="UTF-8"?>
                <S:Envelope xmlns:S="http://schemas.xmlsoap.org/soap/envelope/">
                    <S:Body>
                        <ns2:eliminarCliente xmlns:ns2="${NAMESPACE}">
                            <idCliente>${escapeXml(idCliente)}</idCliente>
                        </ns2:eliminarCliente>
                    </S:Body>
                </S:Envelope>
            """.trimIndent()
            val request = Request.Builder().url(baseUrlCRUD).post(soapEnvelope.toRequestBody("text/xml; charset=utf-8".toMediaType())).addHeader("SOAPAction", "").build()
            val response = client.newCall(request).execute()
            (response.body?.string() ?: "").contains("<return>true</return>")
        } catch (e: Exception) { false }
    }

    suspend fun listarTodosPartidos(): List<PartidoFutbol> = withContext(Dispatchers.IO) {
        try {
            val soapEnvelope = """
                <?xml version="1.0" encoding="UTF-8"?>
                <S:Envelope xmlns:S="http://schemas.xmlsoap.org/soap/envelope/">
                    <S:Body>
                        <ns2:listarTodosPartidos xmlns:ns2="${NAMESPACE}"/>
                    </S:Body>
                </S:Envelope>
            """.trimIndent()
            val request = Request.Builder().url(baseUrlCRUD).post(soapEnvelope.toRequestBody("text/xml; charset=utf-8".toMediaType())).addHeader("SOAPAction", "").build()
            val response = client.newCall(request).execute()
            parsePartidosResponse(response.body?.string() ?: "")
        } catch (e: Exception) { emptyList() }
    }

    suspend fun eliminarPartido(codigo: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val soapEnvelope = """
                <?xml version="1.0" encoding="UTF-8"?>
                <S:Envelope xmlns:S="http://schemas.xmlsoap.org/soap/envelope/">
                    <S:Body>
                        <ns2:eliminarPartido xmlns:ns2="${NAMESPACE}">
                            <codigo>${escapeXml(codigo)}</codigo>
                        </ns2:eliminarPartido>
                    </S:Body>
                </S:Envelope>
            """.trimIndent()
            val request = Request.Builder().url(baseUrlCRUD).post(soapEnvelope.toRequestBody("text/xml; charset=utf-8".toMediaType())).addHeader("SOAPAction", "").build()
            val response = client.newCall(request).execute()
            (response.body?.string() ?: "").contains("<return>true</return>")
        } catch (e: Exception) { false }
    }

    // ==================== PARSERS ====================
    private fun parsePaisesResponse(xml: String): List<ec.edu.monster.model.Pais> {
        val list = mutableListOf<ec.edu.monster.model.Pais>()
        try {
            val factory = XmlPullParserFactory.newInstance()
            val parser = factory.newPullParser()
            parser.setInput(StringReader(xml))
            var eventType = parser.eventType
            var current = ec.edu.monster.model.Pais()
            var currentTag = ""

            while (eventType != XmlPullParser.END_DOCUMENT) {
                when (eventType) {
                    XmlPullParser.START_TAG -> {
                        currentTag = parser.name.substringAfter(":")
                        if (currentTag == "return" || currentTag == "pais") current = ec.edu.monster.model.Pais()
                    }
                    XmlPullParser.TEXT -> {
                        val text = parser.text?.trim() ?: ""
                        if (text.isNotEmpty()) {
                            when (currentTag) {
                                "idPais" -> current.codigo = text
                                "nombrePais" -> current.nombre = text
                            }
                        }
                    }
                    XmlPullParser.END_TAG -> {
                        val tagName = parser.name.substringAfter(":")
                        if ((tagName == "pais" || tagName == "return") && current.codigo.isNotEmpty()) {
                            list.add(current)
                        }
                        currentTag = ""
                    }
                }
                eventType = parser.next()
            }
        } catch (e: Exception) { e.printStackTrace() }
        return list
    }

    private fun parseEstadiosResponse(xml: String): List<ec.edu.monster.model.Estadio> {
        val list = mutableListOf<ec.edu.monster.model.Estadio>()
        try {
            val factory = XmlPullParserFactory.newInstance()
            val parser = factory.newPullParser()
            parser.setInput(StringReader(xml))
            var eventType = parser.eventType
            var current = ec.edu.monster.model.Estadio()
            var currentTag = ""

            while (eventType != XmlPullParser.END_DOCUMENT) {
                when (eventType) {
                    XmlPullParser.START_TAG -> {
                        currentTag = parser.name.substringAfter(":")
                        if (currentTag == "estadio" || currentTag == "return") current = ec.edu.monster.model.Estadio()
                    }
                    XmlPullParser.TEXT -> {
                        val text = parser.text?.trim() ?: ""
                        if (text.isNotEmpty()) {
                            when (currentTag) {
                                "idEstadio" -> current.codigo = text
                                "nombreEstadio" -> current.nombre = text
                                "ciudad" -> current.pais = text
                                "capacidad" -> current.aforo = text.toIntOrNull() ?: 0
                            }
                        }
                    }
                    XmlPullParser.END_TAG -> {
                        val tagName = parser.name.substringAfter(":")
                        if ((tagName == "estadio" || tagName == "return") && current.codigo.isNotEmpty()) {
                            list.add(current)
                        }
                        currentTag = ""
                    }
                }
                eventType = parser.next()
            }
        } catch (e: Exception) { e.printStackTrace() }
        return list
    }

    private fun parseClientesResponse(xml: String): List<ec.edu.monster.model.Cliente> {
        val list = mutableListOf<ec.edu.monster.model.Cliente>()
        try {
            val factory = XmlPullParserFactory.newInstance()
            val parser = factory.newPullParser()
            parser.setInput(StringReader(xml))
            var eventType = parser.eventType
            var current = ec.edu.monster.model.Cliente()
            var currentTag = ""

            while (eventType != XmlPullParser.END_DOCUMENT) {
                when (eventType) {
                    XmlPullParser.START_TAG -> {
                        currentTag = parser.name.substringAfter(":")
                        if (currentTag == "cliente" || currentTag == "return") current = ec.edu.monster.model.Cliente()
                    }
                    XmlPullParser.TEXT -> {
                        val text = parser.text?.trim() ?: ""
                        if (text.isNotEmpty()) {
                            when (currentTag) {
                                "idCliente" -> current.idCliente = text
                                "nombres" -> current.nombres = text
                                "correo" -> current.email = text
                                "telefono" -> current.telefono = text
                            }
                        }
                    }
                    XmlPullParser.END_TAG -> {
                        val tagName = parser.name.substringAfter(":")
                        if ((tagName == "cliente" || tagName == "return") && current.idCliente.isNotEmpty()) {
                            list.add(current)
                        }
                        currentTag = ""
                    }
                }
                eventType = parser.next()
            }
        } catch (e: Exception) { e.printStackTrace() }
        return list
    }



    private fun parseValidarIngresoResponse(xml: String): Boolean {
        return try {
            val factory = XmlPullParserFactory.newInstance()
            val parser = factory.newPullParser()
            parser.setInput(StringReader(xml))
            var eventType = parser.eventType
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG && parser.name.substringAfter(":") == "return") {
                    parser.next()
                    return parser.text?.trim()?.equals("Exitoso", ignoreCase = true) ?: false
                }
                eventType = parser.next()
            }
            false
        } catch (e: Exception) {
            false
        }
    }

    private fun parsePartidosResponse(xml: String): List<PartidoFutbol> {
        val partidos = mutableListOf<PartidoFutbol>()
        try {
            val factory = XmlPullParserFactory.newInstance()
            val parser = factory.newPullParser()
            parser.setInput(StringReader(xml))
            
            var eventType = parser.eventType
            var currentPartido = PartidoFutbol()
            var currentTag = ""
            val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())

            while (eventType != XmlPullParser.END_DOCUMENT) {
                when (eventType) {
                    XmlPullParser.START_TAG -> {
                        currentTag = parser.name.substringAfter(":")
                        if (currentTag == "partido" || currentTag == "return") {
                            currentPartido = PartidoFutbol()
                        }
                    }
                    XmlPullParser.TEXT -> {
                        val text = parser.text?.trim() ?: ""
                        if (text.isNotEmpty()) {
                            when (currentTag) {
                                "codigo" -> currentPartido.codigo = text
                                "equipoLocal" -> currentPartido.equipoLocal = text
                                "equipoVisitante" -> currentPartido.equipoVisitante = text
                                "equipoVistita" -> {
                                    if (currentPartido.equipoVisitante.isBlank()) {
                                        currentPartido.equipoVisitante = text
                                    }
                                }
                                "fecha" -> {
                                    try {
                                        currentPartido.fecha = dateFormat.parse(text) ?: Date()
                                    } catch (e: Exception) { }
                                }
                                "lugar" -> currentPartido.lugar = text
                            }
                        }
                    }
                    XmlPullParser.END_TAG -> {
                        val tagName = parser.name.substringAfter(":")
                        if ((tagName == "partido" || tagName == "return") && currentPartido.codigo.isNotEmpty()) {
                            partidos.add(currentPartido)
                        }
                        currentTag = ""
                    }
                }
                eventType = parser.next()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return partidos
    }

    private fun parseLocalidadesResponse(xml: String): List<LocalidadPartido> {
        val localidades = mutableListOf<LocalidadPartido>()
        try {
            val factory = XmlPullParserFactory.newInstance()
            val parser = factory.newPullParser()
            parser.setInput(StringReader(xml))
            
            var eventType = parser.eventType
            var current = LocalidadPartido()
            var currentTag = ""

            while (eventType != XmlPullParser.END_DOCUMENT) {
                when (eventType) {
                    XmlPullParser.START_TAG -> {
                        currentTag = parser.name.substringAfter(":")
                        if (currentTag == "localidad" || currentTag == "return") {
                            current = LocalidadPartido()
                        }
                    }
                    XmlPullParser.TEXT -> {
                        val text = parser.text?.trim() ?: ""
                        if (text.isNotEmpty()) {
                            when (currentTag) {
                                "codigoLocalidad" -> current.codigoLocalidad = text
                                "codigoPartido" -> current.codigoPartido = text
                                "disponibilidad" -> current.disponibilidad = text.toIntOrNull() ?: 0
                                "precio" -> current.precio = text.toDoubleOrNull() ?: 0.0
                            }
                        }
                    }
                    XmlPullParser.END_TAG -> {
                        val tagName = parser.name.substringAfter(":")
                        if ((tagName == "localidad" || tagName == "return") && current.codigoLocalidad.isNotEmpty()) {
                            localidades.add(current)
                        }
                        currentTag = ""
                    }
                }
                eventType = parser.next()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return localidades
    }

    private fun parseAsientosOcupadosResponse(xml: String): List<ec.edu.monster.model.AsientoOcupado> {
        val list = mutableListOf<ec.edu.monster.model.AsientoOcupado>()
        try {
            val factory = XmlPullParserFactory.newInstance()
            val parser = factory.newPullParser()
            parser.setInput(StringReader(xml))
            var eventType = parser.eventType
            var current = ec.edu.monster.model.AsientoOcupado()
            var currentTag = ""

            while (eventType != XmlPullParser.END_DOCUMENT) {
                when (eventType) {
                    XmlPullParser.START_TAG -> {
                        currentTag = parser.name.substringAfter(":")
                        if (currentTag == "return" || currentTag == "asientoOcupado") {
                            current = ec.edu.monster.model.AsientoOcupado()
                        }
                    }
                    XmlPullParser.TEXT -> {
                        val text = parser.text?.trim() ?: ""
                        if (text.isNotEmpty()) {
                            when (currentTag) {
                                "codigoLocalidad" -> current.loc = text
                                "cantidad" -> current.cant = text.toIntOrNull() ?: 0
                                "comprador" -> current.comprador = text
                                "fechaCompra" -> current.fecha = text
                            }
                        }
                    }
                    XmlPullParser.END_TAG -> {
                        val tagName = parser.name.substringAfter(":")
                        if ((tagName == "return" || tagName == "asientoOcupado") && current.loc.isNotEmpty()) {
                            list.add(current)
                        }
                        currentTag = ""
                    }
                }
                eventType = parser.next()
            }
        } catch (e: Exception) { e.printStackTrace() }
        return list
    }

    private fun parseFacturasResponse(xml: String): List<ec.edu.monster.model.Factura> {
        val list = mutableListOf<ec.edu.monster.model.Factura>()
        try {
            val factory = XmlPullParserFactory.newInstance()
            val parser = factory.newPullParser()
            parser.setInput(StringReader(xml))
            var eventType = parser.eventType
            var current = ec.edu.monster.model.Factura()
            var currentTag = ""
            val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())

            while (eventType != XmlPullParser.END_DOCUMENT) {
                when (eventType) {
                    XmlPullParser.START_TAG -> {
                        currentTag = parser.name.substringAfter(":")
                        if (currentTag == "return" || currentTag == "factura") current = ec.edu.monster.model.Factura()
                    }
                    XmlPullParser.TEXT -> {
                        val text = parser.text?.trim() ?: ""
                        if (text.isNotEmpty()) {
                            when (currentTag) {
                                "idFactura" -> current.idFactura = text.toLongOrNull() ?: 0L
                                "idCliente" -> current.idCliente = text
                                "vendedor" -> current.vendedor = text
                                "subtotal" -> current.subtotal = text.toDoubleOrNull() ?: 0.0
                                "iva" -> current.iva = text.toDoubleOrNull() ?: 0.0
                                "total" -> current.total = text.toDoubleOrNull() ?: 0.0
                                "estado" -> current.estado = text.toIntOrNull() ?: 0
                                "fechaEmision" -> current.fechaEmision = try { dateFormat.parse(text) } catch(e: Exception) { null }
                            }
                        }
                    }
                    XmlPullParser.END_TAG -> {
                        val tagName = parser.name.substringAfter(":")
                        if ((tagName == "return" || tagName == "factura") && current.idFactura > 0) list.add(current)
                        currentTag = ""
                    }
                }
                eventType = parser.next()
            }
        } catch (e: Exception) { e.printStackTrace() }
        return list
    }

    private fun parseDetallesFacturaResponse(xml: String): List<ec.edu.monster.model.DetalleFactura> {
        val list = mutableListOf<ec.edu.monster.model.DetalleFactura>()
        try {
            val factory = XmlPullParserFactory.newInstance()
            val parser = factory.newPullParser()
            parser.setInput(StringReader(xml))
            var eventType = parser.eventType
            var current = ec.edu.monster.model.DetalleFactura()
            var currentTag = ""

            while (eventType != XmlPullParser.END_DOCUMENT) {
                when (eventType) {
                    XmlPullParser.START_TAG -> {
                        currentTag = parser.name.substringAfter(":")
                        if (currentTag == "return" || currentTag == "detalle") current = ec.edu.monster.model.DetalleFactura()
                    }
                    XmlPullParser.TEXT -> {
                        val text = parser.text?.trim() ?: ""
                        if (text.isNotEmpty()) {
                            when (currentTag) {
                                "idDetalle" -> current.idDetalle = text.toLongOrNull() ?: 0L
                                "codigoPartido" -> current.codigoPartido = text
                                "idFactura" -> current.idFactura = text.toLongOrNull() ?: 0L
                                "codigoLocalidad" -> current.codigoLocalidad = text
                                "cantidad" -> current.cantidad = text.toIntOrNull() ?: 0
                                "precioUnitario" -> current.precioUnitario = text.toDoubleOrNull() ?: 0.0
                                "totalDetalle" -> current.totalDetalle = text.toDoubleOrNull() ?: 0.0
                            }
                        }
                    }
                    XmlPullParser.END_TAG -> {
                        val tagName = parser.name.substringAfter(":")
                        if ((tagName == "return" || tagName == "detalle") && current.codigoLocalidad.isNotEmpty()) list.add(current)
                        currentTag = ""
                    }
                }
                eventType = parser.next()
            }
        } catch (e: Exception) { e.printStackTrace() }
        return list
    }

    private fun parseAmortizacionesResponse(xml: String): List<ec.edu.monster.model.Amortizacion> {
        val list = mutableListOf<ec.edu.monster.model.Amortizacion>()
        try {
            val factory = XmlPullParserFactory.newInstance()
            val parser = factory.newPullParser()
            parser.setInput(StringReader(xml))
            var eventType = parser.eventType
            var current = ec.edu.monster.model.Amortizacion()
            var currentTag = ""
            val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())

            while (eventType != XmlPullParser.END_DOCUMENT) {
                when (eventType) {
                    XmlPullParser.START_TAG -> {
                        currentTag = parser.name.substringAfter(":")
                        if (currentTag == "return" || currentTag == "amortizacion") current = ec.edu.monster.model.Amortizacion()
                    }
                    XmlPullParser.TEXT -> {
                        val text = parser.text?.trim() ?: ""
                        if (text.isNotEmpty()) {
                            when (currentTag) {
                                "numeroCuota" -> current.numCuota = text.toIntOrNull() ?: 0
                                "idAmortizacion" -> current.numCredito = text.toIntOrNull() ?: 0
                                "idClienteCredito" -> current.idCliente = text
                                "capital" -> current.capitalCuota = text.toDoubleOrNull() ?: 0.0
                                "interes" -> current.interesCuota = text.toDoubleOrNull() ?: 0.0
                                "montoCuota" -> current.valorCuota = text.toDoubleOrNull() ?: 0.0
                                "saldo" -> current.saldo = text.toDoubleOrNull() ?: 0.0
                                "estadoCuota" -> current.estado = if (text == "PAGADO") 1 else 0
                                "fechaVencimiento" -> current.fechaPago = try { dateFormat.parse(text) } catch(e: Exception) { null }
                            }
                        }
                    }
                    XmlPullParser.END_TAG -> {
                        val tagName = parser.name.substringAfter(":")
                        if ((tagName == "return" || tagName == "amortizacion") && current.numCuota > 0) list.add(current)
                        currentTag = ""
                    }
                }
                eventType = parser.next()
            }
        } catch (e: Exception) { e.printStackTrace() }
        return list
    }

    private fun parseCompraResponse(xml: String): CompraResultado {
        val res = CompraResultado()
        try {
            val factory = XmlPullParserFactory.newInstance()
            val parser = factory.newPullParser()
            parser.setInput(StringReader(xml))
            
            var eventType = parser.eventType
            var currentTag = ""

            while (eventType != XmlPullParser.END_DOCUMENT) {
                when (eventType) {
                    XmlPullParser.START_TAG -> {
                        currentTag = parser.name.substringAfter(":")
                    }
                    XmlPullParser.TEXT -> {
                        val text = parser.text?.trim() ?: ""
                        if (text.isNotEmpty()) {
                            when (currentTag) {
                                "estado" -> res.estado = text.toIntOrNull() ?: -1
                                "mensaje" -> res.mensaje = text
                                "facturaId" -> res.facturaId = text.toLongOrNull() ?: 0L
                                "subtotal" -> res.subtotal = text.toDoubleOrNull() ?: 0.0
                                "iva" -> res.iva = text.toDoubleOrNull() ?: 0.0
                                "total" -> res.total = text.toDoubleOrNull() ?: 0.0
                            }
                        }
                    }
                    XmlPullParser.END_TAG -> {
                        currentTag = ""
                    }
                }
                eventType = parser.next()
            }
        } catch (e: Exception) {
            res.estado = -1
            res.mensaje = "Error parseando XML"
            e.printStackTrace()
        }
        return res
    }

    private fun parseResumenResponse(xml: String): List<ResumenVentaLocalidad> {
        val resumenes = mutableListOf<ResumenVentaLocalidad>()
        try {
            val factory = XmlPullParserFactory.newInstance()
            val parser = factory.newPullParser()
            parser.setInput(StringReader(xml))
            
            var eventType = parser.eventType
            var current = ResumenVentaLocalidad()
            var currentTag = ""

            while (eventType != XmlPullParser.END_DOCUMENT) {
                when (eventType) {
                    XmlPullParser.START_TAG -> {
                        currentTag = parser.name.substringAfter(":")
                        if (currentTag == "resumen" || currentTag == "return") {
                            current = ResumenVentaLocalidad()
                        }
                    }
                    XmlPullParser.TEXT -> {
                        val text = parser.text?.trim() ?: ""
                        if (text.isNotEmpty()) {
                            when (currentTag) {
                                "codigoLocalidad" -> current.codigoLocalidad = text
                                "vendidos" -> current.vendidos = text.toIntOrNull() ?: 0
                                "totalRecaudado" -> current.totalRecaudado = text.toDoubleOrNull() ?: 0.0
                            }
                        }
                    }
                    XmlPullParser.END_TAG -> {
                        val tagName = parser.name.substringAfter(":")
                        if ((tagName == "resumen" || tagName == "return") && current.codigoLocalidad.isNotEmpty()) {
                            resumenes.add(current)
                        }
                        currentTag = ""
                    }
                }
                eventType = parser.next()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return resumenes
    }
}
