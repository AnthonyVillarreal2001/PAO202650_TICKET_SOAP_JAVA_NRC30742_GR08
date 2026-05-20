package ec.edu.monster.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.compose.rememberNavController
import ec.edu.monster.screens.*

@Composable
fun NavigationGraph(navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = "login") {
        
        composable("login") {
            LoginScreen(navController)
        }
        
        composable(
            route = "menu/{usuario}",
            arguments = listOf(navArgument("usuario") { type = NavType.StringType })
        ) { backStackEntry ->
            val usuario = backStackEntry.arguments?.getString("usuario") ?: ""
            MenuScreen(navController, usuario)
        }
        
        composable(
            route = "partidos/{usuario}",
            arguments = listOf(navArgument("usuario") { type = NavType.StringType })
        ) { backStackEntry ->
            val usuario = backStackEntry.arguments?.getString("usuario") ?: ""
            PartidosScreen(navController, usuario)
        }
        
        composable(
            route = "localidades/{codigoPartido}/{usuario}",
            arguments = listOf(
                navArgument("codigoPartido") { type = NavType.StringType },
                navArgument("usuario") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val codigoPartido = backStackEntry.arguments?.getString("codigoPartido") ?: ""
            val usuario = backStackEntry.arguments?.getString("usuario") ?: ""
            LocalidadesScreen(navController, codigoPartido, usuario)
        }
        
        composable("reporte") {
            ReporteScreen(navController)
        }
    }
}
