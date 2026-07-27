package com.erp.client.ui.screens.teacher

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.erp.client.ui.components.ErrorText
import com.erp.client.ui.components.LoadingIndicator
import com.erp.client.ui.components.SuccessText
import com.erp.client.viewmodel.GroupViewModel
import com.erp.client.viewmodel.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PanelManagementScreen(
    groupViewModel: GroupViewModel,
    onNavigateBack: () -> Unit
) {
    var tagName by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var studentTag by remember { mutableStateOf("") }
    var studentEmail by remember { mutableStateOf("") }

    val createGroupState by groupViewModel.createGroupState.collectAsState()
    val addStudentState by groupViewModel.addStudentState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Panel Management") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Text("Create Class Group", style = MaterialTheme.typography.titleMedium)

            OutlinedTextField(
                value = tagName,
                onValueChange = { tagName = it },
                label = { Text("Tag name (e.g. CSE_panel_B_2026_27)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            )
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            )

            when (val state = createGroupState) {
                is UiState.Loading -> LoadingIndicator()
                is UiState.Error -> ErrorText(state.message)
                is UiState.Success -> SuccessText(state.data)
                else -> {}
            }

            Button(
                onClick = { groupViewModel.createGroup(tagName, description) },
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            ) {
                Text("Create Group")
            }

            Divider(modifier = Modifier.padding(vertical = 24.dp))

            Text("Add Student to Group", style = MaterialTheme.typography.titleMedium)

            OutlinedTextField(
                value = studentTag,
                onValueChange = { studentTag = it },
                label = { Text("Tag name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            )
            OutlinedTextField(
                value = studentEmail,
                onValueChange = { studentEmail = it },
                label = { Text("Student email") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            )

            when (val state = addStudentState) {
                is UiState.Loading -> LoadingIndicator()
                is UiState.Error -> ErrorText(state.message)
                is UiState.Success -> SuccessText(state.data)
                else -> {}
            }

            Button(
                onClick = { groupViewModel.addStudentToGroup(studentTag, studentEmail) },
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            ) {
                Text("Add Student")
            }
        }
    }
}
