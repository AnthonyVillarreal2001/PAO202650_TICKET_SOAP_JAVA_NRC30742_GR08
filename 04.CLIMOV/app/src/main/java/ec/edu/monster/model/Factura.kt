package ec.edu.monster.model
import java.util.Date

data class Factura(
    var idFactura: Long = 0,
    var idCliente: String = "",
    var vendedor: String = "",
    var subtotal: Double = 0.0,
    var iva: Double = 0.0,
    var total: Double = 0.0,
    var estado: Int = 0,
    var fechaEmision: Date? = null
)
