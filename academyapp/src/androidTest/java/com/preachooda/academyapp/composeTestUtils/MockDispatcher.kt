package com.preachooda.academyapp.composeTestUtils

import com.google.gson.Gson
import com.preachooda.domain.model.Academy
import com.preachooda.domain.model.AcademyApplication
import com.preachooda.domain.model.ActivityStatus
import com.preachooda.domain.model.Hero
import com.preachooda.domain.model.QualificationExamApplication
import com.preachooda.domain.model.Ticket
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.RecordedRequest

object MockDispatcher : Dispatcher() {
    override fun dispatch(request: RecordedRequest): MockResponse {
        return when(request.path) {
            "/tickets" -> {
                when (request.method) {
                    "POST" -> MockResponse().setResponseCode(200).setBody(Gson().toJson(mockTicket))
                    else -> MockResponse().setResponseCode(501)
                }
            }
            "/heroes" -> {
                when (request.method) {
                    "GET" -> MockResponse().setResponseCode(200).setBody(Gson().toJson(mockHeroes))
                    else -> MockResponse().setResponseCode(501)
                }
            }
            "/qualification-exams" -> {
                when (request.method) {
                    "POST" -> MockResponse().setResponseCode(200).setBody(Gson().toJson(mockQualificationExamApp))
                    else -> MockResponse().setResponseCode(501)
                }
            }
            "/license-tickets" -> {
                when (request.method) {
                    "POST" -> MockResponse().setResponseCode(200).setBody(Gson().toJson(mocAcademyApp))
                    else -> MockResponse().setResponseCode(501)
                }
            }
            else -> MockResponse().setResponseCode(404)
        }
    }

    val mockTicket = Ticket(id = 111000111)
    val mockHeroes = listOf(
        Hero(1, 1, "Hero_1"),
        Hero(2, 2, "Hero_2"),
    )
    val mockQualificationExamApp = QualificationExamApplication(
        academyId = 1,
        hero = Hero(1, 1, "Hero_1"),
        status = ActivityStatus.CREATED
    )
    val mocAcademyApp = AcademyApplication(
        id = 11000011,
        userId = 1,
        printedName = "Тестовый Герой Героевич",
        age = 180,
        quirk = "Работа с огнем",
        educationDocumentNumber = "1122334455",
        message = "message",
        firstAcademy = Academy(
            id = 12,
            label = "Академия 1",
            address = ""
        ),
        secondAcademy = Academy(
            id = 13,
            label = "Академия 2",
            address = ""
        ),
        thirdAcademy = Academy(
            id = 14,
            label = "Академия 3",
            address = ""
        ),
        status = ActivityStatus.CREATED
    )
}
