package com.preachooda.heroapp.composeTestUtils

import com.preachooda.heroapp.di.NetworkModule
import dagger.Module
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [NetworkModule::class]
)
class TestRetrofitModule : NetworkModule() {
    override fun baseUrl() = "http://localhost:8000"
}
