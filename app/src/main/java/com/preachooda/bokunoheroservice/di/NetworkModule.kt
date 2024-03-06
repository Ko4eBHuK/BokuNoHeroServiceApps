package com.preachooda.bokunoheroservice.di

import com.preachooda.assets.util.AuthJwtInterceptor
import com.preachooda.bokunoheroservice.BuildConfig
import com.preachooda.bokunoheroservice.section.academyApplication.network.AcademyApplicationService
import com.preachooda.bokunoheroservice.section.home.network.HomeService
import com.preachooda.bokunoheroservice.section.login.network.LoginService
import com.preachooda.bokunoheroservice.section.newticket.network.NewTicketService
import com.preachooda.bokunoheroservice.section.tickets.network.TicketsService
import com.preachooda.bokunoheroservice.section.ticket.network.TicketService
import com.preachooda.bokunoheroservice.utils.SystemRepository
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
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)

        val authInterceptor = AuthJwtInterceptor(
            obtainToken = { systemRepository.getUserToken() ?: "" }
        )

        return OkHttpClient.Builder()
            .connectTimeout(5, TimeUnit.MINUTES)
            .callTimeout(5, TimeUnit.MINUTES)
            .readTimeout(5, TimeUnit.MINUTES)
            .writeTimeout(5, TimeUnit.MINUTES)
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
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
    fun provideNewTicketService(retrofit: Retrofit): NewTicketService {
        return retrofit.create(NewTicketService::class.java)
    }

    @Provides
    @Singleton
    fun provideTicketService(retrofit: Retrofit): TicketService {
        return retrofit.create(TicketService::class.java)
    }

    @Provides
    @Singleton
    fun provideTicketsService(retrofit: Retrofit): TicketsService {
        return retrofit.create(TicketsService::class.java)
    }

    @Provides
    @Singleton
    fun provideHomeService(retrofit: Retrofit): HomeService {
        return retrofit.create(HomeService::class.java)
    }

    @Provides
    @Singleton
    fun provideLoginService(retrofit: Retrofit): LoginService {
        return retrofit.create(LoginService::class.java)
    }

    @Provides
    @Singleton
    fun provideAcademyApplicationService(retrofit: Retrofit): AcademyApplicationService {
        return retrofit.create(AcademyApplicationService::class.java)
    }
}
