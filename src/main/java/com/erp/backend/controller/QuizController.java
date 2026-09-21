package com.erp.backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.erp.backend.dto.CreateQuizRequest;
import com.erp.backend.dto.MessageResponse;
import com.erp.backend.dto.SubmitQuizRequest;
import com.erp.backend.model.Attendance;
import com.erp.backend.model.Quiz;
import com.erp.backend.service.QuizService;

@RestController
@RequestMapping("/api/quiz")
public class QuizController {

    @Autowired
    private QuizService quizService;

    @PostMapping("/create")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<Quiz> createQuiz(
            @RequestBody CreateQuizRequest request,
            Authentication authentication) {

        String teacherEmail = authentication.getName();

        Quiz quiz = quizService.createQuiz(request, teacherEmail);

        return ResponseEntity.ok(quiz);
    }

    @GetMapping("/my-quizzes")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<List<Quiz>> getMyQuizzes(
            Authentication authentication) {

        String teacherEmail = authentication.getName();

        return ResponseEntity.ok(
                quizService.getTeacherQuizzes(teacherEmail)
        );
    }

    @GetMapping("/available")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<List<Quiz>> getAvailableQuizzes() {
        return ResponseEntity.ok(
                quizService.getAvailableQuizzes());
    }

    @PostMapping("/submit")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<MessageResponse> submitQuiz(@RequestBody SubmitQuizRequest request) {
        String response = quizService.submitQuiz(request);
        return ResponseEntity.ok(new MessageResponse(response));
    }

    @GetMapping("/attendance/{email}")
    @PreAuthorize("hasAnyRole('STUDENT', 'TEACHER', 'ADMIN')")
    public ResponseEntity<List<Attendance>> getAttendance(@PathVariable String email) {
        List<Attendance> attendanceList = quizService.getStudentAttendance(email);
        return ResponseEntity.ok(attendanceList);
    }
}
