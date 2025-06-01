package com.example.weatherapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.weatherapp.ui.theme.WeatherAppTheme

// Define your navigation routes (like React Router paths)
sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Home : Screen("home", "Current", Icons.Default.Home)
    object Search : Screen("search", "Search", Icons.Default.Search)
    object Forecast : Screen("forecast", "Forecast", Icons.Default.DateRange)
    object Settings : Screen("settings", "Settings", Icons.Default.Settings)
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WeatherAppTheme {
                WeatherApp()
            }
        }
    }
}

@Composable
fun WeatherApp() {
    // Navigation controller - like React Router's history
    val navController = rememberNavController()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            BottomNavigationBar(navController = navController)
        }
    ) { innerPadding ->
        // Navigation host - like Routes in React Router
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            // Define each route and its corresponding screen
            composable(Screen.Home.route) {
                CurrentWeatherScreen()
            }
            composable(Screen.Search.route) {
                SearchScreen(navController)
            }
            composable(Screen.Forecast.route) {
                ForecastScreen()
            }
            composable(Screen.Settings.route) {
                SettingsScreen()
            }
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavController) {
    val items = listOf(
        Screen.Home,
        Screen.Search,
        Screen.Forecast,
        Screen.Settings
    )

    // Get current route to highlight active tab
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(
        containerColor = Color(0xFF2196F3),
        contentColor = Color.White
    ) {
        items.forEach { screen ->
            NavigationBarItem(
                icon = {
                    Icon(
                        screen.icon,
                        contentDescription = screen.title
                    )
                },
                label = { Text(screen.title) },
                selected = currentRoute == screen.route,
                onClick = {
                    // Navigate to selected screen
                    navController.navigate(screen.route) {
                        // Pop up to start destination to avoid building large stack
                        popUpTo(navController.graph.startDestinationId)
                        // Avoid multiple copies of same destination
                        launchSingleTop = true
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.White,
                    selectedTextColor = Color.White,
                    unselectedIconColor = Color.White.copy(alpha = 0.7f),
                    unselectedTextColor = Color.White.copy(alpha = 0.7f),
                    indicatorColor = Color.White.copy(alpha = 0.2f)
                )
            )
        }
    }
}

// SCREEN COMPONENTS - Each is like a separate page

@Composable
fun CurrentWeatherScreen() {
    // Your main weather display
    var city by remember { mutableStateOf("London") }
    var temperature by remember { mutableStateOf("22") }
    var condition by remember { mutableStateOf("Sunny") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                color = Color(0xFF2196F3)
            )
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = city,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "${temperature}°",
            fontSize = 72.sp,
            fontWeight = FontWeight.Light,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = condition,
            fontSize = 20.sp,
            color = Color.White.copy(alpha = 0.9f)
        )

        Spacer(modifier = Modifier.height(32.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color.White.copy(alpha = 0.2f)
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Today's Details",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Humidity: 65%",
                    color = Color.White.copy(alpha = 0.9f)
                )
                Text(
                    text = "Wind: 12 km/h",
                    color = Color.White.copy(alpha = 0.9f)
                )
            }
        }
    }
}

@Composable
fun SearchScreen(navController: NavController) {
    var searchText by remember { mutableStateOf("") }
    var searchResults by remember { mutableStateOf(listOf("London", "New York", "Tokyo", "Paris")) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Search Cities",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = searchText,
            onValueChange = { searchText = it },
            label = { Text("Enter city name") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Popular Cities",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // City list
        searchResults.forEach { city ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                onClick = {
                    // Navigate back to home with selected city
                    // TODO: Pass city data back
                    navController.navigate(Screen.Home.route)
                }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = city, fontSize = 16.sp)
                    Icon(
                        Icons.Default.ArrowForward,
                        contentDescription = "Select city"
                    )
                }
            }
        }
    }
}

@Composable
fun ForecastScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "7-Day Forecast",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Mock forecast data
        val forecast = listOf(
            "Today" to "22°C",
            "Tomorrow" to "25°C",
            "Wednesday" to "19°C",
            "Thursday" to "23°C",
            "Friday" to "26°C",
            "Saturday" to "24°C",
            "Sunday" to "21°C"
        )

        forecast.forEach { (day, temp) ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = day, fontSize = 16.sp)
                    Text(text = temp, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun SettingsScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Settings",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        val settings = listOf(
            "Temperature Unit" to "Celsius",
            "Notifications" to "Enabled",
            "Location" to "Auto-detect",
            "Theme" to "System"
        )

        settings.forEach { (setting, value) ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = setting, fontSize = 16.sp)
                    Text(
                        text = value,
                        fontSize = 16.sp,
                        color = Color(0xFF2196F3)
                    )
                }
            }
        }
    }
}