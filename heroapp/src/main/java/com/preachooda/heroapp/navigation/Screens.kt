package com.preachooda.heroapp.navigation

sealed class Screens(val route: String) {
    data object Home : Screens("home")
    data object Login : Screens("login")
    data object Patrolling : Screens("patrolling")
    data object Ticket : Screens("ticket")
}
