package com.pichurchyk.budgetsaver

import android.content.Context
import android.app.Application
import com.pichurchyk.budgetsaver.data.di.supabaseModule
import com.pichurchyk.budgetsaver.di.initKoin
import com.pichurchyk.budgetsaver.di.mainModule
import org.koin.dsl.module

class Application : Application() {
    private var appContext: Context? = null

    override fun onCreate() {
        super.onCreate()
        appContext = this

        initKoin(
            mainModule,
            androidModule,
            supabaseModule
        )
    }

    private val androidModule = module {
        single { this@Application.appContext }
    }
}