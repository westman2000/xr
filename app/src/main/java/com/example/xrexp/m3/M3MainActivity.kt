@file:SuppressLint("NullAnnotationGroup")
@file:OptIn(
    ExperimentalMaterial3AdaptiveApi::class,
    ExperimentalMaterial3Api::class,
    ExperimentalMaterial3XrApi::class,
)

package com.example.xrexp.m3


import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffold
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.layout.ThreePaneScaffoldDestinationItem
import androidx.compose.material3.adaptive.navigation.ThreePaneScaffoldNavigator
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.xr.compose.material3.DefaultNavigationBarOrbiterProperties
import androidx.xr.compose.material3.DefaultNavigationRailOrbiterProperties
import androidx.xr.compose.material3.EnableXrComponentOverrides
import androidx.xr.compose.material3.ExperimentalMaterial3XrApi
import androidx.xr.compose.material3.LocalNavigationBarOrbiterProperties
import androidx.xr.compose.material3.LocalNavigationRailOrbiterProperties
import androidx.xr.compose.spatial.EdgeOffset

class M3MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { EnableXrComponentOverrides { Content() } }
    }
}

@Composable
private fun Content() {
    var navSuiteType: NavigationSuiteType? by remember { mutableStateOf(null) }
    var edgeOffset: EdgeOffset? by remember { mutableStateOf(null) }

    var navSuiteSelectedItem by remember { mutableStateOf(NavSuiteItem.HOME) }

    CompositionLocalProvider(
        LocalNavigationBarOrbiterProperties provides
            DefaultNavigationBarOrbiterProperties.copy(offset = edgeOffset),
        LocalNavigationRailOrbiterProperties provides
            DefaultNavigationRailOrbiterProperties.copy(offset = edgeOffset),
    ) {
        NavigationSuiteScaffold(
            navigationSuiteItems = {
                NavSuiteItem.values().forEach { item ->
                    item(
                        selected = navSuiteSelectedItem == item,
                        onClick = { navSuiteSelectedItem = item },
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        label = { Text(item.label) },
                    )
                }
            },
            layoutType =
                navSuiteType
                    ?: NavigationSuiteScaffoldDefaults.calculateFromAdaptiveInfo(
                        currentWindowAdaptiveInfo()
                    ),
        ) {
            when (navSuiteSelectedItem) {
                NavSuiteItem.HOME -> {
                    Home()
                }
                NavSuiteItem.SETTINGS -> {
                    XrSettingsPane(
                        onNavSuiteTypeChanged = { navSuiteType = it },
                        onOrbiterEdgeOffsetChanged = { edgeOffset = it },
                    )
                }
            }
        }
    }
}

@Composable
private fun Home() {
    val navigator: ThreePaneScaffoldNavigator<Destination> =
        rememberListDetailPaneScaffoldNavigator(
            initialDestinationHistory =
                listOf(ThreePaneScaffoldDestinationItem(ListDetailPaneScaffoldRole.List))
        )
    ListDetailPaneScaffold(
        directive = navigator.scaffoldDirective,
        value = navigator.scaffoldValue,
        listPane = { AnimatedPane { ListPane(navigator) } },
        detailPane = { AnimatedPane { DetailPane(navigator) } },
    )
}

private const val TAG = "MainActivity"
