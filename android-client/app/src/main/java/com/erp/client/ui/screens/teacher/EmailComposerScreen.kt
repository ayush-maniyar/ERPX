package com.erp.client.ui.screens.teacher

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.runtime.LaunchedEffect
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
fun EmailComposerScreen(
    groupViewModel: GroupViewModel,
    onNavigateBack: () -> Unit
) {
    var tagName by remember { mutableStateOf("") }
    var subject by remember { mutableStateOf("") }
    var body by remember { mutableStateOf("") }

    val sendEmailState by groupViewModel.sendEmailState.collectAsState()

    LaunchedEffect(Unit) {
        groupViewModel.resetSendEmailState()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Bulk Email Composer") },
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
                value = tagName,
                onValueChange = { tagName = it },
                label = { Text("Target class tag") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = subject,
                onValueChange = { subject = it },
                label = { Text("Subject") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            )
            OutlinedTextField(
                value = body,
                onValueChange = { body = it },
                label = { Text("Message body") },
                modifier = Modifier.fillMaxWidth().height(200.dp).padding(top = 8.dp)
            )

            when (val state = sendEmailState) {
                is UiState.Loading -> LoadingIndicator()
                is UiState.Error -> ErrorText(state.message)
                is UiState.Success -> SuccessText(state.data)
                else -> {}
            }

            Button(
                onClick = { groupViewModel.sendEmailByTag(tagName, subject, body) },
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
            ) {
                Text("Send to Panel")
            }
        }
    }
}
