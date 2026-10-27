package org.example.biomapa.presentation

import org.example.biomapa.domain.model.Planta
import org.example.biomapa.domain.repository.AuthRepository
import org.example.biomapa.domain.repository.PlantaRepository

// Repositórios sem rede, só para as @Preview das telas que recebem repositório.
internal const val PREVIEW_USER_ID = "preview"

internal object AuthRepositoryPreview : AuthRepository {
    override fun estaLogado() = true

    override fun usuarioAtualId() = PREVIEW_USER_ID

    override suspend fun entrar(
        email: String,
        senha: String,
    ) = Unit

    override suspend fun criarConta(
        nome: String,
        sobrenome: String,
        email: String,
        senha: String,
    ) = Unit

    override suspend fun sair() = Unit
}

internal object PlantaRepositoryPreview : PlantaRepository {
    override suspend fun listar() = emptyList<Planta>()

    override suspend fun buscarPorId(id: Int): Planta? = null

    override suspend fun cadastrar(planta: Planta) = Unit
}
