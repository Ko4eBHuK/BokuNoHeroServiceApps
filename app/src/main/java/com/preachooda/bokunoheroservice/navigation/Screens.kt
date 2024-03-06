package com.preachooda.bokunoheroservice.navigation

sealed class Screens(val route: String) {
    data object Splash : Screens("splashScreen")
    data object Home : Screens("home")
    data object NewTicket : Screens("newTicket")
    data object Tickets : Screens("tickets")
    data object Ticket : Screens("ticket")
    data object Login : Screens("login")
    data object AcademyApplication : Screens("academyApplication")
}
