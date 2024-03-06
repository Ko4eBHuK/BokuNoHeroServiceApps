package com.preachooda.adminapp.composeTestUtils

import com.google.gson.Gson
import com.preachooda.domain.model.ActivityStatus
import com.preachooda.domain.model.Hero
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
                    "GET" -> MockResponse().setResponseCode(200).setBody(Gson().toJson(mockHero))
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
                    "GET" -> MockResponse().setResponseCode(200).setBody(Gson().toJson(mockHero))
                    "PUT" -> MockResponse().setResponseCode(200).setBody(request.body)
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
            else -> MockResponse().setResponseCode(404)
        }
    }

    val mockHero = listOf(
        Hero(1, 1, "Hero_1"),
        Hero(2, 2, "Hero_2"),
    )

    val mockTicketCreated = Ticket(
        id = 111000111,
        heroes = listOf(
            Hero(1, 1, "Hero_1"),
            Hero(2, 2, "Hero_2"),
        ),
        status = ActivityStatus.CREATED,
        description = "Описание заявки"
    )

}
