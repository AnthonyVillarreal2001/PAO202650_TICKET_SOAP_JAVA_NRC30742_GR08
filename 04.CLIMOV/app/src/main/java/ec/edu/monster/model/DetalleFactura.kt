package ec.edu.monster.model

data class DetalleFactura(
    var idDetalle: Long = 0,
    var codigoPartido: String = "",
    var idFactura: Long = 0,
    var codigoLocalidad: String = "",
    var cantidad: Int = 0,
    var precioUnitario: Double = 0.0,
    var totalDetalle: Double = 0.0
)
