package com.pichurchyk.budgetsaver.data.di

import com.pichurchyk.budgetsaver.BuildConfig
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.postgrest.Postgrest

val customSupabaseClient = createSupabaseClient(
    supabaseKey = BuildConfig.SUPABASE_ANON_KEY,
    supabaseUrl = BuildConfig.SUPABASE_URL
) {
    install(Auth)
    install(Postgrest)
}