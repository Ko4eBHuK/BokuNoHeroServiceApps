package com.preachooda.academyapp.di

import com.preachooda.academyapp.BuildConfig
import com.preachooda.academyapp.section.application.network.ApplicationService
import com.preachooda.academyapp.section.applications.network.ApplicationsService
import com.preachooda.academyapp.section.license.network.LicenseService
import com.preachooda.academyapp.section.login.network.LoginService
import com.preachooda.academyapp.section.qualificationExam.network.QualificationExamService
import com.preachooda.academyapp.utils.SystemRepository
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
    fun provideLicenseService(retrofit: Retrofit): LicenseService =
        retrofit.create(LicenseService::class.java)

    @Provides
    @Singleton
    fun provideAdmissionsService(retrofit: Retrofit): ApplicationsService =
        retrofit.create(ApplicationsService::class.java)

    @Provides
    @Singleton
    fun provideAdmissionService(retrofit: Retrofit): ApplicationService =
        retrofit.create(ApplicationService::class.java)

    @Provides
    @Singleton
    fun provideQualificationExamService(retrofit: Retrofit): QualificationExamService =
        retrofit.create(QualificationExamService::class.java)
}