import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import org.example.biomapa.BuildKonfig

val supabase = createSupabaseClient(
    supabaseUrl = BuildKonfig.SUPABASE_URL,
    supabaseKey = BuildKonfig.SUPABASE_ANON_KEY
) {
    install(Auth)
}