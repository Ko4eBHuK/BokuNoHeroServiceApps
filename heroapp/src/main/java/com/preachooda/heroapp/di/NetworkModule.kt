package com.preachooda.heroapp.di

import com.preachooda.assets.util.AuthJwtInterceptor
import com.preachooda.heroapp.BuildConfig
import com.preachooda.heroapp.section.login.network.LoginService
import com.preachooda.heroapp.section.patrolling.network.PatrollingService
import com.preachooda.heroapp.section.ticket.network.TicketService
import com.preachooda.heroapp.utils.SystemRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
open class NetworkModule {
    open fun baseUrl() = BuildConfig.API_URL

    @Provides
    @Singleton
    fun provideOkHttpClient(systemRepository: SystemRepository): OkHttpClient {
        val loggingInterceptorBody = HttpLoggingInterceptor()
        loggingInterceptorBody.setLevel(HttpLoggingInterceptor.Level.BODY)

        val authInterceptor = AuthJwtInterceptor(
            obtainToken = { systemRepository.getHeroToken() ?: "" }
        )

        return OkHttpClient.Builder()
            .connectTimeout(1, TimeUnit.MINUTES)
            .callTimeout(1, TimeUnit.MINUTES)
            .readTimeout(1, TimeUnit.MINUTES)
            .writeTimeout(1, TimeUnit.MINUTES)
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptorBody)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(baseUrl())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideLoginService(retrofit: Retrofit): LoginService =
        retrofit.create(LoginService::class.java)

    @Provides
    @Singleton
    fun provideTicketService(retrofit: Retrofit): TicketService =
        retrofit.create(TicketService::class.java)

    @Provides
    @Singleton
    fun providePatrolService(retrofit: Retrofit): PatrollingService =
        retrofit.create(PatrollingService::class.java)
}
