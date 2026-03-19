package com.calaratjada.insider.ui.navigation

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.app.Activity
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.calaratjada.insider.data.service.AdManager
import com.calaratjada.insider.ui.screens.*
import com.calaratjada.insider.ui.theme.*
import com.calaratjada.insider.ui.viewmodel.ChatViewModel
import androidx.hilt.navigation.compose.hiltViewModel

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Map : Screen("map?poiId={poiId}") {
        fun createRoute(poiId: String? = null) = if (poiId != null) "map?poiId=$poiId" else "map"
    }
    data object Events : Screen("events")
    data object Chat : Screen("chat")
    data object ChatRoom : Screen("chatroom/{roomId}/{roomName}") {
        fun createRoute(roomId: String, roomName: String) = "chatroom/$roomId/$roomName"
    }
    data object Friends : Screen("friends")
    data object Weather : Screen("weather")
    data object Booking : Screen("booking")
    data object Login : Screen("login")
    data object Profile : Screen("profile")
    data object CoinStore : Screen("coinstore")
    data object PoiDetail : Screen("poi/{poiId}") {
        fun createRoute(poiId: String) = "poi/$poiId"
    }
    data object PrivacyPolicy : Screen("privacy_policy")
    data object Impressum : Screen("impressum")
}

enum class BottomNavItem(
    val screen: Screen,
    val label: String,
    val icon: ImageVector
) {
    HOME(Screen.Home, "Home", Icons.Default.Home),
    MAP(Screen.Map, "Karte", Icons.Default.Map),
    EVENTS(Screen.Events, "Events", Icons.Default.CalendarMonth),
    CHAT(Screen.Chat, "Chat", Icons.Default.Chat),
    WEATHER(Screen.Weather, "Wetter", Icons.Default.WbSunny)
}

@Composable
fun AppNavigation(adManager: AdManager? = null) {
    val navController = rememberNavController()
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = currentBackStackEntry?.destination
    val context = LocalContext.current

    // Determine if bottom bar should be visible
    val showBottomBar = currentDestination?.route?.let { route ->
        BottomNavItem.entries.any { it.screen.route == route || route.startsWith(it.screen.route.substringBefore("?")) }
    } ?: true

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                BottomNavBar(
                    navController = navController,
                    currentDestination = currentDestination
                )
            }
        },
        containerColor = Stone50
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(
                bottom = if (showBottomBar) 0.dp else innerPadding.calculateBottomPadding()
            )
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    onPoiClick = { poiId ->
                        // Show interstitial when navigating to POI detail
                        (context as? Activity)?.let { adManager?.showInterstitialIfReady(it) }
                        navController.navigate(Screen.PoiDetail.createRoute(poiId))
                    },
                    onNavigateToWeather = {
                        navController.navigate(Screen.Weather.route) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onNavigateToBooking = {
                        navController.navigate(Screen.Booking.route) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }

            composable(
                route = Screen.Map.route,
                arguments = listOf(
                    navArgument("poiId") {
                        type = NavType.StringType
                        nullable = true
                        defaultValue = null
                    }
                )
            ) { backStackEntry ->
                val poiId = backStackEntry.arguments?.getString("poiId")
                MapScreen(initialPoiId = poiId)
            }

            composable(Screen.Events.route) {
                EventsScreen()
            }

            composable(Screen.Chat.route) {
                val chatViewModel: ChatViewModel = hiltViewModel()
                val nickname by chatViewModel.nickname.collectAsState()
                if (nickname == null) {
                    NicknameScreen(
                        onNicknameSet = { name ->
                            chatViewModel.setNickname(name)
                            chatViewModel.connectAndLogin()
                        }
                    )
                } else {
                    ChatRoomsScreen(
                        onRoomClick = { roomId ->
                            val room = chatViewModel.rooms.value.find { it.id == roomId }
                            val roomName = room?.name ?: roomId
                            navController.navigate(Screen.ChatRoom.createRoute(roomId, roomName))
                        },
                        onFriendsClick = {
                            navController.navigate(Screen.Friends.route)
                        },
                        onProfileClick = {
                            navController.navigate(Screen.Profile.route)
                        },
                        onCoinStoreClick = {
                            navController.navigate(Screen.CoinStore.route)
                        },
                        viewModel = chatViewModel
                    )
                }
            }

            composable(
                route = Screen.ChatRoom.route,
                arguments = listOf(
                    navArgument("roomId") { type = NavType.StringType },
                    navArgument("roomName") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val roomName = backStackEntry.arguments?.getString("roomName") ?: ""
                ChatScreen(
                    roomName = roomName,
                    onBack = {
                        // Show interstitial when leaving chat room
                        (context as? Activity)?.let { adManager?.showInterstitialIfReady(it) }
                        navController.popBackStack()
                    }
                )
            }

            composable(Screen.Friends.route) {
                FriendsScreen(
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Screen.Profile.route) {
                ProfileScreen(
                    onBack = { navController.popBackStack() },
                    onCoinStore = { navController.navigate(Screen.CoinStore.route) },
                    onPrivacyPolicy = { navController.navigate(Screen.PrivacyPolicy.route) },
                    onImpressum = { navController.navigate(Screen.Impressum.route) }
                )
            }

            composable(Screen.CoinStore.route) {
                CoinStoreScreen(
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Screen.Booking.route) {
                BookingScreen()
            }

            composable(Screen.Weather.route) {
                WeatherScreen()
            }

            composable(
                route = Screen.PoiDetail.route,
                arguments = listOf(
                    navArgument("poiId") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val poiId = backStackEntry.arguments?.getString("poiId") ?: return@composable
                PoiDetailScreen(
                    poiId = poiId,
                    onBack = { navController.popBackStack() },
                    onNavigateToMap = { id ->
                        navController.navigate(Screen.Map.createRoute(id)) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                        }
                    }
                )
            }

            composable(Screen.PrivacyPolicy.route) {
                PrivacyPolicyScreen(onBack = { navController.popBackStack() })
            }

            composable(Screen.Impressum.route) {
                ImpressumScreen(onBack = { navController.popBackStack() })
            }
        }
    }
}

@Composable
private fun BottomNavBar(
    navController: NavHostController,
    currentDestination: androidx.navigation.NavDestination?
) {
    Surface(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 12.dp),
        shape = RoundedCornerShape(24.dp),
        color = Stone900.copy(alpha = 0.95f),
        shadowElevation = 16.dp,
        tonalElevation = 0.dp
    ) {
        NavigationBar(
            containerColor = Color.Transparent,
            tonalElevation = 0.dp,
            modifier = Modifier.height(72.dp)
        ) {
            BottomNavItem.entries.forEach { item ->
                val isSelected = currentDestination?.hierarchy?.any {
                    it.route == item.screen.route ||
                    it.route?.startsWith(item.screen.route.substringBefore("?")) == true
                } == true

                NavigationBarItem(
                    icon = {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.label,
                            modifier = Modifier.size(22.dp)
                        )
                    },
                    label = {
                        Text(
                            text = item.label.uppercase(),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    },
                    selected = isSelected,
                    onClick = {
                        val route = when (item.screen) {
                            is Screen.Map -> Screen.Map.createRoute()
                            else -> item.screen.route
                        }
                        navController.navigate(route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = Color.White,
                        unselectedIconColor = Stone400,
                        unselectedTextColor = Stone400,
                        indicatorColor = Emerald500
                    )
                )
            }
        }
    }
}
