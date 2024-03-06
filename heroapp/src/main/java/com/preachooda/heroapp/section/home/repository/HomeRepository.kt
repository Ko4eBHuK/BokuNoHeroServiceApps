package com.preachooda.heroapp.section.home.repository

import com.preachooda.heroapp.utils.SystemRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HomeRepository @Inject constructor(
    private val systemRepository: SystemRepository
) {
    fun logout() {
        systemRepository.clearHeroToken()
    }
}
