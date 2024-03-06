package com.preachooda.academyapp.section.home.repository

import com.preachooda.academyapp.utils.SystemRepository
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
