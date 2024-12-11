package com.cleanease.optimize.navigation

sealed class NavigationItem(val route: String, val name: String = "") {
    data object SignIn : NavigationItem(Screen.SIGN_IN.name)
    data object UsersList : NavigationItem(Screen.USERS_LIST.name)
    data object InvitesList : NavigationItem(Screen.INVITES_LIST.name)
    data object RoomsList : NavigationItem(Screen.ROOMS_LIST.name)
    data object Rooms : NavigationItem(Screen.ROOM.name)
}