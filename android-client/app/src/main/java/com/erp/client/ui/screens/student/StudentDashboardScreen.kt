package com.erp.client.ui.screens.student

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Logout
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

private data class StudentAction(
    val title: String,
    val subtitle: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val onClick: () -> Unit
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentDashboardScreen(
    studentName: String,
    onNavigateToQuizTaking: () -> Unit,
    onNavigateToAttendanceHistory: () -> Unit,
    onNavigateToClassLinks: () -> Unit,
    onLogout: () -> Unit
) {
    val actions = listOf(
        StudentAction("Take a Quiz", "Submit answers for an assigned quiz", Icons.Filled.Assignment, onNavigateToQuizTaking),
        StudentAction("Attendance History", "View your recorded attendance", Icons.Filled.History, onNavigateToAttendanceHistory),
        StudentAction("Class Links", "Join scheduled Google Meet sessions", Icons.Filled.VideoCall, onNavigateToClassLinks),
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Welcome, $studentName") },
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
