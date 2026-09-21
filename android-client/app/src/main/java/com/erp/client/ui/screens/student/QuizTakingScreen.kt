package com.erp.client.ui.screens.student

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.unit.dp
import com.erp.client.ui.components.ErrorText
import com.erp.client.ui.components.LoadingIndicator
import com.erp.client.ui.components.SuccessText
import com.erp.client.viewmodel.QuizViewModel
import com.erp.client.viewmodel.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizTakingScreen(
    quizViewModel: QuizViewModel,
    studentEmail: String,
    onNavigateBack: () -> Unit
) {
    var selectedQuiz by remember { mutableStateOf<com.erp.client.data.remote.dto.QuizDto?>(null) }

    var currentIndex by remember { mutableIntStateOf(0) }

    val answers = remember {
        mutableStateListOf<String>()
    }

    var currentAnswer by remember {
        mutableStateOf("")
    }

    val availableQuizzes by
        quizViewModel.availableQuizzes.collectAsState()

    val submitState by
        quizViewModel.submitQuizState.collectAsState()

    LaunchedEffect(Unit) {
        quizViewModel.resetSubmitQuizState()
        quizViewModel.loadAvailableQuizzes()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (selectedQuiz == null)
                            "Take Quiz"
                        else
                            selectedQuiz!!.title
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            if (selectedQuiz != null) {
                                selectedQuiz = null
                                currentIndex = 0
                                answers.clear()
                                currentAnswer = ""
                            } else {
                                onNavigateBack()
                            }
                        }
                    ) {
                        Icon(
                            Icons.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {

            if (selectedQuiz == null) {

                // ---------------------------------------------------------
                // AVAILABLE QUIZZES
                // ---------------------------------------------------------

                Text(
                    text = "Available Quizzes",
                    style = MaterialTheme.typography.titleLarge
                )

                when (val state = availableQuizzes) {

                    is UiState.Loading -> {
                        LoadingIndicator(
                            modifier = Modifier.padding(top = 16.dp)
                        )
                    }

                    is UiState.Error -> {
                        ErrorText(state.message)
                    }

                    is UiState.Success -> {

                        if (state.data.isEmpty()) {

                            Text(
                                text = "No quizzes are currently available.",
                                modifier = Modifier.padding(top = 12.dp)
                            )

                        } else {

                            state.data.forEach { quiz ->

                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 12.dp)
                                ) {

                                    Column(
                                        modifier = Modifier.padding(16.dp)
                                    ) {

                                        Text(
                                            text = quiz.title,
                                            style = MaterialTheme.typography.titleMedium
                                        )

                                        Text(
                                            text = "Class: ${quiz.targetTag}",
                                            style = MaterialTheme.typography.bodyMedium,
                                            modifier = Modifier.padding(top = 4.dp)
                                        )

                                        Text(
                                            text = "${quiz.questions.size} questions",
                                            style = MaterialTheme.typography.bodyMedium,
                                            modifier = Modifier.padding(top = 4.dp)
                                        )

                                        Button(
                                            onClick = {

                                                selectedQuiz = quiz

                                                answers.clear()

                                                repeat(quiz.questions.size) {
                                                    answers.add("")
                                                }

                                                currentIndex = 0
                                                currentAnswer = ""

                                                quizViewModel.resetSubmitQuizState()
                                            },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(top = 12.dp)
                                        ) {
                                            Text("Start Quiz")
                                        }
                                    }
                                }
                            }
                        }
                    }

                    else -> {}
                }

            } else {

                // ---------------------------------------------------------
                // QUIZ
                // ---------------------------------------------------------

                val quiz = selectedQuiz!!

                val questionCount = quiz.questions.size

                if (questionCount == 0) {

                    Text(
                        text = "This quiz has no questions.",
                        style = MaterialTheme.typography.bodyLarge
                    )

                } else {

                    Text(
                        text = quiz.title,
                        style = MaterialTheme.typography.titleLarge
                    )

                    Text(
                        text = "Class: ${quiz.targetTag}",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 4.dp)
                    )

                    Text(
                        text = "Question ${currentIndex + 1} of $questionCount",
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.padding(top = 12.dp)
                    )

                    LinearProgressIndicator(
                        progress = {
                            (currentIndex + 1f) / questionCount
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                    )

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp)
                    ) {

                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {

                            // ACTUAL QUESTION
                            Text(
                                text = quiz.questions[currentIndex],
                                style = MaterialTheme.typography.titleMedium
                            )

                            // ANSWER
                            OutlinedTextField(
                                value = currentAnswer,
                                onValueChange = {
                                    currentAnswer = it
                                },
                                label = {
                                    Text("Your answer")
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 12.dp)
                            )
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {

                        OutlinedButton(
                            onClick = {

                                answers[currentIndex] =
                                    currentAnswer

                                if (currentIndex > 0) {

                                    currentIndex--

                                    currentAnswer =
                                        answers[currentIndex]
                                }
                            },
                            enabled = currentIndex > 0
                        ) {
                            Text("Previous")
                        }

                        if (currentIndex < questionCount - 1) {

                            Button(
                                onClick = {

                                    answers[currentIndex] =
                                        currentAnswer

                                    currentIndex++

                                    currentAnswer =
                                        answers[currentIndex]
                                }
                            ) {
                                Text("Next")
                            }

                        } else {

                            Button(
                                onClick = {

                                    answers[currentIndex] =
                                        currentAnswer

                                    quizViewModel.submitQuiz(
                                        quiz.id,
                                        studentEmail,
                                        answers.toList()
                                    )
                                }
                            ) {
                                Text("Submit")
                            }
                        }
                    }

                    when (val state = submitState) {

                        is UiState.Loading -> {
                            LoadingIndicator(
                                modifier = Modifier.padding(top = 16.dp)
                            )
                        }

                        is UiState.Error -> {
                            ErrorText(state.message)
                        }

                        is UiState.Success -> {
                            SuccessText(state.data)
                        }

                        else -> {}
                    }
                }
            }
        }
    }
}