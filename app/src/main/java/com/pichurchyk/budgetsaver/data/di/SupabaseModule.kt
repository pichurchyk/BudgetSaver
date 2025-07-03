package com.pichurchyk.budgetsaver.data.di

import com.pichurchyk.budgetsaver.data.model.payload.RefreshTokenPayload
import com.pichurchyk.budgetsaver.data.model.response.RefreshTokenResponse
import com.pichurchyk.budgetsaver.data.preferences.AuthPreferencesActions
import com.pichurchyk.budgetsaver.BuildConfig
import com.pichurchyk.budgetsaver.di.ApiErrorResponse
import com.pichurchyk.budgetsaver.di.DomainException
import com.pichurchyk.budgetsaver.di.DomainException.ApiSpecificException
import io.github.jan.supabase.SupabaseClient
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.RedirectResponseException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.observer.ResponseObserver
import io.ktor.client.plugins.resources.Resources
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import org.koin.dsl.module

val supabaseModule = module {
    single<SupabaseClient> { customSupabaseClient }

    single {
        val httpLogger = co.touchlab.kermit.Logger.withTag("SUPABASE")

        val authPreferences: AuthPreferencesActions = get()

        var accessToken = runBlocking { authPreferences.getAccessToken() }

        HttpClient {
            expectSuccess = true

            install(ContentNegotiation) {
                json(
                    Json {
                        ignoreUnknownKeys = true
                    }
                )
            }

            install(Resources)

            Logging {
                logger = object : Logger {
                    override fun log(message: String) {
                        httpLogger.i { message }
                    }
                }

                level = LogLevel.ALL
            }

            install(Auth) {
                bearer {
                    loadTokens {
                        val accessToken = authPreferences.getAccessToken()
                        val refreshToken = authPreferences.getRefreshToken()

                        if (accessToken.isEmpty() && refreshToken.isEmpty()) {
                            return@loadTokens null
                        }
                        BearerTokens(
                            accessToken,
                            refreshToken
                        )
                    }

                    refreshTokens {
                        try {
                            val currentRefreshToken = authPreferences.getRefreshToken()

                            val refreshUrl = "${BuildConfig.SUPABASE_URL}/auth/v1/token?grant_type=refresh_token"
                            val refreshTokenResponse: RefreshTokenResponse = client
                                .post(refreshUrl) {
                                    markAsRefreshTokenRequest()
                                    setBody(
                                        RefreshTokenPayload(currentRefreshToken)
                                    )
                                    header(HttpHeaders.ContentType, "application/json")
                                }.body()

                            authPreferences.setAccessToken(refreshTokenResponse.accessToken)
                            authPreferences.setRefreshToken(refreshTokenResponse.refreshToken)

                            accessToken = authPreferences.getAccessToken()

                            BearerTokens(
                                refreshTokenResponse.accessToken,
                                refreshTokenResponse.refreshToken
                            )

                        } catch (e: Exception) {
                            e.printStackTrace()
                            null
                        }
                    }
                }
            }

            HttpResponseValidator {
                handleResponseExceptionWithRequest { exception, request ->
                    when (exception) {
                        is ClientRequestException -> {
                            val response = exception.response
                            val errorBody = try {
                                response.body<ApiErrorResponse>()
                            } catch (e: Exception) {
                                null
                            }

                            when (response.status) {
                                HttpStatusCode.Unauthorized -> throw DomainException.UnauthorizedException(
                                    cause = exception
                                )

                                HttpStatusCode.BadRequest -> throw DomainException.BadRequestException(
                                    apiErrorMessage = errorBody?.message,
                                    cause = exception
                                )
                                else -> throw DomainException.UnknownApiException(cause = exception)
                            }
                        }

                        is ServerResponseException -> {
                            throw DomainException.ServerErrorException(cause = exception)
                        }

                        is RedirectResponseException -> {
                            throw exception
                        }

                        is java.net.UnknownHostException, is java.net.SocketTimeoutException, is java.io.IOException -> {
                            throw DomainException.NetworkConnectionException(cause = exception)
                        }

                        else -> {
                            throw DomainException.UnknownApiException(cause = exception)
                        }
                    }
                }
            }

            ResponseObserver { response ->
                httpLogger.d { "HTTP status: ${response.status.value}" }
            }

            defaultRequest {
                url(BuildConfig.SUPABASE_URL)
                header("apikey", BuildConfig.SUPABASE_ANON_KEY)
                header(HttpHeaders.Authorization, "Bearer $accessToken")
                header(HttpHeaders.ContentType, "application/json")
            }
        }
    }
}