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
    private val baseUrlTicketPremium = "http://10.40.69.25:8080/WS_EurekaBank_Server/WSTicketPremium"
    private val baseUrlFederacion = "http://10.40.69.25:8080/WS_EurekaBank_Server/WSFederacion"

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
            var p = password.trim().uppercase()

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

    suspend fun comprarBoleto(codigoPartido: String, codigoLocalidad: String, cantidad: Int, cliente: String): CompraResultado = withContext(Dispatchers.IO) {
        try {
            val soapEnvelope = """
                <?xml version="1.0" encoding="UTF-8"?>
                <S:Envelope xmlns:S="http://schemas.xmlsoap.org/soap/envelope/">
                    <S:Body>
                        <ns2:comprarBoleto xmlns:ns2="${NAMESPACE}">
                            <codigoPartido>${escapeXml(codigoPartido)}</codigoPartido>
                            <codigoLocalidad>${escapeXml(codigoLocalidad)}</codigoLocalidad>
                            <cantidad>$cantidad</cantidad>
                            <cliente>${escapeXml(cliente)}</cliente>
                        </ns2:comprarBoleto>
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

    // ==================== PARSERS ====================

    private fun parseValidarIngresoResponse(xml: String): Boolean {
        return try {
            val factory = XmlPullParserFactory.newInstance()
            val parser = factory.newPullParser()
            parser.setInput(StringReader(xml))
            var eventType = parser.eventType
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG && parser.name == "return") {
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
                        currentTag = parser.name
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
                        if ((parser.name == "partido" || parser.name == "return") && currentPartido.codigo.isNotEmpty()) {
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
                        currentTag = parser.name
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
                        if ((parser.name == "localidad" || parser.name == "return") && current.codigoLocalidad.isNotEmpty()) {
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
                        currentTag = parser.name
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
                        currentTag = parser.name
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
                        if ((parser.name == "resumen" || parser.name == "return") && current.codigoLocalidad.isNotEmpty()) {
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
