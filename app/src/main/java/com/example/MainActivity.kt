package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.EditorScreen
import com.example.ui.screens.ProjectListScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.EditorViewModel

enum class AppScreen {
    PROJECTS_LIST,
    EDITOR
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                NovaCutApp()
            }
        }
    }
}

@Composable
fun NovaCutApp(
    viewModel: EditorViewModel = viewModel()
) {
    var currentScreen by remember { mutableStateOf(AppScreen.PROJECTS_LIST) }

    when (currentScreen) {
        AppScreen.PROJECTS_LIST -> {
            ProjectListScreen(
                viewModel = viewModel,
                onOpenProject = { projectId ->
                    viewModel.loadProject(projectId)
                    currentScreen = AppScreen.EDITOR
                },
                modifier = Modifier.fillMaxSize()
            )
        }
        AppScreen.EDITOR -> {
            EditorScreen(
                viewModel = viewModel,
                onNavigateBack = {
                    currentScreen = AppScreen.PROJECTS_LIST
                },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
