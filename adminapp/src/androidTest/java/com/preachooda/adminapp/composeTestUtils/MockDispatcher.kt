package com.preachooda.adminapp.composeTestUtils

import com.google.gson.Gson
import com.preachooda.domain.model.ActivityStatus
import com.preachooda.domain.model.Hero
import com.preachooda.domain.model.LicenseApplication
import com.preachooda.domain.model.Patrol
import com.preachooda.domain.model.QualificationExamApplication
import com.preachooda.domain.model.Ticket
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.RecordedRequest

object MockDispatcher : Dispatcher() {
    override fun dispatch(request: RecordedRequest): MockResponse {
        return when(request.path) {
            "/heroes/free" -> {
                when (request.method) {
                    "GET" -> MockResponse().setResponseCode(200).setBody(Gson().toJson(mockHero))
                    else -> MockResponse().setResponseCode(501)
                }
            }
            "/license-tickets" -> {
                when (request.method) {
                    "GET" -> MockResponse().setResponseCode(200).setBody(Gson().toJson(listOf(mockLicenseApp)))
                    else -> MockResponse().setResponseCode(501)
                }
            }
            "/license-tickets/1122" -> {
                when (request.method) {
                    "GET" -> MockResponse().setResponseCode(200).setBody(Gson().toJson(mockLicenseApp))
                    "PUT" -> MockResponse().setResponseCode(200).setBody(request.body)
                    else -> MockResponse().setResponseCode(501)
                }
            }
            "/heroes" -> {
                when (request.method) {
                    "GET" -> MockResponse().setResponseCode(200).setBody(Gson().toJson(mockHero))
                    else -> MockResponse().setResponseCode(501)
                }
            }
            "/heroes/1" -> {
                when (request.method) {
                    "GET" -> MockResponse().setResponseCode(200).setBody(Gson().toJson(mockOneHero))
                    "PUT" -> MockResponse().setResponseCode(200).setBody(request.body)
                    else -> MockResponse().setResponseCode(501)
                }
            }
            "/tickets" -> {
                when (request.method) {
                    "GET" -> MockResponse().setResponseCode(200).setBody(Gson().toJson(listOf( mockTicketCreated)))
                    else -> MockResponse().setResponseCode(501)
                }
            }
            "/tickets/111000111" -> {
                when (request.method) {
                    "GET" -> MockResponse().setResponseCode(200).setBody(Gson().toJson(mockTicketCreated))
                    "PUT" -> MockResponse().setResponseCode(200).setBody(request.body)
                    else -> MockResponse().setResponseCode(501)
                }
            }
            "/district-patrollings" -> {
                when (request.method) {
                    "POST" -> MockResponse().setResponseCode(200)
                        .setBody(Gson().toJson(mockPatrol))
                    else -> MockResponse().setResponseCode(501)
                }
            }
            "/qualification-exams" -> {
                when (request.method) {
                    "GET" -> MockResponse().setResponseCode(200).setBody(Gson().toJson(listOf(mockCE)))
                    else -> MockResponse().setResponseCode(501)
                }
            }
            "/qualification-exams/112233" -> {
                when (request.method) {
                    "GET" -> MockResponse().setResponseCode(200).setBody(Gson().toJson(mockCE))
                    "PUT" -> MockResponse().setResponseCode(200).setBody(request.body)
                    else -> MockResponse().setResponseCode(501)
                }
            }
            else -> MockResponse().setResponseCode(404)
        }
    }

    val mockHero = listOf(
        Hero(1, 1, "Hero_1"),
        Hero(2, 2, "Hero_2"),
    )

    val mockOneHero = Hero(1, 1, "Hero_1")

    val mockTicketCreated = Ticket(
        id = 111000111,
        heroes = listOf(
            Hero(1, 1, "Hero_1"),
            Hero(2, 2, "Hero_2"),
        ),
        status = ActivityStatus.CREATED,
        description = "Описание заявки"
    )

    val mockLicenseApp = LicenseApplication(
        id = 1122,
        heroName = "Hero_1",
        quirk = "МПИ",
        birthDate = "03.03.2003",
        educationDocumentNumber = "87346",
        status= ActivityStatus.CREATED
    )
    val mockPatrol = Patrol(
        1122,
        "Хокайдо",
        heroes = listOf(Hero(1, 1, "Hero_1")),
        status = Patrol.Status.PENDING,
        "11:00",
        "12:00"
    )
    val mockCE = QualificationExamApplication(
        112233,
        1,
        Hero(0, 0, "Hero_0"),
        ActivityStatus.CREATED
    )
}
