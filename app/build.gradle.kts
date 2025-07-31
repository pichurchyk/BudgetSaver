import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.google.services)
    alias(libs.plugins.firebase.crashlytics)
    alias(libs.plugins.jetbrainsKotlinSerialization)
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "com.pichurchyk.budgetsaver"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.pichurchyk.budgetsaver"
        minSdk = 28
        targetSdk = 36
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    signingConfigs {
        create("release") {
            storeFile = file(gradleLocalProperties(rootDir).getProperty("KEYSTORE_FILE_PATH"))
            storePassword = gradleLocalProperties(rootDir).getProperty("KEYSTORE_STORE_PASSWORD")
            keyAlias = gradleLocalProperties(rootDir).getProperty("KEYSTORE_KEY_ALIAS")
            keyPassword = gradleLocalProperties(rootDir).getProperty("KEYSTORE_KEY_PASSWORD")
        }
    }

    buildTypes {
        val googleWebClientId: String = gradleLocalProperties(rootDir).getProperty("GOOGLE_WEB_CLIENT_ID")
        val supabaseAnonKey: String = gradleLocalProperties(rootDir).getProperty("SUPABASE_ANON_KEY")
        val supabaseUrl: String = gradleLocalProperties(rootDir).getProperty("SUPABASE_URL")

        getByName("debug") {
            buildConfigField("String", "GOOGLE_WEB_CLIENT_ID", "\"$googleWebClientId\"")
            buildConfigField("String", "SUPABASE_ANON_KEY", "\"$supabaseAnonKey\"")
            buildConfigField("String", "SUPABASE_URL", "\"$supabaseUrl\"")
        }

        release {
            isDebuggable = true

            signingConfig = signingConfigs.getByName("release")

            buildConfigField("String", "GOOGLE_WEB_CLIENT_ID", "\"$googleWebClientId\"")
            buildConfigField("String", "SUPABASE_ANON_KEY", "\"$supabaseAnonKey\"")
            buildConfigField("String", "SUPABASE_URL", "\"$supabaseUrl\"")

            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // Core AndroidX
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)

    // UI & Compose
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.animation)
    implementation(libs.androidx.navigation.compose) // Shared navigation
    implementation(libs.coil.compose)
    implementation(libs.coil.network.ktor2)
    implementation(libs.vico.compose)

    // Networking (Ktor)
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.auth)
    implementation(libs.ktor.client.okhttp)
    implementation(libs.ktor.client.android)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.client.logging)
    implementation(libs.ktor.client.resources)

    // Dependency Injection (Koin)
    implementation(platform(libs.koin.bom))
    implementation(libs.koin.core)
    implementation(libs.koin.compose)

    // Datastore (Shared Preferences)
    implementation(libs.androidx.datastore.preferences)

    // Testing (Shared across modules)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    // Lifecycle & Core
    implementation(libs.androidx.lifecycle.runtime.ktx)

    // Firebase (App-Specific)
    implementation(libs.firebase.crashlytics)

    // Authentication (Google Play Services)
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services)
    implementation(libs.google.id)

    // Supabase Auth (App-Specific)
    implementation(platform(libs.supabase.bom))
    implementation(libs.supabase.auth)

    // Debugging & Serialization (App-Specific)
    debugImplementation(libs.kotlinx.serialization.json)

    // Supabase SDK
    implementation(platform(libs.supabase.bom))
    implementation(libs.supabase.auth)
    implementation(libs.supabase.postgrest)

    implementation(platform(libs.koin.bom))
    implementation(libs.koin.core)

    // Ktor Client (Android-Specific)
    implementation(libs.ktor.client.android)

    // Color picker
    implementation(libs.compose.colorpicker)

    // Emojies
    implementation(libs.emojies)
}
