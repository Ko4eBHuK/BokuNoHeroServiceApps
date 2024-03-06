package com.preachooda.bokunoheroservice.composeTestUtils

import com.google.gson.Gson
import com.preachooda.domain.model.Academy
import com.preachooda.domain.model.AcademyApplication
import com.preachooda.domain.model.ActivityStatus
import com.preachooda.domain.model.Hero
import com.preachooda.domain.model.Ticket
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.RecordedRequest

object MockDispatcher : Dispatcher() {
    override fun dispatch(request: RecordedRequest): MockResponse {
        return when (request.path) {
            "/tickets" -> {
                when (request.method) {
                    "POST" -> MockResponse().setResponseCode(200).setBody(Gson().toJson(mockTicket))
                    "GET" -> MockResponse().setResponseCode(200)
                        .setBody(Gson().toJson(listOf(mockTicketEvaluation)))

                    else -> MockResponse().setResponseCode(501)
                }
            }

            "/tickets/111000111" -> {
                when (request.method) {
                    "GET" -> MockResponse().setResponseCode(200)
                        .setBody(Gson().toJson(mockTicketEvaluation))

                    else -> MockResponse().setResponseCode(501)
                }
            }

            "/academies" -> {
                when (request.method) {
                    "GET" -> MockResponse().setResponseCode(200)
                        .setBody(Gson().toJson(mockAcademyList))

                    else -> MockResponse().setResponseCode(501)
                }
            }

            "/academy-tickets" -> {
                when (request.method) {
                    "POST" -> MockResponse().setResponseCode(200)
                        .setBody(Gson().toJson(mocAcademyApp))

                    else -> MockResponse().setResponseCode(501)
                }
            }

            else -> MockResponse().setResponseCode(404)
        }

    }

    val mockTicket = Ticket(id = 111000111)
    val mockTicketEvaluation = Ticket(
        id = 111000111,
        heroes = listOf(
            Hero(1, 1, "Hero_1"),
            Hero(2, 2, "Hero_2"),
        ),
        status = ActivityStatus.EVALUATION,
        description = "Описание заявки"
    )

    val mockAcademyList = listOf(
        Academy(
            id = 12,
            label = "Академия 1",
            address = ""
        ),
        Academy(
            id = 13,
            label = "Академия 2",
            address = ""
        ),
        Academy(
            id = 14,
            label = "Академия 3",
            address = ""
        )
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