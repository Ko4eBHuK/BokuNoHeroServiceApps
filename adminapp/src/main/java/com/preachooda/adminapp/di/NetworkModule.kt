package com.preachooda.adminapp.di

import com.preachooda.adminapp.BuildConfig
import com.preachooda.adminapp.section.helpTicket.network.HelpTicketService
import com.preachooda.adminapp.section.helpTickets.network.HelpTicketsService
import com.preachooda.adminapp.section.heroRating.network.HeroRatingService
import com.preachooda.adminapp.section.heroesRatings.network.HeroesRatingsService
import com.preachooda.adminapp.section.licenseRecall.network.LicenseRecallService
import com.preachooda.adminapp.section.licenseTicket.network.LicenseTicketService
import com.preachooda.adminapp.section.licenseTickets.network.LicenseTicketsService
import com.preachooda.adminapp.section.login.network.LoginService
import com.preachooda.adminapp.section.patrollingFormation.network.PatrollingFormationService
import com.preachooda.adminapp.section.qualificationExamTicket.network.QualificationExamTicketService
import com.preachooda.adminapp.section.qualificationExamTickets.network.QualificationExamTicketsService
import com.preachooda.adminapp.utils.SystemRepository
import com.preachooda.assets.util.AuthJwtInterceptor
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
            obtainToken = { systemRepository.getUserToken() ?: "" }
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
    fun provideHelpTicketsService(retrofit: Retrofit): HelpTicketsService =
        retrofit.create(HelpTicketsService::class.java)

    @Provides
    @Singleton
    fun provideHelpTicketService(retrofit: Retrofit): HelpTicketService =
        retrofit.create(HelpTicketService::class.java)

    @Provides
    @Singleton
    fun providePatrollingFormationService(retrofit: Retrofit): PatrollingFormationService =
        retrofit.create(PatrollingFormationService::class.java)

    @Provides
    @Singleton
    fun provideQualificationExamTicketsService(retrofit: Retrofit): QualificationExamTicketsService =
        retrofit.create(QualificationExamTicketsService::class.java)

    @Provides
    @Singleton
    fun provideQualificationExamTicketService(retrofit: Retrofit): QualificationExamTicketService =
        retrofit.create(QualificationExamTicketService::class.java)

    @Provides
    @Singleton
    fun provideLicenseTicketsService(retrofit: Retrofit): LicenseTicketsService =
        retrofit.create(LicenseTicketsService::class.java)

    @Provides
    @Singleton
    fun provideLicenseTicketService(retrofit: Retrofit): LicenseTicketService =
        retrofit.create(LicenseTicketService::class.java)

    @Provides
    @Singleton
    fun provideLicenseRecallService(retrofit: Retrofit): LicenseRecallService =
        retrofit.create(LicenseRecallService::class.java)

    @Provides
    @Singleton
    fun provideHeroesRatingsService(retrofit: Retrofit): HeroesRatingsService =
        retrofit.create(HeroesRatingsService::class.java)

    @Provides
    @Singleton
    fun provideHeroRatingService(retrofit: Retrofit): HeroRatingService =
        retrofit.create(HeroRatingService::class.java)
}
