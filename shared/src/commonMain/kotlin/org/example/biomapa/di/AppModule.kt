package org.example.biomapa.di

import org.example.biomapa.data.remote.SupabaseAuthRepository
import org.example.biomapa.data.remote.SupabasePlantaRepository
import org.example.biomapa.data.remote.criarSupabaseClient
import org.example.biomapa.domain.repository.AuthRepository
import org.example.biomapa.domain.repository.PlantaRepository
import org.koin.core.context.startKoin
import org.koin.dsl.module
import org.koin.mp.KoinPlatform

val appModule =
    module {
        single { criarSupabaseClient() }
        single<PlantaRepository> { SupabasePlantaRepository(get()) }
        single<AuthRepository> { SupabaseAuthRepository(get()) }
    }

// Chamado no ponto de entrada de cada plataforma; no Android a Activity pode ser recriada, por isso a checagem.
fun iniciarKoin() {
    if (KoinPlatform.getKoinOrNull() == null) {
        startKoin { modules(appModule) }
    }
}
