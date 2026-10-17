package org.example.biomapa

// Mensagem de erro de cada campo do cadastro de planta; null quando o campo está válido.
data class ErrosPlanta(
    val nome: String? = null,
    val localizacao: String? = null,
    val coordenadas: String? = null,
    val propriedades: String? = null,
) {
    val valido: Boolean
        get() = nome == null && localizacao == null && coordenadas == null && propriedades == null
}

fun validarPlanta(
    nome: String,
    localizacao: String,
    latitude: Double?,
    longitude: Double?,
    propriedades: String,
): ErrosPlanta =
    ErrosPlanta(
        nome = "Informe o nome da planta".takeIf { nome.isBlank() },
        localizacao = "Informe uma referência de onde ela está".takeIf { localizacao.isBlank() },
        coordenadas = "Marque a posição da planta no mapa".takeIf { latitude == null || longitude == null },
        propriedades = "Descreva as propriedades ou usos".takeIf { propriedades.isBlank() },
    )
