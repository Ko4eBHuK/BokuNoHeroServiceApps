package com.preachooda.adminapp.navigation

sealed class Screens(val route: String) {
    data object Login : Screens("login")
    data object Home : Screens("home")
    data object HelpTickets : Screens("helpTickets")
    data object HelpTicket : Screens("helpTicket")
    data object PatrollingFormation : Screens("patrollingFormation")
    data object QualificationExamTickets : Screens("qualificationExamTickets")
    data object QualificationExamTicket : Screens("qualificationExamTicket")
    data object LicenseTickets : Screens("licenseTickets")
    data object LicenseTicket : Screens("licenseTicket")
    data object LicenseRecall : Screens("licenseRecall")
    data object HeroesRatings : Screens("heroesRatings")
    data object HeroRating : Screens("heroRating")
}
