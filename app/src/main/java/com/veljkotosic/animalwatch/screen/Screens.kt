package com.veljkotosic.animalwatch.screen

sealed class Screens(val route: String) {
    object Login : Screens("login")
    object Register : Screens("register")
    object RegistrationDone : Screens("registrationDone")
    object Profile : Screens("profile")
    object Map : Screens("map")
    object Leaderboard : Screens("leaderboard")
}