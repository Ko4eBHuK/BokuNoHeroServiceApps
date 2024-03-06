package com.preachooda.bokunoheroservice.navigation

import androidx.activity.viewModels
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.preachooda.bokunoheroservice.MainActivity
import com.preachooda.bokunoheroservice.section.academyApplication.AcademyApplicationScreen
import com.preachooda.bokunoheroservice.section.academyApplication.viewModel.AcademyApplicationViewModel
import com.preachooda.bokunoheroservice.section.home.HomeScreen
import com.preachooda.bokunoheroservice.section.home.viewModel.HomeViewModel
import com.preachooda.bokunoheroservice.section.login.LoginScreen
import com.preachooda.bokunoheroservice.section.login.viewModel.LoginViewModel
import com.preachooda.bokunoheroservice.section.newticket.NewTicketScreen
import com.preachooda.bokunoheroservice.section.newticket.viewModel.NewTicketViewModel
import com.preachooda.bokunoheroservice.section.splash.SplashScreen
import com.preachooda.bokunoheroservice.section.splash.viewModel.SplashViewModel
import com.preachooda.bokunoheroservice.section.ticket.TicketScreen
import com.preachooda.bokunoheroservice.section.ticket.viewModel.TicketViewModel
import com.preachooda.bokunoheroservice.section.tickets.TicketsScreen
import com.preachooda.bokunoheroservice.section.tickets.viewModel.TicketsViewModel

@Composable
fun SetupNavGraph(
    navController: NavHostController,
    activity: MainActivity
) {
    NavHost(
        navController = navController,
        startDestination = Screens.Splash.route,
        enterTransition = { slideInHorizontally(animationSpec = tween(500)) },
        exitTransition = { slideOutHorizontally(animationSpec = tween(500)) }
    ) {
        composable(route = Screens.Splash.route) {
            val splashViewModel by activity.viewModels<SplashViewModel>()
            SplashScreen(
                navController = navController,
                viewModel = splashViewModel
            )
        }

        composable(route = Screens.Login.route) {
            val loginViewModel by activity.viewModels<LoginViewModel>()
            LoginScreen(
                navController = navController,
                viewModel = loginViewModel
            )
        }

        composable(route = Screens.Home.route) {
            val homeViewModel by activity.viewModels<HomeViewModel>()
            HomeScreen(
                navController = navController,
                viewModel = homeViewModel
            )
        }

        composable(route = Screens.NewTicket.route) {
            val newTicketViewModel by activity.viewModels<NewTicketViewModel>()

            NewTicketScreen(
                navController = navController,
                viewModel = newTicketViewModel
            )
        }

        composable(route = Screens.Tickets.route) {
            val ticketsViewModel by activity.viewModels<TicketsViewModel>()

            TicketsScreen(
                navController = navController,
                viewModel = ticketsViewModel
            )
        }

        composable(
            route = "${Screens.Ticket.route}/{ticketId}",
            arguments = listOf(
                navArgument("ticketId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val ticketViewModel by activity.viewModels<TicketViewModel>()

            TicketScreen(
                navController = navController,
                viewModel = ticketViewModel,
                ticketId = backStackEntry.arguments?.getLong("ticketId") ?: -1
            )
        }

        composable(
            route = Screens.AcademyApplication.route
        ) {
            val academyApplicationViewModel by activity.viewModels<AcademyApplicationViewModel>()

            AcademyApplicationScreen(
                navController = navController,
                viewModel = academyApplicationViewModel
            )
        }
    }
}