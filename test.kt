import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.gotrue.auth
fun test(supabase: SupabaseClient) {
    supabase.auth.importAuthToken("access", "refresh")
}
