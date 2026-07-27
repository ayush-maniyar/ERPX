package com.erp.client.ui.screens.teacher

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.erp.client.ui.components.ErrorText
import com.erp.client.ui.components.LoadingIndicator
import com.erp.client.ui.components.SuccessText
import com.erp.client.viewmodel.QuizViewModel
import com.erp.client.viewmodel.UiState

private data class QuestionEntry(var question: String = "", var answer: String = "")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizBuilderScreen(
    quizViewModel: QuizViewModel,
    onNavigateBack: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var targetTag by remember { mutableStateOf("") }
    val entries = remember { mutableStateListOf(QuestionEntry()) }

    val createQuizState by quizViewModel.createQuizState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Quiz Builder") },
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
                label = { Text("Quiz title") },
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

            Text(
                "Questions",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = 16.dp)
            )

            entries.forEachIndexed { index, entry ->
                Card(modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                "Question ${index + 1}",
                                style = MaterialTheme.typography.labelLarge,
                                modifier = Modifier.weight(1f)
                            )
                            if (entries.size > 1) {
                                IconButton(onClick = { entries.removeAt(index) }) {
                                    Icon(Icons.Filled.Delete, contentDescription = "Remove question")
                                }
                            }
                        }

                        OutlinedTextField(
                            value = entry.question,
                            onValueChange = { entries[index] = entry.copy(question = it) },
                            label = { Text("Question text") },
                            modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
                        )
                        OutlinedTextField(
                            value = entry.answer,
                            onValueChange = { entries[index] = entry.copy(answer = it) },
                            label = { Text("Correct answer") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
                        )
                    }
                }
            }

            OutlinedButton(
                onClick = { entries.add(QuestionEntry()) },
                modifier = Modifier.fillMaxWidth().padding(top = 12.dp)
            ) {
                Text("+ Add Question")
            }

            when (val state = createQuizState) {
                is UiState.Loading -> LoadingIndicator()
                is UiState.Error -> ErrorText(state.message)
                is UiState.Success -> SuccessText(state.data)
                else -> {}
            }

            Button(
                onClick = {
                    quizViewModel.createQuiz(
                        title,
                        targetTag,
                        entries.map { it.question },
                        entries.map { it.answer }
                    )
                },
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp, bottom = 24.dp)
            ) {
                Text("Publish Quiz")
            }
        }
    }
}
