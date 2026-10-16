package br.ufrn.biomapa.domain

// Entidade principal do domínio
data class Planta(
    val id: String,
    val nome: String,
    val descricao: String,
    val propriedadesMedicinais: String,
    val fotoUrl: String?,
    val latitude: Double,
    val longitude: Double,
    val autorId: String
)

// Entidade Usuário
data class Usuario(
    val id: String,
    val nome: String,
    val email: String
)
