package org.example.biomapa.data.remote

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.builtin.Email
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import org.example.biomapa.domain.repository.AuthRepository

// Enquanto o login não está pronto, o app entra direto com um usuário fixo de desenvolvimento.
internal const val DEV_AUTH_BYPASS = true
internal const val DEV_USER_ID = "00000000-0000-0000-0000-000000000000"

class SupabaseAuthRepository(
    private val client: SupabaseClient,
    private val bypassDev: Boolean = DEV_AUTH_BYPASS,
) : AuthRepository {
    override fun estaLogado(): Boolean = bypassDev || client.auth.currentSessionOrNull() != null

    override fun usuarioAtualId(): String? = client.auth.currentUserOrNull()?.id ?: DEV_USER_ID.takeIf { bypassDev }

    override suspend fun entrar(
        email: String,
        senha: String,
    ) {
        client.auth.signInWith(Email) {
            this.email = email
            password = senha
        }
    }

    override suspend fun criarConta(
        nome: String,
        sobrenome: String,
        email: String,
        senha: String,
    ) {
        client.auth.signUpWith(Email) {
            this.email = email
            password = senha
            data =
                buildJsonObject {
                    put("nome", nome)
                    put("sobrenome", sobrenome)
                }
        }
    }

    override suspend fun sair() {
        if (!bypassDev) {
            client.auth.signOut()
        }
    }
}
