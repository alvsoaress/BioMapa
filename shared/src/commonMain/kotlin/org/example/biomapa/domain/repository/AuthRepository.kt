package org.example.biomapa.domain.repository

interface AuthRepository {
    fun estaLogado(): Boolean

    // Id da pessoa logada; null quando não há sessão.
    fun usuarioAtualId(): String?

    suspend fun entrar(
        email: String,
        senha: String,
    )

    suspend fun criarConta(
        nome: String,
        sobrenome: String,
        email: String,
        senha: String,
    )

    suspend fun sair()
}
