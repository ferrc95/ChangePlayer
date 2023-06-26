package com.dicosoluciones.changeplayer.navigation

sealed class AppScreens (val route: String) {
    object HomeScreen: AppScreens(route = "home")
    object PlayerDashboard: AppScreens(route = "player_dashboard")
}
