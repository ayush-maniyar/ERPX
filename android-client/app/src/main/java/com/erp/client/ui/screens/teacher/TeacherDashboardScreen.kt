package com.erp.client.ui.screens.teacher

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.VideoCall
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Alignment

data class TeacherAction(
    val title: String,
    val subtitle: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val onClick: () -> Unit
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeacherDashboardScreen(
    teacherName: String,
    onNavigateToPanelManagement: () -> Unit,
    onNavigateToQuizBuilder: () -> Unit,
    onNavigateToEmailComposer: () -> Unit,
    onNavigateToClassScheduler: () -> Unit,
    onLogout: () -> Unit
) {
    val actions = listOf(
        TeacherAction("Panel Management", "Create class groups and add students", Icons.Filled.Groups, onNavigateToPanelManagement),
        TeacherAction("Quiz Builder", "Create quizzes for a class panel", Icons.Filled.Quiz, onNavigateToQuizBuilder),
        TeacherAction("Bulk Email", "Broadcast an email to a class panel", Icons.Filled.Email, onNavigateToEmailComposer),
        TeacherAction("Video Classes", "Schedule a Google Meet session", Icons.Filled.VideoCall, onNavigateToClassScheduler),
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Welcome, $teacherName") },
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(Icons.Filled.Logout, contentDescription = "Logout")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            actions.forEach { action ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { action.onClick() },
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(action.icon, contentDescription = null, modifier = Modifier.size(32.dp))
                        Spacer(modifier = Modifier.size(16.dp))
                        Column {
                            Text(action.title, style = MaterialTheme.typography.titleMedium)
                            Text(action.subtitle, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        }
    }
}
