package com.erp.client.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.erp.client.data.AppContainer
import com.erp.client.domain.model.Role
import com.erp.client.ui.screens.auth.LoginScreen
import com.erp.client.ui.screens.auth.RegisterScreen
import com.erp.client.ui.screens.student.AttendanceHistoryScreen
import com.erp.client.ui.screens.student.ClassLinksScreen
import com.erp.client.ui.screens.student.QuizTakingScreen
import com.erp.client.ui.screens.student.StudentDashboardScreen
import com.erp.client.ui.screens.teacher.ClassSchedulerScreen
import com.erp.client.ui.screens.teacher.EmailComposerScreen
import com.erp.client.ui.screens.teacher.PanelManagementScreen
import com.erp.client.ui.screens.teacher.QuizBuilderScreen
import com.erp.client.ui.screens.teacher.TeacherDashboardScreen
import com.erp.client.viewmodel.AttendanceViewModel
import com.erp.client.viewmodel.AuthViewModel
import com.erp.client.viewmodel.ClassViewModel
import com.erp.client.viewmodel.GroupViewModel
import com.erp.client.viewmodel.QuizViewModel
import com.erp.client.viewmodel.ViewModelFactory

@Composable
fun ErpNavHost(container: AppContainer) {
    val navController: NavHostController = rememberNavController()
    val factory = remember { ViewModelFactory(container) }

    val authViewModel: AuthViewModel = viewModel(factory = factory)
    val groupViewModel: GroupViewModel = viewModel(factory = factory)
    val quizViewModel: QuizViewModel = viewModel(factory = factory)
    val attendanceViewModel: AttendanceViewModel = viewModel(factory = factory)
    val classViewModel: ClassViewModel = viewModel(factory = factory)

    val session = authViewModel.currentSession()
    val startDestination = when {
        session == null -> Routes.LOGIN
        session.role == Role.TEACHER -> Routes.TEACHER_DASHBOARD
        else -> Routes.STUDENT_DASHBOARD
    }

    NavHost(navController = navController, startDestination = startDestination) {
        composable(Routes.LOGIN) {
            LoginScreen(
                authViewModel = authViewModel,
                onLoginSuccess = { userSession ->
                    val destination = if (userSession.role == Role.TEACHER) Routes.TEACHER_DASHBOARD else Routes.STUDENT_DASHBOARD
                    navController.navigate(destination) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onNavigateToRegister = { navController.navigate(Routes.REGISTER) }
            )
        }

        composable(Routes.REGISTER) {
            RegisterScreen(
                authViewModel = authViewModel,
                onRegisterSuccess = { navController.popBackStack() },
                onNavigateToLogin = { navController.popBackStack() }
            )
        }

        composable(Routes.TEACHER_DASHBOARD) {
            TeacherDashboardScreen(
                teacherName = authViewModel.currentSession()?.name ?: "Teacher",
                onNavigateToPanelManagement = { navController.navigate(Routes.PANEL_MANAGEMENT) },
                onNavigateToQuizBuilder = { navController.navigate(Routes.QUIZ_BUILDER) },
                onNavigateToEmailComposer = { navController.navigate(Routes.EMAIL_COMPOSER) },
                onNavigateToClassScheduler = { navController.navigate(Routes.TEACHER_CLASS_SCHEDULER) },
                onLogout = {
                    authViewModel.logout()
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.PANEL_MANAGEMENT) {
            PanelManagementScreen(
                groupViewModel = groupViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Routes.QUIZ_BUILDER) {
            QuizBuilderScreen(
                quizViewModel = quizViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Routes.EMAIL_COMPOSER) {
            EmailComposerScreen(
                groupViewModel = groupViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Routes.TEACHER_CLASS_SCHEDULER) {
            ClassSchedulerScreen(
                classViewModel = classViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Routes.STUDENT_DASHBOARD) {
            StudentDashboardScreen(
                studentName = authViewModel.currentSession()?.name ?: "Student",
                onNavigateToQuizTaking = { navController.navigate(Routes.QUIZ_TAKING) },
                onNavigateToAttendanceHistory = { navController.navigate(Routes.ATTENDANCE_HISTORY) },
                onNavigateToClassLinks = { navController.navigate(Routes.CLASS_LINKS) },
                onLogout = {
                    authViewModel.logout()
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.QUIZ_TAKING) {
            QuizTakingScreen(
                quizViewModel = quizViewModel,
                studentEmail = authViewModel.currentSession()?.email.orEmpty(),
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Routes.ATTENDANCE_HISTORY) {
            AttendanceHistoryScreen(
                attendanceViewModel = attendanceViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Routes.CLASS_LINKS) {
            ClassLinksScreen(
                classViewModel = classViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
