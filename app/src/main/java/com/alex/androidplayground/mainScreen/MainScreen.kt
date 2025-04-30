package com.alex.androidplayground.mainScreen

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.alex.androidplayground.R
import com.alex.androidplayground.breweryScreen.ui.BreweryDetailsScreen
import com.alex.androidplayground.breweryScreen.ui.BreweryScreen
import com.alex.androidplayground.breweryScreen.ui.state.BreweryDetailsViewModel
import com.alex.androidplayground.breweryScreen.ui.state.BreweryViewModel
import com.alex.androidplayground.core.ui.model.navigation.BreweriesListDest
import com.alex.androidplayground.core.ui.model.navigation.BreweriesNestedNavDest
import com.alex.androidplayground.core.ui.model.navigation.BreweryDetailsDest
import com.alex.androidplayground.core.ui.model.navigation.FileSelectorDest
import com.alex.androidplayground.core.ui.model.navigation.ForegroundServiceDest
import com.alex.androidplayground.core.ui.model.navigation.NavDrawerItem
import com.alex.androidplayground.core.ui.model.navigation.WeatherDest
import com.alex.androidplayground.core.ui.model.navigation.WorkManagerDest
import com.alex.androidplayground.core.ui.model.navigation.getNavDrawerItems
import com.alex.androidplayground.filesScreen.ui.FileScreen
import com.alex.androidplayground.filesScreen.ui.state.FileViewModel
import com.alex.androidplayground.foregroundServiceScreen.ui.ForegroundServiceScreen
import com.alex.androidplayground.foregroundServiceScreen.ui.state.ForegroundServiceScreenViewModel
import com.alex.androidplayground.mainScreen.state.MainViewModel
import com.alex.androidplayground.weatherScreen.ui.WeatherScreen
import com.alex.androidplayground.weatherScreen.ui.state.WeatherViewModel
import com.alex.androidplayground.workManagerScreen.ui.WorkManagerScreen
import kotlinx.collections.immutable.PersistentList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun MainScreen() {
    val viewModel = hiltViewModel<MainViewModel>()
    MainLayout(viewModel.navDrawerItems)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainLayout(navDrawerItems: PersistentList<NavDrawerItem>) {
    val scope = rememberCoroutineScope()
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val selectedItemIndex = rememberSaveable { mutableIntStateOf(0) }
    val isBreweryDetailsScreen = remember { mutableStateOf(false) }
    // Listen to navigation changes (eg back press) to update the selected item in the drawer
    LaunchedEffect(navController) {
        navController.currentBackStackEntryFlow.collect { backStackEntry ->
            isBreweryDetailsScreen.value = try {
                backStackEntry.toRoute<BreweryDetailsDest>()
                true
            } catch (_: Exception) {
                false
            }
            val currentRoute = backStackEntry.destination.route ?: return@collect
            val currentSimpleName = currentRoute.substringAfterLast('.').substringBeforeLast('/')
            val newIndex = navDrawerItems.indexOfFirst { it.destination::class.simpleName == currentSimpleName }
            if (newIndex != -1 && newIndex != selectedItemIndex.intValue) {
                selectedItemIndex.intValue = newIndex
            }
        }
    }
    ModalNavigationDrawer(
        drawerContent = {
            ModalDrawerSheet {
                Spacer(modifier = Modifier.padding(top = 16.dp))
                navDrawerItems.forEachIndexed { index, item ->
                    NavigationDrawerItem(
                        label = {
                            Text(text = item.text)
                        },
                        selected = index == selectedItemIndex.intValue,
                        onClick = {
                            navController.navigate(item.destination) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                            scope.launch { drawerState.close() }
                        },
                        modifier = Modifier
                            .padding(NavigationDrawerItemDefaults.ItemPadding)
                            .testTag("NavItem_${item.text}")
                    )
                }
            }
        }, modifier = Modifier.fillMaxSize(), drawerState = drawerState, gesturesEnabled = !isBreweryDetailsScreen.value
    ) {
        Scaffold(
            topBar = {
                TopBar(
                    isBreweryDetailsScreen = isBreweryDetailsScreen.value,
                    selectedItemIndex = selectedItemIndex.intValue,
                    navDrawerItems = navDrawerItems,
                    drawerState = drawerState,
                    navController = navController,
                    scope = scope
                )
            }
        ) { paddingValues ->
            NavHost(
                modifier = Modifier.padding(paddingValues),
                navController = navController,
                startDestination = FileSelectorDest
            ) {
                composable<FileSelectorDest> {
                    val viewModel = hiltViewModel<FileViewModel>()
                    FileScreen(viewModel)
                }
                composable<ForegroundServiceDest> {
                    val viewModel = hiltViewModel<ForegroundServiceScreenViewModel>()
                    ForegroundServiceScreen(viewModel)
                }
                composable<WorkManagerDest> {
                    WorkManagerScreen()
                }
                composable<WeatherDest> {
                    val viewModel = hiltViewModel<WeatherViewModel>()
                    WeatherScreen(viewModel)
                }
                navigation<BreweriesNestedNavDest>(startDestination = BreweriesListDest) {
                    composable<BreweriesListDest> {
                        val parentEntry = remember(it) { navController.getBackStackEntry<BreweriesNestedNavDest>() }
                        val viewModel = hiltViewModel<BreweryViewModel>(parentEntry)
                        BreweryScreen(viewModel) { navController.navigate(BreweryDetailsDest(it)) }
                    }
                    composable<BreweryDetailsDest> {
                        val dest = it.toRoute<BreweryDetailsDest>()
                        val parentEntry = remember(it) { navController.getBackStackEntry<BreweriesNestedNavDest>() }
                        val viewModel = hiltViewModel<BreweryDetailsViewModel, BreweryDetailsViewModel.Factory>(
                            parentEntry,
                            creationCallback = { factory: BreweryDetailsViewModel.Factory ->
                                factory.create(breweryId = dest.breweryId)
                            }
                        )
                        BreweryDetailsScreen(viewModel)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    isBreweryDetailsScreen: Boolean,
    selectedItemIndex: Int,
    navDrawerItems: PersistentList<NavDrawerItem>,
    drawerState: DrawerState,
    navController: NavHostController,
    scope: CoroutineScope
) {
    TopAppBar(
        title = {
            Text(
                text = if (isBreweryDetailsScreen) stringResource(R.string.brewery_details) else navDrawerItems[selectedItemIndex].text,
                modifier = Modifier.testTag("TopAppBarTitle")
            )
        },
        navigationIcon = {
            IconButton(
                onClick = {
                    if (isBreweryDetailsScreen) navController.popBackStack()
                    else scope.launch { drawerState.open() }
                }
            ) {
                Icon(
                    imageVector = if (isBreweryDetailsScreen) Icons.AutoMirrored.Filled.ArrowBack else Icons.Default.Menu,
                    contentDescription = if (isBreweryDetailsScreen) "Back" else "Menu toggle icon"
                )
            }
        }
    )
}

@Preview
@Composable
fun MainLayoutPreview() {
    MainLayout(getNavDrawerItems())
}