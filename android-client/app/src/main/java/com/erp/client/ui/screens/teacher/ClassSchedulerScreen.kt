package com.erp.client.ui.screens.teacher

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.erp.client.ui.components.ErrorText
import com.erp.client.ui.components.LoadingIndicator
import com.erp.client.ui.components.SuccessText
import com.erp.client.viewmodel.ClassViewModel
import com.erp.client.viewmodel.UiState
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClassSchedulerScreen(
    classViewModel: ClassViewModel,
    onNavigateBack: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var targetTag by remember { mutableStateOf("") }
    var meetLink by remember { mutableStateOf("") }
    var scheduledTime by remember { mutableStateOf("") }

    val createClassState by classViewModel.createClassState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Schedule Video Class") },
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
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Class title") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = targetTag,
                onValueChange = { targetTag = it },
                label = { Text("Target class tag") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            )
            OutlinedTextField(
                value = meetLink,
                onValueChange = { meetLink = it },
                label = { Text("Google Meet link") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            )
            OutlinedTextField(
                value = scheduledTime,
                onValueChange = { scheduledTime = it },
                label = { Text("Scheduled time (yyyy-MM-ddTHH:mm:ss)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            )

            when (val state = createClassState) {
                is UiState.Loading -> LoadingIndicator()
                is UiState.Error -> ErrorText(state.message)
                is UiState.Success -> SuccessText(state.data)
                else -> {}
            }

            Button(
                onClick = { classViewModel.createVideoClass(title, targetTag, meetLink, scheduledTime) },
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
            ) {
                Text("Schedule Class")
            }
        }
    }
}
