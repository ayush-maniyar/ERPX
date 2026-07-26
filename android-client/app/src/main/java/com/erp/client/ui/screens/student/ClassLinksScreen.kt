package com.erp.client.ui.screens.student

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.VideoCall
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.erp.client.ui.components.ErrorText
import com.erp.client.ui.components.LoadingIndicator
import com.erp.client.viewmodel.ClassViewModel
import com.erp.client.viewmodel.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClassLinksScreen(
    classViewModel: ClassViewModel,
    onNavigateBack: () -> Unit
) {
    var tagName by remember { mutableStateOf("") }
    val classListState by classViewModel.classListState.collectAsState()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Class Links") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = tagName,
                    onValueChange = { tagName = it },
                    label = { Text("Your class tag") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    modifier = Modifier.weight(1f)
                )
                Button(
                    onClick = { classViewModel.loadClassesByTag(tagName) },
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Text("Search")
                }
            }

            Box(modifier = Modifier.fillMaxSize().padding(top = 16.dp)) {
                when (val state = classListState) {
                    is UiState.Loading -> LoadingIndicator()
                    is UiState.Error -> ErrorText(state.message)
                    is UiState.Success -> {
                        if (state.data.isEmpty()) {
                            Text("No scheduled classes for this tag yet.", style = MaterialTheme.typography.bodyMedium)
                        } else {
                            LazyColumn(
                                contentPadding = PaddingValues(bottom = 16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(state.data) { videoClass ->
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(16.dp).fillMaxWidth(),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(videoClass.title, style = MaterialTheme.typography.titleMedium)
                                                Text(videoClass.scheduledTime, style = MaterialTheme.typography.bodySmall)
                                            }
                                            IconButton(onClick = {
                                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(videoClass.meetLink))
                                                context.startActivity(intent)
                                            }) {
                                                Icon(Icons.Filled.VideoCall, contentDescription = "Join class")
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    else -> {}
                }
            }
        }
    }
}
