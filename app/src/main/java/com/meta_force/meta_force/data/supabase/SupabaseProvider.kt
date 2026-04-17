package com.meta_force.meta_force.data.supabase

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
    // TODO: move to BuildConfig fields if you prefer (kept inline for now to proceed quickly)
    private const val SUPABASE_URL = "https://qybgnrlszozjhimewkel.supabase.co"
    private const val SUPABASE_KEY = "sb_publishable_nnvdMyVdOClqx-9x62y_Xw_lBTl2bjI"

    val client: SupabaseClient = createSupabaseClient(
        supabaseUrl = SUPABASE_URL,
        supabaseKey = SUPABASE_KEY
    ) {
        install(Auth)
        install(Postgrest)
        install(Functions)
        install(Storage)
    }
}

