package com.preachooda.heroapp.navigation

import androidx.activity.viewModels
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.preachooda.heroapp.section.home.HomeScreen
import com.preachooda.heroapp.MainActivity
import com.preachooda.heroapp.section.home.viewModel.HomeViewModel
import com.preachooda.heroapp.section.login.LoginScreen
import com.preachooda.heroapp.section.login.viewModel.LoginViewModel
import com.preachooda.heroapp.section.patrolling.PatrollingScreen
import com.preachooda.heroapp.section.patrolling.viewModel.PatrollingViewModel
import com.preachooda.heroapp.section.ticket.TicketScreen
import com.preachooda.heroapp.section.ticket.viewModel.TicketViewModel

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

        composable(route = Screens.Patrolling.route) {
            val patrollingViewModel: PatrollingViewModel by activity.viewModels()
            PatrollingScreen(navController = navController, viewModel = patrollingViewModel)
        }

        composable(route = Screens.Ticket.route) {
            val ticketViewModel: TicketViewModel by activity.viewModels()
            TicketScreen(navController = navController, viewModel = ticketViewModel)
        }
    }
}
