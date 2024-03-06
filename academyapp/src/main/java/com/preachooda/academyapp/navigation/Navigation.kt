package com.preachooda.academyapp.navigation

import androidx.activity.viewModels
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.preachooda.academyapp.MainActivity
import com.preachooda.academyapp.section.application.ApplicationScreen
import com.preachooda.academyapp.section.application.viewModel.AdmissionViewModel
import com.preachooda.academyapp.section.applications.ApplicationsScreen
import com.preachooda.academyapp.section.applications.viewModel.ApplicationsViewModel
import com.preachooda.academyapp.section.home.HomeScreen
import com.preachooda.academyapp.section.home.viewModel.HomeViewModel
import com.preachooda.academyapp.section.license.LicenseScreen
import com.preachooda.academyapp.section.license.viewModel.LicenseViewModel
import com.preachooda.academyapp.section.login.LoginScreen
import com.preachooda.academyapp.section.login.viewModel.LoginViewModel
import com.preachooda.academyapp.section.qualificationExam.QualificationExamScreen
import com.preachooda.academyapp.section.qualificationExam.viewModel.QualificationExamViewModel

@Composable
fun SetupNavGraph(
    navController: NavHostController,
    activity: MainActivity
) {
    NavHost(
        navController = navController,
        startDestination = Screens.Login.route,
        enterTransition = { slideInHorizontally(animationSpec = tween(500)) },
        exitTransition = { slideOutHorizontally(animationSpec = tween(500)) }
    ) {
        composable(route = Screens.Login.route) {
            val loginViewModel: LoginViewModel by activity.viewModels()
            LoginScreen(navController = navController, viewModel = loginViewModel)
        }

        composable(route = Screens.Home.route) {
            val homeViewModel: HomeViewModel by activity.viewModels()
            HomeScreen(navController = navController, viewModel = homeViewModel)
        }

        composable(route = Screens.LicenseCreation.route) {
            val licenseViewModel: LicenseViewModel by activity.viewModels()
            LicenseScreen(navController = navController, viewModel = licenseViewModel)
        }

        composable(route = Screens.Applications.route) {
            val admissionsViewModel: ApplicationsViewModel by activity.viewModels()
            ApplicationsScreen(navController = navController, viewModel = admissionsViewModel)
        }

        composable(
            route = "${Screens.Application.route}/{applicationId}",
            arguments = listOf(
                navArgument("applicationId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val admissionViewModel: AdmissionViewModel by activity.viewModels()
            ApplicationScreen(
                navController = navController,
                viewModel = admissionViewModel,
                applicationId = backStackEntry.arguments?.getLong("applicationId") ?: -1
            )
        }

        composable(route = Screens.QualificationExam.route) {
            val qualificationExamViewModel: QualificationExamViewModel by activity.viewModels()
            QualificationExamScreen(
                navController = navController,
                viewModel = qualificationExamViewModel
            )
        }
    }
}
