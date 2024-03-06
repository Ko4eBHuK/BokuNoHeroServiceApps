package com.preachooda.adminapp.navigation

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
import ccom.preachooda.adminapp.section.licenseTicket.LicenseTicketScreen
import com.preachooda.adminapp.MainActivity
import com.preachooda.adminapp.section.helpTicket.HelpTicketScreen
import com.preachooda.adminapp.section.helpTicket.viewModel.HelpTicketViewModel
import com.preachooda.adminapp.section.helpTickets.HelpTicketsScreen
import com.preachooda.adminapp.section.helpTickets.viewModel.HelpTicketsViewModel
import com.preachooda.adminapp.section.heroRating.HeroRatingScreen
import com.preachooda.adminapp.section.heroRating.viewModel.HeroRatingViewModel
import com.preachooda.adminapp.section.heroesRatings.HeroesRatingsScreen
import com.preachooda.adminapp.section.heroesRatings.viewModel.HeroesRatingsViewModel
import com.preachooda.adminapp.section.home.HomeScreen
import com.preachooda.adminapp.section.home.viewModel.HomeViewModel
import com.preachooda.adminapp.section.licenseRecall.LicenseRecallScreen
import com.preachooda.adminapp.section.licenseRecall.viewModel.LicenseRecallViewModel
import com.preachooda.adminapp.section.licenseTicket.viewModel.LicenseTicketViewModel
import com.preachooda.adminapp.section.licenseTickets.LicenseTicketsScreen
import com.preachooda.adminapp.section.licenseTickets.viewModel.LicenseTicketsViewModel
import com.preachooda.adminapp.section.login.LoginScreen
import com.preachooda.adminapp.section.login.viewModel.LoginViewModel
import com.preachooda.adminapp.section.patrollingFormation.PatrollingFormationScreen
import com.preachooda.adminapp.section.patrollingFormation.viewModel.PatrollingFormationViewModel
import com.preachooda.adminapp.section.qualificationExamTicket.QualificationExamTicketScreen
import com.preachooda.adminapp.section.qualificationExamTicket.viewModel.QualificationExamTicketViewModel
import com.preachooda.adminapp.section.qualificationExamTickets.QualificationExamTicketsScreen
import com.preachooda.adminapp.section.qualificationExamTickets.viewModel.QualificationExamTicketsViewModel

@Composable
fun SetupNavGraph(
    navController: NavHostController,
    activity: MainActivity,
    userLogged: Boolean
) {
    NavHost(
        navController = navController,
        startDestination = if (userLogged) Screens.Home.route else Screens.Login.route,
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

        composable(route = Screens.HelpTickets.route) {
            val helpTicketsViewModel: HelpTicketsViewModel by activity.viewModels()
            HelpTicketsScreen(navController = navController, viewModel = helpTicketsViewModel)
        }

        composable(
            route = "${Screens.HelpTicket.route}/{ticketId}",
            arguments = listOf(
                navArgument("ticketId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val helpTicketViewModel: HelpTicketViewModel by activity.viewModels()
            HelpTicketScreen(
                navController = navController,
                viewModel = helpTicketViewModel,
                ticketId = backStackEntry.arguments?.getLong("ticketId") ?: -1
            )
        }

        composable(route = Screens.PatrollingFormation.route) {
            val patrollingFormationViewModel: PatrollingFormationViewModel by activity.viewModels()
            PatrollingFormationScreen(navController = navController, viewModel = patrollingFormationViewModel)
        }

        composable(route = Screens.QualificationExamTickets.route) {
            val qualificationExamTicketsViewModel: QualificationExamTicketsViewModel by activity.viewModels()
            QualificationExamTicketsScreen(navController = navController, viewModel = qualificationExamTicketsViewModel)
        }

        composable(
            route = "${Screens.QualificationExamTicket.route}/{ticketId}",
            arguments = listOf(
                navArgument("ticketId") { type = NavType.LongType }
            )
        ) {
            val qualificationExamTicketViewModel: QualificationExamTicketViewModel by activity.viewModels()
            QualificationExamTicketScreen(
                navController = navController,
                viewModel = qualificationExamTicketViewModel,
                ticketId = it.arguments?.getLong("ticketId")?: -1
            )
        }

        composable(route = Screens.LicenseTickets.route) {
            val licenseTicketsViewModel: LicenseTicketsViewModel by activity.viewModels()
            LicenseTicketsScreen(navController = navController, viewModel = licenseTicketsViewModel)
        }

        composable(
            route = "${Screens.LicenseTicket.route}/{ticketId}",
            arguments = listOf(
                navArgument("ticketId") { type = NavType.LongType }
            )
        ) {
            val licenseTicketViewModel: LicenseTicketViewModel by activity.viewModels()
            LicenseTicketScreen(
                navController = navController,
                viewModel = licenseTicketViewModel,
                ticketId = it.arguments?.getLong("ticketId")?: -1
            )
        }

        composable(route = Screens.LicenseRecall.route) {
            val licenseRecallViewModel: LicenseRecallViewModel by activity.viewModels()
            LicenseRecallScreen(navController = navController, viewModel = licenseRecallViewModel)
        }

        composable(route = Screens.HeroesRatings.route) {
            val heroesRatingsViewModel: HeroesRatingsViewModel by activity.viewModels()
            HeroesRatingsScreen(navController = navController, viewModel = heroesRatingsViewModel)
        }

        composable(
            route = "${Screens.HeroRating.route}/{heroId}",
            arguments = listOf(
                navArgument("heroId") { type = NavType.LongType }
            )
        ) {
            val heroRatingViewModel: HeroRatingViewModel by activity.viewModels()
            HeroRatingScreen(
                navController = navController,
                viewModel = heroRatingViewModel,
                heroId = it.arguments?.getLong("heroId")?: -1
            )
        }
    }
}
