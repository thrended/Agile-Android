@file:OptIn(ExperimentalMaterialApi::class)

package com.example.agileandroidalpha.obsolete_excluded

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.launch

typealias OnExploreItemClicked = () -> Unit

enum class ScrumScreen {
    Projects, Boards, Story, Planning, Review, Retrospective, Backlog, Active, Standup,
    Releases, Reports, Issues, Components, Timesheets, Tests, Test_Automation
}

enum class FuncScreen {
    Register, Login, Settings, Task, Create, Edit, Delete, Artifacts, Help, Teams, Users, Chat
}

enum class TabScreen {
    Test1, Test2, Test3
}

@ExperimentalMaterialApi
@Composable
fun AGDroidHome(
    onExploreItemClicked: OnExploreItemClicked,
    modifier: Modifier = Modifier,
) {
    val scaffoldState = rememberScaffoldState()
    Scaffold(
        scaffoldState = scaffoldState,
        modifier = Modifier.statusBarsPadding(),
        drawerContent = {
            //DroidDrawer()
        }
    ) { padding ->
        val scope = rememberCoroutineScope()
        AGDroidHomeContent(
            modifier = modifier.padding(padding),
            onExploreItemClicked = onExploreItemClicked,
            openDrawer = {
                scope.launch {
                    scaffoldState.drawerState.open()
                }
            }
        )
    }
}

@ExperimentalMaterialApi
@Composable
fun AGDroidHomeContent(
    onExploreItemClicked: OnExploreItemClicked,
    openDrawer: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MainViewModel = MainViewModel(ScrumVault(ScrumData())),
) {

    val suggestedTasks: List<ScrumModel> = remember { emptyList() }

    val onPeopleChanged: (Int) -> Unit = { viewModel.updatePeople(it) }
    var tabSelected by remember { mutableStateOf(TabScreen.Test1) }

    BackdropScaffold(
        modifier = modifier,
        scaffoldState = rememberBackdropScaffoldState(BackdropValue.Revealed),
        frontLayerScrimColor = Color.Unspecified,
        appBar = {
            HomeTabBar(openDrawer, tabSelected, onTabSelected = { tabSelected = it })
        },
        backLayerContent = {
        },
        frontLayerContent = {
            when (tabSelected) {
                TabScreen.Test1 -> {

                }
                TabScreen.Test2 -> {

                }
                TabScreen.Test3 -> {

                }
            }
        }
    )
}

@Composable
private fun HomeTabBar(
    openDrawer: () -> Unit,
    tabSelected: TabScreen,
    onTabSelected: (TabScreen) -> Unit,
    modifier: Modifier = Modifier
) {
    ScrumTabBar(
        modifier = modifier,
        onMenuClicked = openDrawer
    ) { tabBarModifier ->
        ScrumTabs(
            modifier = tabBarModifier,
            titles = TabScreen.values().map { it.name },
            tabSelected = tabSelected,
            onTabSelected = { newTab -> onTabSelected(TabScreen.values()[newTab.ordinal]) }
        )
    }
}

/*

frontLayerContent = {
            when (tabSelected) {
                ScrumScreen.Planning -> {
                    ExploreSection(
                        title = "Explore Flights by Destination",
                        exploreList = suggestedTasks,
                        onItemClicked = onExploreItemClicked
                    )
                }
                ScrumScreen.Active -> {
                    ExploreSection(
                        title = "Explore Properties by Destination",
                        exploreList = viewModel.tabs,
                        onItemClicked = onExploreItemClicked
                    )
                }
                ScrumScreen.Backlog -> {
                    ExploreSection(
                        title = "Explore Restaurants by Destination",
                        exploreList = viewModel.tabs,
                        onItemClicked = onExploreItemClicked
                    )
                }
            }
        }
 when (tabSelected) {
        ScrumScreen.Planning -> FlySearchContent(
            onPeopleChanged = onPeopleChanged,
            onToDestinationChanged = { viewModel.toTabChanged() }
        )
        ScrumScreen.Active -> SleepSearchContent(
            onPeopleChanged = onPeopleChanged
        )
        ScrumScreen.Backlog -> EatSearchContent(
            onPeopleChanged = onPeopleChanged
        )
    }
 */