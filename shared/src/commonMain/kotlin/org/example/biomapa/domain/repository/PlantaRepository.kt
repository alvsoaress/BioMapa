package org.example.biomapa.domain.repository

import org.example.biomapa.domain.model.Planta

interface PlantaRepository {
    suspend fun listar(): List<Planta>

    suspend fun buscarPorId(id: Int): Planta?

    suspend fun cadastrar(planta: Planta)
}
