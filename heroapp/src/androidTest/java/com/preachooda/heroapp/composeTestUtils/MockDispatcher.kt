package com.preachooda.heroapp.composeTestUtils

import com.google.gson.Gson
import com.preachooda.domain.model.ActivityStatus
import com.preachooda.domain.model.Hero
import com.preachooda.domain.model.Patrol
import com.preachooda.domain.model.Ticket
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.RecordedRequest

object MockDispatcher : Dispatcher() {
    override fun dispatch(request: RecordedRequest): MockResponse {
        return when(request.path) {
            "/tickets/hero/1/activeTicket" -> {
                when (request.method) {
                    "GET" -> MockResponse().setResponseCode(200)
                        .setBody(Gson().toJson(mockTicketAssigned))
                    else -> MockResponse().setResponseCode(501)
                }
            }
            "/tickets" -> {
                when (request.method) {
                    "PUT" -> MockResponse().setResponseCode(200).setBody(Gson().toJson(mockTicket))
                    else -> MockResponse().setResponseCode(501)
                }
            }
            "/tickets/111000111" -> {
                when (request.method) {
                    "PUT" -> MockResponse().setResponseCode(200).setBody(request.body)
                    else -> MockResponse().setResponseCode(501)
                }
            }
            "/district-patrollings/active/1" -> {
                when (request.method) {
                    "GET" -> MockResponse().setResponseCode(200)
                        .setBody(Gson().toJson(mockPatrollingService))
                    else -> MockResponse().setResponseCode(501)
                }
            }
            "/district-patrollings/112233" -> {
                when (request.method) {
                    "PUT" -> MockResponse().setResponseCode(200).setBody(request.body)
                    else -> MockResponse().setResponseCode(501)
                }
            }
            else -> MockResponse().setResponseCode(404)
        }
    }

    val mockTicket = Ticket(id = 111000111)
    val mockTicketAssigned = Ticket(
        id = 111000111,
        heroes = listOf(
            Hero(1, 1, "Hero_1"),
            Hero(2, 2, "Hero_2"),
        ),
        status = ActivityStatus.ASSIGNED,
        description = "Описание заявки"
    )
    val mockPatrollingService = Patrol(
        112233,
        "Петроградская",
        heroes = listOf(
            Hero(1, 1, "Hero_1"),
            Hero(2, 2, "Hero_2"),
        ),
        status = Patrol.Status.PENDING,
        "11:00",
        "12:30"
    )

}
