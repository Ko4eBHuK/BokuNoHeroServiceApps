package com.preachooda.academyapp.navigation

sealed class Screens(val route: String) {
    data object Login : Screens("login")
    data object Home : Screens("home")
    data object LicenseCreation : Screens("licenseCreation")
    data object Applications : Screens("applications")
    data object Application : Screens("application")
    data object QualificationExam : Screens("qualificationExam")
}