package com.erp.client.ui.screens.student

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.erp.client.ui.components.ErrorText
import com.erp.client.ui.components.LoadingIndicator
import com.erp.client.ui.components.SuccessText
import com.erp.client.viewmodel.QuizViewModel
import com.erp.client.viewmodel.UiState

/**
 * The backend does not currently expose a "list quizzes by class tag" endpoint —
 * only /api/quiz/create (teacher) and /api/quiz/submit (student) exist. Until that
 * gap is closed, students enter the quiz ID and question count shared by their
 * teacher, then answer one question at a time before submitting.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizTakingScreen(
    quizViewModel: QuizViewModel,
    studentEmail: String,
    onNavigateBack: () -> Unit
) {
    var quizIdInput by remember { mutableStateOf("") }
    var questionCountInput by remember { mutableStateOf("") }
    var quizStarted by remember { mutableStateOf(false) }
    var currentIndex by remember { mutableIntStateOf(0) }
    val answers = remember { mutableStateListOf<String>() }
    var currentAnswer by remember { mutableStateOf("") }

    val submitState by quizViewModel.submitQuizState.collectAsState()

    LaunchedEffect(Unit) {
        quizViewModel.resetSubmitQuizState()
    }

    val questionCount = questionCountInput.toIntOrNull() ?: 0

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Take Quiz") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            if (!quizStarted) {
                Text("Enter the quiz details shared by your teacher", style = MaterialTheme.typography.titleMedium)

                OutlinedTextField(
                    value = quizIdInput,
                    onValueChange = { quizIdInput = it },
                    label = { Text("Quiz ID") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth().padding(top = 12.dp)
                )
                OutlinedTextField(
                    value = questionCountInput,
                    onValueChange = { questionCountInput = it },
                    label = { Text("Number of questions") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                )

                Button(
                    onClick = {
                        answers.clear()
                        repeat(questionCount) { answers.add("") }
                        currentIndex = 0
                        currentAnswer = ""
                        quizStarted = questionCount > 0 && quizIdInput.toLongOrNull() != null
                    },
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
                ) {
                    Text("Start Quiz")
                }
            } else {
                LinearProgressIndicator(
                    progress = { (currentIndex + 1f) / questionCount },
                    modifier = Modifier.fillMaxWidth()
                )

                Card(modifier = Modifier.fillMaxWidth().padding(top = 16.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "Question ${currentIndex + 1} of $questionCount",
                            style = MaterialTheme.typography.labelLarge
                        )
                        OutlinedTextField(
                            value = currentAnswer,
                            onValueChange = { currentAnswer = it },
                            label = { Text("Your answer") },
                            modifier = Modifier.fillMaxWidth().padding(top = 12.dp)
                        )
                    }
                }

                Row(modifier = Modifier.fillMaxWidth().padding(top = 16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    OutlinedButton(
                        onClick = {
                            answers[currentIndex] = currentAnswer
                            if (currentIndex > 0) {
                                currentIndex -= 1
                                currentAnswer = answers[currentIndex]
                            }
                        }
                    ) {
                        Text("Previous")
                    }

                    if (currentIndex < questionCount - 1) {
                        Button(
                            onClick = {
                                answers[currentIndex] = currentAnswer
                                currentIndex += 1
                                currentAnswer = answers[currentIndex]
                            }
                        ) {
                            Text("Next")
                        }
                    } else {
                        Button(
                            onClick = {
                                answers[currentIndex] = currentAnswer
                                quizViewModel.submitQuiz(quizIdInput.toLongOrNull(), studentEmail, answers.toList())
                            }
                        ) {
                            Text("Submit")
                        }
                    }
                }

                when (val state = submitState) {
                    is UiState.Loading -> LoadingIndicator(modifier = Modifier.padding(top = 16.dp))
                    is UiState.Error -> ErrorText(state.message)
                    is UiState.Success -> SuccessText(state.data)
                    else -> {}
                }
            }
        }
    }
}
