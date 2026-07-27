package com.erp.client.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.erp.client.data.AppContainer

class ViewModelFactory(private val container: AppContainer) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        return when (modelClass) {
            AuthViewModel::class.java -> AuthViewModel(container.authRepository) as T
            GroupViewModel::class.java -> GroupViewModel(container.groupRepository) as T
            QuizViewModel::class.java -> QuizViewModel(container.quizRepository) as T
            AttendanceViewModel::class.java -> AttendanceViewModel(container.quizRepository, container.authRepository) as T
            ClassViewModel::class.java -> ClassViewModel(container.classRepository) as T
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
