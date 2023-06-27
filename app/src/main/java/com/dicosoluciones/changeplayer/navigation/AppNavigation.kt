package com.dicosoluciones.changeplayer.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.dicosoluciones.changeplayer.playermanager.ui.screens.PlayersScreen
import com.dicosoluciones.changeplayer.playermanager.ui.PlayersViewModel
import com.dicosoluciones.changeplayer.playermanager.ui.model.PlayerModel
import com.dicosoluciones.changeplayer.playermanager.ui.screens.MatchMakerScreen
import com.dicosoluciones.changeplayer.ui.HomeScreen

@Composable
fun AppNavigation(
    playersViewModel: PlayersViewModel,
) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = AppScreens.HomeScreen.route) {
        composable(route = AppScreens.HomeScreen.route) {
            HomeScreen(navController)
        }
        composable(route = AppScreens.PlayerDashboard.route) {
            PlayersScreen(playersViewModel, navController)
        }
        composable(route = AppScreens.MatchMakerScreen.route) {
            MatchMakerScreen(playersViewModel, navController)
        }
    }
}