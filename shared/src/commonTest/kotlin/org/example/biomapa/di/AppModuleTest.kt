package org.example.biomapa.di

import org.example.biomapa.data.remote.SupabaseAuthRepository
import org.example.biomapa.data.remote.SupabasePlantaRepository
import org.example.biomapa.data.remote.criarSupabaseClient
import org.example.biomapa.domain.repository.AuthRepository
import org.example.biomapa.domain.repository.PlantaRepository
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import kotlin.test.Test
import kotlin.test.assertIs

class AppModuleTest {
    // Substitui o cliente para o teste não depender das chaves do local.properties (vazias no CI).
    private val clienteDeTeste =
        module {
            single { criarSupabaseClient(url = "https://teste.supabase.co", chave = "chave-de-teste") }
        }

    @Test
    fun moduloMontaTodosOsRepositorios() {
        val koin = koinApplication { modules(appModule, clienteDeTeste) }.koin

        assertIs<SupabasePlantaRepository>(koin.get<PlantaRepository>())
        assertIs<SupabaseAuthRepository>(koin.get<AuthRepository>())
    }
}
