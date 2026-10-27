package org.example.biomapa.data.remote

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.postgrest.Postgrest
import org.example.biomapa.BuildKonfig

fun criarSupabaseClient(
    url: String = BuildKonfig.SUPABASE_URL,
    chave: String = BuildKonfig.SUPABASE_ANON_KEY,
): SupabaseClient =
    createSupabaseClient(supabaseUrl = url, supabaseKey = chave) {
        install(Auth)
        install(Postgrest)
    }
