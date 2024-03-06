package com.preachooda.adminapp.section.home.repository

import com.preachooda.adminapp.utils.SystemRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HomeRepository @Inject constructor(
    private val systemRepository: SystemRepository
) {
    fun logout() {
        systemRepository.clearUserToken()
    }
}
