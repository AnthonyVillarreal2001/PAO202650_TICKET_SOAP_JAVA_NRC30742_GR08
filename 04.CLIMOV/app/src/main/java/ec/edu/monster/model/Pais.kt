package ec.edu.monster.model

data class Pais(
    var codigo: String = "",
    var nombre: String = "",
    var continente: String = "",
    var capital: String = "",
    var region: String = "",
    var poblacion: Long = 0,
    var latitud: Double = 0.0,
    var longitud: Double = 0.0
)
