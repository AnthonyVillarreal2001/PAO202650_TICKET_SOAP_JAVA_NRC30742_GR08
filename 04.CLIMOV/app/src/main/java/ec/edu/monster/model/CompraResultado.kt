package ec.edu.monster.model

data class CompraResultado(
    var estado: Int = -1,
    var mensaje: String = "",
    var facturaId: Long = 0L,
    var subtotal: Double = 0.0,
    var iva: Double = 0.0,
    var total: Double = 0.0
)
