package com.alex.androidplayground.core.ui

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.alex.androidplayground.core.ui.model.navigation.FileSelectorDest
import com.alex.androidplayground.core.ui.model.navigation.ForegroundServiceDest
import com.alex.androidplayground.core.ui.model.navigation.WorkManagerDest
import com.alex.androidplayground.core.ui.model.navigation.NavDrawerItem
import com.alex.androidplayground.core.ui.model.navigation.WeatherDest
import com.alex.androidplayground.core.ui.model.navigation.getNavDrawerItems
import com.alex.androidplayground.fileSelectorScreen.ui.FileSelectorScreen
import com.alex.androidplayground.foregroundServiceScreen.ui.ForegroundServiceScreen
import com.alex.androidplayground.workManagerScreen.ui.WorkManagerScreen
import com.alex.androidplayground.core.ui.state.MainViewModel
import com.alex.androidplayground.weatherScreen.ui.WeatherScreen
import kotlinx.collections.immutable.PersistentList
import kotlinx.coroutines.launch

@Composable
fun MainScreen() {
    val viewModel = hiltViewModel<MainViewModel>()
    MainLayout(viewModel.navDrawerItems)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainLayout(navDrawerItems: PersistentList<NavDrawerItem>) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val navController = rememberNavController()
    val selectedItemIndex = rememberSaveable { mutableIntStateOf(0) }
    val scope = rememberCoroutineScope()
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
                            selectedItemIndex.intValue = index
                            navController.navigate(item.destination)
                            scope.launch {
                                drawerState.close()
                            }
                        },
                        modifier = Modifier
                            .padding(NavigationDrawerItemDefaults.ItemPadding)
                            .testTag("NavItem_${item.text}")
                    )
                }
            }
        },
        modifier = Modifier.fillMaxSize(),
        drawerState = drawerState
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = navDrawerItems[selectedItemIndex.intValue].text,
                            modifier = Modifier.testTag("TopAppBarTitle")
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                scope.launch {
                                    drawerState.open()
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Menu toggle icon"
                            )
                        }
                    }
                )
            }
        ) { paddingValues ->
            NavHost(
                modifier = Modifier.padding(paddingValues),
                navController = navController,
                startDestination = FileSelectorDest
            ) {
                composable<FileSelectorDest> {
                    FileSelectorScreen()
                }
                composable<ForegroundServiceDest> {
                    ForegroundServiceScreen()
                }
                composable<WorkManagerDest> {
                    WorkManagerScreen()
                }
                composable<WeatherDest> {
                    WeatherScreen()
                }
            }
        }
    }
}

@Preview
@Composable
fun MainLayoutPreview() {
    MainLayout(getNavDrawerItems())
}