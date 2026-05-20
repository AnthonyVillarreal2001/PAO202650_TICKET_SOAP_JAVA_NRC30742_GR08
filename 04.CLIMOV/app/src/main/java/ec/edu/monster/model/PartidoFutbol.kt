package ec.edu.monster.model

import java.util.Date

data class PartidoFutbol(
    var codigo: String = "",
    var equipoLocal: String = "",
    var equipoVisitante: String = "",
    var fecha: Date = Date(),
    var lugar: String = ""
) {
    val descripcion: String
        get() = "$equipoLocal vs $equipoVisitante"
}
