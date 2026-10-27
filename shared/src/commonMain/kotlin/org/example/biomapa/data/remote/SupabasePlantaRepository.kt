package org.example.biomapa.data.remote

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import org.example.biomapa.domain.model.Planta
import org.example.biomapa.domain.repository.PlantaRepository

private const val TABELA = "plantas"

private val COLUNAS =
    Columns.list("id", "user_id", "nome", "localizacao", "latitude", "longitude", "propriedades_medicas", "foto_url")

class SupabasePlantaRepository(
    private val client: SupabaseClient,
) : PlantaRepository {
    override suspend fun listar(): List<Planta> = client.postgrest[TABELA].select(columns = COLUNAS).decodeList<Planta>()

    override suspend fun buscarPorId(id: Int): Planta? =
        client.postgrest[TABELA]
            .select(columns = COLUNAS) {
                filter { eq("id", id) }
            }.decodeSingleOrNull<Planta>()

    override suspend fun cadastrar(planta: Planta) {
        client.postgrest[TABELA].insert(planta)
    }
}
