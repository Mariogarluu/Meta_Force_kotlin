package com.meta_force.meta_force.data.supabase

import com.meta_force.meta_force.BuildConfig
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.functions.Functions
import io.github.jan.supabase.storage.Storage

/**
 * Central Supabase client for the Android app.
 *
 * IMPORTANT: Only use publishable/anon keys in the client.
 */
object SupabaseProvider {
    /** Valores desde `local.properties` (supabase.url / supabase.key) vía BuildConfig. */
    val client: SupabaseClient = createSupabaseClient(
        supabaseUrl = BuildConfig.SUPABASE_URL,
        supabaseKey = BuildConfig.SUPABASE_KEY
    ) {
        install(Auth)
        install(Postgrest)
        install(Functions)
        install(Storage)
    }
}

