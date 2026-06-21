package ec.edu.monster.model
import java.util.Date

data class Amortizacion(
    var numCredito: Int = 0,
    var idCliente: String = "",
    var fechaCredito: Date? = null,
    var numCuota: Int = 0,
    var fechaPago: Date? = null,
    var capitalCuota: Double = 0.0,
    var interesCuota: Double = 0.0,
    var valorCuota: Double = 0.0,
    var saldo: Double = 0.0,
    var estado: Int = 0
)
