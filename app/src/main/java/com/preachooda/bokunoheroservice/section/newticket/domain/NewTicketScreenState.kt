package com.preachooda.bokunoheroservice.section.newticket.domain

import com.preachooda.assets.util.LocationSimple
import com.preachooda.assets.util.Status
import com.preachooda.domain.model.Ticket

data class NewTicketScreenState(
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val isShowMessage: Boolean = false,
    val isAudioRecording: Boolean = false,
    val isAudioPlaying: Boolean = false,
    val shouldCloseScreen: Boolean = false,
    val message: String = "",
    val ticket: Ticket = Ticket(),
    val locationStatus: Status = Status.LOADING,
    val locationInfo: LocationSimple? = null,
    val availableCategories: Set<Ticket.Category> = setOf(),
    val selectedCategories: Set<Ticket.Category> = setOf(),
    val photosPaths: List<String> = listOf(),
    val videoPath: String? = null,
    val audioPath: String? = null,
    val isInit: Boolean = false
) {
    override fun toString(): String {
        return "NewTicketScreenState = {" +
                "\n\tisLoading = $isLoading" +
                "\n\tisError = $isError" +
                "\n\tisShowMessage = $isShowMessage" +
                "\n\tisLoading = $isAudioRecording" +
                "\n\tshouldCloseScreen = $shouldCloseScreen" +
                "\n\tmessage = $message" +
                "\n\tlocationStatus = $locationStatus" +
                "\n\tlocationInfo = $locationInfo" +
                "\n\tavailableCategories = $availableCategories" +
                "\n\tselectedCategories = $selectedCategories" +
                "\n\tphotosPaths = $photosPaths" +
                "\n\tvideoPath = $videoPath" +
                "\n\taudioPath = $audioPath" +
                "\n}"
    }
}
