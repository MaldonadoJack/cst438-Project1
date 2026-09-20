package com.example.donorproject

import org.junit.Assert.assertEquals
import org.junit.Test

class MainActivityStartScreenTest {

    @Test
    fun aNormalLaunchStartsOnTheLandingScreen() {
        assertEquals(Screen.LANDING, startScreen(showLogin = false))
    }

    @Test
    fun aLogoutLaunchStartsOnTheLoginScreen() {
        assertEquals(Screen.LOGIN, startScreen(showLogin = true))
    }
}
